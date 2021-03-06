package org.ums.persistent.dao.library;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.ums.decorator.library.PublisherDaoDecorator;
import org.ums.decorator.library.SupplierDaoDecorator;
import org.ums.domain.model.immutable.common.Country;
import org.ums.domain.model.immutable.library.Publisher;
import org.ums.domain.model.immutable.library.Supplier;
import org.ums.domain.model.mutable.library.MutablePublisher;
import org.ums.domain.model.mutable.library.MutableSupplier;
import org.ums.generator.IdGenerator;
import org.ums.persistent.model.library.PersistentPublisher;
import org.ums.persistent.model.library.PersistentSupplier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Ifti on 04-Feb-17.
 */
public class PersistentPublisherDao extends PublisherDaoDecorator {
  static String SELECT_ALL = "Select ID,NAME,COUNTRY,CONTACT_PERSON, PHONE, EMAIL,LAST_MODIFIED  FROM MST_PUBLISHER";
  static String SELECT_COUNT = "Select COUNT(ID) FROM MST_PUBLISHER";
  static String UPDATE_ONE =
      "UPDATE MST_PUBLISHER SET NAME = ? ,COUNTRY= ? ,CONTACT_PERSON= ? ,PHONE= ? ,EMAIL= ?, LAST_MODIFIED = "
          + getLastModifiedSql() + " ";
  static String DELETE_ONE = "DELETE FROM MST_PUBLISHER ";

  private JdbcTemplate mJdbcTemplate;
  public IdGenerator mIdGenerator;

  public PersistentPublisherDao(final JdbcTemplate pJdbcTemplate, IdGenerator pIdGenerator) {
    mJdbcTemplate = pJdbcTemplate;
    mIdGenerator = pIdGenerator;
  }

  @Override
  public Publisher get(final Long pId) {
    String query = SELECT_ALL + " Where Id = ?";
    return mJdbcTemplate.queryForObject(query, new Object[] {pId}, new PersistentPublisherDao.PublisherRowMapper());
  }

  @Override
  public List<Publisher> getAll() {
    String query = SELECT_ALL;
    return mJdbcTemplate.query(query, new PersistentPublisherDao.PublisherRowMapper());
  }

  @Override
  public Long create(final MutablePublisher pPublisher) {
    Long id = mIdGenerator.getNumericId();
    pPublisher.setId(id);
    String INSERT_ONE =
        "INSERT INTO MST_PUBLISHER(ID, NAME, COUNTRY, CONTACT_PERSON, PHONE, EMAIL,  LAST_MODIFIED) "
            + "VALUES(?, ?, ?, ?, ?, ?, " + getLastModifiedSql() + ")";

    mJdbcTemplate.update(INSERT_ONE, pPublisher.getId(), pPublisher.getName(), pPublisher.getCountryId(),
        pPublisher.getContactPerson(), pPublisher.getPhoneNumber(), pPublisher.getEmailAddress());

    return pPublisher.getId();
  }

  @Override
  public List<Publisher> getAllForPagination(final Integer pItemPerPage, final Integer pPage,
      final String pWhereClause, final String pOrder) {
    int startIndex = pItemPerPage * pPage - pItemPerPage + 1;
    int endIndex = startIndex + pItemPerPage;
    String query =
        "Select tmp2.*,ind  From (Select ROWNUM ind, tmp1.* From (" + SELECT_ALL + pWhereClause + pOrder
            + ")tmp1 ) tmp2  WHERE ind >=? and ind<=?  ";
    return mJdbcTemplate.query(query, new Object[] {startIndex, endIndex},
        new PersistentPublisherDao.PublisherRowMapper());
  }

  @Override
  public int getTotalForPagination(final String pWhereClause) {
    String query = SELECT_COUNT + pWhereClause;
    return mJdbcTemplate.queryForObject(query, new Object[] {}, Integer.class);
  }

  @Override
  public int update(final MutablePublisher pPublisher) {
    String query = UPDATE_ONE + "WHERE ID = ?";
    return mJdbcTemplate.update(query, pPublisher.getName(), pPublisher.getCountryId(), pPublisher.getContactPerson(),
        pPublisher.getPhoneNumber(), pPublisher.getEmailAddress(), pPublisher.getId());
  }

  @Override
  public int delete(final MutablePublisher pPublisher) {
    String query = DELETE_ONE + "WHERE ID = ?";
    return mJdbcTemplate.update(query, pPublisher.getId());
  }

  class PublisherRowMapper implements RowMapper<Publisher> {
    @Override
    public Publisher mapRow(ResultSet resultSet, int i) throws SQLException {
      PersistentPublisher publisher = new PersistentPublisher();
      publisher.setId(resultSet.getLong("ID"));
      publisher.setName(resultSet.getString("NAME"));
      publisher.setCountryId(resultSet.getInt("COUNTRY"));
      publisher.setContactPerson(resultSet.getString("CONTACT_PERSON"));
      publisher.setPhoneNumber(resultSet.getString("PHONE"));
      publisher.setEmailAddress(resultSet.getString("EMAIL"));
      publisher.setLastModified(resultSet.getString("LAST_MODIFIED"));

      AtomicReference<Publisher> atomicReference = new AtomicReference<>(publisher);
      return atomicReference.get();
    }
  }

}
