/**
 * Created by My Pc on 01-Dec-16.
 */

module ums{

  interface IAdmissionMerit extends ng.IScope{

    semesters:Array<Semester>;
    semester:Semester;
    programTypes:Array<IProgramType>;
    programType:IProgramType;
    faculty:Faculty;
    faculties:Array<Faculty>;
    meritTypes:Array<IMeritListType>;
    meritType:IMeritListType;
    modalData:string;

    admissionStudents:Array<AdmissionStudent>;


    showUploadPortion:boolean;
    searchSpinner:boolean;

    getSemesters:Function;
    fetchMeritList:Function;
    downloadTemplate:Function;
  }

  interface  IProgramType{
    id:string;
    name:string;
  }

  interface IMeritListType{
    id:string;
    name:string;
  }

  class AdmissionMeritList{
    public static $inject = ['appConstants','HttpClient','$scope','$q','notify','$sce','$window','semesterService','facultyService', 'admissionStudentService'];
    constructor(private appConstants: any,
                private httpClient: HttpClient,
                private $scope: IAdmissionMerit,
                private $q:ng.IQService,
                private notify: Notify,
                private $sce:ng.ISCEService,
                private $window:ng.IWindowService,
                private semesterService: SemesterService,
                private facultyService: FacultyService,
                private admissionStudentService:AdmissionStudentService) {

      $scope.programTypes=appConstants.programType;
      $scope.programType = $scope.programTypes[0];
      $scope.searchSpinner = false;
      $scope.modalData="";

      $scope.getSemesters= this.getSemesters.bind(this);
      $scope.fetchMeritList = this.fetchMeritList.bind(this);
      $scope.downloadTemplate = this.downloadTemplate.bind(this);

      this.getFaculties();
      this.getSemesters();
      this.getMeritListTypes();

      Utils.setValidationOptions("form-horizontal");

    }

    private getMeritListTypes():void{
      this.$scope.meritTypes = [];
      this.$scope.meritTypes = this.appConstants.meritListTypes;
      this.$scope.meritType = this.$scope.meritTypes[0];
      console.log("merit list types");
      console.log(this.$scope.meritTypes);
      console.log(this.$scope.meritType);
    }

    private fetchMeritList():void{
      Utils.expandRightDiv();

      this.$scope.searchSpinner=true;
      this.admissionStudentService.fetchMeritList(this.$scope.semester.id, +this.$scope.meritType.id).then((students:Array<AdmissionStudent>)=>{
        if(students.length==0){
          this.$scope.showUploadPortion=true;
        }else{
          this.$scope.showUploadPortion=false;
          this.$scope.admissionStudents=[];
          this.$scope.admissionStudents=students;
        }
        this.$scope.searchSpinner=false;
      });
    }


    private downloadTemplate():void{
      this.admissionStudentService.downloadMeritListExcelFile(this.$scope.semester.id);
    }

    private getSemesters():void{
      this.semesterService.fetchSemesters(+this.$scope.programType.id,5).then((semesters:Array<Semester>)=>{
        this.$scope.semesters=semesters;
        for(var i=0;i<semesters.length;i++){
          if(semesters[i].status==2){
            this.$scope.semester = semesters[i];
            break;
          }
        }
      });
    }

    private processData(modalData:any):void{
      this.$scope.admissionStudents=[];

      this.fillUpMeritStudents(modalData).then((students:Array<AdmissionStudent>)=>{
        this.$scope.showUploadPortion=false;
      });
    }


    private fillUpMeritStudents(modalData:any):ng.IPromise<any>{
      var defer = this.$q.defer();

      var rows = modalData.split("\n");
      var counter:number=0;
      for(var r in rows){
        var cells = rows[r].split('\t');
        if(+r==0 && cells.length>3 || cells.length<3){
          this.notify.error("Wrong format, please paste the excel data again")
          break;
        }else{
          if(+r!=0){
            this.insertDataIntoAdmissionStudents(cells);
          }
        }

      }


      defer.resolve(this.$scope.admissionStudents);
      return defer.promise;
    }

    private insertDataIntoAdmissionStudents(cellData:Array<string>){
      var student:AdmissionStudent= <AdmissionStudent>{};
      student.meritSlNo=+cellData[0];
      student.receiptId=cellData[1];
      student.admissionRoll=cellData[2];

      this.$scope.admissionStudents.push(student);
    }





    private getFaculties(){
      this.facultyService.getAllFaculties().then((faculties:Array<Faculty>)=>{
        this.$scope.faculties=[];
        for(var i=0;i<faculties.length;i++){
          if(faculties[i].shortName!='Business Faculty'){
            this.$scope.faculties.push(faculties[i]);
          }
        }
        this.$scope.faculty=faculties[0];

      });
    }

    private convertToJson():ng.IPromise<any>{
      var defer = this.$q.defer();
      var completeJson={};
      var jsonObject = [];
      var students:Array<AdmissionStudent> = this.$scope.admissionStudents;

      for(var i=0;i<students.length;i++){
        var item:any = {};
        item['meritSlNo']=students[i].meritSlNo;
        item['receiptId'] = students[i].receiptId;
        item['admissionRoll'] = students[i].admissionRoll;
        jsonObject.push(item);
      }

      completeJson["entries"] = jsonObject;
      defer.resolve(completeJson);
      return defer.promise;
    }

  }

  UMS.controller("AdmissionMeritList",AdmissionMeritList);
}