package ir.ac.iust.dml.kg.knowledge.store.services.v1;

import org.eclipse.rdf4j.rio.RDFFormat;

/**
 *
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Format of export
 */
@Deprecated
public enum ExportFormat {
    RDFXML, NTRIPLES, TURTLE, N3, NQUADS, JSONLD, RDFJSON;

    public RDFFormat getRDFFormat() {
        switch (this) {
            case RDFXML:
                return RDFFormat.RDFXML;
            case NTRIPLES:
                return RDFFormat.NTRIPLES;
            case TURTLE:
                return RDFFormat.TURTLE;
            case N3:
                return RDFFormat.N3;
            case NQUADS:
                return RDFFormat.NQUADS;
            case JSONLD:
                return RDFFormat.JSONLD;
            case RDFJSON:
                return RDFFormat.RDFJSON;
        }
        return null;
    }
}
