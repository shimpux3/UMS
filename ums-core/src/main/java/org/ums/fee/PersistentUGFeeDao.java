package org.ums.fee;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.ums.generator.IdGenerator;

public class PersistentUGFeeDao extends UGFeeDaoDecorator {
  String SELECT_ALL = "SELECT ID, FEE_CATEGORY_ID, SEMESTER_ID, FACULTY_ID, AMOUNT, LAST_MODIFIED FROM FEE ";
  String UPDATE_ALL = "UPDATE FEE SET FEE_CATEGORY_ID = ?, SEMESTER_ID = ?, FACULTY_ID = ?, AMOUNT = ?, "
      + "LAST_MODIFIED = " + getLastModifiedSql() + " ";
  String DELETE_ALL = "DELETE FROM FEE ";
  String INSERT_ALL = "INSERT INTO FEE(ID, FEE_CATEGORY_ID, SEMESTER_ID, FACULTY_ID, AMOUNT, LAST_MODIFIED) "
      + "VALUES (?, ?, ?, ?, ?, " + getLastModifiedSql() + ") ";

  private JdbcTemplate mJdbcTemplate;
  private IdGenerator mIdGenerator;

  public PersistentUGFeeDao(JdbcTemplate pJdbcTemplate, IdGenerator pIdGenerator) {
    mJdbcTemplate = pJdbcTemplate;
    mIdGenerator = pIdGenerator;
  }

  @Override
  public List<UGFee> getAll() {
    return mJdbcTemplate.query(SELECT_ALL, new FeeRowMapper());
  }

  @Override
  public UGFee get(Long pId) {
    String query = SELECT_ALL + "WHERE ID = ? ";
    return mJdbcTemplate.queryForObject(query, new Object[] {pId}, new FeeRowMapper());
  }

  @Override
  public int update(MutableUGFee pMutable) {
    String query = UPDATE_ALL + "WHERE ID = ? ";
    return mJdbcTemplate.update(query, pMutable.getFeeCategoryId(), pMutable.getSemesterId(), pMutable.getFacultyId(),
        pMutable.getAmount(), pMutable.getId());
  }

  @Override
  public int delete(MutableUGFee pMutable) {
    String query = DELETE_ALL + "WHERE ID = ? ";
    return mJdbcTemplate.update(query, pMutable.getId());
  }

  @Override
  public Long create(MutableUGFee pMutable) {
    Long id = mIdGenerator.getNumericId();
    mJdbcTemplate.update(INSERT_ALL, id, pMutable.getFeeCategoryId(), pMutable.getSemesterId(),
        pMutable.getFacultyId(), pMutable.getAmount());
    return id;
  }

  @Override
  public List<UGFee> getFee(Integer pFacultyId, Integer pSemesterId) {
    String query = SELECT_ALL + "WHERE (FACULTY_ID = ? OR FACULTY_ID IS NULL) AND SEMESTER_ID = ?";
    return mJdbcTemplate.query(query, new Object[] {pFacultyId, pSemesterId}, new FeeRowMapper());
  }

  @Override
  public List<UGFee> getFee(Integer pFacultyId, Integer pSemesterId, List<FeeCategory> pCategories) {
    Set<String> categoryIds = pCategories.stream().map((category) -> category.getId()).collect(Collectors.toSet());
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("categoryIds", categoryIds);
    parameters.addValue("facultyId", pFacultyId);
    parameters.addValue("semesterId", pSemesterId);

    String query = SELECT_ALL + "WHERE (FACULTY_ID = :facultyId OR FACULTY_ID IS NULL) AND SEMESTER_ID = :semesterId"
        + " AND FEE_CATEGORY_ID IN (:categoryIds)";
    NamedParameterJdbcTemplate namedParameterJdbcTemplate =
        new NamedParameterJdbcTemplate(mJdbcTemplate.getDataSource());
    return namedParameterJdbcTemplate.query(query, parameters, new FeeRowMapper());
  }

  @Override
  public List<UGFee> getLatestFee(Integer pFacultyId, Integer pProgramTypeId) {
    String query =
        SELECT_ALL
            + "WHERE (FACULTY_ID = ? OR FACULTY_ID IS NULL) AND SEMESTER_ID = "
            + "(SELECT SEMESTER_ID FROM MST_SEMESTER WHERE MST_SEMESTER.PROGRAM_TYPE = ? AND MST_SEMESTER.START_DATE = "
            + "(SELECT MAX(MST_SEMESTER.START_DATE) FROM MST_SEMESTER WHERE MST_SEMESTER.PROGRAM_TYPE = ? "
            + "AND MST_SEMESTER.SEMESTER_ID IN (SELECT DISTINCT UG_FEE.SEMESTER_ID FROM UG_FEE))";
    return mJdbcTemplate.query(query, new Object[] {pFacultyId, pProgramTypeId, pProgramTypeId}, new FeeRowMapper());
  }

  private class FeeRowMapper implements RowMapper<UGFee> {
    @Override
    public UGFee mapRow(ResultSet rs, int rowNum) throws SQLException {
      MutableUGFee fee = new PersistentUGFee();
      fee.setId(rs.getLong("ID"));
      fee.setFeeCategoryId(rs.getString("FEE_CATEGORY_ID"));
      fee.setSemesterId(rs.getInt("SEMESTER_ID"));
      if(rs.getObject("FACULTY_ID") != null) {
        fee.setFacultyId(rs.getInt("FACULTY_ID"));
      }
      fee.setAmount(new BigDecimal(rs.getDouble("AMOUNT")));
      fee.setLastModified(rs.getString("LAST_MODIFIED"));
      AtomicReference<UGFee> atomicReference = new AtomicReference<>(fee);
      return atomicReference.get();
    }
  }
}
