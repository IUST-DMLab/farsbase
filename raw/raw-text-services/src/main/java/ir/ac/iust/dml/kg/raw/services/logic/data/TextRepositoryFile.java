/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.logic.data;

public class TextRepositoryFile {
  private boolean directory;
  private String name;

  public TextRepositoryFile(boolean directory, String name) {
    this.directory = directory;
    this.name = name;
  }

  public boolean isDirectory() {
    return directory;
  }

  public void setDirectory(boolean directory) {
    this.directory = directory;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
