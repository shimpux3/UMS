module ums {

    class AdditionalInformation {
        public static $inject = [
            'registrarConstants',
            '$q',
            'notify',
            'employeeInformationService',
            '$stateParams'];


        private additional: IAdditionalInformationModel = null;
        private aoi: IAreaOfInterestInformationModel[] = [];
        readonly userId: string = "";
        private enableEdit: boolean = false;
        private enableAoiEdit: boolean = false;
        private enableEditButton: boolean = false;
        private stateParams: any;

        constructor(private registrarConstants: any,
                    private $q: ng.IQService,
                    private notify: Notify,
                    private employeeInformationService: EmployeeInformationService,
                    private $stateParams: any) {

            this.additional = <IAdditionalInformationModel>{};
            this.aoi = [];
            this.stateParams = $stateParams;
            this.userId = this.stateParams.id;
            this.enableEditButton = this.stateParams.edit;
            this.get('additional');
            this.get('aoi');
        }

        public get(type: string): void {
            if (type === 'additional') {
                this.employeeInformationService.getAdditionalInformation(this.userId).then((additionalInformation: any) => {
                    console.log(additionalInformation);
                    if (additionalInformation) {
                        this.additional = additionalInformation;
                    }
                    else {
                        this.additional = <IAdditionalInformationModel>{};
                    }
                });
            }
            else if (type === 'aoi') {
                this.employeeInformationService.getAoiInformation(this.userId).then((aoiInformation: any) => {
                    if (aoiInformation) {
                        console.log(aoiInformation);
                        this.aoi = aoiInformation;
                    }
                    else {
                        this.aoi = [];
                    }
                });
            }
        }


        private submit(type: string): void {
            if (type === 'additional') {
                this.convertToJson(this.additional).then((json: any) => {
                    this.employeeInformationService.saveAdditionalInformation(json).then((data: any) => {
                        this.enableEdit = false;
                    }).catch((reason: any) => {
                        this.enableEdit = true;
                    });
                });
            }
            else if(type === 'aoi') {
                this.convertToJson(this.aoi).then((json: any) => {
                    this.employeeInformationService.saveAoiInformation(json).then((data: any) => {
                        this.enableAoiEdit = false;
                    }).catch((reason: any) => {
                        this.enableAoiEdit = true;
                    });
                });
            }
        }

        public activeEditButton(canEdit: boolean, type: string): void {
            console.log(canEdit + " " + type);
            if(type === 'additional'){
                this.enableEdit = canEdit;
            }
            else if(type === 'aoi'){
                this.enableAoiEdit = canEdit;
            }
        }

        public delete(index: number): void{
            this.aoi.splice(index, 1);
        }

        public addNew(): void{
            let aoiEntry: IAreaOfInterestInformationModel;
            aoiEntry = {
                employeeId: this.userId,
                areaOfInterest: ""
            };
            this.aoi.push(aoiEntry);
        }

        private convertToJson(object: any): ng.IPromise<any> {
            let defer = this.$q.defer();
            let JsonObject = {};
            JsonObject['entries'] = object;
            defer.resolve(JsonObject);
            return defer.promise;
        }
    }

    UMS.controller("AdditionalInformation", AdditionalInformation);
}