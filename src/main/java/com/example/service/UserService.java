package com.example.service;
import com.example.entity.User;
import com.example.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncryptor passwordEncryptor;

    @Autowired
    private EmailService emailService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
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

        emailService.sendOTPViaEmail(user.getEmail(), "Login Verification OTP", otp, null);
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
    public User getUserById(Long id) {
        return userRepository.findByUser_id(id);
    }

    public List<User> getExternals() {
        return userRepository.findExternalTeachers();
    }

    public List<User> getInternals(){
        return userRepository.findInternalTeachers();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
