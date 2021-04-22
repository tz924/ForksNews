package com.example.forksnews;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements LoaderCallbacks<List<News>> {

  /* CONSTANTS */
  // Reuse the same loader by specifying ID
  private static final int NEWS_LOADER_ID = 1;
  private static final String API_KEY = "9cd3ecdd-3d2b-45c1-8db1-f14ca8bdedc2";
  private static final String BASE_URL = "http://content.guardianapis.com";
  private static final String NEWS_REQUEST_URL = BASE_URL + "/search";
  public static final String LOG_TAG = MainActivity.class.getSimpleName();

  /* Members */
  private List<Section> sections;
  private NewsAdapter newsAdapter;
  private TextView emptyTextView;
  private ImageView loadingIndicator;
  private RecyclerView rvRelated;
  private RecyclerView rvSection;
  private CardView mainCard;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Set up header
    findViews();

    setTextView(R.id.title_header, R.string.title_header);

    setUpSections();

    setUpLoadingView();

    setUpRelated();

    setUpEmptyView();

    if (setUpNetwork()) {
      LoaderManager loaderManager = getLoaderManager();

      Log.i(LOG_TAG, "TEST: calling initLoader()");
      loaderManager.initLoader(NEWS_LOADER_ID, null, this);
    } else {
      setUpNoInternetView();
    }
  }

  private void setUpLoadingView() {
    loadingIndicator.setVisibility(View.VISIBLE);
    emptyTextView.setVisibility(View.VISIBLE);
    emptyTextView.setText(R.string.loading);
  }

  private void findViews() {
    rvSection = findViewById(R.id.section_rv);
    rvRelated = findViewById(R.id.related_rv);
    loadingIndicator = findViewById(R.id.loading_indicator);
    emptyTextView = findViewById(R.id.empty);
    mainCard = findViewById(R.id.card_main);
  }

//  Handles Loader

  /**
   * Set up and return a {@link Loader} for a given ID
   */
  @Override
  public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
    Log.i(LOG_TAG, "TEST: onCreateLoader() called");

    Uri.Builder uriBuilder = Uri.parse(NEWS_REQUEST_URL).buildUpon();
    uriBuilder.appendQueryParameter("q", "conspiracy");
    uriBuilder.appendQueryParameter("show-fields", "thumbnail");
    uriBuilder.appendQueryParameter("show-tags", "contributor");
    uriBuilder.appendQueryParameter("api-key", API_KEY);

    System.out.println(uriBuilder.toString());
    return new NewsLoader(this, uriBuilder.toString());
  }

  /**
   *
   */
  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
    Log.i(LOG_TAG, "TEST: onLoadFinished() called");

    // Hide loading indicator because the data has been loaded
    loadingIndicator.setVisibility(View.GONE);
    emptyTextView.setVisibility(View.GONE);
    mainCard.setVisibility(View.VISIBLE);

    // Set empty state text to display "No news found."
    emptyTextView.setText(getString(R.string.no_news));

    // Update UI
    newsAdapter.clear();

    if (newsList != null && !newsList.isEmpty()) {
      setUpMain(((ArrayList<News>) newsList).remove(0));
      newsAdapter.addAll(newsList);
    } else {
      setUpEmptyView();
    }
  }

  /**
   * Clear out existing data
   */
  @Override
  public void onLoaderReset(Loader<List<News>> loader) {
    Log.i(LOG_TAG, "TEST: onLoaderReset() called");
    newsAdapter.clear();
  }

  // Helper methods
  private void setTextView(int resourceId, int stringId) {
    TextView textView = findViewById(resourceId);
    textView.setText(getString(stringId));
  }

  private void setTextView(int resourceId, String text) {
    TextView textView = findViewById(resourceId);
    textView.setText(text);
  }

  private void setImageView(int resourceId, int imageResourceId) {
    ImageView imageView = findViewById(resourceId);
    imageView.setImageResource(imageResourceId);
  }

  private void setImageView(int resourceId, String url) {
    ImageView imageView = findViewById(resourceId);
    Glide.with(this).load(url).into(imageView);
  }

  private void setUpSections() {
    // Inflate sections with fake data
    sections = new ArrayList<>();
    sections.add(new Section("News"));
    sections.add(new Section("Opinion"));
    sections.add(new Section("Sport"));
    sections.add(new Section("Culture"));
    sections.add(new Section("Lifestyle"));

    SectionAdapter sectionAdapter = new SectionAdapter(sections);
    rvSection.setAdapter(sectionAdapter);

    // set up horizontal scroller and ensure no cutoff on the right bound
    rvSection.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.HORIZONTAL, false) {
      @Override
      public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        lp.width = (int) Math.round(getWidth() * .3);
        return true;
      }
    });
  }

  private void setUpRelated() {
    newsAdapter = new NewsAdapter(new ArrayList<>());
    rvRelated.setAdapter(newsAdapter);
    rvRelated.setLayoutManager(new LinearLayoutManager(this,
        LinearLayoutManager.HORIZONTAL, false) {
      @Override
      public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        lp.width = (int) Math.round(getWidth() * .42);
        return true;
      }
    });

    ItemClickSupport.addTo(rvRelated).setOnItemClickListener(
        (recyclerView, position, v) -> {
          News currentNews = newsAdapter.getItem(position);
          Uri newsUri = Uri.parse(currentNews.getUrl());
          startActivity(new Intent(Intent.ACTION_VIEW, newsUri));
        }
    );
  }


  // TODO
  private void setUpEmptyView() {
    emptyTextView.setText(R.string.no_news);
    emptyTextView.setVisibility(View.VISIBLE);
    loadingIndicator.setVisibility(View.GONE);
    mainCard.setVisibility(View.GONE);
  }

  // TODO
  private boolean setUpNetwork() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
        Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  @RequiresApi(api = VERSION_CODES.O)
  private void setUpMain(News mainNews) {
    setTextView(R.id.title_main, mainNews.getTitle());
    setTextView(R.id.section_main, mainNews.getSection());
    setTextView(R.id.contributor_main, mainNews.getContributor());
    setTextView(R.id.datetime_main, mainNews.getLocalTime());
    setImageView(R.id.image_main, mainNews.getThumbnail());

    mainCard.setOnClickListener(view -> {
      Uri newsUri = Uri.parse(mainNews.getUrl());
      startActivity(new Intent(Intent.ACTION_VIEW, newsUri));
    });
  }

  // TODO
  private void setUpNoInternetView() {
    Log.i(LOG_TAG, "TEST: setUpNoInternetView() called");
    loadingIndicator.setVisibility(View.GONE);
    emptyTextView.setText(R.string.no_internet_connection);
    emptyTextView.setVisibility(View.VISIBLE);
    mainCard.setVisibility(View.GONE);
  }
}