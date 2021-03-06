package org.ums.persistent.model;

import org.springframework.context.ApplicationContext;
import org.ums.context.AppContext;
import org.ums.domain.model.mutable.MutableProgramType;
import org.ums.domain.model.immutable.ProgramType;
import org.ums.manager.ProgramTypeManager;

public class PersistentProgramType implements MutableProgramType {
  private static ProgramTypeManager sManager;

  static {
    ApplicationContext applicationContext = AppContext.getApplicationContext();
    sManager = applicationContext.getBean("programTypeManager", ProgramTypeManager.class);
  }

  int mId;
  String mName;
  String mLastModified;

  public PersistentProgramType() {

  }

  public PersistentProgramType(final ProgramType pProgramType) {
    mId = pProgramType.getId();
    mName = pProgramType.getName();
  }

  public Integer getId() {
    return mId;
  }

  public void setId(final Integer pId) {
    mId = pId;
  }

  public String getName() {
    return mName;
  }

  public void setName(final String pName) {
    mName = pName;
  }

  public void delete() {
    sManager.delete(this);
  }

  @Override
  public Integer create() {
    return sManager.create(this);
  }

  @Override
  public void update() {
    sManager.update(this);
  }

  public MutableProgramType edit() {
    return new PersistentProgramType(this);
  }

  @Override
  public String getLastModified() {
    return mLastModified;
  }

  @Override
  public void setLastModified(String pLastModified) {
    mLastModified = pLastModified;
  }
}
