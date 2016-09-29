package amandeep.com.learningimageloader;

import android.graphics.Bitmap;

/**
 * Created by amandeepsingh on 29/09/16.
 */

public class CacheInput {
    private int position;
    private Bitmap bitmap;

    public CacheInput(int position, Bitmap bitmap) {
        this.position = position;
        this.bitmap = bitmap;
    }

    public int getPosition() {
        return position;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}