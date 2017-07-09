package com.example.android.booklistingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Reggie on 09/07/2017. A custom class to handle downloading of images
 */

class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;
    private ProgressDialog progressDialog;
    private Context context;

    public DownloadImage(ImageView imageView, Context context) {
        this.imageView = imageView;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Create a progressdialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.progress_dialog_title));
        progressDialog.setMessage(context.getString(R.string.progress_dialog_message));
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... URL) {

        String imageURL = URL[0];

        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        // Set the bitmap into ImageView
        imageView.setImageBitmap(result);
        // Close progressdialog
        progressDialog.dismiss();
    }
}
