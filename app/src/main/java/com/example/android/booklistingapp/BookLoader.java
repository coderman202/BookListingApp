package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Reggie on 08/07/2017. A custom loader
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private static final String LOG_TAG = BookLoader.class.getSimpleName();

    private String urlQuery;
    private Context context;

    public BookLoader(Context context, String urlQuery) {
        super(context);
        this.urlQuery = urlQuery;
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // To load in a background thread
    @Override
    public List<Book> loadInBackground() {
        if (urlQuery == null) {
            return null;
        }
        Log.d(LOG_TAG, context.toString());

        // Return the {@link List<Book>} result of the http request.
        return BookUtils.getBookData(context, urlQuery);
    }
}
