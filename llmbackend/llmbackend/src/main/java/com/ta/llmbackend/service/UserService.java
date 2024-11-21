package com.ta.llmbackend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ta.llmbackend.dto.request.GenerateUserReq;
import com.ta.llmbackend.dto.request.UpdateUserReq;
import com.ta.llmbackend.model.User;
import com.ta.llmbackend.repository.UserDb;
import com.ta.llmbackend.exception.BadRequestException;;

@Service
public class UserService {

    @Autowired
    private UserDb userDb;

    // Create user
    public User createNewUser(GenerateUserReq userReq) {

        User user = new User();

        user.setUserId(UUID.fromString(userReq.getId()));
        user.setName(userReq.getName());
        user.setPassword(userReq.getPassword());
        user.setEmail(userReq.getEmail());
        user.setRole(userReq.getRole());

        User temp = userDb.save(user);

        return temp;
    }

    // Read users
    public List<User> getAllUser() {

        return userDb.findAll(Sort.by(Sort.Order.asc("name")));
    }

    // Read user by role
    public List<User> getUserByRole(int role) {

        return userDb.findByRole(role, Sort.by(Sort.Order.asc("name")));
    }

    // Read user by id
    public User getUserById(UUID userId) {

        for (User user : getAllUser()) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        throw new BadRequestException("User with id: " + userId + " not found");
    }

    // Update user by id
    public User updateUserById(UUID userId, UpdateUserReq updateUserReq) {

        User user = getUserById(userId);

        if (user != null) {
            if (updateUserReq.getName() != "") {
                user.setName(updateUserReq.getName());
            }

            if (updateUserReq.getPassword() != "") {
                user.setPassword(updateUserReq.getPassword());
            }

            return userDb.save(user);
        } else {
            return null;
        }
    }

    // Delete user
    public void deleteUser(UUID userId) {

        User user = getUserById(userId);

        userDb.delete(user);
    }
}
