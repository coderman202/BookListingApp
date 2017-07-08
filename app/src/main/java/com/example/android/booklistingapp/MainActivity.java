package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<List<Book>>, View.OnClickListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int BOOK_LOADER_ID = 1;

    // The base Google API URL which requires the search term to be added to it
    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    // Setting the max number of results to a default amount. Future versions may allow the user to
    // select how many results they want to display.
    private static final int MAX_RESULTS_DEFAULT = 20;

    // The edit text for the user to enter a search term and find any books matching that term.
    // And the button for completing the search.
    @BindView(R.id.book_list_search_box) EditText bookSearchEditText;
    @BindView(R.id.search_button) ImageButton searchButton;

    // The RecyclerView which displays the list of book items.
    @BindView(R.id.book_list) RecyclerView bookListView;

    // The custom adapter for displaying list of book items.
    private BookAdapter adapter;

    private String searchTerm;

    private List<Book> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        adapter = new BookAdapter(this, bookList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bookListView.setLayoutManager(layoutManager);
        bookListView.setAdapter(adapter);

        //searchTerm = bookSearchEditText.getText().toString();
        searchTerm = "Far";

        // Check network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check network info and make sure there is one
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // If there is no network info, tell the user
            Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view){

    }

    /**
     * This method prepares the request used to fetch data from Google API
     * @return search query
     */
    public String prepareRequestUrl() {

        String httpRequestUrl = BOOK_REQUEST_URL;
        httpRequestUrl += searchTerm;
        httpRequestUrl += "&maxResults=" + MAX_RESULTS_DEFAULT;
        Log.d(LOG_TAG, httpRequestUrl);

        return(httpRequestUrl);
    }

    // loader instances
    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        String httpRequestUrl = prepareRequestUrl();
        return new BookLoader(this, httpRequestUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> bookList) {


        // filling adapter
        if (bookList != null && !bookList.isEmpty()) {
            adapter.reloadList(bookList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        adapter.clear();
    }

}
