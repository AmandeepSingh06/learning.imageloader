package amandeep.com.learningimageloader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amandeepsingh on 25/09/16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<String> imagesUrl = new ArrayList<>();
    private static int count = 0;

    public void setData(List<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_recycler_view, parent, false);
        RecyclerViewHolder holder = new RecyclerViewHolder(view);
        holder.imageView.setTag(count++);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.textView.setText("Image Number is " + (29 - position));
        ImageLoaderUtils.getInstance().setImage(holder.imageView, getItem(position), position);
    }

    private String getItem(int position) {
        return imagesUrl.get(position);
    }

    @Override
    public int getItemCount() {
        return imagesUrl.size();
    }
}
