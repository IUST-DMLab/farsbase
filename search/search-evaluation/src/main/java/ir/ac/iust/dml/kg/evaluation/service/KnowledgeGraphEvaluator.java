/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.ac.iust.dml.kg.evaluation.service;
import ir.ac.iust.dml.kg.evaluation.model.KnowledgeGraphResponse;
import java.util.List;
/**
 *
 * @author r.fajamfard
 */
public interface KnowledgeGraphEvaluator {
    float calculatePrecision(List<KnowledgeGraphResponse> knowledgeGraphResponses);
    float calculatePrecisionAtK(List<KnowledgeGraphResponse> knowledgeGraphResponses,int k);
}
