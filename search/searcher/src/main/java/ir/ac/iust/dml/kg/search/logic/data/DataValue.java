package ir.ac.iust.dml.kg.search.logic.data;

public class DataValue {
    private String string;
    private String link;

    public DataValue(String string, String link) {
        this.string = string;
        this.link = link;
    }

    public DataValue() {
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
