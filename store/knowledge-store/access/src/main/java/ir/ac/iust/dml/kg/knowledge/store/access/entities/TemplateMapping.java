package ir.ac.iust.dml.kg.knowledge.store.access.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Class for mapping for map template
 * title: is template mapping class
 */
@Deprecated
@XmlType(name = "TemplateMapping", namespace = "http://kg.dml.iust.ac.ir")
@Document(collection = "template-mapping")
@CompoundIndexes({
        @CompoundIndex(name = "property_mapping", def = "{'properties.template': 1, 'properties.property' : 2}", unique = false, sparse = true)
})
public class TemplateMapping {
    @Id
    @JsonIgnore
    private ObjectId id;
    @Indexed(unique = true)
    private String template;
    private List<PropertyMapping> properties;
    private Set<MapRule> rules;
    private long creationEpoch;
    private long modificationEpoch;
    @Indexed
    private Double weight;

    public TemplateMapping() {
    }

    public TemplateMapping(String template) {
        this.creationEpoch = System.currentTimeMillis();
        this.template = template;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<PropertyMapping> getProperties() {
        if (this.properties == null)
            this.properties = new ArrayList<>();
        return properties;
    }

    public void setProperties(List<PropertyMapping> properties) {
        this.properties = properties;
    }

    public Set<MapRule> getRules() {
        if (this.rules == null)
            this.rules = new HashSet<>();
        return rules;
    }

    public void setRules(Set<MapRule> rules) {
        this.rules = rules;
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateMapping that = (TemplateMapping) o;
        return template != null ? template.equals(that.template) : that.template == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (template != null ? template.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s", template);
    }
}
