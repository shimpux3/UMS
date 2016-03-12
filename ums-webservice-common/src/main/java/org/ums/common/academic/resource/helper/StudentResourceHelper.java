package org.ums.common.academic.resource.helper;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.ums.academic.builder.Builder;
import org.ums.academic.model.PersistentSemesterEnrollment;
import org.ums.academic.model.PersistentStudent;
import org.ums.academic.model.PersistentStudentRecord;
import org.ums.academic.model.PersistentUser;
import org.ums.cache.LocalCache;
import org.ums.common.academic.resource.ResourceHelper;
import org.ums.common.academic.resource.StudentResource;
import org.ums.domain.model.mutable.*;
import org.ums.domain.model.readOnly.Role;
import org.ums.domain.model.readOnly.SemesterEnrollment;
import org.ums.domain.model.readOnly.Student;
import org.ums.domain.model.readOnly.StudentRecord;
import org.ums.manager.BinaryContentManager;
import org.ums.manager.ContentManager;
import org.ums.manager.SemesterEnrollmentManager;
import org.ums.manager.StudentRecordManager;
import org.ums.util.UmsUtils;

import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Base64;
import java.util.List;

@Component
public class StudentResourceHelper extends ResourceHelper<Student, MutableStudent, String> {
  @Autowired
  @Qualifier("roleManager")
  ContentManager<Role, MutableRole, Integer> mRoleManager;
  @Autowired
  BinaryContentManager<byte[]> mBinaryContentManager;
  @Autowired
  @Qualifier("studentManager")
  private ContentManager<Student, MutableStudent, String> mManager;
  @Autowired
  private List<Builder<Student, MutableStudent>> mBuilders;

  @Override
  @Transactional
  public Response post(JsonObject pJsonObject, UriInfo pUriInfo) throws Exception {
    MutableStudent mutableStudent = new PersistentStudent();

    LocalCache localCache = new LocalCache();
    for (Builder<Student, MutableStudent> builder : mBuilders) {
      builder.build(mutableStudent, pJsonObject, localCache);
    }

    mutableStudent.setCurrentYear(UmsUtils.FIRST);
    mutableStudent.setCurrentAcademicSemester(UmsUtils.FIRST);
    mutableStudent.setEnrollmentType(Student.EnrollmentType.TEMPORARY);
    mutableStudent.commit(false);

    MutableStudentRecord mutableStudentRecord = new PersistentStudentRecord();
    mutableStudentRecord.setStudentId(mutableStudent.getId());
    mutableStudentRecord.setSemesterId(mutableStudent.getSemesterId());
    mutableStudentRecord.setProgramId(mutableStudent.getProgramId());
    mutableStudentRecord.setYear(UmsUtils.FIRST);
    mutableStudentRecord.setAcademicSemester(UmsUtils.FIRST);
    mutableStudentRecord.setType(StudentRecord.Type.TEMPORARY);
    mutableStudentRecord.setStatus(StudentRecord.Status.UNKNOWN);
    mutableStudentRecord.commit(false);

    MutableUser studentUser = new PersistentUser();
    studentUser.setId(pJsonObject.getString("id"));
    //TODO: Use a password generator to generate temporary password
    String random = RandomStringUtils.randomAlphanumeric(10).toUpperCase();
    studentUser.setTemporaryPassword(random.toCharArray());
    //TODO: Use role name to fetch a particular role, say for Student it should be "student"
    studentUser.setPrimaryRole(mRoleManager.get(11));
    studentUser.setActive(true);
    studentUser.commit(false);

    String encodingPrefix = "base64,", data = pJsonObject.getString("imageData");
    int contentStartIndex = data.indexOf(encodingPrefix) + encodingPrefix.length();
    byte[] imageData = Base64.getDecoder().decode(data.substring(contentStartIndex));

    mBinaryContentManager.create(imageData, pJsonObject.getString("id"), BinaryContentManager.Domain.PICTURE);
    URI contextURI = pUriInfo.getBaseUriBuilder().path(StudentResource.class).path(StudentResource.class, "get").build(mutableStudent.getId());
    Response.ResponseBuilder builder = Response.created(contextURI);
    builder.status(Response.Status.CREATED);

    return builder.build();
  }

  @Override
  protected ContentManager<Student, MutableStudent, String> getContentManager() {
    return mManager;
  }

  @Override
  protected List<Builder<Student, MutableStudent>> getBuilders() {
    return mBuilders;
  }

  @Override
  protected String getEtag(Student pReadonly) {
    return pReadonly.getLastModified();
  }
}
