package com.example.forksnews;

import static com.example.forksnews.MainActivity.LOG_TAG;

import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class to requesting and receiving news data from The Guardian
 */
public final class QueryUtils {

  // static use only
  private QueryUtils() {
  }

  // Handles News

  /**
   * Query The Guardian data and return a list of {@link News} objects.
   */
  @RequiresApi(api = VERSION_CODES.KITKAT)
  public static List<News> fetchNewsData(String requestUrl) {
    Log.i(LOG_TAG, "TEST: fetchNewsData() called");
    return extractNews(getJSONResponse(requestUrl));
  }

  /**
   * Return a list of {@link News} objects that has been built up from parsing a JSON response.
   */
  public static List<News> extractNews(String newsJSON) {
    // If the JSON string is empty or null, then return early.
    if (TextUtils.isEmpty(newsJSON)) {
      return null;
    }

    List<News> newsList = new ArrayList<>();

    try {
      JSONObject base = new JSONObject(newsJSON);
      JSONObject response = base.getJSONObject("response");
      JSONArray results = response.getJSONArray("results");
      for (int i = 0; i < results.length(); i++) {
        JSONObject result = results.getJSONObject(i);

        String title = result.getString("webTitle");
        String section = result.getString("sectionName");
        String publicationDate = result.getString("webPublicationDate");
        String contributor = getContributorFrom(result);
        String url = result.getString("webUrl");
        String thumbnail = result.getJSONObject("fields").getString("thumbnail");

        newsList.add(new News(title, section, publicationDate, contributor, url, thumbnail));
      }
    } catch (JSONException e) {
      Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
    }

    return newsList;
  }

  // Helper methods

  /**
   * Returns new URL object from the given string URL.
   */
  private static URL createUrl(String stringUrl) {
    URL url = null;
    try {
      url = new URL(stringUrl);
    } catch (MalformedURLException e) {
      Log.e(LOG_TAG, "Problem building the URL ", e);
    }
    return url;
  }

  /**
   * Make an HTTP request to the given URL and return a String as the response.
   */
  @RequiresApi(api = VERSION_CODES.KITKAT)
  private static String makeHttpRequest(URL url) throws IOException {
    String jsonResponse = "";

    // If the URL is null, then return early.
    if (url == null) {
      return jsonResponse;
    }

    HttpURLConnection urlConnection = null;
    InputStream inputStream = null;
    try {
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setReadTimeout(10000 /* milliseconds */);
      urlConnection.setConnectTimeout(15000 /* milliseconds */);
      urlConnection.setRequestMethod("GET");
      urlConnection.connect();

      // If the request was successful (response code 200),
      // then read the input stream and parse the response.
      if (urlConnection.getResponseCode() == 200) {
        inputStream = urlConnection.getInputStream();
        jsonResponse = readFromStream(inputStream);
      } else {
        Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
      }
    } catch (IOException e) {
      Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      if (inputStream != null) {
        // Closing the input stream could throw an IOException, which is why
        // the makeHttpRequest(URL url) method signature specifies than an IOException
        // could be thrown.
        inputStream.close();
      }
    }
    return jsonResponse;
  }

  /**
   * Convert the {@link InputStream} into a String which contains the whole JSON response from the
   * server.
   */
  @RequiresApi(api = VERSION_CODES.KITKAT)
  private static String readFromStream(InputStream inputStream) throws IOException {
    StringBuilder output = new StringBuilder();
    if (inputStream != null) {
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
          StandardCharsets.UTF_8);
      BufferedReader reader = new BufferedReader(inputStreamReader);
      String line = reader.readLine();
      while (line != null) {
        output.append(line);
        line = reader.readLine();
      }
    }
    return output.toString();
  }


  /**
   * Make HTTP request and return a json response from a {@param requestUrl}
   */
  @RequiresApi(api = VERSION_CODES.KITKAT)
  private static String getJSONResponse(String requestUrl) {
    // Perform HTTP request to the URL and receive a JSON response back
    String jsonResponse = null;
    try {
      jsonResponse = makeHttpRequest(createUrl(requestUrl));
    } catch (IOException e) {
      Log.e(LOG_TAG, "Problem making the HTTP request.", e);
    }
    return jsonResponse;
  }

  /**
   * Return a properly formatted contributor string from a JSON Object result
   */
  private static String getContributorFrom(JSONObject result) {
    String contributor;
    try {
      contributor = result
          .getJSONArray("tags")
          .getJSONObject(0)
          .getString("webTitle");
      try {
        if (result.getJSONArray("tags").getJSONObject(1) != null) {
          contributor += " et al.";
        }
      } catch (JSONException ignored) {
        // do nothing
      }
    } catch (JSONException e) {
      contributor = "Anonymous";
    }
    return contributor;
  }
}
