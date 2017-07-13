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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressWarnings("FieldCanBeLocal")
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
    @BindView(R.id.search_button) ImageView searchButton;

    // A text view to show the most recently searched term to the user
    @BindView(R.id.search_term)
    TextView searchTermTextView;

    // The RecyclerView which displays the list of book items.
    @BindView(R.id.book_list) RecyclerView bookListView;

    // The Spinner which displays the options for the number of search results
    @BindView(R.id.num_results) Spinner numResultsSpinner;

    // The custom adapter for displaying list of book items.
    private BookAdapter bookAdapter;

    // The custom spinner adapter.
    private NumResultsAdapter numResultsAdapter;

    // The list of integers initialised to populate the spinner.
    private List<Integer> numResultsOptions = new ArrayList<Integer>(){{
        add(10);
        add(20);
        add(30);
        add(40);
    }};

    private int numResultsChoice = MAX_RESULTS_DEFAULT;
    private String searchTerm;

    private List<Book> bookList = new ArrayList<>();

    private String httpRequestUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        searchButton.setOnClickListener(this);

        initSpinner();

        initBookAdapter();
    }

    /**
     * Check network connectivity
     */
    private void initNetworkConnectivityCheck() {
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

    /**
     * For the {@link RecyclerView}, set the {@link BookAdapter} and use a LinearLayoutManager
     * to set it to vertical.
     */
    private void initBookAdapter() {
        bookAdapter = new BookAdapter(this, bookList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        bookListView.setLayoutManager(layoutManager);
        bookListView.setAdapter(bookAdapter);
    }

    /**
     * Create adapter and set the adapter to the spinner. Also set an OnItemSelectedListener to
     * store the user choice.
     */
    private void initSpinner() {
        numResultsAdapter = new NumResultsAdapter(this, numResultsOptions);
        numResultsSpinner.setAdapter(numResultsAdapter);

        numResultsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numResultsChoice = (int)parent.getSelectedItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                numResultsChoice = MAX_RESULTS_DEFAULT;
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.search_button:
                enterSearch();
                break;
        }
    }

    /**
     * Hide keyboard.
     */
    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(LOG_TAG, getString(R.string.keyboard_hide_exception), e);
        }
    }

    /**
     * A small method called when the search button is entered.
     */
    public void enterSearch() {
        bookAdapter.clear();
        searchTerm = bookSearchEditText.getText().toString();
        searchTermTextView.setText(getString(R.string.search_term, searchTerm));
        bookSearchEditText.setText("");
        httpRequestUrl = prepareRequestUrl();
        hideKeyboard();
        initNetworkConnectivityCheck();
        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, this);
        initBookAdapter();
    }

    /**
     * This method prepares the request used to fetch data from Google API
     * @return search query
     */
    public String prepareRequestUrl() {
        String requestUrl = BOOK_REQUEST_URL;
        requestUrl += searchTerm;
        requestUrl += "&maxResults=" + numResultsChoice;
        Log.e(LOG_TAG, requestUrl);

        return (requestUrl);
    }

    // loader instances
    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, httpRequestUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> bookList) {
        // filling bookAdapter
        if (bookList != null && !bookList.isEmpty()) {
            bookAdapter.reloadList(bookList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        bookAdapter.clear();
        bookAdapter.notifyDataSetChanged();
    }
}