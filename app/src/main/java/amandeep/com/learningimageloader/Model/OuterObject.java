package amandeep.com.learningimageloader.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amandeepsingh on 30/09/16.
 */

public class OuterObject implements Serializable {
    private List<InnerObject> urls;

    public List<InnerObject> getUrls() {
        return urls;
    }
}