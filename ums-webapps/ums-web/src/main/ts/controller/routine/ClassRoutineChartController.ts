module ums {
  import ClassRoom = ums.ClassRoom;
  import ClassRoutine = ums.ClassRoutine;

  interface DeptProgram {
    deptId: string;
    programs: Program[];
  }


  interface IConstant {
    id: string;
    name: string;
  }

  export class ClassRoutineChartController {
    private routineData: ClassRoutine[];
    private tableHeader: IRoutineTableHeader[];
    private weekDay: IConstant[];
    private counter: number = 0;
    private showProgressBar: boolean = false;
    private progress: number = 0;
    private courseTeacherList: CourseTeacherInterface[] = [];
    private dayAndTimeMapWithRoutine: any;
    private colSpanWithRoutine: any;

    public static $inject = ['appConstants', 'HttpClient', '$q', 'notify', '$sce', '$window', 'semesterService', 'courseService', 'classRoomService', 'classRoutineService', '$timeout', 'userService', 'routineConfigService', '$state', 'employeeService', 'courseTeacherService'];

    constructor(private appConstants: any,
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

      this.init();
    }

    public init() {
      this.generateHeader();
      this.generateBody();
      if (this.classRoutineService.enableEdit) {
        this.getCourseList();
        this.getClassRoomList();
        this.getTeacherList();
      }
      this.createDayAndTimeMapWithRoutine();
    }

    public createDayAndTimeMapWithRoutine() {
      this.dayAndTimeMapWithRoutine = {};
      this.colSpanWithRoutine = {};
      this.classRoutineService.routineData.forEach((routine: ClassRoutine) => {
        let routineTmp = angular.copy(routine);  //modified routine (only duration is going to be modified)
        routineTmp.duration = (routineTmp.duration / this.routineConfigService.routineConfig.duration);  //this simplified duration will help in determining the col-span.
        this.colSpanWithRoutine[routine.day.toString() + routine.startTime] = routineTmp.duration;
        if (this.dayAndTimeMapWithRoutine[routine.day + routine.startTime] == null) {
          let routineList: ClassRoutine[] = [];
          routineList.push(routineTmp);
          this.dayAndTimeMapWithRoutine[routine.day + routine.startTime] = routineList;
        } else {
          let routineList: ClassRoutine[] = this.dayAndTimeMapWithRoutine[routine.day.toString() + routine.startTime];
          routineList.push(routineTmp);
          this.dayAndTimeMapWithRoutine[routine.day + routine.startTime] = routineList;
        }
      });
    }

    public getDayAndTimeMapWithRoutine(day: string, startTime: string): ClassRoutine[] {
      return this.dayAndTimeMapWithRoutine[day + startTime];
    }

    public getColSpan(day: string, startTime: string): number {
      return this.colSpanWithRoutine[day + startTime];
    }

    public getNextStartTime(day: string, startTime: string, endTime: string): string {
      let nextStartTime: string = "";

      if (this.dayAndTimeMapWithRoutine[day + startTime] != null) {
        let routine: ClassRoutine[] = this.dayAndTimeMapWithRoutine[day + startTime];
        nextStartTime = routine[0].endTime;
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
      this.employeeService.getActiveTeacherByDept().then((teacherList: Employee[]) => {
        this.classRoutineService.teacherList = [];
        this.classRoutineService.teacherList = teacherList;
      })
    }

    public getClassRoomList() {
      //todo fetch dept wise room list
      this.classRoomService.getClassRooms().then((roomList: ClassRoom[]) => {
        this.classRoutineService.roomList = [];
        this.classRoutineService.roomList = roomList;
        console.log("Room list");
        console.log(this.classRoutineService.roomList);
      });
    }

    public edit(day: IConstant, header: IRoutineTableHeader) {
      this.classRoutineService.selectedDay = day;
      this.classRoutineService.selectedHeader = header;

      if (this.dayAndTimeMapWithRoutine[day.id + header.startTime] == null) {
        this.classRoutineService.slotRoutineList = [];
      } else {
        this.classRoutineService.slotRoutineList = this.dayAndTimeMapWithRoutine[day.id + header.startTime];
      }

      $("#routineConfigModal").modal('show');
      this.counter += 2;
      this.$state.go('classRoutine.classRoutineChart.classRoutineSlotEditForm', {}, {reload: 'classRoutine.classRoutineChart.classRoutineSlotEditForm'}
      );
      // $('#routineConfigModal').modal('toggle');
      /*$('#myModal').modal('toggle');
      $('#myModal').modal('show');
      $('#myModal').modal('hide');*/
    }

    public save() {
      this.showProgressBar = true;
      this.progress = 0;
      this.assignSectionsForSessionalCourse().then((routine: ClassRoutine[]) => {
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
              });
            } else {
              this.progress = 100;
              this.courseTeacherList = [];
              $("#routineConfigModal").modal('toggle');
            }

          });
        });
      });
    }

    public extractCourseTeacher(): ng.IPromise<CourseTeacherInterface[]> {
      let defer: ng.IDeferred<CourseTeacherInterface[]> = this.$q.defer();
      this.courseTeacherList = [];
      console.log("Slot routine list");
      console.log(this.classRoutineService.slotRoutineList);
      this.classRoutineService.slotRoutineList.forEach((routine: ClassRoutine) => {
        if (routine.courseTeacher != undefined && routine.courseTeacher.length != 0) {
          console.log("Found one");
          console.log(routine.courseTeacher);
          this.courseTeacherList = this.courseTeacherList.concat(routine.courseTeacher);
        }
      })
      defer.resolve(this.courseTeacherList);
      return defer.promise;
    }

    public saveRoutineData(): ng.IPromise<ClassRoutine[]> {
      let defer: ng.IDeferred<ClassRoutine[]> = this.$q.defer();
      this.classRoutineService.saveOrUpdateClassRoutine(this.classRoutineService.slotRoutineList).then((updatedRoutineList: ClassRoutine[]) => {
        this.classRoutineService.routineData = [];
        this.classRoutineService.routineData = updatedRoutineList;
        console.log("Updated routine list");
        console.log(updatedRoutineList);
        defer.resolve(this.classRoutineService.routineData);
      })
      return defer.promise;
    }

    private assignSectionsForSessionalCourse(): ng.IPromise<ClassRoutine[]> {
      let defer: ng.IDeferred<ClassRoutine[]> = this.$q.defer();
      this.classRoutineService.slotRoutineList.forEach((r: ClassRoutine) => {
        if (r.course.type_value == CourseType.sessional) {
          r.section = r.sessionalSection.id;
        }
      });
      defer.resolve(this.classRoutineService.slotRoutineList);
      return defer.promise;
    }

    public generateBody() {
      console.log("generating body");
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


    public generateHeader() {
      console.log("Generating empty routine");
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

      console.log("table header");
      console.log(this.tableHeader);
    }

  }

  UMS.controller("ClassRoutineChartController", ClassRoutineChartController);
}