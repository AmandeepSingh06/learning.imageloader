package amandeep.com.learningimageloader;

import com.squareup.okhttp.OkHttpClient;

/**
 * Created by amandeepsingh on 30/09/16.
 */

public class NetworkUtils {

    private static OkHttpClient okHttpClient;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }
}
