package org.ums.persistent.model;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.ums.context.AppContext;
import org.ums.domain.model.mutable.MutableClassRoom;
import org.ums.domain.model.immutable.Department;
import org.ums.enums.ClassRoomType;
import org.ums.manager.ClassRoomManager;
import org.ums.manager.DepartmentManager;

public class PersistentClassRoom implements MutableClassRoom {
  private static ClassRoomManager sClassRoomManager;
  private static DepartmentManager sDepartmentManager;

  static {
    ApplicationContext applicationContext = AppContext.getApplicationContext();
    sClassRoomManager = applicationContext.getBean("classRoomManager", ClassRoomManager.class);
    sDepartmentManager = applicationContext.getBean("departmentManager", DepartmentManager.class);
  }

  private Long mId;
  private String mRoomNo;
  private String mDescription;
  private int mTotalRow;
  private int mTotalColumn;
  private int mCapacity;
  private String mDeptId;
  private ClassRoomType mRoomType;
  private Department mDept;
  private boolean mExamSeatPlan;
  private String mLastModified;

  public PersistentClassRoom() {}

  public PersistentClassRoom(final PersistentClassRoom pOriginal) {
    mId = pOriginal.getId();
    mRoomNo = pOriginal.getRoomNo();
    mDescription = pOriginal.getDescription();
    mTotalRow = pOriginal.getTotalRow();
    mTotalColumn = pOriginal.getTotalColumn();
    mCapacity = pOriginal.getCapacity();
    mRoomType = pOriginal.getRoomType();
    mDept = pOriginal.getDept();
    mDeptId = pOriginal.getDeptId();
    mExamSeatPlan = pOriginal.isExamSeatPlan();
  }

  @Override
  public Long getId() {
    return mId;
  }

  @Override
  public void setId(Long pId) {
    mId = pId;
  }

  @Override
  public void setRoomNo(String pRoomNo) {
    mRoomNo = pRoomNo;
  }

  @Override
  public void setDescription(String pDescription) {
    mDescription = pDescription;
  }

  @Override
  public void setTotalRow(int pTotalRow) {
    mTotalRow = pTotalRow;
  }

  @Override
  public void setTotalColumn(int pTotalColumn) {
    mTotalColumn = pTotalColumn;
  }

  @Override
  public void setCapacity(int pTotalCapacity) {
    mCapacity = pTotalCapacity;
  }

  @Override
  public void setRoomType(ClassRoomType pRoomType) {
    mRoomType = pRoomType;
  }

  @Override
  public void setDeptId(String pDeptId) {
    mDeptId = pDeptId;
  }

  @Override
  public void setDept(Department pDept) {
    mDept = pDept;
  }

  @Override
  public void setExamSeatPlan(boolean pExamSeatPlan) {
    mExamSeatPlan = pExamSeatPlan;
  }

  @Override
  public String getRoomNo() {
    return mRoomNo;
  }

  @Override
  public String getDescription() {
    return mDescription;
  }

  @Override
  public int getTotalRow() {
    return mTotalRow;
  }

  @Override
  public int getTotalColumn() {
    return mTotalColumn;
  }

  @Override
  public int getCapacity() {
    return mTotalColumn * mTotalRow;
  }

  @Override
  public ClassRoomType getRoomType() {
    return mRoomType;
  }

  @Override
  public String getDeptId() {
    return mDeptId;
  }

  @Override
  public Department getDept() {
    return mDept == null && !StringUtils.isEmpty(mDeptId) ? sDepartmentManager.get(mDeptId) : mDept;
  }

  @Override
  public boolean isExamSeatPlan() {
    return mExamSeatPlan;
  }

  @Override
  public String getLastModified() {
    return mLastModified;
  }

  @Override
  public void setLastModified(String pLastModified) {
    mLastModified = pLastModified;
  }

  public void delete() {
    sClassRoomManager.delete(this);
  }

  @Override
  public Long create() {
    return sClassRoomManager.create(this);
  }

  @Override
  public void update() {
    sClassRoomManager.update(this);
  }

  public MutableClassRoom edit() {
    return new PersistentClassRoom(this);
  }
}
