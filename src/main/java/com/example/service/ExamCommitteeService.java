package com.example.service;

import com.example.entity.*;
import com.example.repository.CourseRepository;
import com.example.repository.ExamCommitteeRepository;
import com.example.repository.ThirdExaminationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class ExamCommitteeService {
    private static final Logger log = LoggerFactory.getLogger(ExamCommitteeService.class);

    @Autowired
    private ExamCommitteeRepository examCommitteeRepository;
    @Autowired
    private GratuityBillService gratuityBillService;
    @Autowired
    private BillRateService billRateService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ThirdExaminationRepository thirdExaminationRepository;


    public List<ExamCommittee> findAllBySemesterId(Long semesterId){
        return examCommitteeRepository.findBySemester_SemesterId(semesterId);
    }

    public void saveCommittee(ExamCommittee examCommittee){
        examCommitteeRepository.save(examCommittee);
    }

    public ExamCommittee findCommitteeByCommitteeId(Long committeeId){
        return examCommitteeRepository.findBycommitteeId(committeeId);
    }

    public ExamCommittee findCommitteeBySemesterAndSession(Semester semester, String session){
        return examCommitteeRepository.findBySemesterAndSession(semester, session);
    }

    @Transactional
    public ExamCommittee updateStatus(Long id, boolean isComplete) {
        return examCommitteeRepository.findById(id)
                .map(committee -> {
                    committee.setIsCompleted(isComplete);
                    return examCommitteeRepository.save(committee);
                })
                .orElseThrow(() -> new RuntimeException("Committee not found with id " + id));
    }

    public long getTotalCommitteesAsChairman(User user){
        return examCommitteeRepository.countExamCommitteeByChairman(user);
    }

    public long getTotalCommitteesAsInternalMember(User user){
        return examCommitteeRepository.countExamCommitteeByInternalMember1OrInternalMember2(user, user);
    }

    public long getTotalCommitteesAsExternalMember(User user){
        return examCommitteeRepository.countExamCommitteeByExternalMember1(user);
    }

    public long getTotalCommitteesCount() {
        return examCommitteeRepository.count();
    }

    public long getTotalCompletedCommitteesCount() {
        return examCommitteeRepository.countExamCommitteeByIsCompleted(true);
    }

    public long getTotalActiveCommitteesCount() {
        return examCommitteeRepository.countExamCommitteeByIsCompleted(false);
    }

    public Boolean checkQuesModerationEligibility(ExamCommittee examCommittee){
        List<Course> committeeCourses = courseRepository.findBySemesterAndSessionOrderByCourseCodeAsc(examCommittee.getSemester(), examCommittee.getSession());

        if(committeeCourses.isEmpty()){ return false; }
        for(Course course : committeeCourses){
            if(course.getCourseType().equals("Industrial Visit") || course.getCourseType().equals("Viva Voce") || course.getCourseType().equals("Thesis") || course.getCourseType().equals("Project")){
                continue;
            }
           if(course.getCourseType().equals("Theory")){
               if(course.getCourseTeacher() == null || course.getInternalQuesSetterEvaluator() == null || course.getExternalQuesSetterEvaluator() == null){
                   return false;
               }
           }
           else{
               if(course.getCourseTeacher() == null) return false;
           }
        }
        return true;
    }

    public boolean checkViewPermission(User user, ExamCommittee examCommittee) {
        if(user == null){ return false; }
        if(user.getRole().equals("admin") || user.getRole().equals("co-admin")){
            return true;
        }
        return examCommittee.getChairman().getUserId().equals(user.getUserId()) || examCommittee.getInternalMember1().getUserId().equals(user.getUserId()) || examCommittee.getInternalMember2().getUserId().equals(user.getUserId()) || examCommittee.getExternalMember1().getUserId().equals(user.getUserId());
    }

    public boolean checkEditPermission(User user, ExamCommittee examCommittee) {
        if(user == null){ return false; }
        return user.getRole().equals("admin") || user.getRole().equals("co-admin") || user.getUserId().equals(examCommittee.getChairman().getUserId());
    }

    public boolean isMember(User user, ExamCommittee examCommittee){
        if(user == null || examCommittee == null){ return false; }
        return user.getUserId().equals(examCommittee.getChairman().getUserId()) || user.getUserId().equals(examCommittee.getInternalMember1().getUserId()) || user.getUserId().equals(examCommittee.getInternalMember2().getUserId()) || user.getUserId().equals(examCommittee.getExternalMember1().getUserId());
    }

    public boolean isNotCommitteeCourse(ExamCommittee examCommittee, Course course){
        return !examCommittee.getSemester().getSemesterId().equals(course.getSemester().getSemesterId()) || !examCommittee.getSession().equals(course.getSession());
    }

    public void updateStudentCount(ExamCommittee examCommittee, Long studentCount){
        examCommittee.setStudentCount(studentCount);
        examCommitteeRepository.save(examCommittee);
    }

    public boolean checkResultPublicationEligibility(ExamCommittee examCommittee){
        if(examCommittee.getSemester() == null){ return false; }
        List<Course> committeeCourses = courseRepository.findBySemesterAndSessionOrderByCourseCodeAsc(examCommittee.getSemester(), examCommittee.getSession());
        if(committeeCourses.isEmpty()){ return false; }
        for(Course course : committeeCourses){
            if(course.getCourseType().equals("Theory")){
                if(course.getExamineeCount() == null || course.getExamineeCount() <= 0) return false;
            }
        }
        return true;
    }

    @Transactional
    public boolean markResultPublished(ExamCommittee examCommittee) {
        if(examCommittee == null){ return false; }
        try{
            List<Course> committeeCourses = courseRepository.findBySemesterAndSessionOrderByCourseCodeAsc(examCommittee.getSemester(), examCommittee.getSession());
            if(committeeCourses.isEmpty()){ return false; }
            List<Course> theoryCourses = new ArrayList<>();
            for(Course course : committeeCourses){
                if(course.getCourseType().equals("Theory")){
                    theoryCourses.add(course);
                }
            }

            double allowanceOfMember = billRateService.getRateByTask("Member of Exam Committee");
            double quesSettingBillRate = billRateService.getRateByTask("Semester Final Question Setting");
            double scriptEvaluationBillRate = billRateService.getRateByTask("Theory Script Evaluation");
            double thirdExaminationBillRate = billRateService.getRateByTask("Third Examination");
            double classTestBillRate = billRateService.getRateByTask("Class Test");
            double labCourseTeacherBillRate = billRateService.getRateByTask("Lab(Course Teacher)");
            double labExamCommitteeBillRate = billRateService.getRateByTask("Lab(Exam Committee)");
            double tabulationBillRate = billRateService.getRateByTask("Tabulation");
            double comprehensiveTabulationRate = billRateService.getRateByTask("Comprehensive Tabulation");
            Long studentCount = examCommittee.getStudentCount();
            double moderationBillPerMember = Math.max(2000.00, (theoryCourses.size() * quesSettingBillRate) / 4);

            for(int i = 0; i < 4; i++){
                GratuityBill gratuityBill = new GratuityBill();
                GratuityBill tabulationBill = new GratuityBill();
                GratuityBill comprehensiveTabulationBill = new GratuityBill();
                GratuityBill quesModerationBill = new GratuityBill();

                gratuityBill.setCommittee(examCommittee);

                tabulationBill.setCommittee(examCommittee);
                tabulationBill.setTaskName("Tabulation");
                tabulationBill.setBillRate(tabulationBillRate);
                tabulationBill.setNumberOfScriptsOrStudents(studentCount);
                tabulationBill.setTotalBillAmount(tabulationBillRate*studentCount);

                comprehensiveTabulationBill.setCommittee(examCommittee);
                comprehensiveTabulationBill.setTaskName("Comprehensive Tabulation");
                comprehensiveTabulationBill.setBillRate(comprehensiveTabulationRate);
                comprehensiveTabulationBill.setNumberOfScriptsOrStudents(studentCount);
                comprehensiveTabulationBill.setTotalBillAmount(comprehensiveTabulationRate*studentCount);

                quesModerationBill.setCommittee(examCommittee);
                quesModerationBill.setTaskName("Question Moderation");
                quesModerationBill.setTotalBillAmount(moderationBillPerMember);

                if(i == 0){
                    gratuityBill.setBillUser(examCommittee.getChairman());
                    gratuityBill.setTaskName("Allowance of Chairman");
                    gratuityBill.setCourseCodesOrStuIds(Collections.singletonList("Chairman"));
                    double allowanceOfChairman = billRateService.getRateByTask("Chairman of Exam Committee");
                    gratuityBill.setBillRate(allowanceOfChairman);
                    gratuityBill.setTotalBillAmount(allowanceOfChairman);
                    gratuityBillService.saveGratuityBill(gratuityBill);

                    tabulationBill.setBillUser(examCommittee.getChairman());
                    gratuityBillService.saveGratuityBill(tabulationBill);
                    comprehensiveTabulationBill.setBillUser(examCommittee.getChairman());
                    gratuityBillService.saveGratuityBill(comprehensiveTabulationBill);

                    quesModerationBill.setBillUser(examCommittee.getChairman());
                    gratuityBillService.saveGratuityBill(quesModerationBill);
                }
                else{
                    gratuityBill.setTaskName("Allowance of Member");
                    gratuityBill.setCourseCodesOrStuIds(Collections.singletonList("Member"));
                    gratuityBill.setBillRate(allowanceOfMember);
                    gratuityBill.setTotalBillAmount(allowanceOfMember);

                    if(i == 1) {
                        gratuityBill.setBillUser(examCommittee.getInternalMember1());

                        tabulationBill.setBillUser(examCommittee.getInternalMember1());
                        gratuityBillService.saveGratuityBill(tabulationBill);
                        comprehensiveTabulationBill.setBillUser(examCommittee.getInternalMember1());
                        gratuityBillService.saveGratuityBill(comprehensiveTabulationBill);

                        quesModerationBill.setBillUser(examCommittee.getInternalMember1());
                        gratuityBillService.saveGratuityBill(quesModerationBill);
                    }
                    else if(i == 2){
                        gratuityBill.setBillUser(examCommittee.getInternalMember2());

                        tabulationBill.setBillUser(examCommittee.getInternalMember2());
                        gratuityBillService.saveGratuityBill(tabulationBill);
                        comprehensiveTabulationBill.setBillUser(examCommittee.getInternalMember2());
                        gratuityBillService.saveGratuityBill(comprehensiveTabulationBill);

                        quesModerationBill.setBillUser(examCommittee.getInternalMember2());
                        gratuityBillService.saveGratuityBill(quesModerationBill);
                    }
                    else {
                        quesModerationBill.setBillUser(examCommittee.getExternalMember1());
                        gratuityBillService.saveGratuityBill(quesModerationBill);
                    }

                    if(i < 3){//no allowance for external
                        gratuityBillService.saveGratuityBill(gratuityBill);
                    }

                }
            }

            for(Course course : committeeCourses){
                if(course.getCourseType().equals("Theory")){
                    GratuityBill classTestBill = new GratuityBill();

                    Long numOfClassTests = (long) ((course.getCourseCredit() == 3) ? 4 : 3);
                    classTestBill.setBillUser(course.getCourseTeacher());
                    classTestBill.setCommittee(examCommittee);
                    classTestBill.setTaskName("Class Test");
                    classTestBill.setBillRate(classTestBillRate);
                    classTestBill.setNumberOfClassTests(numOfClassTests);
                    classTestBill.setNumberOfScriptsOrStudents(studentCount);
                    classTestBill.setCourseCode(course.getCourseCode());
                    classTestBill.setTotalBillAmount(classTestBillRate * studentCount * numOfClassTests);
                    gratuityBillService.saveGratuityBill(classTestBill);

                    for(int i = 0; i < 2; i++){
                        GratuityBill quesSettingBill = new GratuityBill();
                        GratuityBill scriptEvaluationBill = new GratuityBill();

                        quesSettingBill.setCommittee(examCommittee);
                        quesSettingBill.setCourseCode(course.getCourseCode());
                        quesSettingBill.setTaskName("Question Setting");
                        quesSettingBill.setBillRate(quesSettingBillRate);
                        quesSettingBill.setTotalBillAmount(quesSettingBillRate);

                        scriptEvaluationBill.setCommittee(examCommittee);
                        scriptEvaluationBill.setCourseCode(course.getCourseCode());
                        scriptEvaluationBill.setBillRate(scriptEvaluationBillRate);
                        scriptEvaluationBill.setTaskName("Script Evaluation");
                        scriptEvaluationBill.setTotalBillAmount(scriptEvaluationBillRate * course.getExamineeCount());
                        scriptEvaluationBill.setNumberOfScriptsOrStudents(course.getExamineeCount());

                        if(i == 0){
                            quesSettingBill.setBillUser(course.getInternalQuesSetterEvaluator());
                            scriptEvaluationBill.setBillUser(course.getInternalQuesSetterEvaluator());
                        }
                        else{
                            quesSettingBill.setBillUser(course.getExternalQuesSetterEvaluator());
                            scriptEvaluationBill.setBillUser(course.getExternalQuesSetterEvaluator());
                        }

                        gratuityBillService.saveGratuityBill(quesSettingBill);
                        gratuityBillService.saveGratuityBill(scriptEvaluationBill);

                    }

                    ThirdExamination thirdExamination = thirdExaminationRepository.findByCourseAndExamCommittee(course, examCommittee);
                    if(thirdExamination != null){
                        GratuityBill thirdExaminationBill = new GratuityBill();

                        thirdExaminationBill.setCommittee(examCommittee);
                        thirdExaminationBill.setBillUser(thirdExamination.getExaminer());
                        thirdExaminationBill.setCourseCode(course.getCourseCode());
                        thirdExaminationBill.setCourseCodesOrStuIds(thirdExamination.getStudentsId());
                        thirdExaminationBill.setTaskName("Third Examination");
                        thirdExaminationBill.setBillRate(thirdExaminationBillRate);
                        thirdExaminationBill.setNumberOfScriptsOrStudents(thirdExamination.getScriptsCount());
                        thirdExaminationBill.setTotalBillAmount(Math.max(500.00, thirdExaminationBillRate*thirdExamination.getScriptsCount()));
                        gratuityBillService.saveGratuityBill(thirdExaminationBill);
                    }

                }
                else if(course.getCourseType().equals("Lab")){
                    GratuityBill labCourseBill = new GratuityBill();

                    labCourseBill.setCommittee(examCommittee);
                    labCourseBill.setCourseCode(course.getCourseCode());
                    labCourseBill.setTaskName("Lab/Sessional");
                    labCourseBill.setBillUser(course.getCourseTeacher());
                    labCourseBill.setBillRate(labCourseTeacherBillRate);
                    labCourseBill.setNumberOfScriptsOrStudents(studentCount);
                    labCourseBill.setTotalBillAmount(labCourseTeacherBillRate*studentCount);
                    gratuityBillService.saveGratuityBill(labCourseBill);

                    for(int i = 0; i < 4; i++){
                        GratuityBill labExamCommitteeBill = new GratuityBill();

                        labExamCommitteeBill.setCommittee(examCommittee);
                        labExamCommitteeBill.setCourseCode(course.getCourseCode());
                        labExamCommitteeBill.setTaskName("Lab/Sessional");
                        labExamCommitteeBill.setBillRate(labExamCommitteeBillRate);
                        labExamCommitteeBill.setNumberOfScriptsOrStudents(studentCount);
                        labExamCommitteeBill.setTotalBillAmount(labExamCommitteeBillRate*studentCount);
                        if(i == 0){
                            if(course.getCourseTeacher().getUserId().equals(examCommittee.getChairman().getUserId())){
                                continue;
                            }
                            labExamCommitteeBill.setBillUser(examCommittee.getChairman());
                            gratuityBillService.saveGratuityBill(labExamCommitteeBill);
                        }
                        else if(i == 1){
                            if(course.getCourseTeacher().getUserId().equals(examCommittee.getInternalMember1().getUserId())){
                                continue;
                            }
                            labExamCommitteeBill.setBillUser(examCommittee.getInternalMember1());
                            gratuityBillService.saveGratuityBill(labExamCommitteeBill);
                        }
                        else if(i == 2){
                            if(course.getCourseTeacher().getUserId().equals(examCommittee.getInternalMember2().getUserId())){
                                continue;
                            }
                            labExamCommitteeBill.setBillUser(examCommittee.getInternalMember2());
                            gratuityBillService.saveGratuityBill(labExamCommitteeBill);
                        }
                        else{
                            if(course.getCourseTeacher().getUserId().equals(examCommittee.getExternalMember1().getUserId())){
                                continue;
                            }
                            labExamCommitteeBill.setBillUser(examCommittee.getExternalMember1());
                            gratuityBillService.saveGratuityBill(labExamCommitteeBill);
                        }
                    }

                }
                else if(course.getCourseType().equals("Project") || course.getCourseType().equals("Thesis")){
                    GratuityBill projectCourseBill = new GratuityBill();
                }
            }


            examCommittee.setResultPublished(true);
            examCommitteeRepository.save(examCommittee);
            return true;
        }catch(Exception e){
            log.error("Failed to publish result for committee {}: {}", examCommittee.getCommitteeId(), e.getMessage());
            System.out.println(e.getMessage());
            return false;
        }
    }
}
