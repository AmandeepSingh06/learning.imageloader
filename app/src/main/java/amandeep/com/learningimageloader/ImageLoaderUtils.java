package amandeep.com.learningimageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static android.os.Environment.isExternalStorageRemovable;

/**
 * Created by amandeepsingh on 25/09/16.
 */

public class ImageLoaderUtils {

    private static ImageLoaderUtils instance;
    public static int imageHeight;
    public static int imageWidth;
    public static HashMap<ImageView, LoadingSingleImageAsyncTask> hashMap = new HashMap<>();
    private static LruCache<String, CacheInput> cache;
    public static final Object mDiskCacheLock = new Object();
    public static File cacheDir;
    private static final long MAX_DISK_CACHE_SIZE = 10485760;//10MB
    private static long diskCacheSize = 0;


    public static ImageLoaderUtils getInstance() {
        if (instance == null) {
            instance = new ImageLoaderUtils();
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheMemory = maxMemory / 4;
            cache = new LruCache<String, CacheInput>(cacheMemory) {
                @Override
                protected int sizeOf(String key, CacheInput value) {
                    return value.getBitmap().getByteCount() / 1024;
                }
            };
            cacheDir = getDiskCacheDir(MainApplication.getContext(), "learningImageLoader");
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            } else {
                calculateSizeOfDiskCache();
            }
        }
        return instance;
    }

    private static void calculateSizeOfDiskCache() {
        long length = 0;
        for (File file : cacheDir.listFiles()) {
            if (file.isFile())
                length += file.length();
        }
        setDiskCacheSize(length);
    }

    public static void addBitmapToDiskCache(String key, CacheInput cacheInput) {
        if (getBitmapFromDiskCache(key) != null) {
            return;
        }
        while (spaceNotAvailableInDiskCache(cacheInput.getBitmap())) {
            removeLeastRecentlyAddedItem();
        }
        File file = new File(cacheDir, key);
        try {
            FileOutputStream out = new FileOutputStream(file);
            cacheInput.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, out);
            setDiskCacheSize(getDiskCacheSize() + file.length());
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void removeLeastRecentlyAddedItem() {
        File[] files = cacheDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            for (int j = i + 1; j < files.length; j++) {
                if (files[i].lastModified() > files[j].lastModified()) {
                    File temp = files[i];
                    files[i] = files[j];
                    files[j] = temp;
                }
            }
        }
        long lengthOfItemToBeRemoved = files[0].length();
        if (files[0].delete()) {
            setDiskCacheSize(getDiskCacheSize() - lengthOfItemToBeRemoved);
        }
    }

    private static boolean spaceNotAvailableInDiskCache(Bitmap bitmap) {
        return bitmap.getByteCount() + diskCacheSize > MAX_DISK_CACHE_SIZE;
    }

    public static Bitmap getBitmapFromDiskCache(String key) {
        return BitmapFactory.decodeFile(cacheDir + "/" + key);
    }


    public static void addBitmapToCache(String key, CacheInput cacheInput) {
        if (getBitmapFromCache(key) == null) {
            cache.put(key, cacheInput);
        }
    }

    private static CacheInput getBitmapFromCache(String key) {
        return cache.get(key);
    }

    public static long getDiskCacheSize() {
        return diskCacheSize;
    }

    public static void setDiskCacheSize(long size) {
        diskCacheSize = size;
    }

    public void init(int width, int height) {
        imageWidth = width;
        imageHeight = height;
    }

    public static void setImage(ImageView imageView, String imageUrl, int position) {
        CacheInput input = getBitmapFromCache(imageUrl);
        if (input != null && input.getBitmap() != null && input.getPosition() == position) {
            imageView.setImageBitmap(input.getBitmap());
            hashMap.put(imageView, null);
        } else {
            Bitmap bitmap = getBitmapFromDiskCache(imageUrl);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                hashMap.put(imageView, null);
            } else {
                imageView.setImageResource(android.R.color.transparent);
                imageView.setBackgroundColor(MainApplication.getContext().getResources().getColor(R.color.colorAccent));
                LoadingSingleImageAsyncTask task = new LoadingSingleImageAsyncTask(imageView, position, imageUrl, imageWidth, imageHeight);
                if (!cancelAsyncTask(imageView, position)) {
                    hashMap.put(imageView, task);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }
    }

    private static boolean cancelAsyncTask(ImageView imageView, int position) {
        if (hashMap.containsKey(imageView)) {
            if (hashMap.get(imageView) == null) {
                return false;
            }
            LoadingSingleImageAsyncTask lastTask = hashMap.get(imageView);
            int lastPosition = lastTask.getPosition();
            if (position == lastPosition) {
                return true;
            } else {
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

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
// but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
}
