import re

import Config
import Utils

intab = '۰۱۲۳۴۵۶۷۸۹'
outtab = '0123456789'
trantab = str.maketrans(intab, outtab)


def tehran_university_faculties_extractor():
    tehran_university_faculties = list()
    for i in range(Config.tehran_university_faculties_number_of_pages):
        link = Config.tehran_university_faculties_link_template + str(i+1)
        parsed_page, version = Utils.get_parsed_html_page(link)
        faculties_table = parsed_page.find(
            'table', attrs={'class': 'table table-striped table-bordered responsive-table'})

        table_body = faculties_table.find('tbody')

        for tr in table_body.find_all('tr'):
            raw_data = tr.find_all('td')

            last_name = raw_data[0].get_text()
            first_name = raw_data[1].get_text()
            full_name = first_name + ' ' + last_name

            website = raw_data[0].find('a')['href'] if raw_data[0].find('a') else None

            faculty, group = raw_data[2].get_text().split('/')

            if 'دانشکده ' in group:
                if 'دانشکده ' not in faculty:
                    faculty = group
            else:
                if 'دانشکده ' not in faculty:
                    faculty = 'دانشکده ' + faculty

            grade = raw_data[3].get_text()

            tehran_university_faculties.extend(Utils.generate_each_faculty_tuples
                                               (full_name, faculty, website, link, version,
                                                first_name=first_name, last_name=last_name, grade=grade))

    print(len(tehran_university_faculties))
    Utils.save_json(Config.resources_dir, Config.tehran_university_faculties_filename, tehran_university_faculties)


def ferdowsi_university_faculties_extractor():
    ferdowsi_university_faculties = list()
    for i in range(0, Config.ferdowsi_university_faculties_number_of_faculties,
                   Config.ferdowsi_university_faculties_link_faculties_per_page):
        link = Config.ferdowsi_university_faculties_link_template + str(i+1)
        parsed_page, version = Utils.get_parsed_html_page(link)
        faculties_table = parsed_page.find('table', attrs={'class': 'text4 prtable table table-hover'})
        for tr in faculties_table.find_all('tr')[1:]:
            raw_data = tr.find_all('td')

            last_name = raw_data[0].get_text()
            first_name = raw_data[1].get_text()
            full_name = first_name + ' ' + last_name

            faculty = raw_data[2].get_text()
            group = raw_data[3].get_text()
            website = raw_data[5].get_text()

            ferdowsi_university_faculties.extend(Utils.generate_each_faculty_tuples
                                                 (full_name, faculty, website, link, version,
                                                  first_name=first_name, last_name=last_name, group=group))

    print(len(ferdowsi_university_faculties))
    Utils.save_json(Config.resources_dir, Config.ferdowsi_university_faculties_filename, ferdowsi_university_faculties)


def yazd_university_faculties_extractor():
    yazd_university_faculties = list()
    for i in range(Config.yazd_university_faculties_number_of_pages):
        link = Config.yazd_university_faculties_link_template + str(i+1)
        parsed_page, version = Utils.get_parsed_html_page(link)
        faculty_tables = parsed_page.find_all(
            'table', attrs={'class': 'tm-people uk-teaser-people uk-table '
                                     'uk-table-hover0 uk-table-striped uk-table-condensed'})
        for faculty_table in faculty_tables:
            faculty_table_data = list()
            faculty_data = dict()
            for tr in faculty_table.find_all('tr'):
                for td in tr.find_all('td'):
                    faculty_table_data.append(td)
            for td in faculty_table_data:
                table_data_class = ' '.join(td.attrs.get('class'))
                if table_data_class == 'picture uk-border-remove':
                    faculty_data['image'] = td.find('img').attrs.get('src')
                elif table_data_class == 'uk-text-truncate title':
                    faculty_data['full_name'] = td.find('a').get_text().strip()
                elif table_data_class == 'uk-text-truncate people-border-right subtitle':
                    faculty_data['grade'] = re.sub(r'\[.*\]', '', td.get_text()).strip()
                elif table_data_class == 'mail uk-text-truncate':
                    mail = re.sub(r'\[at\]', '@', td.get_text()).strip()
                    faculty_data['mail'] = re.sub('\s+', '', mail)
                elif table_data_class == 'profile uk-text-truncate people-border-right':
                    faculty_data['website'] = td.find('a').get_text().strip()
                elif table_data_class == 'persianumber uk-text-truncate':
                    faculty_data['address'] = td.get_text().strip()
                elif table_data_class == 'persianumber uk-text-truncate people-border-right':
                    faculty_data['faculty'] = re.sub('\s\s+', ' ', td.get_text().strip())
                elif table_data_class == 'phone persianumber uk-text-truncate':
                    faculty_data['phone'] = re.sub('\s+', '', td.get_text()).translate(trantab)
                elif table_data_class == 'fax persianumber uk-text-truncate people-border-right':
                    faculty_data['fax'] = re.sub('\s+', '', td.get_text()).translate(trantab)
            yazd_university_faculties.extend(
                Utils.generate_each_faculty_tuples(faculty_data['full_name'], faculty_data['faculty'],
                                                   faculty_data['website'], link, version, grade=faculty_data['grade'],
                                                   image=faculty_data['image'], mail=faculty_data['mail'],
                                                   address=faculty_data['address'], phone=faculty_data['phone'],
                                                   fax=faculty_data['fax']))

    print(len(yazd_university_faculties))
    print(Utils.number_of_faculties)
    Utils.save_json(Config.resources_dir, Config.yazd_university_faculties_filename, yazd_university_faculties)
