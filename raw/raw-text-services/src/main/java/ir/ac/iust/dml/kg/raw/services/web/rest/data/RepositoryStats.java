/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.services.web.rest.data;

public class RepositoryStats {
  private SourceStats wikipedia = new SourceStats();
  private SourceStats news = new SourceStats();

  public SourceStats getWikipedia() {
    return wikipedia;
  }

  public void setWikipedia(SourceStats wikipedia) {
    this.wikipedia = wikipedia;
  }

  public SourceStats getNews() {
    return news;
  }

  public void setNews(SourceStats news) {
    this.news = news;
  }

  public class SourceStats {
    private int numberOfArticles;
    private int numberOfSentences;
    private int numberOfRelations;

    public int getNumberOfArticles() {
      return numberOfArticles;
    }

    public void setNumberOfArticles(int numberOfArticles) {
      this.numberOfArticles = numberOfArticles;
    }

    public int getNumberOfSentences() {
      return numberOfSentences;
    }

    public void setNumberOfSentences(int numberOfSentences) {
      this.numberOfSentences = numberOfSentences;
    }

    public int getNumberOfRelations() {
      return numberOfRelations;
    }

    public void setNumberOfRelations(int numberOfRelations) {
      this.numberOfRelations = numberOfRelations;
    }
  }
}

