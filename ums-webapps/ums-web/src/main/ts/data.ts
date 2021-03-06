(function ($) {
    $.fn.Data = function () {
        return {};
    };
    var $this: any = $.fn.Data;

    $.fn.Data.pages = {
        '/index': {title: 'Dashboard', 'breadcrumb': ['Dashboard']},
        '/userHome': {title: 'User Home', 'breadcrumb': ['User Home']},
        '/createSemester': {title: 'Create New Semester', 'breadcrumb': ['Semester']},
        '/showSemesterList': {title: 'Semester List', 'breadcrumb': ['Semester List']},
        '/createSyllabus': {title: 'Create New Syllabus', 'breadcrumb': ['Syllabus']},
        '/showSyllabusList': {title: 'Syllabus List', 'breadcrumb': ['Syllabus List']},
        '/viewFullSyllabus': {title: 'Syllabus', 'breadcrumb': ['Syllabus']},
        '/createStudent': {title: 'Create New Student', 'breadcrumb': ['Student']},
        '/changePassword': {title: 'Change Password', 'breadcrumb': ['Password']},
        '/createUgCourse': {title: 'Create UG Course', 'breadcrumb': ['Course']},
        '/createPgCourse': {title: 'Create PG Course', 'breadcrumb': ['Course']},
        '/semesterSyllabusMap': {title: 'Semester Syllabus Map', 'breadcrumb': ['Semester-Syllabus Map']},
        '/singleUserPassword': {title: 'User Management', 'breadcrumb': ['User Management', 'Single']},
        '/bulkUserPassword': {title: 'User Management ', 'breadcrumb': ['User Management', 'Bulk']},
        '/courseTeacher': {title: 'Course Teacher Assign ', 'breadcrumb': ['Course Teacher']},
        '/classRoutine': {title: 'Class Routine Upload ', 'breadcrumb': ['Class Routine']},
        '/teachersRoutine': {title: 'Class Routine', 'breadcrumb': ['Class Routine']},
        '/studentProfile': {title: 'Student Profile ', 'breadcrumb': ['Student Profile']},
        '/classRoomInfo': {title: 'Class Room Information ', 'breadcrumb': ['Class Room Information']},
        '/examRoutine': {title: 'Exam Routine ', 'breadcrumb': ['Exam Routine']},
        '/optionalCoursesApplication': {title: 'Optional Courses ', 'breadcrumb': ['Optional Course Application']},
        '/optionalCoursesOffer': {title: 'Optional Courses ', 'breadcrumb': ['Optional Course Settings']},
        '/gradeSheetSelectionTeacher/T': {
            title: 'Grade Sheet View/Selection',
            'breadcrumb': ['Grade Sheet View/Selection'],
            'manualName': 'UserGuide-OnlineGradeSubmission',
            'navId': '2029'
        },
        '/applicationCCI': {
            title: 'Application for Clearance/Improvement/Carry',
            'breadcrumb': ['Application for Clearance/Improvement/Carry']
        },
        '/examSeatPlan': {title: 'Exam Seat Plan ', 'breadcrumb': ['Seat Plan ']},
        '/seatPlanRegular': {title: 'Exam Seat Plan - Regular ', 'breadcrumb': ['Seat Plan', 'Regular']},
        '/seatPlanCCI': {title: 'Exam Seat Plan - CCI ', 'breadcrumb': ['Seat Plan', 'CCI']},
        '/publishSeatPlan': {title: 'Publish Seat Plan', 'breadcrumb': ['Seat Plan', 'Publish']},
        '/examiner': {title: 'Assign Preparer-Scrutinizer', 'breadcrumb': ['Preparer-Scrutinizer']},
        '/courseMaterial': {title: 'Course Materials', 'breadcrumb': ['Course Materials']},
        '/studentCourseMaterial': {title: 'Course Materials', 'breadcrumb': ['Course Materials']},
        '/gradeSubmissionDeadLine': {title: 'Grade Submission Deadline', 'breadcrumb': ['Grade Submission Deadline']},
        '/studentAdviser': {title: 'Students\' Advisor', 'breadcrumb': ['Students\' Advisor']},
        '/sectionAssignment': {title: 'Students\' Section', 'breadcrumb': ['Students\' Section']},
        '/advisingStudents': {title: 'Advising Students', 'breadcrumb': ['Advising Students']},
        '/resultProcessing': {title: 'Result Processing', 'breadcrumb': ['Result Processing']},
        '/classAttendance':
            {
                title: 'Attendance Sheet',
                'breadcrumb': ['Attendance Sheet'],
                'navId': '2069',
                'manualName': 'UserGuide-ClassAttendance'
            },
        '/roomBasedRoutine': {title: 'Room-wise Routine', 'breadcrumb': ['Routine', 'Room-wise']},
        '/marksSubmissionStat': {title: 'Marks Submission Statistics', 'breadcrumb': ['Marks Submission Stat.']},
        '/uploadMeritList': {title: 'Upload Admission Merit List', 'breadcrumb': ['Admission Merit List']},
        '/userGuide': {title: 'User Guide', 'breadcrumb': ['User Guide']},
        '/inbox': {title: 'MailBox - Inbox', 'breadcrumb': ['MailBox', 'Inbox']},
        '/composeMail': {title: 'MailBox - Compose Mail', 'breadcrumb': ['MailBox', 'Compose']},
        '/viewMail': {title: 'MailBox - View Mail', 'breadcrumb': ['MailBox', 'View']},
        '/uploadTaletalkData': {title: 'Upload Taletalk Data', 'breadcrumb': ['Admission Taletalk Student List']},
        '/certificateVerification': {
            title: 'Certificate Verification',
            'breadcrumb': ['Admission Certificate Verification']
        },
        '/admissionTotalSeat': {
            title: 'Assign program-wise total seat',
            'breadcrumb': ['Admission Total Seat Assignment']
        },
        '/admissionStudentId': {title: 'Admitted Student Id', 'breadcrumb': ['Admitted Student Information']},
        '/leaveApplication': {title: 'Leave Application', 'breadcrumb': ['Leave Application ']},
        '/leaveApproval': {title: 'Leave Application Approval', 'breadcrumb': ['Leave Application Approval']},
        '/expelledInformation': {title: 'Expelled Students Information', 'breadcrumb': ['Expelled Students Information']},
        '/viewExpelledInformation': {title: 'View Expelled Information', 'breadcrumb': ['Expelled Students Information']},
        '/dailyExamAttendanceReport': {title: 'Daily Exam Attendance Report', 'breadcrumb': ['Daily Exam Attendance Report']},
        '/absentLateComingInfo': {title: 'Absent/Late Coming Info', 'breadcrumb': ['Absent/Late Coming']},
        '/absentLateComingView': {title: 'Absent/Late Coming View', 'breadcrumb': ['Absent/Late Coming']},
        '/dailyExamReport': {title: 'Daily Examination Report', 'breadcrumb': ['Daily Examination Report']},
        '/questionCorrectionInfo': {title: 'Question Correction Info', 'breadcrumb': ['Question Correction Info']},
        '/employeeExamAttendance': {title: 'Employee Exam Attendance', 'breadcrumb': ['Employee Exam Attendance']},
        '/optCourseOffer': {title: 'Optional Course List', 'breadcrumb': ['Offer Optional Course']},
        '/optStudentCourseSelection': {title: 'Optional Course Selection', 'breadcrumb': ['Optional Course Selection']},
        '/publicHolidays': {title: 'Public Holidays', 'breadcrumb': ['Public Holidays  ']},
        '/search': {title: 'Library:Search', 'breadcrumb': ['Search']},
        '/employeeProfile': {title: 'Profile', 'breadcrumb': ['Profile']},
        '/personal': {title: 'Profile', 'breadcrumb': ['Profile']},
        '/academic': {title: 'Profile', 'breadcrumb': ['Profile']},
        '/publication': {title: 'Profile', 'breadcrumb': ['Profile']},
        '/award': {title: 'Profile', 'breadcrumb': ['Profile']},
        '/training': {title: 'Profile', 'breadcrumb': ['Profile']},
        '/experience': {title: 'Profile', 'breadcrumb': ['Profile']},
        '/additional': {title: 'Profile', 'breadcrumb': ['Profile']},
        '/service': {title: 'Profile', 'breadcrumb': ['Profile']},
        '/classRoutineConfig': {title: 'Class Routine Configuration', 'breadcrumb': ['Class Routine Configuration']},
        '/employeeInformation': {title: 'Search', 'breadcrumb': ['Search']}
    };


    $.fn.Data.get = function (id) {
        if (id && $this.pages[id]) {
            return $this.pages[id];
        }
    };
})(jQuery);

