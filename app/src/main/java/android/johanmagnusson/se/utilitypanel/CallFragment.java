package android.johanmagnusson.se.utilitypanel;


import android.johanmagnusson.se.utilitypanel.service.SinchService;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CallFragment extends Fragment {

    public final static String TAG = CallFragment.class.getSimpleName();

    public final static String ARG_CONTACT_NAME = "contact-name";
    public final static String ARG_CONTACT_NUMBER = "contact-number";

    private String mContactName;
    private String mContactNumber;

    private SinchService sinchService;


    public CallFragment() {
        // Required empty public constructor
    }

    public static CallFragment newInstance(String contactName, String contactNumber) {
        CallFragment fragment = new CallFragment();

        Bundle args = new Bundle();
        args.putString(ARG_CONTACT_NAME, contactName);
        args.putString(ARG_CONTACT_NUMBER, contactNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mContactName = getArguments().getString(ARG_CONTACT_NAME);
            mContactNumber = getArguments().getString(ARG_CONTACT_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call, container, false);
    }

}
