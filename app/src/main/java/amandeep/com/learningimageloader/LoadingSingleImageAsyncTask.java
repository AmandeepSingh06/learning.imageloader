package amandeep.com.learningimageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.graphics.BitmapFactory.decodeStream;

/**
 * Created by amandeepsingh on 25/09/16.
 */

public class LoadingSingleImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {
    private WeakReference<ImageView> reference;
    private int position;
    private String imageUrl;
    private int width, height;

    LoadingSingleImageAsyncTask(ImageView imageView, int position, String imageUrl, int width, int height) {
        reference = new WeakReference<ImageView>(imageView);
        this.position = position;
        this.imageUrl = imageUrl;
        this.width = width;
        this.height = height;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection
                    = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            decodeStream(input, null, options);
            options.inSampleSize = ImageLoaderUtils.getInstance().calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            connection
                    = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            Bitmap bitmap = decodeStream(input, null, options);
            if (bitmap != null) {
                ImageLoaderUtils.getInstance().addBitmapToCache(imageUrl, bitmap);
                ImageLoaderUtils.getInstance().addBitmapToDiskCache(imageUrl, bitmap);
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPosition() {
        return position;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            if (shouldDisplayBitmap()) {
                reference.get().setImageBitmap(bitmap);
            }
        }
    }

    private boolean shouldDisplayBitmap() {
        if (reference != null && ImageLoaderUtils.getInstance().hashMap.containsKey(reference.get())) {
            if (ImageLoaderUtils.getInstance().hashMap.get(reference.get()) == null) {
                return false;
            }
            int currentPosition = ImageLoaderUtils.getInstance().hashMap.get(reference.get()).getPosition();
            if (currentPosition == position) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
}
