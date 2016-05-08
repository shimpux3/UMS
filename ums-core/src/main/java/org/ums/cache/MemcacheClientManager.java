package org.ums.cache;


import org.ums.domain.model.common.LastModifier;
import org.ums.manager.CacheManager;

import java.util.HashMap;
import java.util.Map;

public class MemcacheClientManager<R extends LastModifier> implements CacheManager<R> {
  private Map<String, R> mCache;
  private Map<String, String> mLastModified;

  public MemcacheClientManager() {
    mCache = new HashMap<>();
    mLastModified = new HashMap<>();
  }

  @Override
  public void put(String pCacheId, R pReadonly) {
    mCache.put(pCacheId, pReadonly);
    mLastModified.put(pCacheId, pReadonly.getLastModified());
  }

  @Override
  public R get(String pCacheId) throws Exception {
    return mCache.get(pCacheId);
  }

  @Override
  public String getLastModified(final String pCacheId) throws Exception {
    return mLastModified.get(pCacheId);
  }

  @Override
  public void invalidate(String pCacheId) {
    mCache.remove(pCacheId);
    mLastModified.remove(pCacheId);
  }

  @Override
  public void flushAll() throws Exception {
    mCache.clear();
    mLastModified.clear();
  }
}
