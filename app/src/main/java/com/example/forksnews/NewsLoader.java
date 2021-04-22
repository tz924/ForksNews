package com.example.forksnews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of {@link News}s by using an AsyncTask to perform the network
 * request to the given URL
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private static final String LOG_TAG = NewsLoader.class.getSimpleName();
    private String url;

    /**
     * Constructor
     * Sets up private members
     */
    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading() called");
        forceLoad();
    }

    /**
     * Configure tasks in background thread
     */
    @Override
    public List<News> loadInBackground() {
        Log.i(LOG_TAG, "TEST: loadInBackground() called");
        if (this.url == null) return null;
        return QueryUtils.fetchNewsData(this.url);
    }
}
