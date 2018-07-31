package ir.ac.iust.dml.kg.search.services.web.rest;

import ir.ac.iust.dml.kg.search.logic.kgservice.KgServiceLogic;
import ir.ac.iust.dml.kg.search.logic.kgservice.data.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * To avoid adding spring dependency to searcher library, we wrote a wrapper class to use cache feature of spring
 * with ease.
 */
@Service
public class KgServiceLogicServiceWrapper extends KgServiceLogic {

  @Cacheable("getParent")
  @Override
  public ParentNode getParent(String childUrl) {
    return super.getParent(childUrl);
  }

  @Cacheable("getChildren")
  @Override
  public ChildNodes getChildren(String parentUrl) {
    return super.getChildren(parentUrl);
  }

  @Cacheable("getClassInfo")
  @Override
  public ClassInfo getClassInfo(String url) {
    return super.getClassInfo(url);
  }

  @Cacheable("getEntityInfo")
  @Override
  public EntityData getEntityInfo(String url) {
    return super.getEntityInfo(url);
  }

  @Cacheable("getEntityClasses")
  @Override
  public EntityClasses getEntityClasses(String url) {
    return super.getEntityClasses(url);
  }

  @Cacheable("getEntitiesOfClass")
  @Override
  public Entities getEntitiesOfClass(String classUrl, int page, int pageSize) {
    return super.getEntitiesOfClass(classUrl, page, pageSize);
  }


}
