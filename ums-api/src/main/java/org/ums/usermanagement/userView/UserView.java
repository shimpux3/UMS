package org.ums.usermanagement.userView;

import org.ums.domain.model.common.EditType;
import org.ums.domain.model.common.Identifier;
import org.ums.domain.model.common.LastModifier;

import java.io.Serializable;
import java.util.Date;

public interface UserView extends Serializable, EditType<MutableUserView>, Identifier<String>, LastModifier {

  String getUserName();

  String getGender();

  Date getDateOfBirth();

  String getBloodGroup();

  String getFatherName();

  String getMotherName();

  String getMobileNumber();

  String getEmailAddress();

  String getDepartment();

  int getDesignation();

  int getRoleId();

  int getStatus();
}
