module ums {
  export class ReceiptVoucherController {
    public static $inject = ['$scope', '$modal', 'notify', 'AccountService', 'GroupService', '$timeout', 'ReceiptVoucherService', 'VoucherService', 'CurrencyService', 'CurrencyConversionService', 'AccountBalanceService', 'ChequeRegisterService', '$q', 'ReceiptService'];
    private showAddSection: boolean;
    private voucherNo: string;
    private voucherDate: string;
    private currencies: ICurrency[];
    private selectedCurrency: ICurrency;
    private baseCurrency: ICurrency;
    private currencyConversions: ICurrencyConversion[];
    private currencyConversionMapWithCurrency: any;
    private voucherMapWithId: any;
    private postStatus: boolean;
    private totalCredit: number;
    private totalDebit: number;
    private pageNumber: number;
    private itemsPerPage: number;
    private totalVoucherNumber: number;
    private contraVouchers: IReceiptVoucher[];
    private existingVouchers: IReceiptVoucher[];
    private contraVoucherDetail: IReceiptVoucher;
    private detailVouchers: IReceiptVoucher[];
    private paymentVoucherMain: IReceiptVoucher;
    static PAYMENT_VOUCHER_GROUP_FLAG = GroupFlag.YES;
    static RECEIPT_VOUCHER_ID = '7';
    private paymentAccounts: IAccount[];
    private selectedPaymentAccount: IAccount;
    private selectedPaymentAccountCurrentBalance: number;
    private paymentDetailAccounts: IAccount[];
    private totalAmount: number;
    private voucherOfAddModal: IReceiptVoucher;
    private dateFormat: string;
    private searchVoucherNo: string;
    private receipts: IReceipt[];

    constructor($scope: ng.IScope,
                private $modal: any,
                private notify: Notify,
                private accountService: AccountService,
                private groupService: GroupService,
                private $timeout: ng.ITimeoutService,
                private receiptVoucherService: ReceiptVoucherService,
                private voucherService: VoucherService,
                private currencyService: CurrencyService,
                private currencyConversionService: CurrencyConversionService,
                private accountBalanceService: AccountBalanceService,
                private chequeRegisterService: ChequeRegisterService,
                private $q: ng.IQService,
                private receiptService: ReceiptService) {
      this.initialize();
    }

    public initialize() {
      this.pageNumber = 1;
      this.itemsPerPage = 20;
      this.dateFormat = "dd-mm-yyyy";
      this.showAddSection = false;
      this.postStatus=false;
      this.getCurrencyConversions();
      this.getAccounts();
      this.getCurrencies();
      this.getPaginatedVouchers();
      this.getReceipts();
    }

    public getReceipts() {
      this.receiptService.getAllReceipts().then((receipts: IReceipt[]) => {
        this.receipts = receipts;
      });
    }

    public searchVoucher() {
      console.log("In the search voucher");
      console.log(this.searchVoucherNo)
      this.searchVoucherNo=this.searchVoucherNo==''?undefined:this.searchVoucherNo;
      if (this.searchVoucherNo != null) {
        this.getPaginatedVouchers();
      }
      if(this.searchVoucherNo==undefined)
        this.getPaginatedVouchers();
    }

    public showListView() {
      this.initialize();
    }

    public getPaginatedVouchers() {
      this.receiptVoucherService.getAllVouchersPaginated(this.itemsPerPage, this.pageNumber, this.searchVoucherNo).then((paginatedVouchers: IPaginatedReceiptVoucher) => {
        this.existingVouchers = paginatedVouchers.vouchers;
        this.totalVoucherNumber = paginatedVouchers.totalNumber;
      });
    }

    private getAccounts() {
      this.accountService.getAccountsByGroupFlag(GroupFlag.YES).then((accounts: IAccount[]) => {
        this.paymentAccounts = accounts;
        console.log("Payment accounts");
        console.log(accounts);
      });
      this.accountService.getAccountsByGroupFlag(GroupFlag.NO).then((accounts: IAccount[]) => {
        this.paymentDetailAccounts = accounts;
      });
    }

    public getAccountBalance() {
      this.paymentVoucherMain.balanceType = BalanceType.Dr;
      this.accountBalanceService.getAccountBalance(this.paymentVoucherMain.account.id).then((currentBalance: number) => {
        this.selectedPaymentAccountCurrentBalance = currentBalance;
        console.log(accounting.formatNumber(10000));
        console.log(accounting.formatColumn([10000], "$ "));
      });
    }

    public formatCurrency(currency: number): any {
      return accounting.formatMoney(currency, this.selectedCurrency.notation + " ");
    }

    public formatBaseCurrency(currency: number): any {
      let baseCurrencyConversion = currency * (this.currencyConversionMapWithCurrency[this.selectedCurrency.id].baseConversionFactor);

      return accounting.formatMoney(baseCurrencyConversion, this.baseCurrency.notation + " ");
    }

    private getCurrencyConversions() {
      this.currencyConversionService.getAll().then((currencyConversions: ICurrencyConversion[]) => {
        this.currencyConversions = [];
        this.currencyConversionMapWithCurrency = {};
        currencyConversions.forEach((currencyConversion: ICurrencyConversion) => {
          this.currencyConversions.push(currencyConversion);
          this.currencyConversionMapWithCurrency[currencyConversion.currency.id] = currencyConversion;
        });
      });
    }


    private getCurrencies() {
      this.currencyService.getAllCurrencies().then((currencies: ICurrency[]) => {
        this.currencies = currencies;
        console.log("Currencies");
        console.log(currencies);
        this.selectedCurrency = currencies[0];
        this.baseCurrency = currencies.filter((c: ICurrency) => c.currencyFlag == CurrencyFlag.BASE)[0];
      });
    }

    public addButtonClicked() {
      this.postStatus = false;
      this.showAddSection = true;
      this.totalCredit = 0;
      this.totalDebit = 0;
      this.totalAmount = 0;
      this.receiptVoucherService.getVoucherNumber().then((voucherNo: string) => this.voucherNo = voucherNo);
      let currDate: Date = new Date();
      this.contraVouchers = [];
      this.paymentVoucherMain = <IReceiptVoucher>{};
      this.contraVoucherDetail = <IReceiptVoucher>{};
      this.voucherDate = moment(currDate).format("DD-MM-YYYY");
      this.detailVouchers = [];
    }

    public addData() {
      this.voucherOfAddModal = <IReceiptVoucher>{};
      this.selectedPaymentAccountCurrentBalance = 0;
      this.selectedPaymentAccount = <IAccount>{};
      this.voucherOfAddModal.serialNo = this.detailVouchers.length + 1;
      this.voucherOfAddModal.balanceType = BalanceType.Cr;
    }

    public addDataToVoucherTable() {
      if (this.voucherOfAddModal.id==undefined) {
        this.voucherOfAddModal = this.addNecessaryAttributesToVoucher(this.voucherOfAddModal);
        this.detailVouchers.push(this.voucherOfAddModal);
      }
      else{
        this.voucherMapWithId[this.voucherOfAddModal.id]=this.voucherOfAddModal;
      }
      this.countTotalAmount();
    }

    private addNecessaryAttributesToVoucher(voucher: IReceiptVoucher): IReceiptVoucher {
      voucher.accountId = voucher.account.id;
      voucher.voucherNo = this.voucherNo;
      voucher.voucherId = ReceiptVoucherController.RECEIPT_VOUCHER_ID;
      if (voucher != this.paymentVoucherMain)
        voucher.receiptId = voucher.receipt.id;
      voucher.conversionFactor = this.currencyConversionMapWithCurrency[this.selectedCurrency.id].baseConversionFactor;
      voucher.foreignCurrency = voucher.amount != null ? voucher.amount * voucher.conversionFactor : this.selectedPaymentAccountCurrentBalance * voucher.conversionFactor;
      voucher.voucherDate = this.voucherDate;
      voucher.currencyId = this.selectedCurrency.id;
      return voucher;
    }

    public saveVoucher() {
      if (this.paymentVoucherMain == null)
        this.notify.error("Account Name is not selected");
      else {
        this.paymentVoucherMain = this.addNecessaryAttributesToVoucher(this.paymentVoucherMain);
        console.log("payment voucher after adding necessary fields");
        console.log(this.paymentVoucherMain);
        //this.paymentVoucherMain.amount = this.selectedPaymentAccountCurrentBalance + this.totalAmount;
        this.detailVouchers.push(this.paymentVoucherMain);
        this.receiptVoucherService.saveVoucher(this.detailVouchers).then((vouchers: IReceiptVoucher[]) => {
          this.configureVouchers(vouchers);
        });
      }
    }

    public postVoucher() {
      if (this.paymentVoucherMain == null)
        this.notify.error("Account Name is not selected");
      else {
        this.paymentVoucherMain = this.addNecessaryAttributesToVoucher(this.paymentVoucherMain);
        this.paymentVoucherMain.amount = this.totalAmount;
        console.log("Payment voucher amount");
        console.log(this.paymentVoucherMain.amount);
        this.detailVouchers.push(this.paymentVoucherMain);
        this.receiptVoucherService.postVoucher(this.detailVouchers).then((vouchers: IReceiptVoucher[]) => {
          this.configureVouchers(vouchers);
        });
      }
    }

    private configureVouchers(vouchers: IReceiptVoucher[]) {
      this.totalAmount = 0;
      this.voucherMapWithId = {};
      this.postStatus=vouchers[0].postDate!=null?true:false;
      this.voucherDate=Utils.convertFromJacksonDate(vouchers[0].voucherDate);
      vouchers.forEach((v: IReceiptVoucher) => {
        this.voucherMapWithId[v.id] = v;
        v.voucherDate=Utils.convertFromJacksonDate(v.voucherDate);
      });
      this.voucherDate = vouchers[0].voucherDate;
      this.extractMainAndDetailSectionFromVouchers(vouchers).then((updatedVouchers: IReceiptVoucher[]) => {
        this.assignChequeNumberToVouchers(vouchers);
      });
      this.voucherNo = vouchers[0].voucherNo;
    }


    public fetchDetails(paymentVoucher: IReceiptVoucher) {
      this.showAddSection = true;
      this.receiptVoucherService.getVouchersByVoucherNoAndDate(paymentVoucher.voucherNo, paymentVoucher.postDate == null ? paymentVoucher.modifiedDate : paymentVoucher.postDate).then((vouchers: IReceiptVoucher[]) => {
        console.log("details fetched");
        console.log(vouchers);
        this.configureVouchers(vouchers);
      });
    }

    private extractMainAndDetailSectionFromVouchers(vouchers: IReceiptVoucher[]): ng.IPromise<IReceiptVoucher[]> {
      let defer: ng.IDeferred<IReceiptVoucher[]> = this.$q.defer();
      this.detailVouchers = [];
      this.voucherMapWithId = {};
      vouchers.forEach((v: IReceiptVoucher) => {
        this.voucherMapWithId[v.id] = v;
        if (v.balanceType == BalanceType.Dr) {
          this.paymentVoucherMain = v;
          this.getAccountBalance();
        }
        else {
          this.detailVouchers.push(v);
          this.countTotalAmount();
        }
      });
      defer.resolve(vouchers);

      return defer.promise;
    }

    public edit(voucher: IReceiptVoucher) {
      this.voucherOfAddModal = voucher;
    }

    public changeDateFormat(date: string) {
      return Utils.convertFromJacksonDate(date);
    }

    private assignChequeNumberToVouchers(vouchers: IReceiptVoucher[]) {
      let transactionIdList: string[] = [];
      vouchers.forEach((v: IReceiptVoucher) => transactionIdList.push(v.id));

      this.chequeRegisterService.getChequeRegisterList(transactionIdList).then((chequeList: IChequeRegister[]) => {
        console.log("cheque list");
        console.log(chequeList);
        chequeList.forEach((c: IChequeRegister) => {
          let voucher: IReceiptVoucher = this.voucherMapWithId[c.accountTransactionId];
          console.log("Cheque voucher");
          console.log(voucher);
          voucher.chequeNo = c.chequeNo;
          voucher.chequeDate = Utils.convertFromJacksonDate(c.chequeDate);
        });
      });
    }



    public countTotalAmount() {
      this.detailVouchers.forEach((v: IReceiptVoucher) => {
        this.totalAmount = this.totalAmount + v.amount;
        console.log("total amount");
        console.log(this.totalAmount);
      });
    }
  }

  UMS.controller("ReceiptVoucherController", ReceiptVoucherController);
}