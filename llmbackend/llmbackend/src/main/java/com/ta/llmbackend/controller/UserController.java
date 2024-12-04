package com.ta.llmbackend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ta.llmbackend.dto.request.GenerateUserReq;
import com.ta.llmbackend.dto.request.UpdateUserReq;
import com.ta.llmbackend.dto.request.UserAuthReq;
import com.ta.llmbackend.dto.response.AuthResponse;
import com.ta.llmbackend.model.Users;
import com.ta.llmbackend.service.UserService;
import com.ta.llmbackend.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    // POST for creating new user --> Sign Up
    @PostMapping("")
    public ResponseEntity<Object> addUser(@Valid @RequestBody GenerateUserReq userReq) {

        Users newUser = userService.createNewUser(userReq);

        return ResponseUtil.okResponse(newUser, "New user successfully added");
    }

    // POST for authenticating user --> Login
    @Deprecated
    @PostMapping("/auth")
    public ResponseEntity<Object> authentication(@Valid @RequestBody UserAuthReq authReq) {

        String token = userService.authenticateUser(authReq);

        if (token != null) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(token);
            authResponse.setUserInfo(userService.getUserByName(authReq.getUsername()));
            return ResponseUtil.okResponse(authResponse, "Login successfully");
        } else {
            return ResponseUtil.okResponse(null, "Failed to authenticate");
        }
    }

    // GET users
    @GetMapping("")
    public ResponseEntity<Object> getUser(@RequestParam(value = "role", required = false) String searchRole) {

        List<Users> listUser = new ArrayList<>();

        if (searchRole == null || searchRole == "") {
            listUser = userService.getAllUser();
        } else {
            listUser = userService.getUserByRole(Integer.parseInt(searchRole));
        }

        if (listUser.isEmpty()) {
            return ResponseUtil.okResponse(null, "No users found");
        }

        return ResponseUtil.okResponse(listUser, "Successfully retrieve users");
    }

    // GET user by Id
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") String userId) {

        Users user = userService.getUserById(UUID.fromString(userId));

        return ResponseUtil.okResponse(user, "Successfully retrieve user with id: " + user.getUserId());

    }

    // PUT user by Id
    @PutMapping("/{id}/update")
    public ResponseEntity<Object> updateUser(@RequestBody UpdateUserReq updateUserReq,
            @PathVariable("id") String userId) {

        Users user = userService.updateUserById(UUID.fromString(userId), updateUserReq);

        return ResponseUtil.okResponse(user, "User with id: " + userId + " successfully updated");
    }

    // DELETE user by Id
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") String userId) {

        userService.deleteUser(UUID.fromString(userId));

        return ResponseUtil.okResponse(null, "User with id: " + userId + " successfully deleted");
    }

}
