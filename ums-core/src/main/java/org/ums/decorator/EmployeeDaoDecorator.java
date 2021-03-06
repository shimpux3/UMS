package org.ums.decorator;

import org.ums.domain.model.immutable.Employee;
import org.ums.domain.model.mutable.MutableEmployee;
import org.ums.manager.EmployeeManager;

import java.util.List;

public class EmployeeDaoDecorator extends ContentDaoDecorator<Employee, MutableEmployee, String, EmployeeManager>
    implements EmployeeManager {

  @Override
  public List<Employee> getByDesignation(String pDesignationId) {
    return getManager().getByDesignation(pDesignationId);
  }

  @Override
  public Employee getByShortName(String pShortName) {
    return getManager().getByShortName(pShortName);
  }

  @Override
  public List<Employee> getActiveTeachersOfDept(String deptId) {
    return getManager().getActiveTeachersOfDept(deptId);
  }

  @Override
  public List<Employee> getActiveTeachers() {
    return getManager().getActiveTeachers();
  }

  @Override
  public List<Employee> getEmployees(String pDeptId, String pPublicationStatus) {
    return getManager().getEmployees(pDeptId, pPublicationStatus);
  }

  @Override
  public List<Employee> getEmployees(List<String> pEmployeeIdList) {
    return getManager().getEmployees(pEmployeeIdList);
  }

  @Override
  public List<Employee> getEmployees(String pDepartmentId) {
    return getManager().getEmployees(pDepartmentId);
  }

  @Override
  public String getLastEmployeeId(String pDepartmentId, int pEmployeeType) {
    return getManager().getLastEmployeeId(pDepartmentId, pEmployeeType);
  }

  @Override
  public boolean validateShortName(String pShortName) {
    return getManager().validateShortName(pShortName);
  }

  @Override
  public List<Employee> waitingForAccountVerification() {
    return getManager().waitingForAccountVerification();
  }

  @Override
  public List<Employee> downloadEmployeeList(String pDeptList, String pEmployeeTypeList) {
    return getManager().downloadEmployeeList(pDeptList, pEmployeeTypeList);
  }

}
