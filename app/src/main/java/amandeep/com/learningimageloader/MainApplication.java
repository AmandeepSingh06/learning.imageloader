package amandeep.com.learningimageloader;

import android.app.Application;
import android.content.Context;

/**
 * Created by amandeepsingh on 26/09/16.
 */

public class MainApplication extends Application {

    private static MainApplication instance;

    //should not call this
    public MainApplication() {
        instance = this;
    }

    public static MainApplication getInstance() {
        if (instance == null) {
            instance = new MainApplication();
        }
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
