/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.access.repositories;

import ir.ac.iust.dml.kg.raw.services.access.entities.Article;
import ir.ac.iust.dml.kg.raw.services.access.entities.User;
import org.springframework.data.domain.Page;

public interface ArticleRepositoryCustom {
  Page<Article> search(int page, int pageSize, String path, String title,
                       Integer minPercentOfRelations, Boolean approved, User selectedByUser);
}
