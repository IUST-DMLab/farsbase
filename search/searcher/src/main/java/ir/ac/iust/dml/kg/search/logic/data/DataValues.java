package ir.ac.iust.dml.kg.search.logic.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataValues {

    private List<DataValue> values = new ArrayList<DataValue>();

    public DataValues() {
    }

    public DataValues(DataValue... values) {
        Collections.addAll(this.values, values);
    }

    private DataValues add(DataValue value) {
        values.add(value);
        return this;
    }

    public List<DataValue> getValues() {
        return values;
    }

    public void setValues(List<DataValue> values) {
        this.values = values;
    }
}
