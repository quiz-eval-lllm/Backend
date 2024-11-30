package com.ta.llmbackend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ta.llmbackend.dto.request.GenerateQuizReq;
import com.ta.llmbackend.dto.request.UpdateQuizReq;
import com.ta.llmbackend.exception.BadRequestException;
import com.ta.llmbackend.model.Package;
import com.ta.llmbackend.model.QuizActivities;
import com.ta.llmbackend.model.Users;
import com.ta.llmbackend.repository.PackageDb;

@Service
public class PackageService {

    @Autowired
    private PackageDb packageDb;

    @Autowired
    private UserService userService;

    // Create package
    public Package createNewPackage(GenerateQuizReq quizReq) throws IOException {

        Package newPackage = new Package();

        Users user = userService.getUserById(UUID.fromString(quizReq.getUserId()));

        newPackage.setCreator(user);
        newPackage.setTitle(quizReq.getTitle());
        newPackage.setCategory(quizReq.getCategory());
        newPackage.setType(quizReq.getType());
        newPackage.setPrompt(quizReq.getPrompt());
        // TODO: Handle pdf context
        // newPackage.setContext(quizReq.getContext().getBytes());

        Package temp = packageDb.save(newPackage);

        return temp;
    }

    // Read packages
    public List<Package> getAllPackages() {

        return packageDb.findAll(Sort.by(Sort.Order.desc("updatedAt")));
    }

    // Read package by id
    public Package getPackageById(UUID packageId) {

        for (Package item : getAllPackages()) {
            if (item.getPackageId().equals(packageId)) {
                return item;
            }
        }
        throw new BadRequestException("User with id: " + packageId + " not found");
    }

    // Read package from quiz acitivity
    public List<Package> getPackageFromQuizActivities(List<QuizActivities> quizActivitiesList) {

        List<Package> packageList = new ArrayList<>();

        if (quizActivitiesList != null) {
            for (QuizActivities quizActivities : quizActivitiesList) {
                packageList.add(quizActivities.getPackageQuizActivities());
            }
            return packageList;
        } else {
            return null;
        }

    }

    // Read package by finish status
    public List<Package> fiterPackageByFinished(UUID userId, boolean is_finished) {

        Users user = userService.getUserById(userId);
        List<QuizActivities> userQuizActivitiesList = user.getListQuizActivites();
        List<Package> packageUserListFiltered = new ArrayList<>();

        for (QuizActivities quizActivities : userQuizActivitiesList) {
            // finished
            if ((is_finished) && (quizActivities.getFinishedAt() != null)) {
                packageUserListFiltered.add(quizActivities.getPackageQuizActivities());
            }
            // unfinished
            if (!(is_finished) && (quizActivities.getFinishedAt() == null)) {
                packageUserListFiltered.add(quizActivities.getPackageQuizActivities());
            }
        }

        return sortPackageByCreateTime(packageUserListFiltered);
    }

    // Sort package by create time desc
    public List<Package> sortPackageByCreateTime(List<Package> packageList) {

        if (!packageList.isEmpty()) {
            return packageList.stream()
                    .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getUpdatedAt())).toList();
        } else {
            return null;
        }
    }

    // Update package by Id
    public Package updatePackageById(UUID packageId, UpdateQuizReq updateQuizReq) {

        Package quizPackage = getPackageById(packageId);

        if (quizPackage != null) {
            if (updateQuizReq.getTitle() != "") {
                quizPackage.setTitle(updateQuizReq.getTitle());
            }

            if (updateQuizReq.getCategory() != "") {
                quizPackage.setCategory(updateQuizReq.getCategory());
            }

            return packageDb.save(quizPackage);
        } else {
            return null;
        }
    }

    // Delete package by Id
    public void deleteQuizPackage(UUID packageId) {

        Package quizPackage = getPackageById(packageId);

        packageDb.delete(quizPackage);
    }

}
