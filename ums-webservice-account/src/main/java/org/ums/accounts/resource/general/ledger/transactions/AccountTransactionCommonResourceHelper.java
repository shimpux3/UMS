package org.ums.accounts.resource.general.ledger.transactions;

import org.apache.shiro.SecurityUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.ums.accounts.resource.cheque.register.ChequeRegisterBuilder;
import org.ums.accounts.resource.definitions.account.balance.AccountBalanceResourceHelper;
import org.ums.accounts.resource.general.ledger.AccountTransactionBuilder;
import org.ums.accounts.resource.general.ledger.transactions.helper.PaginatedVouchers;
import org.ums.accounts.resource.general.ledger.transactions.helper.TransactionResponse;
import org.ums.builder.Builder;
import org.ums.domain.model.immutable.Company;
import org.ums.domain.model.immutable.accounts.*;
import org.ums.domain.model.mutable.accounts.*;
import org.ums.enums.accounts.definitions.account.balance.BalanceType;
import org.ums.enums.accounts.definitions.voucher.number.control.ResetBasis;
import org.ums.enums.accounts.definitions.voucher.number.control.VoucherType;
import org.ums.generator.IdGenerator;
import org.ums.manager.CompanyManager;
import org.ums.manager.accounts.*;
import org.ums.persistent.model.accounts.PersistentAccountTransaction;
import org.ums.persistent.model.accounts.PersistentChequeRegister;
import org.ums.persistent.model.accounts.PersistentCreditorLedger;
import org.ums.persistent.model.accounts.PersistentDebtorLedger;
import org.ums.resource.ResourceHelper;
import org.ums.usermanagement.user.User;
import org.ums.usermanagement.user.UserManager;
import org.ums.util.UmsUtils;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Monjur-E-Morshed on 31-Jan-18.
 */

public class AccountTransactionCommonResourceHelper extends
    ResourceHelper<AccountTransaction, MutableAccountTransaction, Long> {

  @Autowired
  protected AccountTransactionManager mAccountTransactionManager;
  @Autowired
  protected AccountTransactionBuilder mAccountTransactionBuilder;
  @Autowired
  protected VoucherManager mVoucherManager;
  @Autowired
  protected FinancialAccountYearManager mFinancialAccountYearManager;
  @Autowired
  protected VoucherNumberControlManager mVoucherNumberControlManager;
  @Autowired
  protected CompanyManager mCompanyManager;
  @Autowired
  protected AccountManager mAccountManager;
  @Autowired
  protected AccountBalanceManager mAccountBalanceManager;
  @Autowired
  protected UserManager mUserManager;
  @Autowired
  protected IdGenerator mIdGenerator;
  @Autowired
  protected MonthBalanceManager mMonthBalanceManager;
  @Autowired
  protected ChequeRegisterManager mChequeRegisterManager;
  @Autowired
  protected ChequeRegisterBuilder mChequeRegisterBuilder;
  @Autowired
  protected AccountBalanceResourceHelper mAccountBalanceResourceHelper;

  @Autowired
  protected DebtorLedgerManager mDebtorLedgerManager;

  @Autowired
  protected CreditorLedgerManager mCreditorLedgerManager;

  private enum DateCondition {
    Previous,
    Next
  }

  public TransactionResponse getVoucherNo(Long pVoucherId) throws Exception {
    Voucher voucher = mVoucherManager.get(pVoucherId);
    FinancialAccountYear openFinancialYear = mFinancialAccountYearManager.getOpenedFinancialAccountYear();
    Date currentDay = new Date();
    TransactionResponse transactionResponse = new TransactionResponse();
    Company usersCompany = mCompanyManager.get("01");
    if(currentDay.after(getPreviousDate(openFinancialYear.getCurrentStartDate(), DateCondition.Previous))
        && currentDay.before(getPreviousDate(openFinancialYear.getCurrentEndDate(), DateCondition.Next))) {
      VoucherNumberControl voucherNumberControl = mVoucherNumberControlManager.getByVoucher(voucher, usersCompany);
      Calendar calendar = Calendar.getInstance();
      Date currentDate = new Date();
      calendar.setTime(currentDate);
      return createVoucherNumber(voucher, transactionResponse, voucherNumberControl, calendar, currentDate);

    }
    else {
      transactionResponse.setMessage("Current year is not opened");
      transactionResponse.setVoucherNo("");
      return transactionResponse;
    }
  }

  @NotNull
  private TransactionResponse createVoucherNumber(Voucher pVoucher, TransactionResponse pTransactionResponse,
      VoucherNumberControl pVoucherNumberControl, Calendar pCalendar, Date pCurrentDate) throws Exception {
    if(pVoucherNumberControl.getResetBasis().equals(ResetBasis.YEARLY)) {
      Date firstDate = UmsUtils.convertToDate("01-01-" + pCalendar.get(Calendar.YEAR), "dd-MM-yyyy");
      Date lastDate = UmsUtils.convertToDate("31-12-" + pCalendar.get(Calendar.YEAR), "dd-MM-yyyy");
      return getVoucherNumber(pVoucher, pTransactionResponse, firstDate, lastDate);
    }
    else if(pVoucherNumberControl.getResetBasis().equals(ResetBasis.MONTHLY)) {
      Date firstDate =
          UmsUtils.convertToDate("01-" + pCalendar.get(Calendar.MONTH) + "-" + pCalendar.get(Calendar.YEAR),
              "dd-MM-yyyy");
      Date lastDate =
          UmsUtils.convertToDate(pCalendar.get(Calendar.DAY_OF_MONTH) + "-" + pCalendar.get(Calendar.MONTH) + "-"
              + pCalendar.get(Calendar.YEAR), "dd-MM-yyyy");
      return getVoucherNumber(pVoucher, pTransactionResponse, firstDate, lastDate);
    }
    else if(pVoucherNumberControl.getResetBasis().equals(ResetBasis.WEEKLY)) {
      pCalendar.set(Calendar.DAY_OF_WEEK, pCalendar.getFirstDayOfWeek());
      Date firstDate = pCalendar.getTime();
      pCalendar.setTime(pCurrentDate);
      pCalendar.set(Calendar.DAY_OF_WEEK, pCalendar.getFirstDayOfWeek() + 6);
      Date lastDate = pCalendar.getTime();
      return getVoucherNumber(pVoucher, pTransactionResponse, firstDate, lastDate);
    }
    else if(pVoucherNumberControl.getResetBasis().equals(ResetBasis.DAILY)) {
      return getVoucherNumber(pVoucher, pTransactionResponse, pCurrentDate, pCurrentDate);
    }
    else {
      Integer nextVoucherNumber = mAccountTransactionManager.getTotalVoucherNumberBasedOnCurrentDay(pVoucher) + 1;
      return getVoucherNumberGenerationResponse(pVoucher, pTransactionResponse, nextVoucherNumber);
    }
  }

  private TransactionResponse getVoucherNumber(Voucher pVoucher, TransactionResponse pTransactionResponse,
      Date pFirstDate, Date pLastDate) {
    Integer nextVoucher = getContentManager().getVoucherNumber(pVoucher, pFirstDate, pLastDate) + 1;
    return getVoucherNumberGenerationResponse(pVoucher, pTransactionResponse, nextVoucher);
  }

  @NotNull
  private TransactionResponse getVoucherNumberGenerationResponse(Voucher pVoucher,
      TransactionResponse pTransactionResponse, Integer pNextVoucher) {
    String voucherNumber = generateVoucherNumber(pVoucher, pNextVoucher);
    pTransactionResponse.setMessage("");
    pTransactionResponse.setVoucherNo(voucherNumber);
    return pTransactionResponse;
  }

  @NotNull
  private String generateVoucherNumber(Voucher pVoucher, Integer pNextVoucher) {
    String voucherNumber = "" + pNextVoucher;
    for(int i = 0; i < (6 - pNextVoucher.toString().length()); i++) {
      voucherNumber = "0" + voucherNumber;
    }
    voucherNumber = pVoucher.getShortName() + voucherNumber;
    return voucherNumber;
  }

  private Date getPreviousDate(Date pDate, DateCondition pDateCondition) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(pDate);
    if(pDateCondition.equals(DateCondition.Previous))
      calendar.add(Calendar.DATE, -1);
    else
      calendar.add(Calendar.DATE, +1);
    return calendar.getTime();
  }

  @Transactional
  public List<AccountTransaction> save(JsonArray pJsonValues) throws Exception {
    List<MutableAccountTransaction> transactions = createTransactions(pJsonValues);
    createOrUpdateCreditorLedger(transactions.stream().filter(t -> t.getSupplierCode() != null).collect(Collectors.toList()));
    createOrUpdateDebtorLedger(transactions.stream().filter(t -> t.getCustomerCode() != null).collect(Collectors.toList()));
    transactions.forEach(t -> t.setVoucherNo(t.getVoucherNo().substring(2)));
    return new ArrayList<>(transactions);
  }

  @Transactional
  public Response delete(final MutableAccountTransaction pAccountTransaction) {
    mAccountTransactionManager.delete(pAccountTransaction);
    List<Long> accountTransactionIdList = new ArrayList<>();
    accountTransactionIdList.add(pAccountTransaction.getId());
    List<MutableChequeRegister> chequeRegisterlist =
        mChequeRegisterManager.getByTransactionIdList(accountTransactionIdList);
    mChequeRegisterManager.delete(chequeRegisterlist);
    return Response.accepted().build();
  }

  public PaginatedVouchers getAll(int itemPerPage, int pageNumber, String voucherNO, Long voucherId) {
    Voucher voucher = mVoucherManager.get(voucherId);
    List<MutableAccountTransaction> mutableAccountTransactions = new ArrayList<>();
    int totalNumber = 0;
    if (voucherNO.equals("undefined")) {
      mutableAccountTransactions = getContentManager().getAllPaginated(itemPerPage, pageNumber, voucher);
      totalNumber = getContentManager().getTotalNumber(voucher);
    } else {
      Company company = mCompanyManager.getDefaultCompany();
      voucherNO = company.getId() + voucherNO;
      mutableAccountTransactions = getContentManager().getAllPaginated(itemPerPage, pageNumber, voucher, voucherNO);
      totalNumber = getContentManager().getTotalNumber(voucher, voucherNO);
    }
    mutableAccountTransactions.forEach(a -> {
      a.setVoucherNo(a.getVoucherNo().substring(2));
    });
    PaginatedVouchers paginatedVouchers = new PaginatedVouchers();
    List<AccountTransaction> accountTransactions = new ArrayList<>(mutableAccountTransactions);
    paginatedVouchers.setVouchers(accountTransactions);
    paginatedVouchers.setTotalNumber(totalNumber);
    return paginatedVouchers;
  }

  public List<AccountTransaction> getByVoucherNoAndDate(String pVoucherNo, String pDate) throws Exception {
    Date dateObj = UmsUtils.convertToDate(pDate, "yyyy-MM-dd");
    Company company = mCompanyManager.getDefaultCompany();
    List<MutableAccountTransaction> mutableAccountTransactions = getContentManager().getByVoucherNoAndDate(company.getId() + pVoucherNo, dateObj);
    List<MutableDebtorLedger> debtorLedgers = mDebtorLedgerManager.get(new ArrayList<>(mutableAccountTransactions));
    List<MutableCreditorLedger> creditorLedgers = mCreditorLedgerManager.get(new ArrayList<>(mutableAccountTransactions));
    Map<Long, DebtorLedger> debtorLedgerMapWithTransaction = debtorLedgers.stream().collect(Collectors.toMap(d -> d.getAccountTransactionId(), d -> d));
    Map<Long, CreditorLedger> creditorLedgerMapWithTransaction = creditorLedgers.stream().collect(Collectors.toMap(c -> c.getAccountTransactionId(), c -> c));
    mutableAccountTransactions.forEach(a -> {
      a.setVoucherNo(a.getVoucherNo().substring(2));
      if (debtorLedgerMapWithTransaction.containsKey(a.getId()))
        a.setCustomerCode(debtorLedgerMapWithTransaction.get(a.getId()).getCustomerCode());
      if (creditorLedgerMapWithTransaction.containsKey(a.getId()))
        a.setSupplierCode(creditorLedgerMapWithTransaction.get(a.getId()).getSupplierCode());
    });
    return new ArrayList<>(mutableAccountTransactions);
  }

  @NotNull
  private List<MutableAccountTransaction> createTransactions(JsonArray pJsonValues) throws Exception {
    List<MutableAccountTransaction> newTransactions = new ArrayList<>();
    List<MutableAccountTransaction> updateTransactions = new ArrayList<>();
    List<MutableChequeRegister> chequeRegisters = new ArrayList<>();
    User loggedUser = mUserManager.get(SecurityUtils.getSubject().getPrincipal().toString());
    Company company = mCompanyManager.get("01");
    String voucherNo = "";

    for(int i = 0; i < pJsonValues.size(); i++) {
      PersistentAccountTransaction transaction = new PersistentAccountTransaction();
      mAccountTransactionBuilder.build(transaction, pJsonValues.getJsonObject(i));
      PersistentChequeRegister chequeRegister = new PersistentChequeRegister();
      mChequeRegisterBuilder.build(chequeRegister, pJsonValues.getJsonObject(i));
      transaction.setModifiedBy(loggedUser.getEmployeeId());
      transaction.setModifiedDate(new Date());
      transaction.setVoucherDate(new Date());
      transaction.setCompanyId(company.getId());
      if(i == 0)
        voucherNo =
            transaction.getVoucherNo() == null || transaction.getVoucherNo().equals("") ? getVoucherNo(
                transaction.getVoucher().getId()).getVoucherNo() : transaction.getVoucherNo();
      transaction.setVoucherNo(company.getId() + voucherNo);
      if(transaction.getId() == null) {
        transaction.setId(mIdGenerator.getNumericId());
        newTransactions.add(transaction);
      }
      else {
        updateTransactions.add(transaction);
      }
      chequeRegister = assignInfoToChequeRegisterFromTransaction(chequeRegister, transaction);
      if(chequeRegister.getChequeNo() != null) {
        chequeRegister.setAccountTransactionId(transaction.getId());
        chequeRegisters.add(chequeRegister);
      }
    }
    if(newTransactions.size() > 0)
      mAccountTransactionManager.create(newTransactions);
    if(updateTransactions.size() > 0)
      mAccountTransactionManager.update(updateTransactions);
    newTransactions.addAll(updateTransactions);
    updateOrSaveChequeRegister(chequeRegisters, newTransactions);
    return newTransactions;
  }

  private void updateOrSaveChequeRegister(List<MutableChequeRegister> pChequeRegisters, List<MutableAccountTransaction> pAccountTransactions) {
    List<Long> transactionIdList = pAccountTransactions.stream().map(t -> t.getId()).collect(Collectors.toList());
    List<MutableChequeRegister> existingChequeRegisterList = mChequeRegisterManager.getByTransactionIdList(transactionIdList);
    if (existingChequeRegisterList.size() == 0) {
      pChequeRegisters.forEach(c -> c.setId(mIdGenerator.getNumericId()));
      mChequeRegisterManager.create(pChequeRegisters);
    } else {
      Map<Long, MutableChequeRegister> newChequeRegisterMap = pChequeRegisters.stream().collect(Collectors.toMap(ChequeRegister::getAccountTransactionId, p -> p));
      existingChequeRegisterList.forEach(e -> {
        ChequeRegister chequeRegister = newChequeRegisterMap.get(e.getAccountTransactionId());
        e.setChequeNo(chequeRegister.getChequeNo());
        e.setChequeDate(chequeRegister.getChequeDate());
        e.setModificationDate(chequeRegister.getModificationDate());
        e.setModifiedBy(chequeRegister.getModifiedBy());
      });
      mChequeRegisterManager.update(existingChequeRegisterList);
    }
  }

  private PersistentChequeRegister assignInfoToChequeRegisterFromTransaction(
      PersistentChequeRegister pPersistentChequeRegister, PersistentAccountTransaction pPersistentAccountTransaction) {
    if(pPersistentChequeRegister == null)
      return null;
    pPersistentChequeRegister.setAccountTransactionId(pPersistentAccountTransaction.getId());
    pPersistentChequeRegister.setCompanyId(pPersistentAccountTransaction.getCompanyId());
    pPersistentChequeRegister.setModifiedBy(pPersistentAccountTransaction.getModifiedBy());
    pPersistentChequeRegister.setModificationDate(pPersistentAccountTransaction.getModifiedDate());
    return pPersistentChequeRegister;
  }

  @Transactional
  public List<AccountTransaction> postTransactions(JsonArray pJsonValues) throws Exception {
    List<MutableAccountTransaction> newTransactions = new ArrayList<>();
    List<MutableAccountTransaction> updateTransactions = new ArrayList<>();
    List<MutableChequeRegister> chequeRegisters = new ArrayList<>();
    User loggedUser = mUserManager.get(SecurityUtils.getSubject().getPrincipal().toString());
    Company company = mCompanyManager.get("01");

    for(int i = 0; i < pJsonValues.size(); i++) {
      PersistentAccountTransaction transaction = new PersistentAccountTransaction();
      mAccountTransactionBuilder.build(transaction, pJsonValues.getJsonObject(i));
      PersistentChequeRegister chequeRegister = new PersistentChequeRegister();
      mChequeRegisterBuilder.build(chequeRegister, pJsonValues.getJsonObject(i));
      transaction.setModifiedBy(loggedUser.getEmployeeId());
      transaction.setModifiedDate(new Date());
      transaction.setPostDate(new Date());
      transaction.setCompanyId(company.getId());
      transaction.setVoucherDate(new Date());
      transaction.setVoucherNo(company.getId() + transaction.getVoucherNo());
      if(transaction.getId() == null) {
        transaction.setId(mIdGenerator.getNumericId());
        newTransactions.add(transaction);
      } else {
        updateTransactions.add(transaction);
      }
      chequeRegister = assignInfoToChequeRegisterFromTransaction(chequeRegister, transaction);
      if(chequeRegister.getChequeNo() != null) {
        chequeRegister.setAccountTransactionId(transaction.getId());
        chequeRegisters.add(chequeRegister);
      }
    }
    if(newTransactions.size() > 0)
      mAccountTransactionManager.create(newTransactions);
    if(updateTransactions.size() > 0)
      mAccountTransactionManager.update(updateTransactions);
    newTransactions.addAll(updateTransactions);
    updateOrSaveChequeRegister(chequeRegisters, newTransactions);
    updateAccountBalance(newTransactions);
    createOrUpdateCreditorLedger(newTransactions.stream().filter(t -> t.getSupplierCode() != null).collect(Collectors.toList()));
    createOrUpdateDebtorLedger(newTransactions.stream().filter(t -> t.getCustomerCode() != null).collect(Collectors.toList()));
    newTransactions.forEach(a -> a.setVoucherNo(a.getVoucherNo().substring(2)));
    return new ArrayList<>(newTransactions);
  }

  private void updateAccountBalance(List<MutableAccountTransaction> pTransactions) {
    FinancialAccountYear currentFinancialAccountYear = mFinancialAccountYearManager.getOpenedFinancialAccountYear();
    List<Account> accounts = new ArrayList<>();
    Map<Long, MutableAccountTransaction> accountMapWithTransaction = new HashMap<>();
    BigDecimal totalDebit = BigDecimal.valueOf(0.00);
    BigDecimal totalCredit = BigDecimal.valueOf(0.00);
    for(MutableAccountTransaction a : pTransactions) {
      accounts.add(a.getAccount());
      accountMapWithTransaction.put(a.getAccount().getId(), a);
      if(a.getBalanceType().equals(BalanceType.Cr)) {
        totalCredit = totalCredit.add(a.getAmount());
      }
      else {
        totalDebit = totalDebit.add(a.getAmount());
      }
    }
    if(!totalCredit.equals(totalDebit) && pTransactions.get(0).getVoucher().getId().equals(VoucherType.JOURNAL_VOUCHER))
      new Exception(new Throwable("Debit and credit must be same for journal voucher"));
    List<MutableAccountBalance> accountBalanceList =
        generateUpdatedAccountBalance(currentFinancialAccountYear, accounts, accountMapWithTransaction);
    mAccountBalanceManager.update(accountBalanceList);

    // createOrUpdateMonthBalance(accountMapWithTransaction, accountBalanceList);
  }

  private void createOrUpdateDebtorLedger(List<AccountTransaction> pAccountTransactions) {
    List<MutableDebtorLedger> existingDebtorLedgers = mDebtorLedgerManager.get(pAccountTransactions);
    Map<Long, MutableDebtorLedger> existingDebtorLedgerMap = existingDebtorLedgers.stream().collect(Collectors.toMap(p -> p.getAccountTransactionId(), p -> p));
    List<MutableDebtorLedger> updateList = new ArrayList<>();
    List<MutableDebtorLedger> newList = new ArrayList<>();
    pAccountTransactions.forEach(t -> {
      if (existingDebtorLedgerMap.containsKey(t.getId())) {
        MutableDebtorLedger debtorLedger = assignValuesToDebtorLedger(existingDebtorLedgerMap.get(t.getId()), t);
        updateList.add(debtorLedger);
      } else {
        MutableDebtorLedger debtorLedger = new PersistentDebtorLedger();
        debtorLedger = assignValuesToDebtorLedger(debtorLedger, t);
        debtorLedger.setId(mIdGenerator.getNumericId());
        newList.add(debtorLedger);
      }
    });
    if(updateList.size()>0)
      mDebtorLedgerManager.update(updateList);
    if(newList.size()>0)
      mDebtorLedgerManager.create(newList);
  }

  private void createOrUpdateCreditorLedger(List<AccountTransaction> pAccountTransactions) {
    List<MutableCreditorLedger> existingDebtorLedgers = mCreditorLedgerManager.get(pAccountTransactions);
    Map<Long, MutableCreditorLedger> existingCreditorLedgerMap = existingDebtorLedgers.stream().collect(Collectors.toMap(p -> p.getAccountTransactionId(), p -> p));
    List<MutableCreditorLedger> updateList = new ArrayList<>();
    List<MutableCreditorLedger> newList = new ArrayList<>();
    pAccountTransactions.forEach(t -> {
      if (existingCreditorLedgerMap.containsKey(t.getId())) {
        MutableCreditorLedger creditorLedger = assignValuesToCreditorLedger(existingCreditorLedgerMap.get(t.getId()), t);
        updateList.add(creditorLedger);
      } else {
        MutableCreditorLedger creditorLedger = new PersistentCreditorLedger();
        creditorLedger = assignValuesToCreditorLedger(creditorLedger, t);
        creditorLedger.setId(mIdGenerator.getNumericId());
        newList.add(creditorLedger);
      }
    });
    if (updateList.size() > 0)
      mCreditorLedgerManager.update(updateList);
    if (newList.size() > 0)
      mCreditorLedgerManager.create(newList);
  }

  private MutableDebtorLedger assignValuesToDebtorLedger(MutableDebtorLedger pMutableDebtorLedger,
      AccountTransaction pAccountTransaction) {
    MutableDebtorLedger debtorLedger = pMutableDebtorLedger;
    debtorLedger.setCustomerCode(pAccountTransaction.getCustomerCode());
    debtorLedger.setAccountTransactionId(pAccountTransaction.getId());
    debtorLedger.setModifiedBy(pAccountTransaction.getModifiedBy());
    debtorLedger.setModificationDate(pAccountTransaction.getModifiedDate());
    debtorLedger.setBalanceType(pAccountTransaction.getBalanceType());
    debtorLedger.setPaidAmount(pAccountTransaction.getPaidAmount());
    debtorLedger.setAmount(pAccountTransaction.getAmount());
    debtorLedger.setSerialNo(pAccountTransaction.getSerialNo());
    debtorLedger.setVoucherDate(pAccountTransaction.getVoucherDate());
    debtorLedger.setVoucherNo(pAccountTransaction.getVoucherNo());
    debtorLedger.setCompanyId(pAccountTransaction.getCompanyId());
    debtorLedger.setInvoiceNo(pAccountTransaction.getInvoiceNo());
    debtorLedger.setInvoiceDate(pAccountTransaction.getInvoiceDate());
    debtorLedger.setPaidAmount(pAccountTransaction.getPaidAmount());
    return debtorLedger;
  }

  public MutableCreditorLedger assignValuesToCreditorLedger(MutableCreditorLedger pMutableCreditorLedger,
      AccountTransaction pAccountTransaction) {
    MutableCreditorLedger creditorLedger = pMutableCreditorLedger;
    creditorLedger.setSupplierCode(pAccountTransaction.getSupplierCode());
    creditorLedger.setAccountTransactionId(pAccountTransaction.getId());
    creditorLedger.setModifiedBy(pAccountTransaction.getModifiedBy());
    creditorLedger.setModificationDate(pAccountTransaction.getModifiedDate());
    creditorLedger.setBalanceType(pAccountTransaction.getBalanceType());
    creditorLedger.setPaidAmount(pAccountTransaction.getPaidAmount());
    creditorLedger.setAmount(pAccountTransaction.getAmount());
    creditorLedger.setSerialNo(pAccountTransaction.getSerialNo());
    creditorLedger.setVoucherDate(pAccountTransaction.getVoucherDate());
    creditorLedger.setVoucherNo(pAccountTransaction.getVoucherNo());
    creditorLedger.setCompanyId(pAccountTransaction.getCompanyId());
    creditorLedger.setBillNo(pAccountTransaction.getBillNo());
    creditorLedger.setBillDate(pAccountTransaction.getBillDate());
    creditorLedger.setPaidAmount(pAccountTransaction.getPaidAmount());
    return creditorLedger;
  }

  public void adjustTotalDebitOrCreditBalance(MutableAccountTransaction pTransaction, MutableMonthBalance pMonthBalance) {
    if(pTransaction.getBalanceType().equals(BalanceType.Cr)) {
      pMonthBalance.setTotalMonthCreditBalance(pMonthBalance.getTotalMonthCreditBalance() == null ? pTransaction
          .getAmount() : pMonthBalance.getTotalMonthCreditBalance().add(pTransaction.getAmount()));
      pMonthBalance.setTotalMonthDebitBalance(pMonthBalance.getTotalMonthDebitBalance() == null ? new BigDecimal(0)
          : pMonthBalance.getTotalMonthDebitBalance());
    }
    else {
      pMonthBalance.setTotalMonthDebitBalance(pMonthBalance.getTotalMonthDebitBalance() == null ? pTransaction
          .getAmount() : pMonthBalance.getTotalMonthDebitBalance().add(pTransaction.getAmount()));
      pMonthBalance.setTotalMonthCreditBalance(pMonthBalance.getTotalMonthCreditBalance() == null ? new BigDecimal(0)
          : pMonthBalance.getTotalMonthCreditBalance());
    }

  }

  @NotNull
  public List<MutableAccountBalance> generateUpdatedAccountBalance(FinancialAccountYear pCurrentFinancialAccountYear,
      List<Account> pAccounts, Map<Long, MutableAccountTransaction> pAccountMapWithTransaction) {
    List<MutableAccountBalance> accountBalanceList =
        mAccountBalanceManager.getAccountBalance(pCurrentFinancialAccountYear.getCurrentStartDate(),
            pCurrentFinancialAccountYear.getCurrentEndDate(), pAccounts);

    updateTotalBalance(pAccountMapWithTransaction, accountBalanceList);

    return accountBalanceList;
  }

  public void updateTotalBalance(Map<Long, MutableAccountTransaction> pAccountMapWithTransaction,
      List<MutableAccountBalance> pAccountBalanceList) {
    for(MutableAccountBalance a : pAccountBalanceList) {
      a.setModifiedDate(new Date());
      MutableAccountTransaction accountTransaction = pAccountMapWithTransaction.get(a.getAccountCode());
      a =
          mAccountBalanceResourceHelper.updateMonthlyDebitAndCreditBalance(a, accountTransaction,
              accountTransaction.getAmount());
      if(accountTransaction.getBalanceType().equals(BalanceType.Cr))
        a.setTotCreditTrans(a.getTotCreditTrans() == null ? accountTransaction.getAmount() : a.getTotCreditTrans().add(
            accountTransaction.getAmount()));
      else
        a.setTotDebitTrans(a.getTotDebitTrans() == null ? accountTransaction.getAmount() : a.getTotDebitTrans().add(
            accountTransaction.getAmount()));
    }
  }

  @Override
  public Response post(JsonObject pJsonObject, UriInfo pUriInfo) throws Exception {
    return null;
  }

  @Override
  public AccountTransactionManager getContentManager() {
    return mAccountTransactionManager;
  }

  @Override
  protected Builder<AccountTransaction, MutableAccountTransaction> getBuilder() {
    return mAccountTransactionBuilder;
  }

  @Override
  protected String getETag(AccountTransaction pReadonly) {
    return null;
  }
}
