/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.repositories;

import ir.ac.iust.dml.kg.raw.services.access.entities.DependencyPattern;
import org.springframework.data.domain.Page;

public interface DependencyPatternRepositoryCustom {

  Page<DependencyPattern> search(int page, int pageSize, Integer maxSentenceLength,
                                 Integer minSize, Boolean approved);
}
