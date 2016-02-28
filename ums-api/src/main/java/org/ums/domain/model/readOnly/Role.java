package org.ums.domain.model.readOnly;

import org.ums.domain.model.common.EditType;
import org.ums.domain.model.common.Identifier;
import org.ums.domain.model.mutable.MutableRole;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface Role extends Serializable, Identifier<Integer>, EditType<MutableRole> {
  String getName();

  Set<String> getPermissions();
}
