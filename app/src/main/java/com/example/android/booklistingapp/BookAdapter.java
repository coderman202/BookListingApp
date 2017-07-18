package com.example.android.booklistingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Reggie on 08/07/2017. A custom adapter for displaying book information in a
 * RecyclerView
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private static final String LOG_TAG = BookAdapter.class.getSimpleName();
    private List<Book> bookList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.book_author)
        TextView authorView;
        @BindView(R.id.book_title)
        TextView titleView;
        @BindView(R.id.book_rating)
        TextView ratingView;
        @BindView(R.id.book_year)
        TextView yearView;
        @BindView(R.id.book_thumbnail)
        ImageView thumbnailView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Instantiates a new {@link BookAdapter}.
     *
     * @param context  the context
     * @param bookList the book list
     */
    BookAdapter(Context context, List<Book> bookList) {
        this.bookList = bookList;
        this.context = context;
    }

    /**
     * Reload the bookList.
     *
     * @param bookList the bookList
     */
    public void reloadList(List<Book> bookList) {
        clear();
        this.bookList.addAll(bookList);
        notifyDataSetChanged();
    }

    /**
     * Clear the bookList.
     */
    public void clear() {
        this.bookList.clear();
    }

    /**
     * Setting the views for all the elements in the RecyclerView item.
     *
     * @param viewHolder the view holder
     * @param position   the current item position
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Book book = bookList.get(position);

        String author = book.getAuthorsString();
        if (author.isEmpty()) {
            author = context.getString(R.string.unknown_author);
        }

        String title = book.getTitle();
        if (title.isEmpty()) {
            title = context.getString(R.string.unknown_title);
        }

        viewHolder.authorView.setText(author);
        viewHolder.titleView.setText(title);
        viewHolder.ratingView.setText(book.getOverallRatings());
        viewHolder.yearView.setText(book.getYear());

        // Set the content description for the thumbnail.
        String contentDescription = context.getString(R.string.book_cover, book.getTitle());
        viewHolder.thumbnailView.setContentDescription(contentDescription);

        setThumbnail(book, viewHolder);
    }

    private void setThumbnail(Book book, ViewHolder viewHolder) {
        new DownloadImage(viewHolder.thumbnailView, context).execute(book.getThumbnailURL());
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent, false);
        return new ViewHolder(view);
    }
}