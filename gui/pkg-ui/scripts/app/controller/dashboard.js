app
    .controller('DashboardController', function ($scope, RestService, $cookieStore, $mdDialog, $location) {
        $scope.items = [
            {
                header: 'سامانه‌های جستجو',
                icon: 'pp_icons_semantic_search',
                cssClass: 'icon-search',
                links: [
                    {title: 'جستجو', url: rootURL + '/search/html/index.html'},
                    {title: 'افزودن الگو', url: ''},
                    {title: 'ارزیابی جستجو', url: rootURL + '/evaluation/'},
                    {title: 'مدیریت بازخورد کاربران', url: '#!/search/feedback'}
                ],
            },
            {
                header: 'سامانه‌های نگاشت',
                icon: 'pp_icons_IT_Consulting',
                cssClass: 'icon-mappings',
                links: [
                    {title: 'نگاشت خصیصه', url: '#!/mappings/property'},
                    {title: 'نگاشت الگو', url: '#!/mappings/template'}
                ],
            },
            {
                header: 'سامانه‌های هستان‌شناسی',
                icon: 'pp_icons_ontologie',
                cssClass: 'icon-ontology',
                links: [
                    {title: 'مدیریت هستان‌شناسی', url: '#!/ontology/tree'},
                ],
            },
            {
                header: 'منابع گراف دانش',
                icon: 'pp_icons_Data_Analysis',
                cssClass: 'icon-resources',
                links: [
                    {title: 'نمایش منابع', url: rootURL + '/mapping/html/triples.html'}
                ],
            },
            {
                header: 'گراف دانش',
                icon: 'pp_icons_data_linking',
                cssClass: 'icon-knowledge-graph',
                links: [
                    {title: 'مخزن سه‌تایی ویرتوسو', url: virtuosoURL + '/conductor/'},
                    {title: 'SPARQL', url: virtuosoURL + '/sparql'}
                ],
            },
            {
                header: 'سامانه‌های خبرگان',
                icon: 'pp_icons_Information_Architect',
                cssClass: 'icon-experts',
                links: [
                    {title: 'نظارت سه‌تایی‌ها', url: rootURL + '/expert/'},
                    {title: 'مدیریت کاربران', url: rootURL + '/ui'},
                    {title: 'پنل ادمین', url: '#!/reports/subjects'}
                ],
            },
            {
                header: 'سامانه‌های متن خام',
                icon: 'pp_icons_textanalyse',
                cssClass: 'icon-raw-text',
                links: [
                    {title: 'سامانه متن خام', url: rootURL + '/raw/'}
                ],
            }
        ];
    });
