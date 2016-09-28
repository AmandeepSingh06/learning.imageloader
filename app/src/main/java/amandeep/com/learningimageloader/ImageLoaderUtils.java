package amandeep.com.learningimageloader;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.util.HashMap;

/**
 * Created by amandeepsingh on 25/09/16.
 */

public class ImageLoaderUtils {

    private static ImageLoaderUtils instance;
    public static int imageHeight;
    public static int imageWidth;
    public static HashMap<ImageView, LoadingSingleImageAsyncTask> hashMap = new HashMap<>();

    public static ImageLoaderUtils getInstance() {
        if (instance == null) {
            instance = new ImageLoaderUtils();
        }
        return instance;
    }

    public void init(int width, int height) {
        imageWidth = width;
        imageHeight = height;
    }

    public static void setImage(ImageView imageView, String imageUrl, int position) {
        LoadingSingleImageAsyncTask task = new LoadingSingleImageAsyncTask(imageView, position, imageUrl, imageWidth, imageHeight);

        if (!cancelAsyncTask(imageView, position)) {
            hashMap.put(imageView, task);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static boolean cancelAsyncTask(ImageView imageView, int position) {
        if (hashMap.containsKey(imageView)) {
            LoadingSingleImageAsyncTask lastTask = hashMap.get(imageView);
            int lastPosition = lastTask.getPosition();
            if (position == lastPosition) {
                return true;
            } else {
                imageView.setImageResource(android.R.color.transparent);
                imageView.setBackgroundColor(MainApplication.getContext().getResources().getColor(R.color.colorAccent));
                if (!lastTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                    lastTask.cancel(true);
                }
            }
        }
        return false;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int requiredWidth, int requiredHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > requiredHeight || width > requiredWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= requiredHeight
                    && (halfWidth / inSampleSize) >= requiredWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
