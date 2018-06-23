package org.ums.decorator;

import org.ums.domain.model.immutable.StudentsExamAttendantInfo;
import org.ums.domain.model.mutable.MutableStudentsExamAttendantInfo;
import org.ums.manager.StudentsExamAttendantInfoManager;

/**
 * Created by Monjur-E-Morshed on 6/9/2018.
 */
public class StudentsExamAttendantInfoDaoDecorator
    extends
    ContentDaoDecorator<StudentsExamAttendantInfo, MutableStudentsExamAttendantInfo, Long, StudentsExamAttendantInfoManager>
    implements StudentsExamAttendantInfoManager {

}
