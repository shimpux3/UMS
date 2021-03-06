package org.ums.persistent.dao.accounts;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.ums.decorator.accounts.AccountTransactionDaoDecorator;
import org.ums.domain.model.immutable.accounts.Account;
import org.ums.domain.model.immutable.accounts.AccountTransaction;
import org.ums.domain.model.immutable.accounts.Voucher;
import org.ums.domain.model.mutable.accounts.MutableAccountTransaction;
import org.ums.enums.accounts.definitions.account.balance.BalanceType;
import org.ums.enums.accounts.general.ledger.vouchers.AccountTransactionType;
import org.ums.generator.IdGenerator;
import org.ums.persistent.model.accounts.PersistentAccountTransaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Monjur-E-Morshed on 29-Jan-18.
 */
public class PersistantAccountTransactionDao extends AccountTransactionDaoDecorator {

  private JdbcTemplate mJdbcTemplate;
  private NamedParameterJdbcTemplate mNamedParameterJdbcTemplate;
  private IdGenerator mIdGenerator;

  String INSERT_ONE =
      "insert INTO DT_TRANSACTION(ID, COMP_CODE, VOUCHER_NO, VOUCHER_DATE, SERIAL_NO, ACCOUNT_ID, VOUCHER_ID, AMOUNT, BALANCE_TYPE,RECEIPT_ID, NARRATION, F_CURRENCY, CURRENCY_ID, CONV_FACTOR, TYPE, MODIFIED_BY, MODIFIED_DATE,POST_DATE,LAST_MODIFIED, INVOICE_NO, INVOICE_DATE, PAID_AMOUNT) VALUES "
          + "             (:id, :compCode, :voucherNo, :voucherDate, :serialNo, :accountId, :voucherId, :amount, :balanceType,:receiptId, :narration, :foreignCurrency, :currencyId, :conversionFactor, :type, :modifiedBy, :modifiedDate, :postDate,"
          + getLastModifiedSql() + ",:invoiceNo, :invoiceDate, :paidAmount)";
  String UPDATE_ONE = "UPDATE DT_TRANSACTION " + "SET " + "  COMP_CODE     = :compCode, "
      + "  DIVISION_CODE = :divisionCode, " + "  VOUCHER_NO    = :voucherNo, " + "  VOUCHER_DATE  = :voucherDate, "
      + "  SERIAL_NO     = :serialNo, " + "  ACCOUNT_ID    = :accountId, " + "  VOUCHER_ID    = :voucherId, "
      + "  AMOUNT        = :amount, " + "  BALANCE_TYPE  = :balanceType, " + "  NARRATION     = :narration, "
      + "  F_CURRENCY    = :foreignCurrency, " + "  CURRENCY_ID   = :currencyId, "
      + "  CONV_FACTOR   = :conversionFactor, " + "  PROJ_NO       = :projNo, " + "  STAT_FLAG     = :statFlag, "
      + "  STAT_UP_FLAG  = :statUpFlag, " + "  RECEIPT_ID    = :receiptId, " + "  POST_DATE     = :postDate, "
      + "  MODIFIED_BY   = :modifiedBy, "
      + "  MODIFIED_DATE = :modifiedDate, INVOICE_NO=:invoiceNo, INVOICE_DATE=:invoiceDate, PAID_AMOUNT=:paidAmount,"
      + "  TYPE          = :type , last_modified=" + getLastModifiedSql() + "  WHERE ID = :id";

  public PersistantAccountTransactionDao(JdbcTemplate pJdbcTemplate,
      NamedParameterJdbcTemplate pNamedParameterJdbcTemplate, IdGenerator pIdGenerator) {
    mJdbcTemplate = pJdbcTemplate;
    mNamedParameterJdbcTemplate = pNamedParameterJdbcTemplate;
    mIdGenerator = pIdGenerator;
  }

  @Override
  public AccountTransaction get(Long pId) {
    String query = "SELECT * FROM DT_TRANSACTION WHERE ID=:ID";
    try {
      Map parameterMap = new HashMap();
      parameterMap.put("ID", pId);
      return mNamedParameterJdbcTemplate.queryForObject(query, parameterMap,
          new PersistentAccountTransactionRowMapper());
    } catch(EmptyResultDataAccessException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public List<MutableAccountTransaction> getAccountTransactions(Date pFromDate, Date pToDate, Account pAccount) {
    String query =
        "select * from DT_TRANSACTION where (VOUCHER_DATE>=:fromDate and VOUCHER_DATE<=:toDate) and ACCOUNT_ID=:accountId";
    Map parameterMap = new HashMap();
    parameterMap.put("fromDate", pFromDate);
    parameterMap.put("toDate", pToDate);
    parameterMap.put("accountId", pAccount.getId());
    return mNamedParameterJdbcTemplate.query(query, parameterMap, new PersistentAccountTransactionRowMapper());
  }

  @Override
  public List<MutableAccountTransaction> getAccountTransactions(Date pFromDate, Date pToDate, List<Account> pAccounts) {
    if (pAccounts.size() == 0) return null;

    String query =
        "select * from DT_TRANSACTION where (VOUCHER_DATE>=:fromDate and VOUCHER_DATE<=:toDate) and ACCOUNT_ID in(:accountIdList)";
    Map parameterMap = new HashMap();
    parameterMap.put("fromDate", pFromDate);
    parameterMap.put("toDate", pToDate);
    parameterMap.put("accountIdList", pAccounts.stream().map(a -> a.getId()).collect(Collectors.toList()));
    return mNamedParameterJdbcTemplate.query(query, parameterMap, new PersistentAccountTransactionRowMapper());
  }

  @Override
  public List<MutableAccountTransaction> getAccountTransactions(Date pFromDate, Date pToDate) {

    String query = "select * from DT_TRANSACTION where (VOUCHER_DATE>=:fromDate and VOUCHER_DATE<=:toDate) ";
    Map parameterMap = new HashMap();
    parameterMap.put("fromDate", pFromDate);
    parameterMap.put("toDate", pToDate);
    return mNamedParameterJdbcTemplate.query(query, parameterMap, new PersistentAccountTransactionRowMapper());
  }

  @Override
  public List<MutableAccountTransaction> getByVoucherNo(String pVoucherNo) {
    String query =
        "SELECT DT_TRANSACTION.* " + "FROM DT_TRANSACTION, FIN_ACCOUNT_YEAR "
            + "WHERE VOUCHER_NO=? AND VOUCHER_DATE >= FIN_ACCOUNT_YEAR.CURRENT_START_DATE AND "
            + "      VOUCHER_DATE <= FIN_ACCOUNT_YEAR.CURRENT_END_DATE";
    return mJdbcTemplate.query(query, new Object[] {pVoucherNo}, new PersistentAccountTransactionRowMapper());
  }

  @Override
  public List<MutableAccountTransaction> getByVoucherNoAndDate(String pVoucherNo, Date pDate) {
    String query = "select * from DT_TRANSACTION where VOUCHER_NO=? and trunc(MODIFIED_DATE)=TRUNC(?)";
    return mJdbcTemplate.query(query, new Object[] {pVoucherNo, pDate}, new PersistentAccountTransactionRowMapper());
  }

  @Override
  public List<Long> create(List<MutableAccountTransaction> pMutableList) {
    String query=INSERT_ONE;
    Map<String, Object>[] paramObjects = getParamObjects(pMutableList);
    mNamedParameterJdbcTemplate.batchUpdate(query, paramObjects);
    return pMutableList.stream().map(p -> p.getId()).collect(Collectors.toList());
  }

  private Map<String, Object>[] getParamObjects(List<MutableAccountTransaction> pMutableList) {
    Map<String, Object>[] parameterMaps = new HashMap[pMutableList.size()];
    for(int i = 0; i < pMutableList.size(); i++) {
      parameterMaps[i] = getInsertOrUpdateParameters(pMutableList.get(i));
    }
    return parameterMaps;
  }

  @Override
  public int update(MutableAccountTransaction pMutable) {
    Map parameter = getInsertOrUpdateParameters(pMutable);
    return mNamedParameterJdbcTemplate.update(UPDATE_ONE, parameter);
  }

  @Override
  public int delete(MutableAccountTransaction pMutable) {
    String query = "delete from DT_TRANSACTION where id=?";
    return mJdbcTemplate.update(query, pMutable.getId());
  }

  @Override
  public int delete(List<MutableAccountTransaction> pMutableList) {
    List<Long> idList = pMutableList.stream().map(m -> m.getId()).collect(Collectors.toList());
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("idList", idList);
    String query = "delete from DT_TRANSACTION where id in(:idList)";
    return mNamedParameterJdbcTemplate.update(query, parameters);
  }

  @Override
  public int update(List<MutableAccountTransaction> pMutableList) {
    Map<String, Object>[] parameters = getParamObjects(pMutableList);
    return mNamedParameterJdbcTemplate.batchUpdate(UPDATE_ONE, parameters).length;
  }

  private List<Object[]> getCreateParams(List<MutableAccountTransaction> pMutableAccountTransactions) {
    List<Object[]> params = new ArrayList<>();

    for(AccountTransaction accountTransaction : pMutableAccountTransactions) {
      params.add(new Object[] {accountTransaction.getId(), accountTransaction.getCompany().getId(),
          accountTransaction.getVoucherNo(), accountTransaction.getVoucherDate(), accountTransaction.getSerialNo(),
          accountTransaction.getAccount().getId(), accountTransaction.getVoucher().getId(),
          accountTransaction.getAmount(), accountTransaction.getBalanceType().getValue(),
          accountTransaction.getNarration(), accountTransaction.getForeignCurrency(),
          accountTransaction.getCurrency().getId(), accountTransaction.getConversionFactor(),
          accountTransaction.getPostDate(), accountTransaction.getAccountTransactionType().getValue(),
          accountTransaction.getModifiedBy(), accountTransaction.getModifiedDate()});
    }
    return params;
  }

  @Override
  public Integer getTotalVoucherNumberBasedOnCurrentDay(Voucher pVoucher) {
    String query = "";
    return super.getTotalVoucherNumberBasedOnCurrentDay(pVoucher);
  }

  @Override
  public Integer getVoucherNumber(Voucher pVoucher, Date pStartDate, Date pEndDate) {
    String query =
        "SELECT COUNT(VOUCHER_NO) " + "FROM ( " + "  SELECT DISTINCT (VOUCHER_NO) VOUCHER_NO "
            + "  FROM DT_TRANSACTION " + "  WHERE VOUCHER_ID = ? AND (MODIFIED_DATE >= ? OR MODIFIED_DATE <= ?))";
    return mJdbcTemplate.queryForObject(query, new Object[] {pVoucher.getId(), pStartDate, pEndDate}, Integer.class);
  }

  @Override
  public List<String> getVouchers(Voucher pVoucher, Date pStartDate, Date pEndDate) {
    String query =
        "SELECT DISTINCT (VOUCHER_NO) VOUCHER_NO " +
            "      FROM DT_TRANSACTION " +
            "      WHERE VOUCHER_ID = ? AND (MODIFIED_DATE >= ? OR MODIFIED_DATE <=?)";
    return mJdbcTemplate.query(query, new Object[]{pVoucher.getId(), pStartDate, pEndDate}, (rs, i) -> rs.getString("voucher_no"));
  }

  @Override
  public Integer getTotalNumber(Voucher pVoucher) {
    String query =
        "select count(DISTINCT(VOUCHER_NO)) from  DT_TRANSACTION,FIN_ACCOUNT_YEAR where VOUCHER_ID=? and FIN_ACCOUNT_YEAR.YEAR_CLOSING_FLAG='O' and balance_type='Dr' and (VOUCHER_DATE>=FIN_ACCOUNT_YEAR.CURRENT_START_DATE and "
            + "VOUCHER_DATE<=FIN_ACCOUNT_YEAR.CURRENT_END_DATE)";
    return mJdbcTemplate.queryForObject(query, new Object[] {pVoucher.getId()}, Integer.class);
  }

  @Override
  public Integer getTotalNumber(Voucher pVoucher, String voucherNo) {
    String query =
        "select count(DISTINCT(VOUCHER_NO)) from  DT_TRANSACTION,FIN_ACCOUNT_YEAR where VOUCHER_ID=? AND VOUCHER_NO=? and  BALANCE_TYPE='Dr' and FIN_ACCOUNT_YEAR.YEAR_CLOSING_FLAG='O' and (VOUCHER_DATE>=FIN_ACCOUNT_YEAR.CURRENT_START_DATE and "
            + "VOUCHER_DATE<=FIN_ACCOUNT_YEAR.CURRENT_END_DATE)";
    return mJdbcTemplate.queryForObject(query, new Object[] {pVoucher.getId(), voucherNo}, Integer.class);
  }

  @Override
  public List<MutableAccountTransaction> getAllPaginated(int itemPerPage, int pageNumber, Voucher voucher) {
    int startIndex = (itemPerPage * (pageNumber - 1)) + 1;
    int endIndex = startIndex + itemPerPage - 1;
    String query =
        "SELECT temp2.* "
            + "FROM (SELECT "
            + "        ROWNUM row_num, "
            + "        temp.* "
            + "      FROM (SELECT DT_TRANSACTION.* "
            + "            FROM DT_TRANSACTION "
            + "            WHERE (VOUCHER_NO, SERIAL_NO, MODIFIED_DATE) IN (SELECT "
            + "                                                               DT_TRANSACTION.VOUCHER_NO, "
            + "                                                               SERIAL_NO, "
            + "                                                               MAX(DT_TRANSACTION.MODIFIED_DATE) "
            + "                                                                 AS MODIFIED_DATE "
            + "                                                             FROM DT_TRANSACTION, FIN_ACCOUNT_YEAR "
            + "                                                             WHERE YEAR_CLOSING_FLAG = 'O' AND "
            + "                                                                   DT_TRANSACTION.MODIFIED_DATE >= "
            + "                                                                   FIN_ACCOUNT_YEAR.CURRENT_START_DATE AND "
            + "                                                                   DT_TRANSACTION.MODIFIED_DATE <= "
            + "                                                                   FIN_ACCOUNT_YEAR.CURRENT_END_DATE "
            + "                                                                   AND SERIAL_NO = 1 AND "
            + "                                                                   VOUCHER_ID = ? "
            + "                                                             GROUP BY DT_TRANSACTION.VOUCHER_NO, SERIAL_NO)  "
            + "                   " + "            ORDER BY MODIFIED_DATE DESC) temp) temp2 "
            + "WHERE row_num >= ? AND row_num <= ?";
    return mJdbcTemplate.query(query, new Object[] {voucher.getId(), startIndex, endIndex},
        new PersistentAccountTransactionRowMapper());
  }

  @Override
  public List<MutableAccountTransaction> getAllPaginated(int itemPerPage, int pageNumber, Voucher voucher,
      String voucherNo) {
    int startIndex = (itemPerPage * (pageNumber - 1)) + 1;
    int endIndex = startIndex + itemPerPage - 1;
    String query =
        "SELECT temp2.* "
            + "FROM (SELECT "
            + "        ROWNUM row_num, "
            + "        temp.* "
            + "      FROM (SELECT DT_TRANSACTION.* "
            + "            FROM DT_TRANSACTION "
            + "            WHERE (VOUCHER_NO, SERIAL_NO, MODIFIED_DATE) IN (SELECT "
            + "                                                               DT_TRANSACTION.VOUCHER_NO, "
            + "                                                               SERIAL_NO, "
            + "                                                               MAX(DT_TRANSACTION.MODIFIED_DATE) "
            + "                                                                 AS MODIFIED_DATE "
            + "                                                             FROM DT_TRANSACTION, FIN_ACCOUNT_YEAR "
            + "                                                             WHERE YEAR_CLOSING_FLAG = 'O' AND "
            + "                                                                   DT_TRANSACTION.MODIFIED_DATE >= "
            + "                                                                   FIN_ACCOUNT_YEAR.CURRENT_START_DATE AND "
            + "                                                                   DT_TRANSACTION.MODIFIED_DATE <= "
            + "                                                                   FIN_ACCOUNT_YEAR.CURRENT_END_DATE "
            + "                                                                   AND SERIAL_NO = 1 AND "
            + "                                                                   VOUCHER_ID = ? and VOUCHER_NO=? "
            + "                                                             GROUP BY DT_TRANSACTION.VOUCHER_NO, SERIAL_NO)  "
            + "                   " + "            ORDER BY MODIFIED_DATE DESC) temp) temp2 "
            + "WHERE row_num >= ? AND row_num <= ?";
    return mJdbcTemplate.query(query, new Object[] {voucher.getId(), voucherNo, startIndex, endIndex},
        new PersistentAccountTransactionRowMapper());
  }

  private Map getInsertOrUpdateParameters(MutableAccountTransaction pMutableAccountTransaction) {
    Map parameters = new HashMap();
    parameters.put("id", pMutableAccountTransaction.getId());
    parameters.put("compCode", pMutableAccountTransaction.getCompany().getId());
    parameters.put("divisionCode", pMutableAccountTransaction.getDivisionCode());
    parameters.put("voucherNo", pMutableAccountTransaction.getVoucherNo());
    parameters.put("voucherDate", pMutableAccountTransaction.getVoucherDate());
    parameters.put("serialNo", pMutableAccountTransaction.getSerialNo());
    parameters.put("accountId", pMutableAccountTransaction.getAccount().getId());
    parameters.put("voucherId", pMutableAccountTransaction.getVoucher().getId());
    parameters.put("amount", pMutableAccountTransaction.getAmount());
    parameters.put("balanceType", pMutableAccountTransaction.getBalanceType().getValue());
    parameters.put("narration", pMutableAccountTransaction.getNarration());
    parameters.put("foreignCurrency", pMutableAccountTransaction.getForeignCurrency());
    parameters.put("currencyId", pMutableAccountTransaction.getCurrency().getId());
    parameters.put("conversionFactor", pMutableAccountTransaction.getConversionFactor());
    parameters.put("projNo", pMutableAccountTransaction.getProjNo());
    parameters.put("statFlag", pMutableAccountTransaction.getStatFlag());
    parameters.put("statUpFlag", pMutableAccountTransaction.getStatUpFlag());
    parameters.put("type", pMutableAccountTransaction.getAccountTransactionType() == null ? null
        : pMutableAccountTransaction.getAccountTransactionType().getValue());

    parameters.put("modifiedBy", pMutableAccountTransaction.getModifiedBy());
    parameters.put("receiptId", pMutableAccountTransaction.getReceiptId());
    parameters.put("modifiedDate", pMutableAccountTransaction.getModifiedDate());
    parameters.put("postDate", pMutableAccountTransaction.getPostDate());
    parameters.put("invoiceNo", pMutableAccountTransaction.getInvoiceNo());
    parameters.put("invoiceDate", pMutableAccountTransaction.getInvoiceDate());
    parameters.put("paidAmount", pMutableAccountTransaction.getPaidAmount());
    return parameters;
  }

  class PersistentAccountTransactionRowMapper implements RowMapper<MutableAccountTransaction> {
    @Override
    public MutableAccountTransaction mapRow(ResultSet pResultSet, int pI) throws SQLException {
      MutableAccountTransaction transaction = new PersistentAccountTransaction();
      transaction.setId(pResultSet.getLong("id"));
      transaction.setCompanyId(pResultSet.getString("comp_code"));
      transaction.setDivisionCode(pResultSet.getString("division_code"));
      transaction.setVoucherNo(pResultSet.getString("voucher_no"));
      transaction.setVoucherDate(pResultSet.getDate("voucher_date"));
      transaction.setSerialNo(pResultSet.getInt("serial_no"));
      transaction.setAccountId(pResultSet.getLong("account_id"));
      transaction.setVoucherId(pResultSet.getLong("voucher_id"));
      transaction.setAmount(pResultSet.getBigDecimal("amount"));
      transaction.setBalanceType(BalanceType.get(pResultSet.getString("balance_type")));
      transaction.setReceiptId(pResultSet.getLong("receipt_id") == 0 ? null : pResultSet.getLong("receipt_id"));
      transaction.setNarration(pResultSet.getString("narration"));
      transaction.setForeignCurrency(pResultSet.getBigDecimal("f_currency"));
      transaction.setCurrencyId(pResultSet.getLong("currency_id"));
      transaction.setConversionFactor(pResultSet.getBigDecimal("conv_factor"));
      transaction.setProjNo(pResultSet.getString("proj_no"));
      transaction.setStatFlag(pResultSet.getString("stat_flag"));
      transaction.setStatUpFlag(pResultSet.getString("stat_up_flag"));
      transaction.setPostDate(pResultSet.getDate("post_date"));
      transaction.setAccountTransactionType(AccountTransactionType.get(pResultSet.getString("type")));
      transaction.setModifiedBy(pResultSet.getString("modified_by"));
      transaction.setModifiedDate(pResultSet.getDate("modified_date"));
      transaction.setInvoiceNo(pResultSet.getString("invoice_no"));
      transaction.setInvoiceDate(pResultSet.getDate("invoice_date"));
      transaction.setPaidAmount(pResultSet.getBigDecimal("paid_amount"));
      return transaction;
    }
  }
}
