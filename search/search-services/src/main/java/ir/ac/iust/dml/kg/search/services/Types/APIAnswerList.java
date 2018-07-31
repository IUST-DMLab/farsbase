package ir.ac.iust.dml.kg.search.services.Types;

import ir.ac.iust.dml.kg.search.logic.data.ResultEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ali on 6/22/17.
 */
public class APIAnswerList {
    private List answer = new ArrayList();

    public List<Object> getAnswer() {
        return answer;
    }

    public void addAnswer(Object answer) {
        this.answer.add(answer);
    }
}
