package com.example.forksnews;

import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class News extends Item {

  private static final String LOG_TAG = News.class.getSimpleName();

  private String section;
  private String publicationDate;
  private String contributor;
  private String url;
  private String thumbnail;
  private ZonedDateTime datetime;

  /**
   * @param title           The tile string of the news e.g. "Alex Salmond speech – first minister
   *                        hits back over Scottish independence – live"
   * @param section         The section name in string. e.g. Politics
   * @param publicationDate A formatted UTC datetime string e.g. "2021-04-01T16:18:13Z"
   * @param contributor     The name of the contributor
   * @param url             The url on the Guardian site
   * @param thumbnail       The link fo the thumbnail image
   */
  @RequiresApi(api = VERSION_CODES.O)
  public News(String title, String section, String publicationDate, String contributor,
      String url, String thumbnail) {
    super(title);
    this.section = section;
    this.publicationDate = publicationDate;
    parseDate();
    this.contributor = contributor;
    this.url = url;
    this.thumbnail = thumbnail;
  }

  // Customized methods


  // Getters and Setters
  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  public String getPublicationDate() {
    return publicationDate;
  }

  public void setPublicationDate(String publicationDate) {
    this.publicationDate = publicationDate;
  }

  public String getContributor() {
    return contributor;
  }

  public void setContributor(String contributor) {
    this.contributor = contributor;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }


  @RequiresApi(api = VERSION_CODES.O)
  public String getPast() {
    Instant now = Instant.now();
    Duration duration = Duration.between(this.datetime.toInstant(), now);
    if (duration.toMinutes() < 60) {
      return duration.toMinutes() + " minutes ago";
    } else if (duration.toHours() < 24) {
      return duration.toHours() + " hours ago";
    } else {
      return duration.toDays() + " days ago";
    }
  }

  // helper methods
  @RequiresApi(api = VERSION_CODES.O)
  private void parseDate() {
    this.datetime = ZonedDateTime.parse(this.publicationDate);
  }

  @RequiresApi(api = VERSION_CODES.O)
  public String getLocalTime() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd HH:mm",
        Locale.getDefault());
    return formatter.format(this.datetime);
  }
}
