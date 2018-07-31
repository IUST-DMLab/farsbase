package ir.ac.iust.dml.kg.resource.extractor;


import java.util.HashSet;

/**
 * Farsi Knowledge Graph Project
 * Iran University of Science and Technology (Year 2017)
 * Developed by HosseiN Khademi khaledi
 *
 * Convert label to new label
 * for example convert 'hossein (birth 79)' to 'hossein'
 * if you not return original label, it has been ignored
 */
public interface ILabelConverter {
    HashSet<String> convert(String label);
}
