package org.ums.ems.profilemanagement.additional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersistentAreaOfInterestInformationDao extends AreaOfInterestInformationDaoDecorator {

  static String INSERT_ONE = "INSERT INTO EMP_AOI_INFO (EMPLOYEE_ID, AOI, LAST_MODIFIED) VALUES (?, ?, "
      + getLastModifiedSql() + ")";

  static String GET_ONE = "SELECT EMPLOYEE_ID, AOI FROM EMP_AOI_INFO ";

  static String DELETE_ONE = "DELETE FROM EMP_AOI_INFO ";

  static String EXISTS_ONE = "SELECT COUNT(EMPLOYEE_ID) FROM EMP_AOI_INFO";

  private JdbcTemplate mJdbcTemplate;

  public PersistentAreaOfInterestInformationDao(final JdbcTemplate pJdbcTemplate) {
    mJdbcTemplate = pJdbcTemplate;
  }

  @Override
  public String create(MutableAreaOfInterestInformation pMutable) {
    mJdbcTemplate.update(INSERT_ONE, pMutable.getId(), pMutable.getAreaOfInterest());
    return pMutable.getId();
  }

  @Override
  public List<String> create(List<MutableAreaOfInterestInformation> pMutable) {
    List<String> stringList = new ArrayList<>();
    for(MutableAreaOfInterestInformation mutableAreaOfInterestInformation : pMutable) {
      stringList.add(create(mutableAreaOfInterestInformation));
    };
    return stringList;
  }

  @Override
  public AreaOfInterestInformation get(final String pId) {
    String query = GET_ONE + " WHERE EMPLOYEE_ID = ?";
    return mJdbcTemplate.queryForObject(query, new Object[] {pId},
        new PersistentAreaOfInterestInformationDao.RoleRowMapper());
  }

  @Override
  public List<AreaOfInterestInformation> getAll(final String pEmployeeId) {
    String query = GET_ONE + " WHERE EMPLOYEE_ID = ?";
    return mJdbcTemplate.query(query, new Object[] {pEmployeeId},
        new PersistentAreaOfInterestInformationDao.RoleRowMapper());
  }

  @Override
  public int delete(final MutableAreaOfInterestInformation pMutable) {
    String query = DELETE_ONE + " WHERE EMPLOYEE_ID = ?";
    return mJdbcTemplate.update(query, pMutable.getId());
  }

  @Override
  public boolean exists(String pEmployeeId) {
    String query = EXISTS_ONE + " WHERE EMPLOYEE_ID = ?";
    return mJdbcTemplate.queryForObject(query, new Object[] {pEmployeeId}, Boolean.class);
  }

  class RoleRowMapper implements RowMapper<AreaOfInterestInformation> {

    @Override
    public AreaOfInterestInformation mapRow(ResultSet resultSet, int i) throws SQLException {
      PersistentAreaOfInterestInformation persistentAreaOfInterestInformation =
          new PersistentAreaOfInterestInformation();
      persistentAreaOfInterestInformation.setId(resultSet.getString("EMPLOYEE_ID"));
      persistentAreaOfInterestInformation.setAreaOfInterest(resultSet.getString("AOI"));
      return persistentAreaOfInterestInformation;
    }
  }
}
