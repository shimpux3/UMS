package org.ums.processor.userhome;

import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.ums.manager.EmployeeManager;
import org.ums.usermanagement.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfficialsHomeProcessor extends AbstractUserHomeProcessor {
  @Autowired
  EmployeeManager employeeManager;

  @Override
  public UserInfo process(Subject pCurrentSubject) {
    String userId = pCurrentSubject.getPrincipal().toString();
    User user = mUserManager.get(userId);

    UserInfo userInfo = new UserInfo();
    List<Map<String, String>> profileContent = new ArrayList<>();

    Map<String, String> userName = new HashMap<>();
    userName.put("key", "Name");
    userName.put("value", user.getName());
    profileContent.add(userName);

    Map<String, String> department = new HashMap<>();
    department.put("key", "Department/ Office");
    department.put("value", user.getDepartment().getLongName());
    profileContent.add(department);

    Map<String, String> designation = new HashMap<>();
    designation.put("key", "Designation");
    designation.put("value", employeeManager.getByShortName(user.getId()).getDesignation().getDesignationName());
    profileContent.add(designation);

    userInfo.setInfoList(profileContent);
    userInfo.setUserRole(user.getPrimaryRole().getName());

    return userInfo;
  }
}
