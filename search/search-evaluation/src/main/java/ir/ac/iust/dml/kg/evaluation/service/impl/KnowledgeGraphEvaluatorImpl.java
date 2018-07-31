/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.service.impl;

import ir.ac.iust.dml.kg.evaluation.model.KnowledgeGraphResponse;
import ir.ac.iust.dml.kg.evaluation.model.UserJudgment;
import ir.ac.iust.dml.kg.evaluation.model.UserResponse;
import ir.ac.iust.dml.kg.evaluation.service.KnowledgeGraphEvaluator;
import ir.ac.iust.dml.kg.evaluation.service.UserResponseService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author r.farjamfard
 */
public class KnowledgeGraphEvaluatorImpl implements KnowledgeGraphEvaluator {

    private final UserResponseService userResponseService;

    public KnowledgeGraphEvaluatorImpl(UserResponseService userResponseService) {
        this.userResponseService = userResponseService;
    }

    @Override
    public float calculatePrecision(List<KnowledgeGraphResponse> knowledgeGraphResponses) {
        List<Float> allPrecisions = new ArrayList<>();
        for (KnowledgeGraphResponse kgResponse : knowledgeGraphResponses) {
            List<UserResponse> userResponses = this.userResponseService.getJudgedUserResponseByQuery(kgResponse.getQuery());
            if (userResponses != null && userResponses.size() > 0) { //calculate precision for judged queries
                int totalResultCount = kgResponse.getUriList().size();
                Set<String> relevantAnswers = getAllRelevantAnswers(userResponses);
                int totalRelevantAnswers = relevantAnswers.size();
                int totalCount = totalResultCount;
                if (totalRelevantAnswers > 0) {
                    totalCount = Math.min(totalResultCount, totalRelevantAnswers);
                }
                int truePositiveCounts = caculateTruePositiveCounts(relevantAnswers, kgResponse.getUriList());
                float precision = (float) truePositiveCounts / (float) totalCount;
                allPrecisions.add(precision);
            }
        }
        float totalPrecision = calculateAverage(allPrecisions);
        return totalPrecision;
    }

    private int caculateTruePositiveCounts(Set<String> relevantAnswers, List<String> kgUriList) {

        int truePositiveAnswers = 0;

        if (!kgUriList.isEmpty()) {
            for (String uri : kgUriList) {
                if (relevantAnswers.contains(uri)) {
                    truePositiveAnswers++;
                }
            }//
        }

        return truePositiveAnswers;
    }

    private Set<String> getAllRelevantAnswers(List<UserResponse> userResponses) {
        Set<String> allAnswers = getAllJudgedAnswers(userResponses);
        Set<String> relevantAnswers = new HashSet<>();

        for (String answer : allAnswers) {
            if (isRelevant(answer, userResponses)) {
                relevantAnswers.add(answer);
            }
        }
        return relevantAnswers;

    }

    private Set<String> getAllJudgedAnswers(List<UserResponse> userResponses) {
        Set<String> allAnswers = new HashSet<>();
        for (UserResponse userResponse : userResponses) {
            if (userResponse != null) {
                List<UserJudgment> judgments = userResponse.getJudgmentList();
                if (judgments != null) {
                    for (UserJudgment judgment : judgments) {
                        allAnswers.add(judgment.getAnswer());
                    }
                }
            }
        }
        return allAnswers;
    }

    private boolean isRelevant(String uri, List<UserResponse> userResponses) {
        int relevantJudgments = 0;
        int notRelevantJudgments = 0;
        int totalJudgetCount = 0;
        for (UserResponse userResponse : userResponses) {
            List<UserJudgment> judgmentList = userResponse.getJudgmentList();
            for (UserJudgment judgment : judgmentList) {
                if (uri.equalsIgnoreCase(judgment.getAnswer())) {
                    totalJudgetCount++;
                    if (judgment.isRelevant()) {
                        relevantJudgments++;
                    } else {
                        notRelevantJudgments++;
                    }
                }
            }
        }

        if (relevantJudgments > 0 && notRelevantJudgments == 0) //all experts judged as relevant
        {
            return true;
        } else if (relevantJudgments == 0 && notRelevantJudgments > 0) //all experts judged as not relevant
        {
            return false;
        } else //some expert judged as relevant and some others judged as not relevant
        // we will choose majority vote
        {
            if (relevantJudgments >= notRelevantJudgments) { //relavant judgments are major
                return true;
            } else {
                return false;
            }
        }
    }

    private float calculateAverage(List<Float> items) {
        Float sum = 0f;
        for (Float item : items) {
            sum = sum + item;
        }
        return sum / items.size();
    }

    /*private int caculateTotalPositiveCount(List<UserResponse> userResponses) {

     return 0;
     }*/
    @Override
    public float calculatePrecisionAtK(List<KnowledgeGraphResponse> knowledgeGraphResponses, int k) {
        if (k <= 1) {
            k = 1;
        }
        for (KnowledgeGraphResponse kgResponse : knowledgeGraphResponses) {
            if (kgResponse.getUriList() != null) {
                if (kgResponse.getUriList().size() > k) {
                    kgResponse.setUriList(kgResponse.getUriList().subList(0, k));
                }
            }
        }

        return this.calculatePrecision(knowledgeGraphResponses);
    }

}
