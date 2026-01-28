package com.example.controller;

import com.example.entity.Course;
import com.example.entity.ExamCommittee;
import com.example.entity.ThirdExamination;
import com.example.entity.User;
import com.example.service.CourseService;
import com.example.service.ExamCommitteeService;
import com.example.service.ThirdExaminationService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @PostMapping("/api/assign-thirdexaminer")
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

            Long scriptsCount = (long) studentsId.size();
            if(scriptsCount == 0){
                map.put("message", "Please enter one or more student IDs separated by comma!");
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

            if(!course.getSemester().getSemesterId().equals(examCommittee.getSemester().getSemesterId()) || !course.getSession().equals(examCommittee.getSession())){
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

}
