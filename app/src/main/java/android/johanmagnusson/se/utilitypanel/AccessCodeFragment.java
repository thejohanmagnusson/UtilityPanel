package android.johanmagnusson.se.utilitypanel;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import static android.johanmagnusson.se.utilitypanel.IntercomContactsFragment.ARG_DEVICE_KEY;

public class AccessCodeFragment extends Fragment {

    public static final String TAG = AccessCodeFragment.class.getSimpleName();

    public static AccessCodeFragment newInstance(String deviceKey) {
        Bundle args = new Bundle();
        args.putString(ARG_DEVICE_KEY, deviceKey);

        AccessCodeFragment fragment = new AccessCodeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public AccessCodeFragment() {
    }
}
