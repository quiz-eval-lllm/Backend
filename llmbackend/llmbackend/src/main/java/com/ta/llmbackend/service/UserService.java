package com.ta.llmbackend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
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

    // Create Superadmin
    public void addAdmin() {
        List<Users> user = getUserByRole(0);

        if (user.isEmpty()) {
            Users admin = new Users();
            admin.setUserId(UUID.fromString("01939099-4d0b-76ff-b8ad-a2091c502353"));
            admin.setPassword(bCryptPasswordEncoder.encode("12345"));
            admin.setName("admin");
            admin.setRole(0);
            userDb.save(admin);
        }
    }

    // Create user
    public Users createNewUser(GenerateUserReq userReq) {

        // Check if a user with the same name or email already exists
        List<Users> existingUsers = getAllUser();
        for (Users existingUser : existingUsers) {
            if (existingUser.getName().equalsIgnoreCase(userReq.getName())) {
                throw new BadRequestException("User with name: " + userReq.getName() + " already exists");
            }
            if (userReq.getEmail() != null && existingUser.getEmail() != null &&
                    existingUser.getEmail().equalsIgnoreCase(userReq.getEmail())) {
                throw new BadRequestException("User with email: " + userReq.getEmail() + " already exists");
            }
        }

        Users user = new Users();
        user.setUserId(UUID.fromString(userReq.getId()));
        user.setName(userReq.getName());
        user.setPassword(bCryptPasswordEncoder.encode(userReq.getPassword()));

        if (userReq.getEmail() != null && !userReq.getEmail().isEmpty()) {
            user.setEmail(userReq.getEmail());
        } else {
            user.setEmail(null);
        }

        user.setRole(userReq.getRole());

        Users temp = userDb.save(user);

        return temp;
    }

    // Authenticate user
    @Deprecated
    public String authenticateUser(UserAuthReq userAuthReq) {

        Users users = getUserByName(userAuthReq.getUsername());

        if (bCryptPasswordEncoder.matches(userAuthReq.getPassword(), users.getPassword())) {
            return jwtUtils.generateToken(users.getEmail(), users.getUserId(),
                    String.valueOf(users.getRole()));
        }

        throw new AccessDeniedException("Failed to authenticate");

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

    // Read user by name
    public Users getUserByName(String name) {

        for (Users user : getAllUser()) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        throw new BadRequestException("User with name: " + name + " not found");
    }

    // Update user by id
    public Users updateUserById(UUID userId, UpdateUserReq updateUserReq) {

        Users user = getUserById(userId);

        if (user != null) {
            if (updateUserReq.getName() != "" || !updateUserReq.getName().isEmpty()) {
                user.setName(updateUserReq.getName());
            }

            if (updateUserReq.getPassword() != "" || !updateUserReq.getPassword().isEmpty()
                    || updateUserReq.getPassword() != null) {
                user.setPassword(bCryptPasswordEncoder.encode(updateUserReq.getPassword()));
            }

            if (updateUserReq.getEmail() != "" || !updateUserReq.getEmail().isEmpty()) {
                user.setEmail(updateUserReq.getEmail());
            }

            if (updateUserReq.getRole() != null) {
                user.setRole(updateUserReq.getRole());
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
