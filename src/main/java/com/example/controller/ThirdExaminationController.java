package com.example.controller;

import com.example.entity.*;
import com.example.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ThirdExaminationController {
    @Autowired
    private ThirdExaminationService thirdExaminationService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;
    @Autowired
    private ExamCommitteeService examCommitteeService;
    @Autowired
    private CommitteeActivityService committeeActivityService;

    @PostMapping("/api/assign/third-examiner")
    @ResponseBody
    public ResponseEntity<Object> assignThirdExaminer(@RequestBody Map<String, String> payload, HttpSession session) {
        User user = (User)session.getAttribute("user");
        Map<Object, Object> map = new HashMap<>();
        if(user == null){
            map.put("message", "Unauthorized: Please login first");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }
        try{
            Long committeeId = Long.parseLong(payload.get("committeeId"));
            Long examinerId = Long.parseLong(payload.get("examinerId"));
            Long courseId = Long.parseLong(payload.get("courseId"));

            String rawStudentsId = payload.getOrDefault("rawStudentsId", "");
            List<String> studentsId = Arrays.stream(rawStudentsId.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            Set<String> studentIdSet = new HashSet<>();
            for(String studentId : studentsId){
                if(!studentIdSet.add(studentId)){
                    map.put("message", "Bad Request: Duplicate student ID found: " + studentId);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
                }
                if(!UtilityService.validateStudentId(studentId)){
                    map.put("message", "Bad Request: Invalid Student ID : " + studentId + ". Please follow the correct format(e.g: IT22016)");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
                }
            }

            Long scriptsCount = (long) studentsId.size();
            if(scriptsCount == 0){
                map.put("message", "Bad Request: Please enter one or more student IDs separated by comma!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }

            Course course = courseService.findById(courseId);
            User examiner = userService.getUserById(examinerId);
            ExamCommittee examCommittee = examCommitteeService.findCommitteeByCommitteeId(committeeId);

            if(examiner == null || course == null || examCommittee == null){
                map.put("message", "Bad Request: Invalid parameters");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }
            if(!examCommitteeService.checkEditPermission(user, examCommittee)){
                map.put("message", "Forbidden: Only committee chairman and admins have permission to perform this action.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
            }
            if(examCommittee.isResultPublished()){
                map.put("message", "Conflict: Result has been already published of this course and related exam committee!\nYou can't perform this action now.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(map);
            }
            System.out.println("Course type: " + course.getCourseType());
            if(!course.getCourseType().equals("Theory")){
                map.put("message", "Bad Request: Only theory courses can be assigned for third examination.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }

            if(examCommitteeService.isNotCommitteeCourse(examCommittee, course)){
                map.put("message", "Bad Request: This course does not belong to the committee you provided.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }

            if(course.getCourseTeacher() == null || course.getInternalQuesSetterEvaluator() == null || course.getExternalQuesSetterEvaluator() == null){
                map.put("message", "Bad Request: Please assign course teacher, question setter and primary examiners at first.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }

            if(course.getCourseTeacher().getUserId().equals(examiner.getUserId()) || examCommitteeService.isMember(examiner, examCommittee)){
                map.put("message", "Forbidden: Course teacher or committee member can not be the third examiner.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
            }

            if(thirdExaminationService.saveThirdExamination(examCommittee, course, examiner, rawStudentsId, studentsId, scriptsCount)){
                CommitteeActivity activity = new CommitteeActivity();
                activity.setExamCommittee(examCommittee);
                activity.setPerformedBy(user);
                activity.setPriority(10);
                activity.setActionTitle("Assigning Third Examiner");
                activity.setDetails("Third examiner has been assigned successfully for " + course.getCourseName() + " course");
                activity.setTimestamp(LocalDate.now());
                committeeActivityService.saveCommitteeActivity(activity);

                map.put("message", "Success: Third examiner has been assigned successfully for selected course.");
                return ResponseEntity.status(HttpStatus.OK).body(map);
            }
            else{
                map.put("message", "Error: Third examination could not be saved or This examiner is already assigned to this course for this committee.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
            }


        }catch(Exception e){
            map.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }

    @GetMapping("/details/third-examination/{thirdExaminationId}")
    @ResponseBody
    public ResponseEntity<Object> getStudentIds(@PathVariable Long thirdExaminationId, HttpSession session){
        User user = (User) session.getAttribute("user");
        Map<String, Object> map = new HashMap<>();
        if(user == null){
            map.put("message", "Unauthorized: You are not logged in!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
        }

       try{
           String studentsId = thirdExaminationService.getStudentsIdById(thirdExaminationId);
           map.put("studentsId", studentsId);
           map.put("message", "Successfully fetched students ID");
           return ResponseEntity.status(HttpStatus.OK).body(map);
       }catch(Exception e){
           map.put("message", e.getMessage());
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
       }
    }

}
