package ir.ac.iust.dml.kg.knowledge.store.access2.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Keep latest version of each module
 */
@XmlType(name = "version", namespace = "http://kg.dml.iust.ac.ir")
@Document(collection = "versions")
public class Version {
    @Id
    @JsonIgnore
    private ObjectId id;
    @Indexed(unique = true)
    private String module;
    private Integer nextVersion;
    private Integer activeVersion;
    private long creationEpoch;
    private long modificationEpoch;


    public Version() {
    }

    public Version(String module) {
        this.module = module;
        this.nextVersion = 1;
        this.creationEpoch = System.currentTimeMillis();
    }

    public String getIdentifier() {
        return id.toString();
    }

    public void addNextVersion() {
        if (nextVersion == null) nextVersion = 1;
        else nextVersion++;
    }


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Integer getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(Integer nextVersion) {
        this.nextVersion = nextVersion;
    }

    public Integer getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(Integer activeVersion) {
        this.activeVersion = activeVersion;
    }

    public long getCreationEpoch() {
        return creationEpoch;
    }

    public void setCreationEpoch(long creationEpoch) {
        this.creationEpoch = creationEpoch;
    }

    public long getModificationEpoch() {
        return modificationEpoch;
    }

    public void setModificationEpoch(long modificationEpoch) {
        this.modificationEpoch = modificationEpoch;
    }
}
