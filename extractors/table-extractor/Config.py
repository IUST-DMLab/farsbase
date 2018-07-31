from os.path import dirname, realpath, join

current_dir = dirname(realpath(__file__))
resources_dir = join(current_dir, 'resources')

http_response_date_template = '%a, %d %b %Y %H:%M:%S %Z'

tehran_university_faculties_link_template = 'http://ut.ac.ir/fa/faculty?page='
tehran_university_faculties_number_of_pages = 103
tehran_university_faculties_filename = 'tehran_university_faculties'

ferdowsi_university_faculties_link_template = 'https://www.um.ac.ir/index.php?module=Professors&func=view&pm=' \
                                              '&alpha=&pname=&ccode=&startnum='
ferdowsi_university_faculties_link_faculties_per_page = 30
ferdowsi_university_faculties_number_of_faculties = 822
ferdowsi_university_faculties_filename = 'ferdowsi_university_faculties'

yazd_university_faculties_link_template = 'https://www.yazd.ac.ir/university/faculties/active/'
yazd_university_faculties_number_of_pages = 21
yazd_university_faculties_filename = 'yazd_university_faculties'
