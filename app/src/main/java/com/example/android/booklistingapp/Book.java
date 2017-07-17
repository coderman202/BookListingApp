package com.example.android.booklistingapp;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Reggie on 08/07/2017. A custom class for books
 */
public class Book {

    // The title of the book.
    private String title;

    // A list of author(s).
    private List<String> authors;

    // The publisher.
    private String publisher;

    // A list of al the categories the book falls in to.
    private List<String> categories;

    // A description of the book.
    private String description;

    // The date the book was published
    private Date datePublished;

    // The number of pages of the book
    private int numPages;

    // The cover of the book
    private String thumbnailURL;

    // A rating for the book, set the default to 0
    private float rating = 0;

    // The number of ratings a book has gotten. Default set to 0;
    private int ratingsCount = 0;

    /**
     * Instantiates a new Book.
     *
     * @param title         the title
     * @param authors       the authors
     * @param publisher     the publisher
     * @param categories    the categories
     * @param description   the description
     * @param datePublished the date published
     * @param numPages      the num pages
     * @param thumbnailURL  the thumbnail url
     * @param rating        the rating
     * @param ratingsCount  the ratings count
     */
    public Book(String title, List<String> authors, String publisher, List<String> categories,
                String description, Date datePublished, int numPages, String thumbnailURL,
                float rating, int ratingsCount) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.categories = categories;
        this.description = description;
        this.datePublished = datePublished;
        this.numPages = numPages;
        this.thumbnailURL = thumbnailURL;
        this.rating = rating;
        this.ratingsCount = ratingsCount;
    }

    /**
     * Gets rating.
     *
     * @return the rating
     */
    public float getRating() {
        return rating;
    }

    /**
     * Gets ratings count.
     *
     * @return the ratings count
     */
    public int getRatingsCount() {
        return ratingsCount;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * Gets authors.
     *
     * @return the authors
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * Gets publisher.
     *
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Gets categories.
     *
     * @return the categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets date published.
     *
     * @return the date published
     */
    public Date getDatePublished() {
        return datePublished;
    }

    /**
     * Gets num pages.
     *
     * @return the num pages
     */
    public int getNumPages() {
        return numPages;
    }


    /**
     * Gets thumbnail url.
     *
     * @return the thumbnail url
     */
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    /**
     * Get the year the book was published as a String.
     *
     * @return the string
     */
    public String getYear(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        return sdf.format(this.datePublished);
    }

    /**
     * Get a String of all the authors.
     *
     * @return the string
     */
    public String getAuthorsString(){
        return  TextUtils.join(", ", this.authors);
    }

    /**
     * Get a String of all the categories.
     *
     * @return the string
     */
    public String getCategoriesString(){
        return TextUtils.join(", ", this.categories);
    }

    /**
     * Get a String of the ratings with the number of ratings the book has in brackets.
     *
     * @return the string
     */
    public String getOverallRatings(){
        return this.rating + "(" + this.ratingsCount + ")";
    }
}
