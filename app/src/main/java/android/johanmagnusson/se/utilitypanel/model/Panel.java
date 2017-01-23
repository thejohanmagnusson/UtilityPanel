package android.johanmagnusson.se.utilitypanel.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class Panel {

    private String description;
    public String getDescription() {
        return description;
    }

    private String defaultFeature;
    public String getDefaultFeature() { return defaultFeature; }

    private HashMap<String, Object> features;
    public HashMap<String, Object> getFeaturesAsHashMap() { return features; }

    public boolean hasFeature(String feature) {
        return features.keySet().contains(feature);
    }

    public List<String> getFeatures() {
        ArrayList<String> list = new ArrayList<>();

        for(String key : features.keySet()) {
            list.add(key);
        }

        return list;
    }

    public Panel() {
        // Default constructor required for calls to DataSnapshot.getValue(Panel.class)
    }
}
