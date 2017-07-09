package com.example.android.booklistingapp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Reggie on 08/07/2017. A utility class for handling the requesting and fetching of
 * book info.
 */

public final class BookUtils {

    private static final int RESPONSE_CODE = 200;
    private static final String LOG_TAG = BookUtils.class.getSimpleName();
    private static final int TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final String REQUEST_METHOD = "GET";
    private static final String CHARSET_NAME = "UTF-8";
    private static final int SLEEP_MILLIS = 2000;

    private static Context context;
    private static URL requestUrl;

    // Google Book API Keys
    private static final String API_KEY_ITEMS = "items";
    private static final String API_KEY_VOLUME_INFO = "volumeInfo";
    private static final String API_KEY_TITLE = "title";
    private static final String API_KEY_AUTHORS = "authors";
    private static final String API_KEY_PUBLISHER = "publisher";
    private static final String API_KEY_PUBLISHED_DATE = "publishedDate";
    private static final String API_KEY_CATEGORIES = "categories";
    private static final String API_KEY_PAGECOUNT = "pageCount";
    private static final String API_KEY_RATING = "averageRating";
    private static final String API_KEY_RATINGS_COUNT = "ratingsCount";
    private static final String API_KEY_DESCRIPTION = "description";
    private static final String API_KEY_IMAGE_LINKS = "imageLinks";
    private static final String API_KEY_THUMBNAIL = "smallThumbnail";

    private BookUtils(){

    }

    /**
     * Make a URL request to return a {@link List<Book>}
     * @param   context             context
     * @param   requestUrlString    A request url in String form
     * @return                      A list of books.
     */
    public static List<Book> getBookData(Context context, String requestUrlString){
        BookUtils.context = context;
        requestUrl = createUrl(requestUrlString);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(requestUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.http_request_exception), e);
        }

        // Getting the list of {@link Book}
        List<Book> bookList = extractDetailsFromJSON(jsonResponse);
        Log.d(LOG_TAG, jsonResponse);

        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return bookList;
    }

    /**
     * This method checks the URL request String to ensure it is properly formed and returns a
     * valid URL if so, or null otherwise.
     */
    private static URL createUrl(String requestUrlString) {
        URL url = null;
        try {
            url = new URL(requestUrlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, context.getString(R.string.url_exception), e);
        }
        return url;
    }

    /**
     * Make A HTTP request to the given URL and return a JSON response in String form.
     * @param url       the url.
     * @return String   the JSON response in String form
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, context.getString(R.string.response_code_exception,
                        urlConnection.getResponseCode()));
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.json_exception), e);
        } catch(NullPointerException e){
            Log.e(LOG_TAG, context.getString(R.string.null_pointer_exception), e);
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName(CHARSET_NAME));
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
     * Return a list of {@link Book} objects retrieved from parsing a JSON response.
     */
    private static List<Book> extractDetailsFromJSON(String jsonReponse) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonReponse)) {
            return null;
        }

        // Create an empty ArrayList used to add books.
        List<Book> bookList = new ArrayList<>();

        try {
            // JSON object from the API request
            JSONObject jsonResponseOject;
            // An array of books that are contained in the above JSON object
            JSONArray bookArray;
            // The current book in the array which will be accessed to access the the information.
            // of the book.
            JSONObject currentBook;

            // The JSON object which holds the title, author(s) and publisher details.
            JSONObject volumeInfo;

            // The JSON array which stores an array of one or more authors, if any.
            JSONArray authorsArray;
            List<String> authorList = new ArrayList<>();

            String title = "";
            String publisher = "";
            String description = "";

            // The JSON object which stores the links to the thumbnails
            JSONObject imageLinks;
            String thumbnailUrl = "";

            // The JSON array which stores an array of one or more categories, if any.
            JSONArray categoriesArray;
            List<String> categoryList = new ArrayList<>();

            // The String of the date and I will store the date in a date object
            String datePublished;
            Date publishDate = null;


            int numPages = 0;
            float rating = 0;
            int ratingsCount = 0;


            jsonResponseOject = new JSONObject(jsonReponse);

            if (jsonResponseOject.has(API_KEY_ITEMS)) {
                bookArray = jsonResponseOject.getJSONArray(API_KEY_ITEMS);

                for (int i = 0; i < bookArray.length(); i++) {

                    currentBook = bookArray.getJSONObject(i);
                    volumeInfo = currentBook.getJSONObject(API_KEY_VOLUME_INFO);
                    
                    if(volumeInfo.has(API_KEY_TITLE)){
                        title = volumeInfo.getString(API_KEY_TITLE);
                    }                    

                    // Get all the authors if there are any
                    if (volumeInfo.has(API_KEY_AUTHORS)) {
                        authorsArray = volumeInfo.getJSONArray(API_KEY_AUTHORS);
                        for (int j = 0; j < authorsArray.length(); j++) {
                            authorList.add(authorsArray.getString(j));
                        }
                    }

                    // Get the publisher
                    if (volumeInfo.has(API_KEY_PUBLISHED_DATE)) {
                        publisher = volumeInfo.getString(API_KEY_PUBLISHER);
                    }

                    // Get published date and convert it to a Date object
                    if (volumeInfo.has(API_KEY_PUBLISHED_DATE)) {
                        datePublished = volumeInfo.getString(API_KEY_PUBLISHED_DATE);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        try{
                            publishDate = sdf.parse(datePublished);
                        }
                        catch (ParseException e){
                            Log.e(LOG_TAG, context.getString(R.string.parse_exception), e);
                        }
                    }

                    // Get all categories if there are any
                    if (volumeInfo.has(API_KEY_CATEGORIES)) {
                        categoriesArray = volumeInfo.getJSONArray(API_KEY_CATEGORIES);
                        for (int j = 0; j < categoriesArray.length(); j++) {
                            categoryList.add(categoriesArray.getString(j));
                        }
                    }

                    // Get page count
                    if (volumeInfo.has(API_KEY_PAGECOUNT)) {
                        numPages = volumeInfo.getInt(API_KEY_PAGECOUNT);
                    }

                    // Get book description
                    if (volumeInfo.has(API_KEY_DESCRIPTION)) {
                        description = volumeInfo.getString(API_KEY_DESCRIPTION);
                    }

                    // Get value for rating if the key exists
                    if (volumeInfo.has(API_KEY_RATING)) {
                        rating = (float) volumeInfo.getDouble(API_KEY_RATING);
                    }

                    // Get value for count of the number of ratings if the key exists
                    if (volumeInfo.has(API_KEY_RATINGS_COUNT)) {
                        ratingsCount = volumeInfo.getInt(API_KEY_RATINGS_COUNT);
                    }

                    // Get the thumbnail url
                    if (volumeInfo.has(API_KEY_IMAGE_LINKS)) {
                        imageLinks = volumeInfo.getJSONObject(API_KEY_IMAGE_LINKS);
                        if (imageLinks.has(API_KEY_THUMBNAIL)) {
                            thumbnailUrl = imageLinks.getString(API_KEY_THUMBNAIL);
                        }
                    }

                    Book book = new Book(title, authorList, publisher, categoryList, description, publishDate, numPages, thumbnailUrl, rating, ratingsCount);

                    // Add the new {@link Book} object to the list of books
                    bookList.add(book);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, context.getString(R.string.json_exception), e);
        }
        return bookList;
    }
}
