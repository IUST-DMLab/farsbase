/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2018)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.rulebased;

import edu.stanford.nlp.ling.CoreAnnotation;

public class ResourceTagAnnotation implements CoreAnnotation<String> {
  public ResourceTagAnnotation() {
  }

  public Class<String> getType() {
    return String.class;
  }
}
