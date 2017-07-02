module ums {
  export interface ReloadRef {
    reloadList: boolean;
  }

  export class ListDues {
    public static $inject = ['$scope', 'StudentDuesService', '$modal'];
    public dues: StudentDue[];
    public filters: Filter[] = [];
    public reloadReference: ReloadRef = {reloadList: false};

    constructor($scope: ng.IScope, private studentDuesService: StudentDuesService, private $modal: any) {
      this.navigate();
      $scope.$watch(() => this.filters, ()=> {
        this.navigate();
      }, true);

      $scope.$watch(() => this.reloadReference, (newVal: ReloadRef) => {
        if (newVal.reloadList) {
          this.navigate();
        }
      }, true);
    }

    public navigate(url?: string): void {
      this.studentDuesService.listDues(this.filters, url).then((dues: StudentDue[]) => {
        this.dues = dues;
        this.reloadReference.reloadList = false;
      });
    }

    public addDues(due?: StudentDue): void {
      this.$modal.open({
        templateUrl: 'views/fee/dues/add.dues.html',
        controller: AddDues,
        resolve: {
          studentDue: () => due,
          reload: () => this.reloadReference,
          loadMyCtrl: ['$ocLazyLoad', ($ocLazyLoad) => {
            return $ocLazyLoad.load({
              files: [
                'vendors/bootstrap-datepicker/css/datepicker.css',
                'vendors/bootstrap-datepicker/js/bootstrap-datepicker.js'
              ]
            });
          }]
        }
      });
    }
  }

  UMS.controller('ListDues', ListDues);
}
