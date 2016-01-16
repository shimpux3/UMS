package org.ums.academic.dao;


import org.ums.manager.ContentManager;

import java.util.List;

public class ContentDaoDecorator<R, M, I, C extends ContentManager<R, M, I>> implements ContentManager<R, M, I> {
  private C mManager;

  protected static String getLastModifiedSql() {
    return "to_char(sysdate,'YYYYMMDDHHMISS')";
  }

  public C getManager() {
    return mManager;
  }

  public void setManager(C pManager) {
    mManager = pManager;
  }

  public List<R> getAll() throws Exception {
    return getManager().getAll();
  }

  public R get(final I pId) throws Exception {
    return getManager().get(pId);
  }

  @Override
  public R validate(R pReadonly) throws Exception {
    return pReadonly;
  }

  public void update(final M pMutable) throws Exception {
    getManager().update(pMutable);
  }

  public void delete(final M pMutable) throws Exception {
    getManager().delete(pMutable);
  }

  public void create(final M pMutable) throws Exception {
    getManager().create(pMutable);
  }
}
