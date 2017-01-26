package android.johanmagnusson.se.utilitypanel.constant;

import java.util.ArrayList;
import java.util.List;

public final class Feature {
    public static final String ACCESS_CODE = "access-code";
    public static final String INTERCOM = "intercom";

    public static List<String> getSupported() {
        List<String> features = new ArrayList<>();
        features.add(ACCESS_CODE);
        features.add(INTERCOM);

        return features;
    }
}
