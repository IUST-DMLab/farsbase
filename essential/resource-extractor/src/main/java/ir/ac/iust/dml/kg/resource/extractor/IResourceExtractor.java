package ir.ac.iust.dml.kg.resource.extractor;

import java.util.List;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Interface for extract resource
 */
public interface IResourceExtractor {
    void setup(IResourceReader reader, int pageSize) throws Exception;

    void setup(IResourceReader reader, ILabelConverter converter, int pageSize) throws Exception;

    List<MatchedResource> search(String text, Boolean removeSubset, Boolean removeCategory);

    Resource getResourceByIRI(String iri);
}
