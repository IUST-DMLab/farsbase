package ir.ac.iust.dml.kg.resource.extractor.ahocorasick;

import ir.ac.iust.dml.kg.resource.extractor.Resource;

import java.util.*;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * State of aho-corasick
 */
class State {
    private final boolean root;
    private final String path;
    private final Map<String, State> success = new HashMap<>();
    private Emit defaultEmit = null;
    private Set<Emit> allEmits = new HashSet<>();
    private State failure;

    State() {
        this.path = "/";
        this.root = true;
    }

    State(String path) {
        this.path = path;
        this.root = false;
    }

    // Add resource and return final state
    State addState(Resource resource, String[] words) {
        State state = this;
        StringBuilder path = new StringBuilder();
        for (String word : words) {
            path.append(word);
            state = state.success.computeIfAbsent(word, k -> new State(path.toString()));
        }
        return state;
    }

    // Add emit to this state
    void addEmit(Resource resource, String[] words) {
        if (this.defaultEmit == null) {
            this.defaultEmit = new Emit(words);
            this.allEmits.add(defaultEmit);
        }
        this.defaultEmit.add(resource);
    }

    State getFailure() {
        return failure;
    }

    void setFailure(State failure) {
        if (this.failure != null)
            throw new RuntimeException("Every node must has only one failure");
        this.failure = failure;
        this.allEmits.addAll(failure.allEmits); //Add failure emit to this
    }

    Set<String> getTransitions() {
        return success.keySet();
    }

    Collection<State> getNextStates() {
        return success.values();
    }

    State nextState(String transition) {
        final State next = success.get(transition);
        if (root)
            return next == null ? this : next;
        return next;
    }

    @Override
    public String toString() {
        return path;
    }

    public Set<Emit> getAllEmits() {
        return allEmits;
    }
}
