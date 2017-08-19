module ums{
    interface IAgendaAndResolution extends ng.IScope{
        getAgendaResolution: Function;
    }
    interface IConstants{
        id: number;
        name: string;
    }
    interface IAgendaResolution{
        id: string;
        agendaNo: string;
        agenda: string;
        agendaEditor: string;
        resolution: string;
        resolutionEditor: string;
        scheduleId: string;
        showExpandButton: boolean;
    }
    class AgendaAndResolution{
        public static $inject = ['registrarConstants', '$scope', '$q', 'notify', 'meetingService'];
        private meetingTypes: IConstants[] = [];
        private meetingSchedules: IMeetingScheduleModel[] = [];
        private meetingNumbers: number[] = [];
        private meetingType: IConstants = null;
        private showEditorForAgenda: boolean = false;
        private showEditorForResolution: boolean = false;
        private showAgendaAndResolutionDiv: boolean = false;
        private showResolutionDiv: boolean = true;
        private showMasterExpandButton: boolean = true;
        private showRightDiv: boolean = false;
        private showCancelButton: boolean = false;
        private agendaAndResolutions: IAgendaResolution[] = [];
        private tempAgendaAndResolution: IAgendaResolution [] = [];
        private agendaNo: string = "";
        private agenda: string = "";
        private resolution: string = "";
        private meetingNumber: IAgendaResolution = <IAgendaResolution>{};

        constructor(private registrarConstants: any,
                    private $scope: IAgendaAndResolution,
                    private $q: ng.IQService,
                    private notify: Notify, private meetingService: MeetingService) {

            $scope.getAgendaResolution = this.getAgendaResolution.bind(this);

            this.meetingTypes = registrarConstants.meetingTypes;
            CKEDITOR.replace('CKEditorForAgenda');
            CKEDITOR.replace('CKEditorForResolution');
            CKEDITOR.instances['CKEditorForAgenda'].setData("");
            CKEDITOR.instances['CKEditorForResolution'].setData("");
        }

        private getMeetingNumber(): void{
            if(this.meetingType.id == null){
                this.notify.error("Select A Meeting Type");
            }
            else {
                this.meetingSchedules = [];
                this.meetingService.getMeetingNumber(this.meetingType.id).then((response: any) =>{
                    for(let i = 0; i < response.length; i++) {
                        this.meetingSchedules[i] = response[i];
                    }
                });
            }
        }

        private getAgendaResolution(): void{
            Utils.expandRightDiv();
            this.showRightDiv = true;
        }

        private toggleEditor(type: string, param: string): void{
            if(type === 'agenda') {
                if (param == "editor") {
                    this.showEditorForAgenda = true;
                }
                else {
                    this.showEditorForAgenda = false;
                }
            }
            else if(type === "resolution") {
                if (param == "editor") {
                    this.showEditorForResolution = true;
                }
                else {
                    this.showEditorForResolution = false;
                }
            }
        }

        private toggleAgendaAndResolutionDiv(param: string): void{
            if(param === "show"){
                this.showAgendaAndResolutionDiv = true;
            }
            else{
                this.showAgendaAndResolutionDiv = false;
            }
        }

        private toggleResolutionDiv(param: string): void{
            if(param === "show"){
                this.showResolutionDiv = true;
            }
            else{
                this.showResolutionDiv = false;
            }
        }

        private toggleExpand(param: string, index: number): void{
            if(param == "hide"){
                this.agendaAndResolutions[index].showExpandButton = false;
            }
            else{
                this.agendaAndResolutions[index].showExpandButton = true;
            }
        }

        private toggleExpandAll(param: string): void{
            if(param === "hide"){
                this.showMasterExpandButton = false;
                for(let i = 0; i < this.agendaAndResolutions.length; i++){
                    this.agendaAndResolutions[i].showExpandButton = false;
                }
            }
            else{
                this.showMasterExpandButton = true;for(let i = 0; i < this.agendaAndResolutions.length; i++){
                    this.agendaAndResolutions[i].showExpandButton = true;
                }

            }
        }

        private addAgendaAndResolution(): void{
            this.showCancelButton = false;  // Will be removed when update button arrive
            let editorForAgenda: string = "N";
            let editorForResolution: string = "N";
            if(CKEDITOR.instances['CKEditorForAgenda'].getData() != ""){
                this.agenda = CKEDITOR.instances['CKEditorForAgenda'].getData();
                editorForAgenda = "Y";
            }
            if(CKEDITOR.instances['CKEditorForResolution'].getData() != ""){
                this.resolution = CKEDITOR.instances['CKEditorForResolution'].getData();
                editorForResolution = "Y";
            }
            let agendaResolutionEntry: IAgendaResolution;
            agendaResolutionEntry = {
                id: "",
                agendaNo: this.agendaNo,
                agenda: this.agenda,
                agendaEditor: editorForAgenda,
                resolution: this.resolution,
                resolutionEditor: editorForResolution,
                scheduleId: this.meetingNumber.id,
                showExpandButton: true
            };
            this.agendaAndResolutions.push(agendaResolutionEntry);
            this.convertToJson(agendaResolutionEntry).then((json: any) => {
                this.meetingService.saveAgendaResolution(json).then(() =>{
                   this.notify.success("Successfully Saved");
                });
            });
            this.reset();
        }

        private edit(index: number): void{
            this.tempAgendaAndResolution = [];
            this.toggleAgendaAndResolutionDiv('show');
            this.showCancelButton = true;
            this.agendaNo = this.agendaAndResolutions[index].agendaNo;
            if(this.agendaAndResolutions[index].agendaEditor == "Y"){
                CKEDITOR.instances['CKEditorForAgenda'].setData(this.agendaAndResolutions[index].agenda);
                this.toggleEditor('agenda', 'editor');
            }
            else {
                this.agenda = this.agendaAndResolutions[index].agenda;
                this.toggleEditor('agenda','text');
            }
            if(this.agendaAndResolutions[index].resolutionEditor == "Y"){
                CKEDITOR.instances['CKEditorForResolution'].setData(this.agendaAndResolutions[index].resolution);
                this.toggleEditor('resolution', 'editor');
            }
            else {
                this.resolution = this.agendaAndResolutions[index].resolution;
                this.toggleEditor('resolution', 'text');
            }
            this.tempAgendaAndResolution = this.agendaAndResolutions.splice(index, 1);
        }

        private reset(): void{
            this.agendaNo = "";
            this.agenda = "";
            this.resolution = "";
            CKEDITOR.instances['CKEditorForAgenda'].setData("");
            CKEDITOR.instances['CKEditorForResolution'].setData("");
        }

        private cancel(): void{
            this.showCancelButton = false;
            this.agendaAndResolutions.push(this.tempAgendaAndResolution[0]);
            this.tempAgendaAndResolution = [];
            this.reset();
        }

        private convertToJson(convertingObject: IAgendaResolution): ng.IPromise<any>{
            let defer = this.$q.defer();
            let JsonObject = {};
            JsonObject['entries'] = convertingObject;
            defer.resolve(JsonObject);
            return defer.promise;
        }
    }

    UMS.controller("AgendaAndResolution", AgendaAndResolution);
}