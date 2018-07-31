package ir.ac.iust.dml.kg.resource.extractor;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Type of resource
 */
public enum ResourceType {
    Entity, Property, Category;


    @Override
    public String toString() {
        switch (this) {
            case Property:
                return "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property";
            case Entity:
                return "http://www.w3.org/2000/01/rdf-schema#Resource";
            case Category:
                return "http://www.w3.org/2009/08/skos-reference/skos.html#Concept";
        }
        throw new RuntimeException("Unknown type");
    }
}
