/*
 * Farsi Knowledge Graph Project
 *  Iran University of Science and Technology (Year 2017)
 *  Developed by Majid Asgari.
 */

package ir.ac.iust.dml.kg.raw.triple.extractor;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings({"WeakerAccess", "unused"})
public class FeedItem {
  @SerializedName("crawl_date")
  private String crawlDate;
  @SerializedName("pub_date")
  private String pubDate;
  private String source;
  private String text;
  private String title;

  public String getCrawlDate() {
    return crawlDate;
  }

  public void setCrawlDate(String crawlDate) {
    this.crawlDate = crawlDate;
  }

  public String getPubDate() {
    return pubDate;
  }

  public void setPubDate(String pubDate) {
    this.pubDate = pubDate;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
