package com.ta.llmbackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ta.llmbackend.exception.BadRequestException;
import com.ta.llmbackend.model.Evaluation;
import com.ta.llmbackend.model.Package;
import com.ta.llmbackend.model.QuizActivities;
import com.ta.llmbackend.model.User;
import com.ta.llmbackend.repository.EvaluationDb;
import com.ta.llmbackend.repository.QuizActivitiesDb;

@Service
public class QuizService {

    @Autowired
    private QuizActivitiesDb quizActivitiesDb;

    @Autowired
    private EvaluationDb evaluationDb;

    @Autowired
    private UserService userService;

    @Autowired
    private PackageService packageService;

    // Create quiz activity
    public QuizActivities createQuizActivity(UUID userId, UUID packageId) {

        User user = userService.getUserById(userId);
        Package packageQuiz = packageService.getPackageById(packageId);

        if (user.getRole() == 1) {
            QuizActivities quizActivities = new QuizActivities();

            quizActivities.setUser(user);
            quizActivities.setPackageQuizActivities(packageQuiz);

            QuizActivities temp = quizActivitiesDb.save(quizActivities);

            return temp;
        }

        throw new BadRequestException("Forbidden to assign user with: " + userId);
    }

    // Read all quiz activites
    public List<QuizActivities> getAllQuizActivities() {

        return quizActivitiesDb.findAll(Sort.by(Sort.Order.desc("updatedAt")));
    }

    // Read quiz by id
    public QuizActivities getQuizActivitiesById(UUID quizId) {

        for (QuizActivities quizActivities : getAllQuizActivities()) {
            if (quizActivities.getQuizId().equals(quizId)) {
                return quizActivities;
            }
        }
        throw new BadRequestException("Quiz with id: " + quizId + " not found");
    }

    // Read quiz available to certain user
    public List<QuizActivities> getQuizActivitiesByUser(UUID userId) {

        List<QuizActivities> listQuizActivities = new ArrayList<>();

        for (QuizActivities quizActivities : getAllQuizActivities()) {
            if (quizActivities.getUser().getUserId().equals(userId)) {
                listQuizActivities.add(quizActivities);
            }
        }

        if (!listQuizActivities.isEmpty()) {
            return listQuizActivities;
        }
        throw new BadRequestException("Quiz with user id: " + userId + " not found");
    }

    // Filter quiz by finish status
    public List<QuizActivities> getQuizActivitiesByFinished(List<QuizActivities> listQuizActivities,
            boolean isFinished) {

        List<QuizActivities> quizActivitiesFilteredList = new ArrayList<>();

        for (QuizActivities quizActivities : listQuizActivities) {
            // finished
            if ((isFinished) && (quizActivities.getFinishedAt() != null)) {
                quizActivitiesFilteredList.add(quizActivities);
            }
            // unfinished
            if (!(isFinished) && (quizActivities.getFinishedAt() == null)) {
                quizActivitiesFilteredList.add(quizActivities);
            }
        }

        return quizActivitiesFilteredList;

    }

    // Finish quiz activites
    public void finishedQuizActivity(UUID quizId) {

        QuizActivities quizActivities = getQuizActivitiesById(quizId);

        quizActivities.setFinishedAt(LocalDateTime.now());

        quizActivitiesDb.save(quizActivities);
    }

    // Delete quiz activities
    public void deleteQuizActivities(UUID quizId) {

        QuizActivities quizActivities = getQuizActivitiesById(quizId);

        quizActivitiesDb.delete(quizActivities);
    }

    // Restart quiz ectivities
    public QuizActivities emptyQuizData(UUID quizId) {

        QuizActivities quizActivities = getQuizActivitiesById(quizId);

        for (Evaluation evaluation : quizActivities.getEvaluationList()) {
            evaluation.setQuizActivities(null);
            evaluationDb.save(evaluation);
        }

        quizActivities.setEvaluationList(null);
        quizActivities.setFinishedAt(null);
        quizActivities.setFinalScore(0);

        return quizActivitiesDb.save(quizActivities);
    }

}
