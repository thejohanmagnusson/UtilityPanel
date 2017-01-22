package android.johanmagnusson.se.utilitypanel.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class Panel {

    private String description;
    public String getDescription() {
        return description;
    }

    private boolean hasIntercom;
    public boolean getHasIntercomFeature() { return hasIntercom; }

    private boolean hasAccessCode;
    public boolean getHasAccessCodeFeature() { return hasAccessCode; }

    private String accessCode;
    public String getAccessCode() { return accessCode; }

    public Panel() {
        // Default constructor required for calls to DataSnapshot.getValue(Panel.class)
    }

    public Panel(String name) {
        this.description = name;
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("description", description);
        result.put("hasIntercom", hasIntercom);
        result.put("hasAccessCode", hasAccessCode);
        result.put("accessCode", accessCode);

        return result;
    }
}
