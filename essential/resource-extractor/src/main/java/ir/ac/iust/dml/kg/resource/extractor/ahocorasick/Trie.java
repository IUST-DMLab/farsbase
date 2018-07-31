package ir.ac.iust.dml.kg.resource.extractor.ahocorasick;

import ir.ac.iust.dml.kg.resource.extractor.MatchedResource;
import ir.ac.iust.dml.kg.resource.extractor.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * A trie on words
 */
class Trie {
    private final State root = new State();

    void add(Resource resource, String[] words) {
        root.addState(resource, words).addEmit(resource, words);
    }

    void constructFailureStates() {
        final Queue<State> queue = new LinkedBlockingDeque<>();

        // First, set the fail state of all depth 1 states to the root state
        for (State depthOneState : root.getNextStates()) {
            depthOneState.setFailure(root);
            queue.add(depthOneState);
        }

        // Second, determine the fail state for all depth > 1 state
        while (!queue.isEmpty()) {
            final State currentState = queue.remove();

            for (String transition : currentState.getTransitions()) {
                final State targetState = currentState.nextState(transition);
                queue.add(targetState);

                State traceFailureState = currentState.getFailure();
                while (traceFailureState != null && traceFailureState.nextState(transition) == null)
                    traceFailureState = traceFailureState.getFailure();

                final State newFailureState = traceFailureState.nextState(transition);
                targetState.setFailure(newFailureState);
            }
        }
    }

    List<MatchedResource> parseText(String[] words, boolean removeSubset) {
        final List<MatchedResource> resources = new ArrayList<>();
        State currentState = root;

        for (int pos = 0; pos < words.length; pos++) {
            String word = words[pos];
            currentState = nextState(currentState, word);
            MatchedResource bestMatch = null;
            for (Emit emit : currentState.getAllEmits()) {
                final MatchedResource current = new MatchedResource(pos - emit.getWords().length + 1, pos, emit.getResource(), emit.getAmbiguities());
                if (bestMatch == null || current.getStart() < bestMatch.getStart())
                    bestMatch = current;
                else
                    current.setSubsetOf(bestMatch);
                if (!removeSubset)
                    resources.add(current);
            }
            if (removeSubset && bestMatch != null)
                resources.add(bestMatch);
        }
        return resources;
    }

    private State nextState(State currentState, String word) {
        State newCurrentState = currentState.nextState(word);

        while (newCurrentState == null) {
            currentState = currentState.getFailure();
            newCurrentState = currentState.nextState(word);
        }

        return newCurrentState;
    }
}
