module ums {
  export interface ICatalogingScope extends ng.IScope {
    data: any;
    fillSampleData: Function;
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
    callbackList : Array<Function>;
    selectedTab: string;
    supplierList: Array<ISupplier>;
    publisherList: Array<IPublisher>;
    contributorList: Array<IContributor>;
    countryList: Array<ICountry>;
    showSupplierSelect2: boolean;
    showPublisherSelect2: boolean;
    showContributorSelect2: boolean;
    reloadSuppliers : Function;
    reloadPublishers: Function;
    collectionList : Array<any>;
    recordList : Array<IRecord>;
  }

  export class Cataloging {
    public static $inject = ['$scope', '$q', 'notify', 'libConstants','supplierService','publisherService','contributorService','catalogingService','countryService'];
    constructor(private $scope: ICatalogingScope,
                private $q: ng.IQService, private notify: Notify, private libConstants: any,
                private supplierService : SupplierService, private publisherService : PublisherService, private contributorService : ContributorService,
                private catalogingService : CatalogingService, private countryService : CountryService) {

      $scope.addNewRow = this.addNewRow.bind(this);
      $scope.deleteRow = this.deleteRow.bind(this);
      $scope.addNewItems = this.addNewItems.bind(this);
      $scope.showHideItemsTable = this.showHideItemsTable.bind(this);
      $scope.setMaterialTypeName = this.setMaterialTypeName.bind(this);
      $scope.saveRecord = this.saveRecord.bind(this);
      $scope.saveItem = this.saveItem.bind(this);
      $scope.saveBulkItems = this.saveBulkItems.bind(this);
      $scope.setBulkItemsValue = this.setBulkItemsValue.bind(this);
      $scope.fillSampleData = this.fillSampleData.bind(this);
      $scope.reloadSuppliers = this.reloadSuppliers.bind(this);
      $scope.reloadPublishers = this.reloadPublishers.bind(this);
      $scope.supplierService = supplierService;
      $scope.publisherService = publisherService;
      $scope.contributorService = contributorService;
      $scope.notifyService = notify;

      this.$scope.showRecordInfo = true;
      this.$scope.showItemInfo = false;


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
        readOnlyMode: false,
        supplierReadOnlyMode:false,
        showItemMainButtonPanel: true,
        multipleItemAdd: false,
        bulkAddCount: 4,
        collapsedItemTable: false,
        headerTitle: "Add New - Book Record"
      };
      // setTimeout(this.$scope.showRecordUI(), 1000);
      // $scope.$watch('selectedMaterialId', function (NewValue, OldValue) {
      //   console.log("bbbb");
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
      $scope.record.imprint = <IImprint>{};
      $scope.record.physicalDescription = <IPhysicalDescription>{};

      $scope.record.language = Utils.NUMBER_SELECT;
      $scope.record.materialType = 1;
      this.setMaterialTypeName(1);
      $scope.record.status = Utils.NUMBER_SELECT;
      $scope.record.bindingType = Utils.NUMBER_SELECT;
      $scope.record.acqType = Utils.NUMBER_SELECT;

      $scope.item = <IItem> {};
      $scope.supplier = <ISupplier> {};
      $scope.bulk = {
        config: {}
      };


      $scope.record.contributorList = Array <IContributor>();
      $scope.record.subjectList = Array<ISubjectEntry>();
      $scope.record.noteList = Array <INoteEntry>();
      $scope.callbackList = Array<Function>();

      this.initNavButtonCallbacks();

      this.addNewRow("");
      this.addNewRow("contributor");
      this.addNewRow("note");
      this.addNewRow("subject");
      this.initializeDatePickers();
      catalogingService.fetchItems("1");
      this.fetchRecords();
      this.setRecordHeaderTitle();

      this.getAllSuppliers();
      this.getAllContributors();
      this.getAllPublishers();
      this.loadCountries();

      $scope.showSupplierSelect2 = false;
      $scope.showPublisherSelect2 = false;
      $scope.showContributorSelect2 = false;
    }

    private fetchRecords() : void {
      this.catalogingService.fetchRecords().then((response : any ) => {
        console.log(response);
        console.log( response.entries);
        this.$scope.recordList = response.entries;

        console.log(this.$scope.recordList);
      }, function errorCallback(response) {
        this.notify.error(response);
      });
    }

    /**
     * Settings of navigation buttons callback. We will send this to NavigationButton directive .
     */
    private initNavButtonCallbacks() {
      let recordCallback= function() {
        $("#headerTitle").html("Add Record");
      };
      let itemCallback =  function() {
        $("#headerTitle").html("Add Items");
      };


      this.$scope.callbackList.push(recordCallback);
      this.$scope.callbackList.push(itemCallback);

      this.$scope.selectedTab = "recordNav";

      //Items for Korean-English Dictionary (Call # 005.133/B174o
    }

    private setRecordHeaderTitle() {
      if($("#mfnNo").val() === undefined)
          $("#headerTitle").html("Add Record");
      else
        $("#headerTitle").html("...... Record .....");
    }


    /**
     * Set Material name when user select material type from the materil type
     * drop down.
     */
    public setMaterialTypeName(id) {
      angular.forEach(this.$scope.data.materialTypeOptions, (attr: any)  => {
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
        if(this.$scope.record.contributorList != undefined)
          size = this.$scope.record.contributorList.length+1;

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
        if(this.$scope.record.noteList != undefined)
          size = this.$scope.record.noteList.length+1;
        let note: INoteEntry;
        note = {viewOrder:size, note: ""};
        this.$scope.record.noteList.push(note);
      }
      else if (tableType == "subject") {
        let subject: ISubjectEntry;
        if(this.$scope.record.subjectList != undefined)
          size = this.$scope.record.subjectList.length+1;
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
        this.$scope.bulkItemList.push(item);
      }
    }


    /**
     * Show-Hide Items Table
     */
    private showHideItemsTable(action: string) {
      if (action == "show") {
        this.$scope.data.collapsedItemTable = false;
        $("#itemsDiv").show(400);
      }
      else {
        this.$scope.data.collapsedItemTable = true;
        $("#itemsDiv").hide(400);
      }
    }

    //ToDo: Need to move it to a Directive
    private initializeDatePickers() {
      setTimeout(function () {
        let itemSelector = $('.datepicker-default');
        itemSelector.datepicker();
        itemSelector.on('change', function () {
          $('.datepicker').hide();
        });
      }, 200);
    }


    /**
     * Save Record
     */
    private saveRecord(): void {
      let action = $("button[type=button][clicked=true]").val();
      this.catalogingService.saveRecord(this.$scope.record).then((response : any ) => {
        this.notify.show(response);
        this.saveRecordPostOperation(action);
      }, function errorCallback(response) {
        this.notify.error(response);
      });
    }

    /**
     * Save Record Post Operation.
     */
    private saveRecordPostOperation(action : string) {
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
      this.catalogingService.saveItem(this.$scope.item).then((response : any ) => {
        this.notify.show(response);
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
      this.catalogingService.saveBulkItems(complete_json).then((response : any ) => {
        this.notify.show(response);
      }, function errorCallback(response) {
        this.notify.error(response);
      });
    }

    /**
     * Set common values for Bulk Items
     */
    private setBulkItemsValue(): void {
      let bulkItemList = this.$scope.bulkItemList;
      this.catalogingService.setBulkItemsValue(bulkItemList, this.$scope.bulk.config);
      this.setSelect2ValuesForBulkItems();
    }


    /**
     * Set Select2 Values for Bulk Items Row, during a new save operation.
     */
    private setSelect2ValuesForBulkItems() : void {
      let data = $("#configSupplierName").select2("data");
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


    private getAllSuppliers(): void {
      this.supplierService.fetchAllSuppliers().then((response : any ) => {
        this.$scope.supplierList = response.entries;
        this.$scope.showSupplierSelect2 = true;
      }, function errorCallback(response) {
        this.notify.error(response);
      });
    }



    private getAllPublishers(): void {
      this.publisherService.fetchAllPublishers().then((response : any ) => {
        this.$scope.publisherList = response.entries;
        this.$scope.showPublisherSelect2 = true;
      }, function errorCallback(response) {
        this.notify.error(response);
      });
    }

    private getAllContributors(): void {
      this.contributorService.fetchAllContributors().then((response : any ) => {
        this.$scope.contributorList = response.entries;
        this.$scope.showContributorSelect2 = true;
      }, function errorCallback(response) {
        this.notify.error(response);
      });
    }

    private reloadSuppliers() : void {
      let data = $("#supplier").select2("data");
      let searchTerm = data.text;
      this.$scope.showSupplierSelect2 = false;
      this.getAllSuppliers();
      setTimeout(() => {this.$scope.showSupplierSelect2 = true;
        setTimeout(() => {
            Utils.setSelect2Value("supplierSelect2Div",searchTerm);
        }, 200);
      }, 300);
    }


    private reloadPublishers() : void {
      this.$scope.showPublisherSelect2 = false;
      this.getAllPublishers();
      setTimeout(() => {this.$scope.showPublisherSelect2 = true}, 300);
    }

    private reloadContributors() : void {
      this.$scope.showContributorSelect2 = false;
      this.getAllContributors();
      setTimeout(() => {this.$scope.showContributorSelect2 = true}, 300);
    }

    private loadCountries() : void {
      this.countryService.getCountryList().then((response : any ) => {
        // this.$scope.countryList = response.entries;
        this.$scope.collectionList = Array<any>();
        this.$scope.collectionList.push(response.entries);
      }, function errorCallback(response) {
        this.notify.error(response);
      });
    }


    /**/
    private fillSampleData() {
      this.$scope.data.readOnlyMode = false;
      this.$scope.record.mfnNo = undefined;
      this.$scope.record.language = 1;
      this.$scope.record.status = 0;
      this.$scope.record.bindingType = 1;
      this.$scope.record.acqType = 1;
      this.$scope.record.language = 1;
      let offSet = (new Date).getMilliseconds();
      this.$scope.record.title = "Material Title " + offSet;
      this.$scope.record.subTitle = "Sub Title " + offSet;

      if ($("#gmd"))
        this.$scope.record.gmd = "General Material Description " + offSet;
      if ($("#seriesTitle"))
        this.$scope.record.seriesTitle = "Series " + offSet;
      if ($("#volumeNo"))
        this.$scope.record.volumeNo = "Volume No " + offSet;
      if ($("#volumeTitle"))
        this.$scope.record.volumeTitle = "Volume Title " + offSet;
      if ($("#serialIssueNo"))
        this.$scope.record.serialIssueNo = "Serial Issue No " + offSet;
      if ($("#serialNumber"))
        this.$scope.record.serialNumber = "Serial No " + offSet;
      if ($("#serialSpecial"))
        this.$scope.record.serialSpecial = "Serial Special " + offSet;
      if ($("#libraryLacks"))
        this.$scope.record.libraryLacks = "Library Lacks " + offSet;
      if ($("#changedTitle"))
        this.$scope.record.changedTitle = "Changed Title " + offSet;
      if ($("#isbn"))
        this.$scope.record.isbn = "ISBN " + offSet;
      if ($("#corpAuthorMain"))
        this.$scope.record.corpAuthorMain = "Corporate Author Main " + offSet;
      if ($("#corpSubBody"))
        this.$scope.record.corpSubBody = "Corporate Sub Body " + offSet;
      if ($("#corpCityCountry"))
        this.$scope.record.corpCityCountry = "City, Country " + offSet;
      if ($("#edition"))
        this.$scope.record.edition = "Edition " + offSet;
      if ($("#corpSubBody"))
        this.$scope.record.translateTitleEdition = "Translate Title Edition " + offSet;
      if ($("#issn"))
        this.$scope.record.issn = "ISSN " + offSet;
      if ($("#callNo"))
        this.$scope.record.callNo = "Call No " + offSet;
      if ($("#classNo"))
        this.$scope.record.classNo = "Class No " + offSet;
      if ($("#callDate"))
        this.$scope.record.callDate = "11-11-2017";
      if ($("#authorMark"))
        this.$scope.record.authorMark = "Author Mark " + offSet;

      this.$scope.record.imprint.placeOfPublication = "Publication Place " + offSet;
      this.$scope.record.imprint.yearDateOfPublication = "Year of Publication " + offSet;
      this.$scope.record.imprint.copyRightDate = "09-09-2016";

      this.$scope.record.physicalDescription.pagination = "Pagination " + offSet;
      this.$scope.record.physicalDescription.illustrations = "Illustrations " + offSet;
      this.$scope.record.physicalDescription.accompanyingMaterials = "Materials " + offSet;
      this.$scope.record.physicalDescription.dimensions = "Dimensions " + offSet;
      this.$scope.record.physicalDescription.paperQuality = "Paper Quality " + offSet;

      this.$scope.record.noteList[0].note = "Note 0";
      this.$scope.record.subjectList[0].subject = "Subject 0";
      this.$scope.record.keywords = "Keyword1, ";


    }

  }

  UMS.controller("Cataloging", Cataloging);
}