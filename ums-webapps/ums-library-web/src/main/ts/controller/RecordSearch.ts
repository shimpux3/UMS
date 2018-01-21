module ums {
    export interface IRecordSearchScope extends ng.IScope {
        data: any;
        recordList: Array<IRecord>;
        pagination: any;
        pageChanged: Function;
        navigateRecord: Function;
        recordIdList: Array<String>;
        search: any;
        choice: string;

        doSearch: Function;

        record: IRecord;
        recordDetails: Function;
    }

    export class RecordSearch {
        public static $inject = ['$scope', '$q', 'notify', 'libConstants', 'catalogingService', '$stateParams'];

        constructor(private $scope: IRecordSearchScope,
                    private $q: ng.IQService, private notify: Notify, private libConstants: any,
                    private catalogingService: CatalogingService, private $stateParams: any) {

            $scope.recordList = Array<IRecord>();
            $scope.recordIdList = Array<String>();
            $scope.data = {
                itemPerPage: 10,
                totalRecord: 0
            };


            $scope.pagination = {};
            $scope.pagination.currentPage = 1;
            $scope.pageChanged = this.pageChanged.bind(this);
            $scope.navigateRecord = this.navigateRecord.bind(this);
            $scope.doSearch = this.doSearch.bind(this);
            $scope.search = {
                queryTerm: '',
                searchType: 'basic',
                itemType: Array(),
                title: '',
                author: '',
                subject: '',
                corpAuthor: '',
                publisher: '',
                yearFrom: '',
                yearTo: ''
            };

            $scope.recordDetails = this.recordDetails.bind(this);

            if ($stateParams["1"] == null || $stateParams["1"] == "old") {
                var filter: IFilter = JSON.parse(localStorage.getItem("lms_search_filter"));
                this.$scope.search.queryTerm = filter.basicQueryTerm;
                this.$scope.choice = filter.basicQueryField;

            } else {
                this.$scope.search.searchType = "basic";
                this.$scope.choice = "any";
            }

            this.prepareFilter();

            if ($stateParams["1"] == null || $stateParams["1"] == "old") {
                var page = Number(localStorage.getItem("lms_page"));
                $scope.pagination.currentPage = page;
                this.fetchRecords(page);
            }
            else {
                this.fetchRecords(1);
            }
        }

        private prepareFilter() {
            var filter: IFilter = <IFilter> {};

            filter.searchType = this.$scope.search.searchType;
            if (this.$scope.search.searchType == 'basic') {
                filter.basicQueryField = this.$scope.choice;
                filter.basicQueryTerm = this.$scope.search.queryTerm;
            }
            else if (this.$scope.search.searchType == 'advanced') {
                // filter.advancedQueryMap = <IAdvancedSearchMap>();
                // // var advanceSearchMap: any = [];
                // // advanceSearchMap[0] = {key: 'materialType', value: this.$scope.search.itemType};
                // // advanceSearchMap[1] = {key: 'title', value: this.$scope.search.title};
                // // advanceSearchMap[2] = {key: 'author', value: this.$scope.search.author};
                // // advanceSearchMap[3] = {key: 'subject', value: this.$scope.search.subject};
                // // advanceSearchMap[4] = {key: 'coprAuthor', value: this.$scope.search.coprAuthor};
                // // advanceSearchMap[5] = {key: 'publisher', value: this.$scope.search.publisher};
                // // advanceSearchMap[6] = {key: 'yearFrom', value: this.$scope.search.yearFrom};
                // // advanceSearchMap[7] = {key: 'yearTo', value: this.$scope.search.yearTo};
                // // for(var i = 0; i < advanceSearchMap.length; i++) {
                //
                // filter.advancedQueryMap[0].key = 'material_txt';
                // filter.advancedQueryMap[0].value = this.$scope.search.itemType;

            }
            this.$scope.search.filter = filter;
            localStorage["lms_search_filter"] = JSON.stringify(filter);
        }

        private doSearch(): void {
            this.prepareFilter();
            this.fetchRecords(1);
        }


        // common
        private pageChanged(pageNumber) {
            this.fetchRecords(pageNumber);
        }

        private fetchRecords(pageNumber: number): void {
            this.catalogingService.fetchRecords(pageNumber, this.$scope.data.itemPerPage, "", this.$scope.search.filter).then((response: any) => {
                this.$scope.recordIdList = Array<String>();
                this.$scope.recordList = response.entries;
                this.$scope.data.totalRecord = response.total;
                for (var i = 0; i < this.$scope.recordList.length; i++) {
                    this.$scope.recordIdList.push(this.$scope.recordList[i].mfnNo);
                }

                localStorage["lms_records"] = JSON.stringify(this.$scope.recordIdList);
                localStorage["lms_current_index"] = 0;
                localStorage["lms_total_record"] = response.total;
                localStorage["lms_page"] = pageNumber;

            }, function errorCallback(response) {
                this.notify.error(response);
            });
        }

        private navigateRecord(operation: string, mrnNo: string, pageNumber: number, currentIndex: number): void {
            localStorage["lms_page"] = pageNumber;
            localStorage["lms_current_index"] = currentIndex;
            window.location.href = "#/cataloging/" + operation + "/record/" + mrnNo;
        }

        private recordDetails(recordIndex: number) {
            this.prepareRecord(recordIndex);
            this.$scope.record = <IRecord>{};
            this.$scope.record = this.$scope.recordList[recordIndex];
        }

        private prepareRecord(index: number): void {
            var defer = this.$q.defer();
            this.$scope.recordList[index].contributorList = Array<IContributor>();
            this.$scope.recordList[index].subjectList = Array<ISubjectEntry>();
            this.$scope.recordList[index].noteList = Array<INoteEntry>();

            var jsonObj = $.parseJSON(this.$scope.recordList[index].contributorJsonString);

            if (jsonObj != null) {
                for (var i = 0; i < jsonObj.length; i++) {
                    var contributor = <IContributor> {};
                    contributor.viewOrder = jsonObj[i].viewOrder;
                    contributor.role = jsonObj[i].role;
                    contributor.id = jsonObj[i].id;
                    this.$scope.recordList[index].contributorList.push(contributor);
                }
            }

            var jsonObj = $.parseJSON(this.$scope.recordList[index].noteJsonString);

            if (jsonObj != null) {
                for (var i = 0; i < jsonObj.length; i++) {
                    var note = {viewOrder: jsonObj[i].viewOrder, note: jsonObj[i].note};
                    this.$scope.recordList[index].noteList.push(note);
                }
            }

            var jsonObj = $.parseJSON(this.$scope.recordList[index].subjectJsonString);
            if (jsonObj != null) {
                for (var i = 0; i < jsonObj.length; i++) {
                    var subject = {viewOrder: jsonObj[i].viewOrder, subject: jsonObj[i].subject};
                    this.$scope.recordList[index].subjectList.push(subject);
                }
            }

            var jsonObj = $.parseJSON(this.$scope.recordList[index].physicalDescriptionString);
            if (jsonObj != null) {
                var physicalDescription = {
                    pagination: jsonObj.pagination,
                    illustrations: jsonObj.illustrations,
                    accompanyingMaterials: jsonObj.accompanyingMaterials,
                    dimensions: jsonObj.dimensions,
                    paperQuality: jsonObj.paperQuality
                };
                this.$scope.recordList[index].physicalDescription = physicalDescription;
            }
        }

    }


    UMS.controller("RecordSearch", RecordSearch);
}