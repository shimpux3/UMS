///<reference path="../../../../../ums-web-core/src/main/ts/lib/jquery.tokenfield.d.ts"/>
module ums {
    export interface ICatalogingScope extends ng.IScope {
        data: any;
        showRecordInfo: boolean;
        showItemInfo: boolean;
        addNewRow: Function;
        deleteRow: Function;
        roleDropDown: string;
        contributorNameDropDown: string;
        contributors: Array<IContributor>;
        bulkItemList: Array<IItem>;
        addNewItems: Function;
        showHideItemsTable: Function;
        setMaterialTypeName: Function;
        record: IRecord;
        item: IItem;
        items: Array<IItem>;
        supplier: ISupplier;
        saveRecord: Function;
        saveItem: Function;
        saveBulkItems: Function;
        bulk: any;
        setBulkItemsValue: Function;
        itemList: Array<IItem>;
        supplierService: any;
        publisherService: any;
        contributorService: any;
        notifyService: any;
        callbackList: Array<Function>;
        selectedTab: string;
        supplierList: Array<ISupplier>;
        publisherList: Array<IPublisher>;
        contributorList: Array<IContributor>;
        countryList: Array<ICountry>;
        showSupplierSelect2: boolean;
        showPublisherSelect2: boolean;
        showContributorSelect2: boolean;
        reloadSuppliers: Function;
        reloadPublishers: Function;
        reloadContributors: Function;
        collectionList: Array<any>;
        recordList: Array<IRecord>;
        recordId: string;
        goPrevious: Function;
        goNext: Function;
        mangeRecordNavigator: Function;
        showLeftNav: boolean;
        showRightNav: boolean;
        fetchItem: Function;
        resetItemForm: Function;
        enableEdit: Function;
        makeCallNo: Function;
        deleteRecord: Function;
        validateRecordBeforeDelete: Function;
        deleteItem: Function;
        confirmation: Function;
        itemId: string;
        canRecordDelete: boolean;
        canItemDelete: boolean;
        state: any;
        showContributorLoader: boolean;
        showPublisherLoader: boolean;
        showSupplierLoader: boolean;
    }

    interface ICurrency {
        id: string;
        name: string;
    }

    export class Cataloging {
        public static $inject = ['$scope', '$q', 'notify', 'libConstants', 'supplierService', 'publisherService', 'contributorService', 'catalogingService',
            'countryService', '$state', '$stateParams', 'HttpClient'];

        constructor(private $scope: ICatalogingScope,
                    private $q: ng.IQService, private notify: Notify, private libConstants: any,
                    private supplierService: SupplierService, private publisherService: PublisherService, private contributorService: ContributorService,
                    private catalogingService: CatalogingService, private countryService: CountryService, private $state: any, private $stateParams: any, private httpClient: HttpClient) {

            $scope.state = $state;

            $scope.addNewRow = this.addNewRow.bind(this);
            $scope.deleteRow = this.deleteRow.bind(this);
            $scope.addNewItems = this.addNewItems.bind(this);
            $scope.showHideItemsTable = this.showHideItemsTable.bind(this);
            $scope.setMaterialTypeName = this.setMaterialTypeName.bind(this);
            $scope.saveRecord = this.saveRecord.bind(this);
            $scope.saveItem = this.saveItem.bind(this);
            $scope.saveBulkItems = this.saveBulkItems.bind(this);
            $scope.setBulkItemsValue = this.setBulkItemsValue.bind(this);
            $scope.reloadSuppliers = this.reloadSuppliers.bind(this);
            $scope.reloadPublishers = this.reloadPublishers.bind(this);
            $scope.reloadContributors = this.reloadContributors.bind(this);
            $scope.makeCallNo = this.makeCallNo.bind(this);
            $scope.deleteRecord = this.deleteRecord.bind(this);
            $scope.validateRecordBeforeDelete = this.validateRecordBeforeDelete.bind(this);
            $scope.deleteItem = this.deleteItem.bind(this);
            $scope.confirmation = this.confirmation.bind(this);

            $scope.goNext = this.goNext.bind(this);
            $scope.goPrevious = this.goPrevious.bind(this);
            $scope.mangeRecordNavigator = this.mangeRecordNavigator.bind(this);
            $scope.fetchItem = this.fetchItem.bind(this);
            $scope.resetItemForm = this.resetItemForm.bind(this);
            $scope.enableEdit = this.enableEdit.bind(this);

            $scope.supplierService = supplierService;
            $scope.publisherService = publisherService;
            $scope.contributorService = contributorService;
            $scope.notifyService = notify;
            $scope.showContributorLoader = false;
            $scope.showPublisherLoader = false;
            $scope.showSupplierLoader = false;

            this.$scope.showRecordInfo = true;
            this.$scope.showItemInfo = false;

            $scope.items = Array<IItem>();
            $scope.contributors = Array<IContributor>();
            $scope.bulkItemList = Array<IItem>();
            $scope.recordList = Array<IRecord>();

            $scope.data = {
                languageOptions: libConstants.languages,
                bindingTypeOptions: libConstants.bindingTypes,
                acquisitionTypeOptions: libConstants.acquisitionTypes,
                contributorRoleOptions: libConstants.libContributorRoles,
                recordStatusOptions: libConstants.recordStatus,
                itemOptions: libConstants.itemStatus,
                materialTypeOptions: libConstants.materialTypes,
                journalFrequencyOptions: libConstants.journalFrequency,
                gmdOptions: libConstants.gmdOptions,
                currencies: libConstants.currencyTypes,
                readOnlyMode: false,
                supplierReadOnlyMode: false,
                showItemMainButtonPanel: true,
                multipleItemAdd: false,
                bulkAddCount: 4,
                collapsedItemTable: false,
                headerTitle: "Add New - Book Record",
                itemReadOnlyMode: false
            };
            // setTimeout(this.$scope.showRecordUI(), 1000);
            // $scope.$watch('selectedMaterialId', function (NewValue, OldValue) {
            // }, true);

            jQuery.validator.addMethod("cRequired", function (value, element) {
                if ($(element).attr('id') == "recordStatus" && value == 101101)
                    return false;
                else if ($("#recordStatus").val() == 0)
                    return true;
                else if ($("#recordStatus").val() == 2 && (value == "" || value == 101101))
                    return false;
                else return true;
            }, "This field is required");

            $scope.record = <IRecord>{};
            $scope.record.classNo = "";
            $scope.record.authorMark = "";
            $scope.record.callYear = 0;
            $scope.record.callEdition = "";
            $scope.record.callVolume = "";
            $scope.record.imprint = <IImprint>{};
            $scope.record.physicalDescription = <IPhysicalDescription>{};

            $scope.record.language = Utils.NUMBER_SELECT;
            $scope.record.gmd = Utils.NUMBER_SELECT;
            $scope.record.materialType = 1;
            this.setMaterialTypeName(1);
            $scope.record.status = 0;


            $scope.item = <IItem> {};
            $scope.item.acqType = Utils.NUMBER_SELECT;
            $scope.supplier = <ISupplier> {};
            $scope.item.status = 2;
            $scope.item.currency = 1;
            $scope.item.bindingType = Utils.NUMBER_SELECT;
            $scope.bulk = {
                config: {}
            };

            $scope.bulk.config.currency = 1;
            $scope.bulk.config.status = 2;
            $scope.bulk.config.acqType = Utils.NUMBER_SELECT;
            $scope.bulk.config.bindingType = Utils.NUMBER_SELECT;


            $scope.record.contributorList = Array<IContributor>();
            $scope.record.subjectList = Array<ISubjectEntry>();
            $scope.record.noteList = Array<INoteEntry>();
            $scope.callbackList = Array<Function>();

            $scope.showLeftNav = false;
            $scope.showRightNav = false;
            this.$scope.recordId = $stateParams["2"];
            if (this.$scope.recordId != null && this.$scope.recordId != "-1") {
                this.fetchRecord(this.$scope.recordId);
                this.mangeRecordNavigator();
                $("#o1").removeClass("active");
                $("#o2").removeClass("active").addClass("active");
            }
            if ($stateParams["1"] == "view") {
                this.$scope.data.readOnlyMode = true;
            }

            this.initNavButtonCallbacks();

            // this.addNewRow("");
            this.addNewRow("contributor");
            this.addNewRow("note");
            this.addNewRow("subject");
            // this.initializeDatePickers();
            this.setRecordHeaderTitle();

            /*this.$scope.contributorList = contributor;
            this.$scope.supplierList = supplier;
            this.$scope.publisherList = publisher;
            this.loadCountries();
            $scope.showSupplierSelect2 = true;
            $scope.showPublisherSelect2 = true;
            $scope.showContributorSelect2 = true;*/

            if(localStorage.getItem("contributors")){
                $scope.contributorList = JSON.parse(localStorage.getItem("contributors"));
            }
            else{
                this.getAllContributors();
            }
            if(localStorage.getItem("suppliers")){
                $scope.supplierList = JSON.parse(localStorage.getItem("suppliers"));
            }
            else{
                this.getAllSuppliers();
            }
            if(localStorage.getItem("publishers")){
                $scope.publisherList = JSON.parse(localStorage.getItem("publishers"));
            }
            else{
                this.getAllPublishers();
            }

            this.loadCountries();
            $scope.showSupplierSelect2 = true;
            $scope.showPublisherSelect2 = true;
            $scope.showContributorSelect2 = true;

        }

        private resetItemForm(): void {
            this.$scope.item = <IItem> {};
            this.$scope.item.currency = 1;
            this.$scope.item.acqType = Utils.NUMBER_SELECT;
            this.$scope.item.status = 2;
            this.$scope.item.bindingType = Utils.NUMBER_SELECT;
            this.$scope.data.itemReadOnlyMode = false;
            $('#supplier').select2('enable');
            $('#supplier').select2('data', null)
        }

        private mangeRecordNavigator(): void {

            if (this.$scope.recordId == "") {
                this.$scope.showLeftNav = false;
                this.$scope.showRightNav = false;
            } else {
                var currentIndex = Number(localStorage.getItem("lms_current_index"));
                var page = Number(localStorage.getItem("lms_page"));
                var totalRecord = Number(localStorage.getItem("lms_total_record"));

                if (currentIndex == 0 && page == 1) {
                    this.$scope.showLeftNav = false;
                } else {
                    this.$scope.showLeftNav = true;
                }

                var recordIdList: Array<any> = JSON.parse(localStorage.getItem("lms_records"));

                //var aa = Number(((page-1)*10))+Number(recordIdList.length);
                var aa = Number(((page - 1) * 10)) + Number(currentIndex) + 1;
                if (totalRecord == aa) {
                    this.$scope.showRightNav = false;
                }
                else {
                    this.$scope.showRightNav = true;
                }
            }

        }

        private fetchRecord(recordId: string): void {
            this.catalogingService.fetchRecord(recordId).then((response: any) => {
                this.$scope.record = response;

                this.$scope.record.contributorList = Array<IContributor>();
                this.$scope.record.subjectList = Array<ISubjectEntry>();
                this.$scope.record.noteList = Array<INoteEntry>();

                // this.setMaterialTypeName(this.$scope.record.materialType);

                var jsonObj = $.parseJSON(this.$scope.record.contributorJsonString);

                if (jsonObj != null) {
                    for (var i = 0; i < jsonObj.length; i++) {
                        var contributor = <IContributor> {};
                        contributor.viewOrder = jsonObj[i].viewOrder;
                        contributor.role = jsonObj[i].role;
                        contributor.id = jsonObj[i].id;
                        this.$scope.record.contributorList.push(contributor);
                    }
                }

                var jsonObj = $.parseJSON(this.$scope.record.noteJsonString);

                if (jsonObj != null) {
                    for (var i = 0; i < jsonObj.length; i++) {
                        var note = {viewOrder: jsonObj[i].viewOrder, note: jsonObj[i].note};
                        this.$scope.record.noteList.push(note);
                    }
                }

                var jsonObj = $.parseJSON(this.$scope.record.subjectJsonString);
                if (jsonObj != null) {
                    for (var i = 0; i < jsonObj.length; i++) {
                        var subject = {viewOrder: jsonObj[i].viewOrder, subject: jsonObj[i].subject};
                        this.$scope.record.subjectList.push(subject);
                    }
                }

                var jsonObj = $.parseJSON(this.$scope.record.physicalDescriptionString);
                if (jsonObj != null) {
                    var physicalDescription = {
                        pagination: jsonObj.pagination,
                        illustrations: jsonObj.illustrations,
                        accompanyingMaterials: jsonObj.accompanyingMaterials,
                        dimensions: jsonObj.dimensions
                    };
                    this.$scope.record.physicalDescription = physicalDescription;
                }


                setTimeout(() => {
                    // Utils.setSelect2Value("recordPublisherDiv","publisher", this.$scope.record.imprint.publisherName);
                    $("#publisher").select2('data', {
                        id: this.$scope.record.imprint.publisher,
                        text: this.$scope.record.imprint.publisherName
                    });
                    if (this.$scope.data.readOnlyMode) {
                        $('#publisher').select2('disable');
                    } else {
                        $('#publisher').select2('enable');
                    }

                }, 4000);

                setTimeout(() => {


                    for (var i = 0; i < this.$scope.record.contributorList.length; i++) {
                        $("#contributor" + i).select2('data', {
                            viewOrder: this.$scope.record.contributorList[i].viewOrder,
                            id: this.$scope.record.contributorList[i].id,
                            text: this.$scope.contributorList[this.$scope.contributorList.map(function (e) {
                                return e.id;
                            }).indexOf(this.$scope.record.contributorList[i].id)].name,
                            role: this.$scope.record.contributorList[i].role
                        });
                    }

                }, 4000);

                setTimeout(() => {
                    $('#keywords').tokenfield('setTokens', this.$scope.record.keywords);
                }, 4000);
                $("#headerTitle").html("Record : " + this.$scope.record.title);


            }, function errorCallback(response) {
                this.notify.error(response);
            });
        }


        private fetchItems(mfn: string): void {
            this.catalogingService.fetchItems(mfn).then((data: any) => {
                this.$scope.itemList = data;
            });
        }


        private fetchItem(itemId: string, mode: string): void {
            this.catalogingService.fetchItem(itemId).then((response: any) => {
                this.$scope.item = response;

                if (mode == "view") {
                    this.$scope.data.itemReadOnlyMode = true;
                    $('#supplier').select2('disable');
                } else {
                    this.$scope.data.itemReadOnlyMode = false;
                    $('#supplier').select2('enable');
                }


                setTimeout(() => {
                    $("#supplier").select2('data', {
                        id: this.$scope.item.supplier.id,
                        text: this.$scope.item.supplier.name
                    });
                }, 4000);


                // $("#headerTitle").html("Record : "+this.$scope.record.title);
            }, function errorCallback(response) {
                this.notify.error(response);
            });
        }

        private goPrevious() {

            var recordIdList: Array<any> = JSON.parse(localStorage.getItem("lms_records"));
            var currentIndex = Number(localStorage.getItem("lms_current_index"));
            var page = Number(localStorage.getItem("lms_page"));

            var previousIndex = currentIndex - 1;

            if (previousIndex < 0) {
                var tPage = page - 1;
                this.fetchRecords(tPage).then((response: any) => {
                    var recordIdList = Array<string>();
                    this.$scope.recordList = response.entries;
                    for (var i = 0; i < this.$scope.recordList.length; i++) {
                        recordIdList.push(this.$scope.recordList[i].mfnNo);
                    }

                    localStorage["lms_records"] = JSON.stringify(recordIdList);
                    localStorage["lms_page"] = tPage;
                    localStorage["lms_current_index"] = recordIdList.length - 1;

                    this.fetchRecord(recordIdList[recordIdList.length - 1]);
                    this.fetchItems(recordIdList[recordIdList.length - 1]);


                }, function errorCallback(response) {
                    this.notify.error(response);
                });
            } else {
                var recordId: string = recordIdList[previousIndex];

                localStorage["lms_current_index"] = previousIndex;
                this.fetchRecord(recordId);
                this.fetchItems(recordId);
            }
            this.mangeRecordNavigator();
        }

        private goNext() {

            var recordIdList: Array<any> = JSON.parse(localStorage.getItem("lms_records"));
            var currentIndex = Number(localStorage.getItem("lms_current_index"));
            var page = Number(localStorage.getItem("lms_page"));

            var nextIndex = currentIndex + 1;


            if (nextIndex > recordIdList.length - 1) {

                var tPage = page + 1;
                this.fetchRecords(tPage).then((response: any) => {
                    var recordIdList = Array<string>();
                    this.$scope.recordList = response.entries;
                    for (var i = 0; i < this.$scope.recordList.length; i++) {
                        recordIdList.push(this.$scope.recordList[i].mfnNo);
                    }

                    localStorage["lms_records"] = JSON.stringify(recordIdList);
                    localStorage["lms_current_index"] = 0;
                    localStorage["lms_page"] = tPage;
                    this.fetchRecord(recordIdList[0]);
                    this.fetchItems(recordIdList[0]);

                }, function errorCallback(response) {
                    this.notify.error(response);
                });

            } else {
                var recordId: string = recordIdList[nextIndex];
                localStorage["lms_current_index"] = nextIndex;
                this.fetchRecord(recordId);
                this.fetchItems(recordId);

            }
            this.mangeRecordNavigator();
        }

        private fetchRecords(pageNumber: number): ng.IPromise<any> {
            var filter: IFilter = JSON.parse(localStorage.getItem("lms_search_filter"));
            return this.catalogingService.fetchRecords(pageNumber, 10, "", filter);
        }

        /**
         * Settings of navigation buttons callback. We will send this to NavigationButton directive .
         */
        private initNavButtonCallbacks() {
            let that = this;
            let recordCallback = function () {
                if (that.$scope.record.mfnNo == "" || that.$scope.record.mfnNo == null) {
                    $("#headerTitle").html("Add Record");
                } else {
                    $("#headerTitle").html("Record : " + that.$scope.record.title);
                }
            };
            let itemCallback = function () {
                if (that.$scope.record.mfnNo == "" || that.$scope.record.mfnNo == null) {
                    $("#headerTitle").html("Add Items");
                }
                else {
                    $("#headerTitle").html("Items : " + that.$scope.record.title);
                    that.catalogingService.fetchItems(that.$scope.record.mfnNo).then((itemList: any) => {
                        that.$scope.itemList = itemList;
                    }, function errorCallback(response) {
                        that.notify.error(response);
                    });
                }
            };
            this.$scope.callbackList.push(recordCallback);
            this.$scope.callbackList.push(itemCallback);
            this.$scope.selectedTab = "recordNav";
            //Items for Korean-English Dictionary (Call # 005.133/B174o
        }

        private setRecordHeaderTitle() {
            if ($("#mfnNo").val() === undefined)
                $("#headerTitle").html("Add Record");
            else
                $("#headerTitle").html("...... Record .....");
        }


        /**
         * Set Material name when user select material type from the materil type
         * drop down.
         */
        public setMaterialTypeName(id) {
            angular.forEach(this.$scope.data.materialTypeOptions, (attr: any) => {
                if (attr.id == id) {
                    this.$scope.record.materialTypeName = attr.name;
                }
            });
        }

        /**
         * Add new row for the following dynamic tables
         * Contributor, Note and Subject
         */
        private addNewRow(tableType: string) {
            let size = 1;
            if (tableType == 'contributor') {
                if (this.$scope.record.contributorList != undefined)
                    size = this.$scope.record.contributorList.length + 1;

                let contributor: IContributor = <IContributor>{};
                contributor.viewOrder = size;
                //{viewOrder: size, name: "", role: 1, id: ""};
                this.$scope.record.contributorList.push(contributor);
                let index = size - 1;
                //ToDo: This should be removed and check whether it still works or not
                /*setTimeout(function () {
                  $("#contributorName" + index).rules("add", "required");
                  $('#ami' + index).bind('$destroy', function () {
                    $(this).select2('destroy');
                  });
                }, 1000);*/
            }
            else if (tableType == "note") {
                if (this.$scope.record.noteList != undefined)
                    size = this.$scope.record.noteList.length + 1;
                let note: INoteEntry;
                note = {viewOrder: size, note: ""};
                this.$scope.record.noteList.push(note);
            }
            else if (tableType == "subject") {
                let subject: ISubjectEntry;
                if (this.$scope.record.subjectList != undefined)
                    size = this.$scope.record.subjectList.length + 1;
                subject = {viewOrder: size, subject: ""};
                this.$scope.record.subjectList.push(subject);
            }
        }


        /**
         * Delete dynamic table rows
         * Using Add More- Delete feature
         */
        private deleteRow(event: any, index: number) {
            if (event == "contributor") {
                this.$scope.record.contributorList.splice(index, 1);
            }
            else if (event == "note") {
                this.$scope.record.noteList.splice(index, 1);
            }
            else if (event == "subject") {
                this.$scope.record.subjectList.splice(index, 1);
            }
        }

        /**
         * Add Multiple Items by a Single Click
         */
        private addNewItems() {
            this.$scope.data.multipleItemAdd = true;
            this.showHideItemsTable("hide");
            this.$scope.bulkItemList = Array<IItem>();
            for (let i = 0; i < this.$scope.data.bulkAddCount; i++) {
                let item = <IItem> {};
                item.mfnNo = this.$scope.record.mfnNo;
                this.$scope.bulkItemList.push(item);
            }
        }


        /**
         * Show-Hide Items Table
         */
        private showHideItemsTable(action: string) {
            if (action == "show") {
                this.fetchItems(this.$scope.record.mfnNo);
                this.$scope.data.collapsedItemTable = false;
                $("#itemsDiv").show(400);
            }
            else {
                this.$scope.data.collapsedItemTable = true;
                $("#itemsDiv").hide(400);
            }
        }

        //ToDo: Need to move it to a Directive
        // private initializeDatePickers() {
        //     this.notify.success("Changed too");
        //   setTimeout(function () {
        //     let itemSelector = $('.datepicker-default');
        //     itemSelector.datepicker({changeMonth:true, changeYear: true});
        //     itemSelector.on('change', function () {
        //       $('.datepicker').hide();
        //     });
        //   }, 200);
        // }


        /**
         * Save Record
         */
        private saveRecord(): void {
            let action = $("button[type=button][clicked=true]").val();
            this.catalogingService.saveRecord(this.$scope.record).then((response: any) => {
                this.notify.show(response);
                this.saveRecordPostOperation(action);
            }, function errorCallback(response) {
                this.notify.error(response);
            });
        }

        /**
         * Save Record Post Operation.
         */
        private saveRecordPostOperation(action: string) {
            if (action == "save_edit_items") {
                //this.showItemUI();
            }
            else if (action == "save_view") {
                this.$scope.data.readOnlyMode = true;
            }
        }

        /**
         * Save Item
         */
        private saveItem(): void {
            this.$scope.item.mfnNo = this.$scope.record.mfnNo;
            this.catalogingService.saveItem(this.$scope.item).then((response: any) => {
                this.notify.show(response);
                this.fetchItems(this.$scope.record.mfnNo);
            }, function errorCallback(response) {
                this.notify.error(response);
            });
        }


        /**
         * Saves more than one item at a time
         */
        private saveBulkItems(): void {
            let complete_json = {};
            complete_json["items"] = this.$scope.bulkItemList;
            this.catalogingService.saveBulkItems(complete_json).then((response: any) => {
                this.notify.show(response);
            }, function errorCallback(response) {
                this.notify.error(response);
            });
        }

        /**
         * Set common values for Bulk Items
         */
        private setBulkItemsValue(): void {
            if (this.validateBulkItemList() == "") {
                let bulkItemList = this.$scope.bulkItemList;
                this.catalogingService.setBulkItemsValue(bulkItemList, this.$scope.bulk.config);
                this.setSelect2ValuesForBulkItems();
            }
            else {
                this.notify.error(this.validateBulkItemList());
            }

        }


        private validateBulkItemList(): string {
            let errorMessage = "";
            if (this.$scope.bulk.config.copyStartFrom == undefined || this.$scope.bulk.config.copyStartFrom == null) {
                errorMessage += "Copy number is empty\n";
            }
            if (this.$scope.bulk.config.firstAccession == undefined || this.$scope.bulk.config.firstAccession == null) {
                errorMessage += "First accession is empty\n";
            }
            if (this.$scope.bulk.config.incrementSegment == undefined || this.$scope.bulk.config.incrementSegment == null) {
                errorMessage += "Increment segment is empty\n";
            }
            if (this.$scope.bulk.config.status == undefined || this.$scope.bulk.config.status == null) {
                errorMessage += "Status is empty"
            }
            if (this.$scope.bulk.config.price == undefined || this.$scope.bulk.config.price == null) {
                this.$scope.bulk.config.price = "";
            }

            return errorMessage;
        }


        /**
         * Set Select2 Values for Bulk Items Row, during a new save operation.
         */
        private setSelect2ValuesForBulkItems(): void {
            let data = $("#configSupplierName").select2("data");
            if(data != null) {
                let searchTerm = data.text;
                $('#bulkItemContainer').find('.select2-input').each(function (index) {
                    let inputElement: any = $(this)[0];
                    let inputElementId = inputElement.id;

                    $("#bulkContributorName" + index).select2("search", searchTerm);
                    let e = jQuery.Event("keydown");
                    e.which = 13;
                    $("#" + inputElementId).trigger(e);
                });
            }
        }


        private getAllSuppliers(): void {
            this.supplierService.fetchAllSuppliers().then((response: any) => {
                this.$scope.supplierList = response.entries;
                this.$scope.showSupplierSelect2 = true;
                this.setLocalStorageItem("suppliers", JSON.stringify(response.entries));
            }, function errorCallback(response) {
                this.notify.error(response);
            });
        }


        private getAllPublishers(): void {
            this.publisherService.fetchAllPublishers().then((response: any) => {
                this.$scope.publisherList = response.entries;
                this.$scope.showPublisherSelect2 = true;
                this.setLocalStorageItem("publishers", JSON.stringify(response.entries));
            }, function errorCallback(response) {
                this.notify.error(response);
            });
        }

        private setLocalStorageItem(key: string, value: any): void{
            localStorage.setItem(key, value);
        }

        private getLocalStorageItem(key: string): any{
            return localStorage.getItem(key);
        }

        private removeLocalStorageItem(key: string): void{
            return localStorage.removeItem(key);
        }

        private getAllContributors(): void {
            this.contributorService.fetchAllContributors().then((response: any) => {
                this.$scope.contributorList = response.entries;
                this.$scope.showContributorSelect2 = true;
                this.setLocalStorageItem("contributors", JSON.stringify(response.entries));
            }, function errorCallback(response) {
                this.notify.error(response);
            });
        }

        private reloadSuppliers(): void {
            this.$scope.showSupplierSelect2 = false;
            this.getAllSuppliers();
            this.$scope.showSupplierLoader = true;
            let data = $("#supplier").select2("data");
            if (data == null || data == undefined) {
                setTimeout(() => {
                    this.$scope.showSupplierLoader = false;
                }, 3000);
            }
            else {
                let searchTerm = data.text;
                this.getAllSuppliers();
                setTimeout(() => {
                    setTimeout(() => {
                        Utils.setSelect2Value("supplierSelect2Div", "supplier", searchTerm);
                    }, 2000);
                    this.$scope.showSupplierLoader = false;
                }, 3000);
            }
        }


        private reloadPublishers(): void {
            this.$scope.showPublisherSelect2 = false;
            this.getAllPublishers();
            this.$scope.showPublisherLoader = true;
            let data = $("#publisher").select2("data");
            if (data == null || data == undefined) {
                setTimeout(() => {
                    this.$scope.showPublisherLoader = false;
                }, 3000);
            }
            else {
                let searchTerm = data.text;
                setTimeout(() => {
                    setTimeout(() => {
                        Utils.setSelect2Value("recordPublisherDiv", "publisher", searchTerm);
                    }, 2000);
                    this.$scope.showPublisherLoader = false;
                }, 3000)
            }
        }

        private reloadContributors(): void {
            this.$scope.showContributorSelect2 = false;
            this.getAllContributors();
            this.$scope.showContributorLoader = true;
            var text: string[] = new Array(this.$scope.record.contributorList.length);

            for (var i = 0; i < this.$scope.record.contributorList.length; i++) {
                let data = $("#contributor" + i).select2("data");
                if (data == null || data == undefined) {
                    text[i] = "";
                }
                else {
                    text[i] = data.text;
                }
            }
            setTimeout(() => {
                setTimeout(() => {
                    for (var i = 0; i < this.$scope.record.contributorList.length; i++) {
                        if (text[i] != "") {
                            Utils.setSelect2Value("recordContributorDiv" + i, "contributor" + i, text[i]);
                        }
                    }
                }, 3000);
                this.$scope.showContributorLoader = false;
            }, 3500);
        }

        private loadCountries(): void {
            this.countryService.getAll().then((response: any) => {
                // this.$scope.countryList = response.entries;
                this.$scope.collectionList = Array<any>();
                this.$scope.collectionList.push(response.entries);
            }, function errorCallback(response) {
                this.notify.error(response);
            });
        }

        private enableEdit() {
            this.$scope.data.readOnlyMode = false;
        }

        private makeCallNo(): void {
            this.$scope.record.callNo = "";
            this.$scope.record.callNo += this.$scope.record.classNo != "" ? this.$scope.record.classNo : "";
            this.$scope.record.callNo += this.$scope.record.authorMark != "" ? "/" + this.$scope.record.authorMark : "";
            this.$scope.record.callNo += (this.$scope.record.callYear != 0 && this.$scope.record.callYear != null) ? "/" + this.$scope.record.callYear : "";
            this.$scope.record.callNo += this.$scope.record.callEdition != "" ? "/" + this.$scope.record.callEdition : "";
            this.$scope.record.callNo += this.$scope.record.callVolume != "" ? "/" + this.$scope.record.callVolume : "";
        }

        public deleteItem(itemId: string): void {
            this.$scope.canItemDelete = true;
            this.$scope.itemId = itemId;
            this.catalogingService.fetchItem(itemId).then((data: any) => {
                if (data.circulationStatus != 0) {
                    this.$scope.canItemDelete = false;
                }
            });
        }

        public confirmation(): void {
            this.catalogingService.deleteItem(this.$scope.itemId).then(() => {
                this.fetchItems(this.$scope.record.mfnNo);
            });
        }


        public deleteRecord(): void {
            this.catalogingService.deleteRecord(this.$scope.record.mfnNo).then((response: any) => {
                if (response == "Success") {
                    setTimeout(() => {
                        this.$scope.state.go("cataloging.search", {1: 'new'});
                    }, 1000)
                }
            });
        }

        public validateRecordBeforeDelete(): void {
            this.$scope.canRecordDelete = true;
            this.$scope.items = Array<IItem>();
            this.catalogingService.fetchItems(this.$scope.record.mfnNo).then((results: any) => {
                this.$scope.items = results;
                if (this.$scope.items.length > 0) {
                    for (var i = 0; i < this.$scope.items.length; i++) {
                        if (this.$scope.items[i].circulationStatus != 0) {
                            this.$scope.canRecordDelete = false;
                            break;
                        }
                    }
                }
            });
        }
    }

    UMS.controller("Cataloging", Cataloging);
}