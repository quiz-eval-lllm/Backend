package com.ta.llmbackend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ta.llmbackend.dto.request.GenerateUserReq;
import com.ta.llmbackend.dto.request.UpdateUserReq;
import com.ta.llmbackend.dto.request.UserAuthReq;
import com.ta.llmbackend.model.Users;
import com.ta.llmbackend.repository.UserDb;
import com.ta.llmbackend.security.JwtUtils;
import com.ta.llmbackend.exception.BadRequestException;;

@Service
public class UserService {

    @Autowired
    private UserDb userDb;

    @Autowired
    JwtUtils jwtUtils;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    // Create user
    public Users createNewUser(GenerateUserReq userReq) {

        Users user = new Users();

        user.setUserId(UUID.fromString(userReq.getId()));
        user.setName(userReq.getName());
        user.setPassword(bCryptPasswordEncoder.encode(userReq.getPassword()));
        user.setEmail(userReq.getEmail());
        user.setRole(userReq.getRole());

        Users temp = userDb.save(user);

        return temp;
    }

    // Authenticate user
    @Deprecated
    public String authenticateUser(UserAuthReq userAuthReq) {

        Users users = getUserByEmail(userAuthReq.getEmail());

        if (bCryptPasswordEncoder.matches(userAuthReq.getPassword(), users.getPassword())) {
            return jwtUtils.generateToken(users.getEmail(), users.getUserId(),
                    String.valueOf(users.getRole()));
        }

        throw new BadRequestException("Failed to authenticate");

    }

    // Read users
    public List<Users> getAllUser() {

        return userDb.findAll(Sort.by(Sort.Order.asc("name")));
    }

    // Read user by role
    public List<Users> getUserByRole(int role) {

        return userDb.findByRole(role, Sort.by(Sort.Order.asc("name")));
    }

    // Read user by id
    public Users getUserById(UUID userId) {

        for (Users user : getAllUser()) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        throw new BadRequestException("User with id: " + userId + " not found");
    }

    // Read user by email
    public Users getUserByEmail(String email) {

        for (Users user : getAllUser()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        throw new BadRequestException("User with email: " + email + " not found");
    }

    // Update user by id
    public Users updateUserById(UUID userId, UpdateUserReq updateUserReq) {

        Users user = getUserById(userId);

        if (user != null) {
            if (updateUserReq.getName() != "") {
                user.setName(updateUserReq.getName());
            }

            if (updateUserReq.getPassword() != "") {
                user.setPassword(bCryptPasswordEncoder.encode(updateUserReq.getPassword()));
            }

            return userDb.save(user);
        } else {
            return null;
        }
    }

    // Delete user
    public void deleteUser(UUID userId) {

        Users user = getUserById(userId);

        userDb.delete(user);
    }
}
