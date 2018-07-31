package ir.ac.iust.dml.kg.knowledge.store.access2.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * data class for encapsulate source
 * Each triple has one source
 * module: is name of extractor: wiki/text/table
 * version: is version of extractor that increment each time it has been run
 * url: page address that triple has extracted
 * parameters: parameter of extracted triples
 * precession: is not used
 */
public class Source {
    private String module;
    private Integer version;
    private String url;
    private Map<String, String> parameters;
    private Double precession;

    public Source() {
    }

    public Source(String module, String url) {
        this.module = module;
        this.url = url;
    }

    public Source(String module, Integer version, String url, Map<String, String> parameters, Double precession) {
        this.module = module;
        this.version = version;
        this.url = url;
        this.parameters = parameters;
        this.precession = precession;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParameters() {
        if (parameters == null)
            parameters = new HashMap<>();
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Double getPrecession() {
        return precession;
    }

    public void setPrecession(Double precession) {
        this.precession = precession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Source source = (Source) o;

        if (module != null ? !module.equals(source.module) : source.module != null) return false;
        return url != null ? url.equals(source.url) : source.url == null;
    }

    @Override
    public int hashCode() {
        int result = module != null ? module.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Source{ %s@%s}", url, module);
    }
}
