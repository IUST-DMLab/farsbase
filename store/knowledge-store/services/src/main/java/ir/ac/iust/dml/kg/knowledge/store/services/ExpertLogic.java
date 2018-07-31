package ir.ac.iust.dml.kg.knowledge.store.services;

import ir.ac.iust.dml.kg.knowledge.store.access.entities.ExpertVote;
import ir.ac.iust.dml.kg.knowledge.store.access.entities.TripleState;

import java.util.Set;

/**
 *
 */
public class ExpertLogic {
    public static TripleState makeState(Set<ExpertVote> votes) {
        int approveCount = 0;
        int rejectCount = 0;
        for (ExpertVote v : votes) {
            switch (v.getVote()) {
                case Approve:
                    approveCount++;
                    break;
                case Reject:
                    rejectCount++;
                    break;
                case VIPApprove:
                    return TripleState.Approved;
                case VIPReject:
                    return TripleState.Rejected;
            }
        }
        if (approveCount >= 3) return TripleState.Approved;
        if (rejectCount >= 2) return TripleState.Rejected;
        return TripleState.None;
    }
}
