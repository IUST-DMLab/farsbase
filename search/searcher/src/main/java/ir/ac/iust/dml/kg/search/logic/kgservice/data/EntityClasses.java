package ir.ac.iust.dml.kg.search.logic.kgservice.data;

import java.util.ArrayList;
import java.util.List;

public class EntityClasses {
  private String mainClass;
  private List<String> classTree = new ArrayList<>();

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  public List<String> getClassTree() {
    return classTree;
  }

  public void setClassTree(List<String> classTree) {
    this.classTree = classTree;
  }
}
