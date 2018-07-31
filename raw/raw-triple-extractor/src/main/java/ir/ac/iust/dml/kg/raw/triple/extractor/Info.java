/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Mohammad Abdous.
 */

package ir.ac.iust.dml.kg.raw.triple.extractor;

public class Info {

    private String extractionStart;
    private String extractionEnd;
    private String module;


    public String getExtractionStart() {
        return extractionStart;
    }

    public void setExtractionStart(String extractionStart) {
        this.extractionStart = extractionStart;
    }

    public String getExtractionEnd() {
        return extractionEnd;
    }

    public void setExtractionEnd(String extractionEnd) {
        this.extractionEnd = extractionEnd;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
