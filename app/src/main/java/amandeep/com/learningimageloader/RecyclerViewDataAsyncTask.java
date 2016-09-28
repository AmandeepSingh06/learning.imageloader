package amandeep.com.learningimageloader;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by amandeepsingh on 25/09/16.
 */

public class RecyclerViewDataAsyncTask extends AsyncTask<Void, Void, List<String>> {

    private String url;
    private CallBackInterface callBackInterface;

    RecyclerViewDataAsyncTask(String url, CallBackInterface callBackInterface) {
        this.url = url;
        this.callBackInterface = callBackInterface;
    }

    @Override
    protected List<String> doInBackground(Void... params) {

        URL url = null;
        try {
            url = new URL(getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String response = convertResponseString(in);
            if (response != null) {
                ArrayList<String> imageUrls = new ArrayList<>();
                JSONObject object = new JSONObject(response);
                JSONArray urls = (JSONArray) object.get("urls");
                for (int position = 0; position < urls.length(); position++) {
                    JSONObject obj = (JSONObject) urls.get(position);
                    String imageUrl = obj.getString("url");
                    imageUrls.add(imageUrl);
                }
                return imageUrls;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertResponseString(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        try {
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(List<String> strings) {
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
        void onSuccess(List<String> imageUrls);

        void onFailure(String exception);
    }
}
