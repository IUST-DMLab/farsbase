import extractors
import time
import Utils
import Config


def main():
    extraction_info_dict = dict()
    extraction_info_dict['module'] = 'web_table_extractor'
    extraction_info_dict['extractionStart'] = int(time.time())
    extractors.tehran_university_faculties_extractor()
    extractors.ferdowsi_university_faculties_extractor()
    extractors.yazd_university_faculties_extractor()
    extraction_info_dict['extractionEnd'] = int(time.time())
    Utils.save_json(Config.resources_dir, 'info.json', extraction_info_dict)


if __name__ == '__main__':
    main()
