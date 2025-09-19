package com.example.service;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncryptor passwordEncryptor;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(User user) {
        user.setPassword(passwordEncryptor.hashPassword(user.getPassword()));
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
