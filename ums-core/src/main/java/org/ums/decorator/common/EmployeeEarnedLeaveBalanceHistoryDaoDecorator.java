package org.ums.decorator.common;

import org.ums.decorator.ContentDaoDecorator;
import org.ums.domain.model.immutable.common.EmployeeEarnedLeaveBalanceHistory;
import org.ums.domain.model.mutable.common.MutableEmployeeEarnedLeaveBalanceHistory;
import org.ums.manager.common.EmployeeEarnedLeaveBalanceHistoryManager;

import java.util.List;

public class EmployeeEarnedLeaveBalanceHistoryDaoDecorator
    extends
    ContentDaoDecorator<EmployeeEarnedLeaveBalanceHistory, MutableEmployeeEarnedLeaveBalanceHistory, Long, EmployeeEarnedLeaveBalanceHistoryManager>
    implements EmployeeEarnedLeaveBalanceHistoryManager {
  @Override
  public List<EmployeeEarnedLeaveBalanceHistory> getAllEarnedLeaveBalanceHistoryOfActiveEmployees() {
    return getManager().getAllEarnedLeaveBalanceHistoryOfActiveEmployees();
  }
}
