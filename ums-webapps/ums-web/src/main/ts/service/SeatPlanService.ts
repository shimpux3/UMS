module ums{
  import IPromise = ng.IPromise;

  export interface ISeatPlan {
    id: number;
    idStr: string;
    roomId: number;
    rowNo: number;
    colNo: number;
    studentId: string;
    examType: number;
    semesterId: number;
    groupNo: number;
  }

  export class SeatPlanService{


    public static $inject = ['appConstants','HttpClient','$q','notify','$sce','$window'];
    constructor(private appConstants: any, private httpClient: HttpClient,
                private $q:ng.IQService, private notify: Notify,
                private $sce:ng.ISCEService,private $window:ng.IWindowService) {

    }

    public getSeatPlanAttendanceSheetReport(programType:number,
                                            semesterId:number,
                                            examType:number,
                                            examDate:string
                                            ): IPromise<any>{
      var contentType: string = UmsUtil.getFileContentType("pdf");

      var defer= this.$q.defer();

      this.httpClient.get("/ums-webservice-academic/academic/seatplan/programType/"+programType+"/semesterId/"+semesterId+"/examType/"+examType+"/examDate/"+examDate,  'application/pdf',
          (data:any, etag:string) => {
            var file = new Blob([data], {type: 'application/pdf'});
            var fileURL = this.$sce.trustAsResourceUrl(URL.createObjectURL(file));
            this.$window.open(fileURL);
            defer.resolve("success");
          },
          (response:ng.IHttpPromiseCallbackArg<any>) => {
            console.error(response);
            defer.resolve("failure");
          },'arraybuffer');

      return defer.promise;
    }

    public getSeatPlanInfo(semesterId?: number,
                           examType?: number,
                           examDate?: string,
                           studentId?: string): ng.IPromise<ISeatPlan> {
      let defer: ng.IDeferred<ISeatPlan> = this.$q.defer();
      examDate='11-12-2017';
      this.httpClient.get("academic/seatPlan/by-student/semester-id/" + semesterId + "/exam-type/" + examType + "/exam-date/" + examDate + "/student-id/" + studentId, HttpClient.MIME_TYPE_JSON, (response => {
        console.log(response.entries);
        defer.resolve(response.entries)
      }));

      return defer.promise;
    }


    public updateSeatPlan(seatPlan: ISeatPlan): ng.IPromise<boolean> {
      let defer: ng.IDeferred<boolean> = this.$q.defer();
      this.httpClient.put("academic/seatplan/updateSeat", seatPlan, HttpClient.MIME_TYPE_JSON)
          .success((response: any) => {
            this.notify.success("Successfully Saved");
          }).error((error) => {
        console.error(error);
      });
      return defer.promise;
    }

    public getSeatPlanTopSheetReport(programType:number,
                                     semesterId:number,
                                     examType:number,
                                     examDate:string
                                                       ): IPromise<any>{

      var defer= this.$q.defer();

      this.httpClient.get("/ums-webservice-academic/academic/seatplan/topsheet/programType/"+programType+"/semesterId/"+semesterId+"/examType/"+examType+"/examDate/"+examDate,  'application/pdf',
          (data:any, etag:string) => {
            var file = new Blob([data], {type: 'application/pdf'});
            var fileURL = this.$sce.trustAsResourceUrl(URL.createObjectURL(file));
            this.$window.open(fileURL);
            defer.resolve("success");
          },
          (response:ng.IHttpPromiseCallbackArg<any>) => {
            console.error(response);
            defer.resolve("failure");
          },'arraybuffer');

      return defer.promise;
    }

    public  getSeatPlanByStudent(semesterId?: number,
                                 examType?: number,
                                 examDate?: string,
                                 studentId?: string): ng.IPromise<ISeatPlan> {

      let defer: ng.IDeferred<ISeatPlan> = this.$q.defer();


      let url:any="/ums-webservice-academic/academic/seatplan/splan/semesterId/"+semesterId+"/examType/"+examType+"/examDate/"+examDate+"/studentId/"+studentId;

      this.httpClient.get(url, HttpClient.MIME_TYPE_JSON,
          (response:any)=>defer.resolve(response.entries[0]));
      return defer.promise;
    }


  }

  UMS.service("seatPlanService",SeatPlanService);
}