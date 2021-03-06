module ums{
  interface DeptProgram {
    deptId: string;
    programs: Program[];
  }


  interface IConstant {
    id: string;
    name: string;
  }

  interface IClassRoutineChartScope extends ng.IScope{
    init:Function;
  }

  export class ClassRoutineChartController {
    private routineData: ClassRoutine[];
    private tableHeader: IRoutineTableHeader[];
    private weekDay: IConstant[];
    private counter: number = 0;
    private showProgressBar: boolean = false;
    private progress: number = 0;
    private courseTeacherList: CourseTeacherInterface[] = [];
    private colSpanWithRoutine: {[key:string]:number};
    private showSlotEditForm: boolean = false;
    private showRoutineChart: boolean = false;

    public static $inject = ['$scope','appConstants', 'HttpClient', '$q', 'notify', '$sce', '$window', 'semesterService', 'courseService', 'classRoomService', 'classRoutineService', '$timeout', 'userService', 'routineConfigService', '$state', 'employeeService', 'courseTeacherService'];

    constructor(private $scope:IClassRoutineChartScope,
                private appConstants: any,
                private httpClient: HttpClient,
                private $q: ng.IQService,
                private notify: Notify,
                private $sce: ng.ISCEService,
                private $window: ng.IWindowService,
                private semesterService: SemesterService,
                private courseService: CourseService,
                private classRoomService: ClassRoomService,
                private classRoutineService: ClassRoutineService,
                private $timeout: ng.ITimeoutService,
                private userService: UserService,
                private routineConfigService: RoutineConfigService,
                private $state: any,
                private employeeService: EmployeeService,
                private courseTeacherService: CourseTeacherService) {

      $scope.init = this.init.bind(this);
    }

    public init() {
      if (this.classRoutineService.enableEdit) {
        this.getCourseList();
        this.getClassRoomList();
        this.getTeacherList();
      }
      this.showProgressBar = false;
      this.showSlotEditForm=false;
      this.showRoutineChart = false;
      this.progress=0;
      this.createRoutineBody();
    }

    public createRoutineBody() {
      this.showSlotEditForm = false;
      this.showProgressBar = false;
      this.showRoutineChart = false;
      this.progress=0;
      this.generateHeader();
      this.generateBody();
      this.classRoutineService.dayAndTimeMapWithRoutine = {};
      this.classRoutineService.dayAndTimeMapWithRoutineSlot = {};
      this.createDayAndTimeMapWithGroup();
      this.getCourseTeacher();
    }

    private createDayAndTimeMapWithGroup(){
      this.classRoutineService.dayAndTimeMapWithGroup = {};
      this.classRoutineService.dayAndTimeMapWithRoutine={};
      this.createGroupMapWithRoutineSlot().then((groupMap)=>{
        this.classRoutineService.groupList.forEach((g:number)=>{
          let routineSlot = this.classRoutineService.groupMapWithRoutineSlot[g];
          this.classRoutineService.dayAndTimeMapWithGroup[routineSlot.day+routineSlot.startTime]=g;
          //this.classRoutineService.dayAndTimeMapWithRoutine[routineSlot.day+routineSlot.startTime] = routineSlot.routineList;
        });
        this.createDayAndTimeMapWithRoutine();
      });


    }

    public createGroupMapWithRoutineSlot(): ng.IPromise<any>{
      let defer: ng.IDeferred<any> = this.$q.defer();
      this.classRoutineService.groupList = [];
      this.classRoutineService.groupMapWithRoutineSlot={};
      this.classRoutineService.routineData.forEach((routine:ClassRoutine)=>{
        if(this.classRoutineService.groupMapWithRoutineSlot[routine.slotGroup]==undefined || this.classRoutineService.groupMapWithRoutineSlot[routine.slotGroup]==null){
          let routineSlot: RoutineSlot=<RoutineSlot>{};
          routineSlot.startTime = routine.startTime;
          routineSlot.endTime = routine.endTime;
          routineSlot.groupNo = routine.slotGroup;
          routineSlot.day = routine.day;
          routineSlot.routineList = [];
          routineSlot.routineList.push(routine);
          this.classRoutineService.groupList.push(routine.slotGroup);
          this.classRoutineService.groupMapWithRoutineSlot[routine.slotGroup] = routineSlot;
        }else{
          let routineSlot : RoutineSlot = angular.copy( this.classRoutineService.groupMapWithRoutineSlot[routine.slotGroup]);
          let routineStartTime = moment(routine.startTime,'hh:mm A');
          let routineEndTime = moment(routine.endTime, 'hh:mm A');
          let slotStartTime = moment(routineSlot.startTime, 'hh:mm A');
          let slotEndTime = moment(routineSlot.endTime, 'hh:mm A');
          routineSlot.startTime = routineStartTime<slotStartTime? routine.startTime: routineSlot.startTime;
          routineSlot.endTime = routineEndTime>slotEndTime? routine.endTime: routineSlot.endTime;
          routineSlot.routineList.push(routine);
          this.classRoutineService.groupMapWithRoutineSlot[routine.slotGroup] = routineSlot;
        }
      })

      defer.resolve(this.classRoutineService.groupMapWithRoutineSlot);
      return defer.promise;
    }



    public createDayAndTimeMapWithRoutineSlot() {
      this.colSpanWithRoutine = {};
      this.classRoutineService.groupList.forEach((group: number) => {
        let routineSlot: RoutineSlot = this.classRoutineService.groupMapWithRoutineSlot[group];
        this.colSpanWithRoutine[routineSlot.day + routineSlot.startTime] = moment(routineSlot.endTime,'hh:mm A').unix()-moment(routineSlot.startTime,'hh:mm A').unix();
        this.classRoutineService.dayAndTimeMapWithRoutineSlot[routineSlot.day + routineSlot.startTime] = routineSlot;
      });
    }

    public updateSlotBody(){
      this.classRoutineService.groupList.forEach((group:number)=>{
        let routineSlot: RoutineSlot = this.classRoutineService.groupMapWithRoutineSlot[group];
        let routineList:ClassRoutine[] = angular.copy(routineSlot.routineList);

      })
    }

    private deleteRoutine(routineList: ClassRoutine[], routine): ClassRoutine[]{
      for(var i=0; i<routineList.length; i++){
        if(routineList[i]==routine){
          routineList.splice(i,1);
          break;
        }
      }
      return routineList;
    }



    public createDayAndTimeMapWithRoutine() {
      this.colSpanWithRoutine = {};
      this.classRoutineService.routineData.forEach((routine: ClassRoutine) => {
        let routineTmp = angular.copy(routine);  //modified routine (only duration is going to be modified)
        routineTmp.duration = routineTmp.duration;//(routineTmp.duration / this.routineConfigService.routineConfig.duration);  //this simplified duration will help in determining the col-span.
        this.colSpanWithRoutine[routine.day.toString() + routine.startTime] = routineTmp.duration;
        if (this.classRoutineService.dayAndTimeMapWithRoutineSlot[routine.day + routine.startTime] == null) {
          let routineList: ClassRoutine[] = [];
          routineList.push(routineTmp);

          this.classRoutineService.dayAndTimeMapWithRoutine[routine.day + routine.startTime] = routineList;
        } else {
          let routineList: ClassRoutine[] = this.classRoutineService.dayAndTimeMapWithRoutine[routine.day.toString() + routine.startTime];
          routineList.push(routineTmp);
          this.classRoutineService.dayAndTimeMapWithRoutine[routine.day + routine.startTime] = routineList;
        }
      });

    }

    public getDayAndTimeMapWithRoutine(day: string, startTime: string): RoutineSlot {
      if(this.classRoutineService.dayAndTimeMapWithGroup[day + startTime]==undefined){
        return undefined;
      }else{
        let groupNumber:number = this.classRoutineService.dayAndTimeMapWithGroup[day + startTime];
        return this.classRoutineService.groupMapWithRoutineSlot[groupNumber];
      }

    }

    public getColSpan(day: string, startTime: string): number {
      if(this.classRoutineService.dayAndTimeMapWithGroup[day+startTime]!=undefined){
        let groupNo = this.classRoutineService.dayAndTimeMapWithGroup[day+startTime];
        let routineSlot : RoutineSlot = this.classRoutineService.groupMapWithRoutineSlot[groupNo];
        var slotStartTime:any = moment(routineSlot.startTime,'hh:mm A');
        var slotEndTime:any = moment(routineSlot.endTime, 'hh:mm A');
        return slotEndTime.diff(slotStartTime,'minutes');
      }else{
        return this.routineConfigService.routineConfig.duration;
      }

    }

    public getNextStartTime(day: string, startTime: string, endTime: string): string {
      let nextStartTime: string = "";

      if ( this.classRoutineService.dayAndTimeMapWithGroup[day + startTime] ) {
        let group:number = this.classRoutineService.dayAndTimeMapWithGroup[day+startTime];
        let routineSlot: RoutineSlot = this.classRoutineService.groupMapWithRoutineSlot[group];
        nextStartTime = routineSlot.endTime;
      }
      else if (startTime == this.routineConfigService.routineConfig.startTime) {
        nextStartTime = endTime;
      }
      else {
        nextStartTime = endTime;
      }
      return nextStartTime;
    }

    public getDayWiseTableHeader(day: string): IRoutineTableHeader[] {
      let modifiedTableHeader: IRoutineTableHeader[] = [];
      let nextStartTime: string = '';
      for (var i = 0; i < this.tableHeader.length; i++) {
        if (i == 0) {
          nextStartTime = this.getNextStartTime(day, this.tableHeader[i].startTime, this.tableHeader[i].endTime);
          modifiedTableHeader.push(this.tableHeader[i]);
        }
        //let nextStartTimeTmp = this.getNextStartTime(day, this.tableHeader[i].startTime, this.tableHeader[i].endTime);
        else if (nextStartTime == this.tableHeader[i].startTime) {
          modifiedTableHeader.push(this.tableHeader[i]);
          nextStartTime = this.getNextStartTime(day, this.tableHeader[i].startTime, this.tableHeader[i].endTime);
        } else {
          //do nothing
        }
      }

      return modifiedTableHeader;
    }

    public getCourseList() {
      this.courseService.getCourse(this.classRoutineService.selectedSemester.id,
          this.classRoutineService.selectedProgram.id,
          +this.classRoutineService.studentsYear,
          +this.classRoutineService.studentsSemester).then((courseList: Course[]) => {
        this.classRoutineService.courseList = [];
        this.classRoutineService.courseList = courseList;
      })
    }

    public getTeacherList() {
      this.employeeService.getActiveTeachers().then((teacherList: Employee[]) => {
        this.classRoutineService.teacherList = [];
        this.classRoutineService.teacherList = teacherList;
      })
    }

    public getCourseTeacher() {

      if(this.classRoutineService.showSectionWiseRoutine){
        this.courseTeacherService.getCourseTeacherByProgramAndSemesterAndYearAndAcademicSemester(
            this.classRoutineService.selectedSemester.id,
            this.classRoutineService.selectedProgram.id,
            this.classRoutineService.selectedTheorySection.id,
            +this.classRoutineService.studentsYear,
            +this.classRoutineService.studentsSemester
        ).then((courseTeacherList: CourseTeacherInterface[]) => {
          this.courseTeacherList = courseTeacherList;
          this.createCourseTeacherMap();
          this.createCourseTeacherWithSectionMap();
          this.showRoutineChart = true;
        })
      }else if(this.classRoutineService.showRoomWiseRoutine || this.classRoutineService.showTeacherWiseRoutine){
        let courseIdList: string[] = [];
        this.classRoutineService.routineData.forEach((r:ClassRoutine)=>courseIdList.push(r.course.id));

        this.courseTeacherService.getCourseTeacherBySemesterAndCourseList(this.classRoutineService.selectedSemester.id, courseIdList).then((courseTeacherList:CourseTeacherInterface[])=>{
          this.courseTeacherList = courseTeacherList;
          this.createCourseTeacherMap();
          this.createCourseTeacherWithSectionMap();
          this.showRoutineChart = true;
        });
      }

    }



    public getClassRoomList() {
      //todo fetch dept wise room list
      this.classRoomService.getClassRooms().then((roomList: ClassRoom[]) => {
        this.classRoutineService.roomList = [];
        this.classRoutineService.roomList = roomList;
      });
    }

    public edit(day: IConstant, header: IRoutineTableHeader) {
      this.classRoutineService.selectedDay = day;
      this.classRoutineService.selectedHeader = header;
      if (this.classRoutineService.dayAndTimeMapWithGroup[day.id + header.startTime]) {
        let routineList: ClassRoutine[] = this.getDayAndTimeMapWithRoutine(day.id, header.startTime).routineList;
        routineList.forEach((r:ClassRoutine)=>console.log(r));
        this.classRoutineService.slotRoutineList = this.getDayAndTimeMapWithRoutine(day.id, header.startTime).routineList;
        this.assignCourseTeachersToSlotRoutineList();
        this.showSlotEditForm=true;
      } else {
        this.classRoutineService.slotRoutineList = [];
        this.showSlotEditForm=true;
      }

      $("#routineConfigModal").modal('show');
      this.counter += 2;
      // this.$state.go('classRoutine.classRoutineChart.classRoutineSlotEditForm', {}, {reload: 'classRoutine.classRoutineChart.classRoutineSlotEditForm'}
      // );
    }

    private assignCourseTeachersToSlotRoutineList() {
      this.classRoutineService.slotRoutineList.forEach((r: ClassRoutine) => {
        if (this.classRoutineService.courseTeacherWithSectionMap[r.courseId + r.section] != undefined) {
          r.courseTeacher = this.classRoutineService.courseTeacherWithSectionMap[r.courseId + r.section];
        }
        else {
          if (this.classRoutineService.courseTeacherMapWithCourseIdAndSection[r.courseId+r.section]) {
            let courseTeacherList = angular.copy(this.classRoutineService.courseTeacherMapWithCourseIdAndSection[r.courseId+r.section]);
            courseTeacherList.forEach((c: CourseTeacherInterface) => c.id = undefined);
            r.courseTeacher = courseTeacherList;
          }
          else
            r.courseTeacher = [];
        }
      });
    }

    /*
      * public save():void
      *
      * Three process -->
      * 1. Assign sections for sessional section. The sessional section is assigned to 'section' field of the class routine
      * 2. Save routine and update routine data.
      * 3. Extract course teacher from class routine data
      * 4. Save and update course teacher information.
      * */

    public saveSlotData() {

      this.showProgressBar = true;
      this.progress = 0;

      this.assignSectionsForSessionalCourseAndConvertTime().then((routine: ClassRoutine[]) => {


        this.progress = 10;
        this.saveRoutineData().then((updatedRoutineList: ClassRoutine[]) => {
          this.classRoutineService.routineData = [];
          this.classRoutineService.routineData = updatedRoutineList;
          this.progress = 50;
          this.extractCourseTeacher().then((courseTeacherlist: CourseTeacherInterface[]) => {
            this.progress = 70;
            if (courseTeacherlist.length > 0) {
              this.courseTeacherService.saveOrUpdateCourseTeacher(courseTeacherlist).then((updatedCourseTeacherList: CourseTeacherInterface[]) => {
                this.progress = 100;
                this.courseTeacherList = [];
                this.courseTeacherList = updatedCourseTeacherList;
                $("#routineConfigModal").modal('toggle');
                this.showSlotEditForm=false;
                this.createRoutineBody();
                this.createCourseTeacherMap();
                this.createCourseTeacherWithSectionMap();
                this.$timeout.apply(() => {
                  this.showProgressBar = false;
                }, 100);
              });
            } else {
              this.progress = 100;
              this.courseTeacherList = [];
              $("#routineConfigModal").modal('toggle');
              this.showSlotEditForm=false;
              this.createRoutineBody();
              this.$timeout.apply(() => {
                this.showProgressBar = false;
              }, 100);
            }

          });
        });
      });
    }

    public save() {
      if (this.classRoutineService.slotRoutineList.length == 0) {
        this.createRoutineBody();
      } else {
        this.checkRequiredFieldsInSlotRoutineList().then((foundIssue: boolean)=>{
          if(foundIssue==false)
            this.saveSlotData();
        });
      }
    }

    private checkRequiredFieldsInSlotRoutineList(): ng.IPromise<boolean>{
      let defer: ng.IDeferred<boolean>  = this.$q.defer();
      let foundIssue: boolean = false;
      for(var i=0; i<this.classRoutineService.slotRoutineList.length; i++){
        let routine : ClassRoutine= this.classRoutineService.slotRoutineList[i];
        if(routine.course==undefined || routine.course==null){
          this.notify.error("Please select course");
          foundIssue= true;
          break;
        }
        else if(routine.room==null || routine.room==undefined){
          this.notify.error("Please select roomNo");
          foundIssue= true;
          break;
        }
        else if(moment(routine.startTimeObj).diff(moment(routine.endTimeObj), 'minutes')>300){
          this.notify.error("Time duration is more than expected.");
          foundIssue= true;
          break;
        }
      }

      defer.resolve(foundIssue);
      return defer.promise;
    }

    public createCourseTeacherMap() {
      this.classRoutineService.courseTeacherMapWithCourseIdAndSection = {};
      this.courseTeacherList.forEach((courseTeacher: CourseTeacherInterface) => {
        let courseTeacherList: CourseTeacherInterface[] = [];
        if (this.classRoutineService.courseTeacherMapWithCourseIdAndSection[courseTeacher.courseId+ courseTeacher.section] == undefined) {
          courseTeacherList.push(courseTeacher);
          this.classRoutineService.courseTeacherMapWithCourseIdAndSection[courseTeacher.courseId+courseTeacher.section] = courseTeacherList;
        } else {
          courseTeacherList = this.classRoutineService.courseTeacherMapWithCourseIdAndSection[courseTeacher.courseId+courseTeacher.section];
          courseTeacherList.push(courseTeacher);
          this.classRoutineService.courseTeacherMapWithCourseIdAndSection[courseTeacher.courseId+courseTeacher.section] = courseTeacherList;
        }
      })
    }

    public createCourseTeacherWithSectionMap() {
      this.classRoutineService.courseTeacherWithSectionMap = {};
      this.courseTeacherList.forEach((courseTeacher: CourseTeacherInterface) => {
        let courseTeacherList: CourseTeacherInterface[] = [];
        if (this.classRoutineService.courseTeacherWithSectionMap[courseTeacher.courseId + courseTeacher.section] == undefined) {
          courseTeacherList.push(courseTeacher);
          this.classRoutineService.courseTeacherWithSectionMap[courseTeacher.courseId + courseTeacher.section] = courseTeacherList;
        } else {
          courseTeacherList = this.classRoutineService.courseTeacherWithSectionMap[courseTeacher.courseId + courseTeacher.section];
          courseTeacherList.push(courseTeacher);
          this.classRoutineService.courseTeacherWithSectionMap[courseTeacher.courseId + courseTeacher.section] = courseTeacherList;
        }
      })
    }

    public extractCourseTeacher(): ng.IPromise<CourseTeacherInterface[]> {
      let defer: ng.IDeferred<CourseTeacherInterface[]> = this.$q.defer();
      this.courseTeacherList = [];
      this.classRoutineService.slotRoutineList.forEach((routine: ClassRoutine) => {
        if (routine.courseTeacher != undefined && routine.courseTeacher.length != 0) {
          routine.courseTeacher.forEach((c: CourseTeacherInterface) => c.section = routine.section);
          this.courseTeacherList = this.courseTeacherList.concat(routine.courseTeacher);
        }
      })
      this.courseTeacherList.forEach((c: CourseTeacherInterface) => c.semesterId = this.classRoutineService.selectedSemester.id.toString());
      defer.resolve(this.courseTeacherList);
      return defer.promise;
    }

    public saveRoutineData(): ng.IPromise<ClassRoutine[]> {
      let defer: ng.IDeferred<ClassRoutine[]> = this.$q.defer();
      this.classRoutineService.saveOrUpdateClassRoutine(this.classRoutineService.slotRoutineList).then((updatedRoutineList: ClassRoutine[]) => {
        this.classRoutineService.routineData = [];
        this.classRoutineService.routineData = updatedRoutineList;
        defer.resolve(this.classRoutineService.routineData);
      })
      return defer.promise;
    }

    private assignSectionsForSessionalCourseAndConvertTime(): ng.IPromise<ClassRoutine[]> {

      let defer: ng.IDeferred<ClassRoutine[]> = this.$q.defer();
      this.classRoutineService.slotRoutineList.forEach((r: ClassRoutine) => {
        if (r.course.type_value == CourseType.sessional) {
          r.section = r.sessionalSection.id;
        }
        if(r.startTimeObj)
          r.startTime = moment(r.startTimeObj).format("hh:mm A");
        if(r.endTimeObj)
          r.endTime = moment(r.endTimeObj).format("hh:mm A");
      });
      defer.resolve(this.classRoutineService.slotRoutineList);
      return defer.promise;
    }

    public generateBody() {
      let weekDays: IConstant[] = [];
      weekDays = this.appConstants.weekday;
      this.classRoutineService.weekDayMapWithId = {};
      weekDays.forEach((w: IConstant) => this.classRoutineService.weekDayMapWithId[w.id] = w.name);
      this.weekDay = [];
      for (var i = 0; i < weekDays.length; i++) {
        if (+weekDays[i].id >= this.routineConfigService.routineConfig.dayFrom && +weekDays[i].id <= this.routineConfigService.routineConfig.dayTo)
          this.weekDay.push(weekDays[i]);
      }
    }

    public getCourseTeacherByCourseAndSection(courseId: string, section: string): string {
      let courseTeacherList: CourseTeacherInterface[] = [];
      if (this.classRoutineService.courseTeacherWithSectionMap[courseId + section] != undefined)
        courseTeacherList = this.classRoutineService.courseTeacherWithSectionMap[courseId + section];
      let teacherListStr = '';
      if (courseTeacherList.length > 0) {
        for (var i = 0; i < courseTeacherList.length; i++) {
          if (i == 0 && courseTeacherList.length == 1)
            teacherListStr = '(' + courseTeacherList[i].shortName;
          else if (i == 0)
            teacherListStr = '(' + teacherListStr + courseTeacherList[i].shortName;
          else if (i == 0 && courseTeacherList.length == 1)
            teacherListStr = teacherListStr + courseTeacherList[i].shortName;
          else
            teacherListStr = teacherListStr + ", " + courseTeacherList[i].shortName;
        }
        teacherListStr = teacherListStr + ")";
      } else {
        teacherListStr = '(TBA)'
      }

      return teacherListStr;
    }


    public generateHeader() {
      let startTime: any = {};
      startTime = moment(this.routineConfigService.routineConfig.startTime, 'hh:mm A');
      let endTime = moment(this.routineConfigService.routineConfig.endTime, 'hh:mm A');
      this.tableHeader = [];
      while (startTime < endTime) {
        let tableHeaderTmp: IRoutineTableHeader = <IRoutineTableHeader>{};
        tableHeaderTmp.startTime = moment(startTime).format('hh:mm A');
        startTime = moment(startTime).add(this.routineConfigService.routineConfig.duration, 'm').toDate();
        tableHeaderTmp.endTime = moment(startTime).format('hh:mm A');
        this.tableHeader.push(tableHeaderTmp);
      }

    }

  }

  class RoutineChartDir implements ng.IDirective{
    constructor(){

    }

    public restrict:string = 'EA';
    public scope={

    };

    public controller = ClassRoutineChartController;
    public controllerAs = "vm";
    public link = (scope:any, element:any, attributes:any)=>{
        scope.init();
    }

    public templateUrl:string="./views/directive/class-routine/routine-chart-dir.html";

  }

  UMS.directive("routineChartDir", [()=>{
    return new RoutineChartDir();
  }]);
}