package org.ums.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ums.ems.cv.EmployeeListGenerator;
import org.ums.logs.GetLog;
import org.ums.manager.EmployeeManager;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

@Component
@Path("academic/employee")
@Produces(Resource.MIME_TYPE_JSON)
@Consumes(Resource.MIME_TYPE_JSON)
public class EmployeeResource extends MutableEmployeeResource {
  @Autowired
  EmployeeManager mManager;

  @Autowired
  EmployeeListGenerator mEmployeeListGenerator;

  @GET
  @Path("/all")
  @GetLog(message = "Get all employees")
  public JsonObject getAll(@Context HttpServletRequest httpServletRequest) throws Exception {
    return mEmployeeResourceHelper.getAll(mUriInfo);
  }

  @GET
  @Path("/getActiveTeachersByDept")
  public JsonObject getActiveTeachersByDepartment() {
    return mEmployeeResourceHelper.getActiveTeachersByDept(mUriInfo);
  }

  @GET
  @Path("/getActiveTeachers")
  public JsonObject getActiveTeachers() {
    return mEmployeeResourceHelper.getActiveTeachers(mUriInfo);
  }

  @GET
  @Path("/employeeById")
  public JsonObject getEmployeeById() {
    return mEmployeeResourceHelper.getByEmployeeId(mUriInfo);
  }

  @GET
  @Path("/designation/{designationId}")
  public JsonObject getByDesignation(final @Context Request pRequest,
      final @PathParam("designationId") String designationId) {
    return mEmployeeResourceHelper.getByDesignation(designationId, pRequest, mUriInfo);
  }

  @GET
  @Path(PATH_PARAM_OBJECT_ID)
  public Response get(final @Context Request pRequest, final @PathParam("object-id") String pObjectId) throws Exception {
    return mEmployeeResourceHelper.get(pObjectId, pRequest, mUriInfo);
  }

  @GET
  @Path("/search")
  public JsonObject searchUser(@QueryParam("term") String pTerm, @QueryParam("page") int pPage) throws Exception {
    return mEmployeeResourceHelper.searchUserByName(pTerm, pPage, mUriInfo);
  }

  @GET
  @Path("/searchByDepartment")
  public JsonObject searchUserByChildren(@QueryParam("term") String pTerm, @QueryParam("page") int pPage)
      throws Exception {
    return mEmployeeResourceHelper.searchUserByDepartment(pTerm, pPage, mUriInfo);
  }

  @GET
  @Path("/getEmployee/publicationStatus/{publication-status}")
  public JsonObject getEmployees(final @PathParam("publication-status") String pPublicationStatus,
      final @Context Request pRequest) {
    return mEmployeeResourceHelper.getEmployees(pPublicationStatus, pRequest, mUriInfo);
  }

  @GET
  @Path("/employeeById/departmentId/{department-id}")
  @GetLog(message = "Get employees by department")
  public JsonObject getEmployees(@Context HttpServletRequest httpServletRequest, final @Context Request pRequest,
      final @PathParam("department-id") String pDepartmentId) {
    return mEmployeeResourceHelper.getEmployees(pDepartmentId, mUriInfo);
  }

  @GET
  @Path("/newId/deptId/{dept-id}/employeeType/{employee-type}")
  @GetLog(message = "Get new employee id")
  public JsonObject getNewEmployeeId(@Context HttpServletRequest httpServletRequest, final @Context Request pRequest,
      final @PathParam("dept-id") String pDeptId, final @PathParam("employee-type") int pEmployeeType) {
    return mEmployeeResourceHelper.getCurrentMaxEmployeeId(pDeptId, pEmployeeType);
  }

  @GET
  @Path("/validate/{short-name}")
  public boolean validate(final @Context Request pRequest, final @PathParam("short-name") String pShortName) {
    return mEmployeeResourceHelper.validateShortName(pShortName);
  }

  @GET
  @Path("/status/pending")
  public JsonObject getEmployeeListWaitingForAccountVerification(@Context HttpServletRequest httpServletRequest,
      final @Context Request pRequest) {
    return mEmployeeResourceHelper.getEmployeesWaitingForAccountVerification(mUriInfo);
  }

  @GET
  @Path("/report/employeeList/deptList/{dept-list}/empTypeList/{emp-type}/choice/{choice}")
  @Produces("application/pdf")
  @GetLog(message = "Download Employee List")
  public StreamingOutput getEmployeeCV(@Context HttpServletRequest httpServletRequest, final @Context Request pRequest,
      final @PathParam("dept-list") String deptList, final @PathParam("emp-type") String empType,
      final @PathParam("choice") Integer choice) {
    return new StreamingOutput() {
      @Override
      public void write(OutputStream pOutputStream) throws IOException, WebApplicationException {
        try {
          mEmployeeListGenerator.printEmployeeList(deptList, empType, choice, pOutputStream);
        } catch(Exception e) {
          throw new WebApplicationException(e);
        }
      }
    };
  }
}
