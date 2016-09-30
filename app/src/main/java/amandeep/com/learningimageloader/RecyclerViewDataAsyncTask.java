package amandeep.com.learningimageloader;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import amandeep.com.learningimageloader.Model.InnerObject;
import amandeep.com.learningimageloader.Model.OuterObject;

/**
 * Created by amandeepsingh on 25/09/16.
 */

public class RecyclerViewDataAsyncTask extends AsyncTask<Void, Void, List<InnerObject>> {

    private String url;
    private CallBackInterface callBackInterface;

    RecyclerViewDataAsyncTask(String url, CallBackInterface callBackInterface) {
        this.url = url;
        this.callBackInterface = callBackInterface;
    }

    @Override
    protected List<InnerObject> doInBackground(Void... params) {

        Request request = new Request.Builder().url(getUrl()).build();
        try {
            Response response = NetworkUtils.getOkHttpClient().newCall(request).execute();
            if (response != null) {
                Gson gson = new Gson();
                OuterObject outerObject = gson.fromJson(response.body().charStream(), OuterObject.class);
                return outerObject.getUrls();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<InnerObject> strings) {
        if (callBackInterface != null) {
            if (strings != null && strings.size() > 0) {
                callBackInterface.onSuccess(strings);
            } else {
                callBackInterface.onFailure("Exception Occurred");
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public void unregisterCallback() {
        callBackInterface = null;
    }

    public interface CallBackInterface {
        void onSuccess(List<InnerObject> imageUrls);

        void onFailure(String exception);
    }
}
