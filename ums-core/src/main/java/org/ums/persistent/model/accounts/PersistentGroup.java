package org.ums.persistent.model.accounts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.springframework.context.ApplicationContext;
import org.ums.context.AppContext;
import org.ums.domain.model.mutable.accounts.MutableGroup;
import org.ums.manager.accounts.GroupManager;
import org.ums.serializer.UmsDateSerializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Monjur-E-Morshed on 20-Dec-17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersistentGroup implements MutableGroup {

  @JsonIgnore
  private static GroupManager sGroupManager;

  static {
    ApplicationContext applicationContext = AppContext.getApplicationContext();
    sGroupManager = applicationContext.getBean("groupManager", GroupManager.class);
  }

  private Long mId;
  @JsonIgnore
  private String mStringId;
  private String mCompanyCode;
  private String mGroupCode;
  private String mGroupName;
  private String mMainGroup;
  private String mReservedFlag;
  private String mFlag;
  private String mDisplayCode;
  private BigDecimal mTaxLimit;
  private BigDecimal mTdsPercent;
  private String mDefaultComp;
  private String mStatusFlag;
  private String mStatusUpFlag;
  @JsonIgnore
  private Date mModifiedDate;
  private String mModifiedBy;
  private String mLastModified;
  @JsonIgnore
  private PersistentGroup mMainGroupObject;
  @JsonIgnore
  private Boolean mFlagBoolValue;

  public PersistentGroup() {}

  public PersistentGroup(final PersistentGroup pPersistentGroup) {
    mId = pPersistentGroup.getId();
    mCompanyCode = pPersistentGroup.getCompCode();
    mGroupCode = pPersistentGroup.getGroupCode();
    mGroupName = pPersistentGroup.getGroupName();
    mMainGroup = pPersistentGroup.getMainGroup();
    mReservedFlag = pPersistentGroup.getReservedFlag();
    mFlag = pPersistentGroup.getFlag();
    mTaxLimit = pPersistentGroup.getTaxLimit();
    mTdsPercent = pPersistentGroup.getTdsPercent();
    mDefaultComp = pPersistentGroup.getDefaultComp();
    mStatusFlag = pPersistentGroup.getStatFlag();
    mStatusUpFlag = pPersistentGroup.getStatUpFlag();
    mModifiedDate = pPersistentGroup.getModifiedDate();
    mModifiedBy = pPersistentGroup.getModifiedBy();
    mDisplayCode = pPersistentGroup.getDisplayCode();
  }

  @Override
  public String getDisplayCode() {
    return mDisplayCode;
  }

  @Override
  public void setDisplayCode(String pDisplayCode) {
    mDisplayCode = pDisplayCode;
  }

  public PersistentGroup getMainGroupObject() {
    return mMainGroupObject;
  }

  @JsonIgnore
  public void setMainGroupObject(PersistentGroup pMainGroupObject) {
    mMainGroupObject = pMainGroupObject;
  }

  public Boolean getFlagBoolValue() {
    return mFlagBoolValue;
  }

  public void setFlagBoolValue(Boolean pFlagBoolValue) {
    mFlagBoolValue = pFlagBoolValue;
  }

  @Override
  @JsonIgnore
  public void setStringId(Long pId) {
    mStringId = pId.toString();
  }

  @Override
  public String getStringId() {
    return mStringId;
  }

  @Override
  public void setCompCode(String pCompanyCode) {
    mCompanyCode = pCompanyCode;
  }

  @Override
  public void setGroupCode(String pGroupCode) {
    mGroupCode = pGroupCode;
  }

  @Override
  public void setGroupName(String pGroupName) {
    mGroupName = pGroupName;
  }

  @Override
  public void setMainGroup(String pMainGroup) {
    mMainGroup = pMainGroup;
  }

  @Override
  public void setReservedFlag(String pReservedFlag) {
    mReservedFlag = pReservedFlag;
  }

  @Override
  public void setFlag(String pFlag) {
    mFlag = pFlag;
  }

  @Override
  public void setTaxLimit(BigDecimal pTexLimit) {
    mTaxLimit = pTexLimit;
  }

  @Override
  public void setTdsPercent(BigDecimal pTdsPercent) {
    mTdsPercent = pTdsPercent;
  }

  @Override
  public void setDefaultComp(String pDefaultCompCode) {
    mDefaultComp = pDefaultCompCode;
  }

  @Override
  public void setStatFlag(String pStatusFlag) {
    mStatusFlag = pStatusFlag;
  }

  @Override
  public void setStatUpFlag(String pStatusUpFlag) {
    mStatusUpFlag = pStatusUpFlag;
  }

  @Override
  @JsonIgnore
  public void setModifiedDate(Date pLastModifiedDate) {
    mModifiedDate = pLastModifiedDate;
  }

  @Override
  public void setModifiedBy(String pAuthenticationCode) {
    mModifiedBy = pAuthenticationCode;
  }

  @Override
  public Long create() {
    return sGroupManager.create(this);
  }

  @Override
  public void update() {
    sGroupManager.update(this);
  }

  @Override
  public void delete() {
    sGroupManager.delete(this);
  }

  @Override
  @JsonDeserialize(as = Long.class)
  public void setId(Long pId) {
    mId = pId;
  }

  @Override
  public String getCompCode() {
    return mCompanyCode;
  }

  @Override
  public String getGroupCode() {
    return mGroupCode;
  }

  @Override
  public String getGroupName() {
    return mGroupName;
  }

  @Override
  public String getMainGroup() {
    return mMainGroup;
  }

  @Override
  public String getReservedFlag() {
    return mReservedFlag;
  }

  @Override
  public String getFlag() {
    return mFlag;
  }

  @Override
  public BigDecimal getTaxLimit() {
    return mTaxLimit;
  }

  @Override
  public BigDecimal getTdsPercent() {
    return mTdsPercent;
  }

  @Override
  public String getDefaultComp() {
    return mDefaultComp;
  }

  @Override
  public String getStatFlag() {
    return mStatusFlag;
  }

  @Override
  public String getStatUpFlag() {
    return mStatusUpFlag;
  }

  @Override
  @JsonSerialize(using = UmsDateSerializer.class)
  public Date getModifiedDate() {
    return mModifiedDate;
  }

  @Override
  public String getModifiedBy() {
    return mModifiedBy;
  }

  @Override
  public MutableGroup edit() {
    return new PersistentGroup(this);
  }

  @Override
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  public Long getId() {
    return mId;
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
