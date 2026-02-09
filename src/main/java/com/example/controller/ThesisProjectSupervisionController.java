package com.example.controller;

import com.example.entity.*;
import com.example.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.*;


@Controller
public class ThesisProjectSupervisionController {

    private final ExamCommitteeService examCommitteeService;
    private final CourseService courseService;
    private final UserService userService;
    private final ThesisProjectSupervisionService thesisProjectSupervisionService;
    private final CommitteeActivityService committeeActivityService;

    public ThesisProjectSupervisionController(ExamCommitteeService examCommitteeService, CourseService courseService, UserService userService, ThesisProjectSupervisionService thesisProjectSupervisionService, CommitteeActivityService committeeActivityService) {
        this.examCommitteeService = examCommitteeService;
        this.courseService = courseService;
        this.userService = userService;
        this.thesisProjectSupervisionService = thesisProjectSupervisionService;
        this.committeeActivityService = committeeActivityService;
    }

    @PostMapping("/api/thesis-project/assign-supervisors")
    @ResponseBody
    public ResponseEntity<Object> assignSupervisors(@RequestBody Map<String,Object> payload, HttpSession session){
        User user = (User)session.getAttribute("user");
        Map<String, String> map = new HashMap<>();
        if(user == null){
            map.put("message","Unauthorized: Please login first");
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        try{
            Long committeeId = Long.valueOf(payload.get("committeeId").toString());
            Long courseId = Long.valueOf(payload.get("courseId").toString());

            ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(committeeId);
            Course course = courseService.findById(courseId);
            if(examCommittee == null || course == null){
                map.put("message","Bad Request : Course or Exam Committee not found");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }

            if(!examCommitteeService.checkEditPermission(user, examCommittee)){
                map.put("message","Forbidden : Only chairman and admins have permission to modify anything in this exam committee");
                return new ResponseEntity<>(map, HttpStatus.FORBIDDEN);
            }
            if(examCommitteeService.isNotCommitteeCourse(examCommittee, course)){
                map.put("message", "Mismatch : The provided course does not belong to the provided exam committee.!");
                return new ResponseEntity<>(map, HttpStatus.CONFLICT);
            }

            if(examCommittee.isResultPublished()){
                map.put("message", "Conflict: Result of this exam committee has already published!\nYou can't re-assign supervisors right now.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
            }

            if(thesisProjectSupervisionService.existsByCourseAndExamCommittee(course, examCommittee)){
                map.put("message", "Bad Request : Supervisors have already been assigned for this course - " + course.getCourseName());
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }

            long totalStudent = 0L;

            List<Map<String, Object>> rows = (List<Map<String, Object>>) payload.get("superVisionData");
            List<ThesisProjectSupervision.Internal> supervisorsList = new ArrayList<>();
            Set<Long> teacherIds = new HashSet<>();
            for(Map<String, Object> row : rows) {
                Long teacherId = Long.valueOf(row.get("teacherId").toString());
                Long groupCount = Long.valueOf(row.get("numberOfGroups").toString());
                Long studentCount = Long.valueOf(row.get("numberOfStudents").toString());
                totalStudent += studentCount;

                if(studentCount <= 0 || groupCount <= 0){
                    map.put("message", "Bad Request : Student/Group count should be a positive integer");
                    return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
                }

                User supervisor = userService.getUserById(teacherId);
                supervisorsList.add(new ThesisProjectSupervision.Internal(supervisor, groupCount, studentCount));

                System.out.println("Processing Teacher: " + supervisor.getName() + " GroupCount: " + groupCount + " StudentCount: " + studentCount);
                if(!teacherIds.add(teacherId)){
                    map.put("message", "Bad Request : Duplicate teacher entry found: " + supervisor.getName());
                    return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
                }
            }

            if(examCommittee.getStudentCount() > totalStudent){
                map.put("message", "Bad Request: Total registered students of this examination committee is " + examCommittee.getStudentCount() + ", but you are trying to assign supervisors for " + totalStudent + " students.");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }

            User externalTeacher = examCommittee.getExternalMember1();

            ThesisProjectSupervision thesisProjectSupervision = new ThesisProjectSupervision();
            thesisProjectSupervision.setCourse(course);
            thesisProjectSupervision.setExamCommittee(examCommittee);
            thesisProjectSupervision.setExternalTeacher(externalTeacher);
            thesisProjectSupervision.setInternalTeachers(supervisorsList);
            thesisProjectSupervisionService.save(thesisProjectSupervision);

            CommitteeActivity activity = new CommitteeActivity();
            activity.setExamCommittee(examCommittee);
            activity.setPerformedBy(user);
            activity.setTimestamp(LocalDate.now());
            activity.setPriority(10);
            activity.setActionTitle("Assigning Supervisors");
            activity.setDetails("Supervisors has been assigned successfully for " + course.getCourseName() + " course");
            committeeActivityService.saveCommitteeActivity(activity);

            map.put("message", "Success : Supervisors has been assigned successfully for selected course - " + course.getCourseName() + "!\nTotal Students: " + totalStudent);
            return new ResponseEntity<>(map, HttpStatus.OK);

        }catch(Exception e){
            map.put("message","Internal Error: " + e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
