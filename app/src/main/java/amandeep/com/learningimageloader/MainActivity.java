package amandeep.com.learningimageloader;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends FragmentActivity implements RecyclerViewDataAsyncTask.CallBackInterface {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private final String url = "https://api.myjson.com/bins/55q7k";
    private RecyclerViewDataAsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoaderUtils();
        initViews();
        getDataFromServer();
    }

    private void initImageLoaderUtils() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int margin = (int) MainApplication.getContext().getResources().getDimension(R.dimen.activity_horizontal_margin);
        width = width - 2 * margin;
        int height = (int) MainApplication.getContext().getResources().getDimension(R.dimen.image_height);
        ImageLoaderUtils.getInstance().init(width, height);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public void getDataFromServer() {
        task = new RecyclerViewDataAsyncTask(url, this);
        task.execute();
    }

    @Override
    public void onSuccess(List<String> imageUrls) {
        task = null;
        recyclerViewAdapter.setData(imageUrls);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String exception) {
        task = null;
        Toast.makeText(this, exception, Toast.LENGTH_LONG);
    }

    @Override
    protected void onDestroy() {
        if (task != null) {
            task.unregisterCallback();
            task = null;
        }
        super.onDestroy();

    }
}
