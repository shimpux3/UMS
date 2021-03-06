module ums {
    interface IEmployeeInformation extends ng.IScope {
        getEmployees: Function;
        filterd: Array<Employee>;
    }

    class EmployeeSearch {
        public static $inject = ['appConstants', 'registrarConstants', '$scope', '$q', 'notify', 'departmentService', 'designationService', 'employeeService',
            'employeeSearchService', '$state', 'userService'];
        private searchBy: string = "";
        private changedUserName: string = "";
        private showSearchByUserId: boolean = false;
        private showSearchByUserName: boolean = false;
        private showSearchByDepartment: boolean = false;
        private showListOfEmployeesPanel: boolean = true;
        private showEmployeeProfilePanel: boolean = false;
        private changedDepartment: IDepartment;
        private allUser: Array<Employee>;
        private modifiedUserList: Employee[];
        private departments: IDepartment[] = [];
        private designations: IDesignation[] = [];
        private filteredDesignation: IDesignation[] = [];
        private employee: Employee;
        private showInformationPanel: boolean = false;
        private indexValue: number = 0;
        private changedUserId: string = "";
        private changedStatus: ICommon = <ICommon>{};
        private changedEmpType: ICommon = <ICommon>{};
        private currentPageNumber: number = 1;
        private itemsPerPage: number = 10;
        private totalItemsNumber: number = 0;
        private enablePreviousButton: boolean = false;
        private enableNextButton: boolean = false;
        private state: any;
        private deptList: IDepartment[];
        private empTypeList: any;
        private printType: any;
        private finalListOfDept: string[] = [];
        private finalListOfEmpType: number[] = [];
        private choice: number = 1;
        private employeeBasicInfoEdit: Employee;
        private setIndex: number;
        private currentUser: LoggedInUser;
        private statuses: ICommon[] = [];
        private empTypes: ICommon[] = [];
        private showLoader: boolean = true;

        constructor(private appConstants: any,
                    private registrarConstants: any,
                    private $scope: IEmployeeInformation,
                    private $q: ng.IQService,
                    private notify: Notify,
                    private departmentService: DepartmentService,
                    private designationService: DesignationService,
                    private employeeService: EmployeeService,
                    private employeeSearchService: EmployeeSearchService,
                    private $state: any,
                    private userService: UserService) {

            $scope.filterd = Array<Employee>();
            this.state = $state;
            $scope.getEmployees = this.getEmployees.bind(this);

            this.statuses = this.registrarConstants.empStatus;
            this.empTypes = this.appConstants.employeeTypes;
            this.initialization();
        }

        private initialization() {
            this.showLoader = true;
            this.employeeService.getAll().then((data) => {
                this.allUser = data;
                this.totalItemsNumber = data.length;
                this.showLoader = false;
                this.departmentService.getAll().then((departments: any) => {
                    this.departments = departments;

                    this.designationService.getAll().then((designations: any) => {
                        this.designations = designations;
                        this.filteredDesignation = designations;
                    });

                    this.userService.fetchCurrentUserInfo().then((result) => {
                        this.currentUser = result;
                        if (this.currentUser.roleId === 1041 || this.currentUser.roleId == 7701 || this.currentUser.roleId == 7108 || this.currentUser.roleId == 7303
                            || this.currentUser.roleId == 7005 || this.currentUser.roleId == 7403 || this.currentUser.roleId == 7503) {
                            this.modifiedUserList = this.allUser.filter((value, index) => {
                                return this.currentUser.departmentId == this.allUser[index].deptOfficeId && this.allUser[index].employeeType == 3;
                            });
                        }
                    });
                });
            });
        }

        private view(user: any, index?: number): void {
            this.employee = <Employee>{};
            this.employee = user;
            this.indexValue = index;
            this.checkPreviousAndNextButtons();
            this.showListOfEmployeesPanel = false;
            this.showEmployeeProfilePanel = true;
            this.showInformationPanel = true;
            Utils.expandRightDiv();
            this.state.go("employeeSearch.employeeProfile", {id: this.employee.id});
        }

        private checkPreviousAndNextButtons(): void {
            if (this.indexValue <= 0) {
                this.enablePreviousButton = false;
            }
            else {
                this.enablePreviousButton = true;
            }
            if (this.indexValue >= this.$scope.filterd.length - 1) {
                this.enableNextButton = false;
            }
            else {
                this.enableNextButton = true;
            }
        }

        private showSearchByField(): void {
            this.employee = <Employee>{};
            this.showListOfEmployeesPanel = true;
            this.showEmployeeProfilePanel = false;
        }

        private getEmployees(): void {
            console.log("I am here");
            this.employee = <Employee>{};
            this.showEmployeeProfilePanel = false;
            this.showListOfEmployeesPanel = true;
            Utils.expandRightDiv();
        }

        private findUser(): boolean {
            for (let i = 0; i < this.allUser.length; i++) {
                if (this.allUser[i].id === this.changedUserId) {
                    this.employee = <Employee> {};
                    this.employee = this.allUser[i];
                    return true;
                }
            }
            return false;
        }

        private previous(): void {
            this.indexValue = this.indexValue - 1;
            this.view(this.$scope.filterd[this.indexValue], this.indexValue);
        }

        private next(): void {
            this.indexValue = this.indexValue + 1;
            this.view(this.$scope.filterd[this.indexValue], this.indexValue);
        }

        private downloadPdf(userId: string) {
            this.employeeSearchService.getEmployeeCV(userId);
        }

        private downloadEmployeeList() {
            if (!this.printType) {
                this.notify.error("Please select a dept/Office category");
            }
            else if (this.printType == 1 && !this.empTypeList) {
                this.notify.error("Please select employee types");
            }
            else if (this.printType == 2 && (!this.empTypeList || !this.deptList)) {
                this.notify.error("Please select deptList and employee types");
            }
            else {
                if (this.printType == 1) {
                    this.deptList = [];
                    for (let i = 0; i < this.departments.length; i++) {
                        this.deptList.push(this.departments[i]);
                    }
                }
                this.prepareList();
                this.employeeSearchService.getEmployeeListPdf(this.finalListOfDept, this.finalListOfEmpType, this.choice);

            }
        }

        private prepareList(): ng.IPromise<any> {
            let defer = this.$q.defer();
            this.finalListOfDept = [];
            this.finalListOfEmpType = [];
            for (let i = 0; i < this.deptList.length; i++) {
                this.finalListOfDept[i] = (this.deptList[i].id);
            }

            for (let j = 0; j < this.empTypeList.length; j++) {
                this.finalListOfEmpType[j] = (+this.empTypeList[j]);
            }
            return defer.promise;
        }

        set setIndexValue(pIndex: number) {
            this.setIndex = pIndex;
        }

        get getIndexValue(): number {
            return this.setIndex;
        }

        public startEdit(index: number): void {
            this.setIndexValue = index;
            this.employeeBasicInfoEdit = <Employee>{};
            this.employeeBasicInfoEdit = this.$scope.filterd[index];
            let dept: IDepartment[] = this.departments.filter((val, index) => {
                return this.employeeBasicInfoEdit.deptOfficeId == this.departments[index].id;
            });
            this.employeeBasicInfoEdit.departmentObj = dept[0];
            this.filterDesignationSelection();

        }

        public editBasicInfo(): void {
            if (this.employeeBasicInfoEdit.id && this.employeeBasicInfoEdit.designation && this.employeeBasicInfoEdit.status) {
                this.employeeService.update(this.employeeBasicInfoEdit.id, this.employeeBasicInfoEdit).then((response) => {
                    this.notify.success("Updated Successfully");
                    this.reload();
                });
            }
            else {
                this.notify.error("Please complete all the fields properly.");
            }
        }

        public filterDesignationSelection(): void {
            this.filteredDesignation = [];
            this.employeeService.getDesignation(this.employeeBasicInfoEdit.deptOfficeId.toString()).then((response: any) => {
                if (response.length < 1) {
                    this.notify.error("No designation found");
                }
                else {
                    for (let i = 0; i < response.length; i++) {
                        for (let j = 0; j < this.designations.length; j++) {
                            if (response[i].designationId == this.designations[j].id) {
                                this.filteredDesignation.push(this.designations[j]);
                            }
                        }
                    }
                }

                let des: IDesignation[] = this.filteredDesignation.filter((val, index) => {
                    return this.employeeBasicInfoEdit.designation == this.filteredDesignation[index].id;
                });

                this.employeeBasicInfoEdit.designationObj = des[0];
            });
        }

        public setDesignationId(): void {
            if (this.employeeBasicInfoEdit.designationObj) {
                this.employeeBasicInfoEdit.designation = this.employeeBasicInfoEdit.designationObj.id;
            }
            else {
                this.employeeBasicInfoEdit.designation = undefined;
            }
        }

        private reload(): void {
            this.employeeService.getAll().then((data) => {
                this.allUser = data;
            });
        }
    }

    UMS.controller("EmployeeSearch", EmployeeSearch);
}