package ir.ac.iust.dml.kg.raw.distantsupervison;

/**
 * Created by hemmatan on 8/2/2017.
 */
public class DepTreeNone {
    private int head;

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private String pos;
    private String role;

    public DepTreeNone(String pos, int head, String role){
        this.head = head;
        this.pos= pos;
        this.role = role;
    }
}
