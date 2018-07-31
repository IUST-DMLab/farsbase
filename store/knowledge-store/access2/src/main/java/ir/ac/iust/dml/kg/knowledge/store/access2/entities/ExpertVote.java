package ir.ac.iust.dml.kg.knowledge.store.access2.entities;

import javax.xml.bind.annotation.XmlType;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Vote object for each expert
 * module is interface that used by expert for example: web
 * expert is uid of voter in interface
 */
@XmlType(name = "ExpertVote", namespace = "http://kg.dml.iust.ac.ir")
public class ExpertVote {
    private String module;
    private String expert;
    private Vote vote;

    public ExpertVote() {
    }

    public ExpertVote(String module, String expert, Vote vote) {
        this.module = module;
        this.expert = expert;
        this.vote = vote;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getExpert() {
        return expert;
    }

    public void setExpert(String expert) {
        this.expert = expert;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpertVote that = (ExpertVote) o;

        if (module != null ? !module.equals(that.module) : that.module != null) return false;
        return expert != null ? expert.equals(that.expert) : that.expert == null;
    }

    @Override
    public int hashCode() {
        int result = module != null ? module.hashCode() : 0;
        result = 31 * result + (expert != null ? expert.hashCode() : 0);
        return result;
    }


}
