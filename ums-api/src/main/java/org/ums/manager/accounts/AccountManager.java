package org.ums.manager.accounts;

import org.ums.domain.model.immutable.Company;
import org.ums.domain.model.immutable.accounts.Account;
import org.ums.domain.model.mutable.accounts.MutableAccount;
import org.ums.enums.accounts.definitions.group.GroupFlag;
import org.ums.enums.common.AscendingOrDescendingType;
import org.ums.manager.ContentManager;

import java.util.List;

/**
 * Created by Monjur-E-Morshed on 28-Dec-17.
 */
public interface AccountManager extends ContentManager<Account, MutableAccount, Long> {
  Integer getSize(Company pCompany);

  Account getAccount(Long pAccountCode, Company pCompany);

  boolean exists(Long pAccountCode, Company pCompany);

  List<Account> getAllPaginated(int itemPerPage, int pageNumber, AscendingOrDescendingType ascendingOrDescendingType,
      Company company);

  List<Account> getAccounts(String pAccountName);

  List<Account> getExcludingGroups(List<String> groupCodeList, Company company);

  List<Account> getIncludingGroups(List<String> groupCodeList, Company company);

  /**
   * @param pGroupFlag
   * @return will return accounts based on group flag.
   */
  List<Account> getAccounts(GroupFlag pGroupFlag);

  List<Account> getAccounts(Company pCompany);
}
