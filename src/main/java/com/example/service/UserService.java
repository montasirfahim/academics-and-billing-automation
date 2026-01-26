package com.example.service;
import com.example.entity.User;
import com.example.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncryptor passwordEncryptor;

    @Autowired
    private EmailService emailService;

    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .map(User::createSafeCopy)
                .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncryptor.hashPassword(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public void generateAndSendOTP(User user) throws MessagingException, UnsupportedEncodingException {
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(10000, 100000));

        emailService.sendOTPViaEmail(user.getEmail(), "Login Verification OTP", otp);
        user.setLoginOTP(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(3));
        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public User validateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if(passwordEncryptor.verifyPassword(password, user.getPassword())) {
                return user;
            }
        }

        return null;
    }

    public Boolean checkPassword(Long userId, String password) {
        User user = userRepository.findByUserId(userId);
        return user != null && passwordEncryptor.verifyPassword(password, user.getPassword());
    }

    @Transactional
    public Boolean updatePassword(Long userId, String newPassword) {
        User user = userRepository.findByUserId(userId);
        if(user == null){
            return false;
        }
        String hashedPassword = passwordEncryptor.hashPassword(newPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return true;
    }

    public Boolean updateDesignation(Long userId, String newDesignation) {
        User user = userRepository.findByUserId(userId);
        Map<String, String> map = new HashMap<>();
        map.put("Lecturer", "Grade 9");
        map.put("Assistant Professor", "Grade 6");
        map.put("Associate Professor", "Grade 4");
        map.put("Professor", "Grade 3");
        map.put("Registrar", "Grade 9");
        map.put("Deputy Registrar", "Grade 4");

        Map<String, String> gradeCategory = new HashMap<>();
        gradeCategory.put("Grade 1", "Category 1");
        gradeCategory.put("Grade 2", "Category 1");
        gradeCategory.put("Grade 3", "Category 1");
        gradeCategory.put("Grade 4", "Category 1");
        gradeCategory.put("Grade 5", "Category 1");
        gradeCategory.put("Grade 6", "Category 2");
        gradeCategory.put("Grade 7", "Category 2");
        gradeCategory.put("Grade 8", "Category 2");
        gradeCategory.put("Grade 9", "Category 2");
        gradeCategory.put("Grade 10", "Category 2");
        gradeCategory.put("Grade 11", "Category 3");
        gradeCategory.put("Grade 12", "Category 3");
        gradeCategory.put("Grade 13", "Category 3");
        gradeCategory.put("Grade 14", "Category 3");
        gradeCategory.put("Grade 15", "Category 3");
        gradeCategory.put("Grade 16", "Category 3");

        if(user != null){
            String newGrade = map.get(newDesignation);
            String newCategory = gradeCategory.get(newGrade);

            user.setDesignation(newDesignation);
            user.setSalaryGrade(newGrade);
            user.setGradingCategory(newCategory);
            userRepository.save(user);

            return true;
        }
        return false;
    }

    public User getUserById(Long id) {
        return userRepository.findByUserId(id);
    }

    public List<User> getExternals() {
        return userRepository.findExternalTeachers().stream().map(User::createSafeCopy).collect(Collectors.toList());
    }

    public List<User> getInternals(){
        return userRepository.findInternalTeachers().stream().map(User::createSafeCopy).collect(Collectors.toList());
    }

}
