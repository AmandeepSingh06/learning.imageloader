package amandeep.com.learningimageloader;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by amandeepsingh on 25/09/16.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image_view);
    }
}
