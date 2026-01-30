package com.example.controller;

import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.entity.User;
import com.example.entity.ThesisProjectSupervision;
import com.example.service.CourseService;
import com.example.service.ExamCommitteeService;
import com.example.service.ThesisProjectSupervisionService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;


@Controller
public class ThesisProjectSupervisionController {

    private final ExamCommitteeService examCommitteeService;
    private final CourseService courseService;
    private final UserService userService;
    private final ThesisProjectSupervisionService thesisProjectSupervisionService;

    public ThesisProjectSupervisionController(ExamCommitteeService examCommitteeService, CourseService courseService, UserService userService, ThesisProjectSupervisionService thesisProjectSupervisionService) {
        this.examCommitteeService = examCommitteeService;
        this.courseService = courseService;
        this.userService = userService;
        this.thesisProjectSupervisionService = thesisProjectSupervisionService;
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
//            if(!examCommittee.getStudentCount().equals(totalStudent)){
//
//            }

            List<Map<String, Object>> rows = (List<Map<String, Object>>) payload.get("superVisionData");
            List<ThesisProjectSupervision.Internal> supervisorsList = new ArrayList<>();
            for(Map<String, Object> row : rows) {
                Long teacherId = Long.valueOf(row.get("teacherId").toString());
                Long groupCount = Long.valueOf(row.get("numberOfGroups").toString());
                Long studentCount = Long.valueOf(row.get("numberOfStudents").toString());
                totalStudent += studentCount;

                User supervisor = userService.getUserById(teacherId);
                supervisorsList.add(new ThesisProjectSupervision.Internal(supervisor, groupCount, studentCount));

                System.out.println("Processing Teacher: " + supervisor.getName() + " GroupCount: " + groupCount + " StudentCount: " + studentCount);
            }
            User externalTeacher = examCommittee.getExternalMember1();

            ThesisProjectSupervision thesisProjectSupervision = new ThesisProjectSupervision();
            thesisProjectSupervision.setCourse(course);
            thesisProjectSupervision.setExamCommittee(examCommittee);
            thesisProjectSupervision.setExternalTeacher(externalTeacher);
            thesisProjectSupervision.setInternalTeachers(supervisorsList);
            thesisProjectSupervisionService.save(thesisProjectSupervision);

            map.put("message", "Success : Supervisors has been assigned successfully for selected course - " + course.getCourseName() + "!\nTotal Students: " + totalStudent);
            return new ResponseEntity<>(map, HttpStatus.OK);

        }catch(Exception e){
            map.put("message","Internal Error: " + e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
