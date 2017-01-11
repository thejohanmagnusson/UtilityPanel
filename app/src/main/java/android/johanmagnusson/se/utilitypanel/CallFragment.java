package android.johanmagnusson.se.utilitypanel;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.johanmagnusson.se.utilitypanel.service.SinchService;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CallFragment extends Fragment {

    public final static String TAG = CallFragment.class.getSimpleName();

    public final static String ARG_USERNAME = "username";
    public final static String ARG_CONTACT_NAME = "contact-name";
    public final static String ARG_CONTACT_NUMBER = "contact-number";

    private String mUsername;
    private String mContactName;
    private String mContactNumber;

    private boolean mBound;
    private SinchService mSinchService;


    public CallFragment() {
        mBound = false;
    }

    public static CallFragment newInstance(String username, String contactName, String contactNumber) {
        CallFragment fragment = new CallFragment();

        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_CONTACT_NAME, contactName);
        args.putString(ARG_CONTACT_NUMBER, contactNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUsername = getArguments().getString(ARG_USERNAME);
            mContactName = getArguments().getString(ARG_CONTACT_NAME);
            mContactNumber = getArguments().getString(ARG_CONTACT_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        bindStartRegisterService();
    }

    private void bindStartRegisterService(){
        // Bind/create service
        // mBound is set by onServiceConnected in mConnection
        if(!mBound) {
            Intent intent = new Intent(getActivity(), SinchService.class);
            getActivity().bindService(intent, mConnection, getActivity().BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unbindUnregisterService();
    }

    private void unbindUnregisterService(){
        if(mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    // Handle connection to service and setup listener
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            // Get service and set bound
            SinchService.SinchBinder binder = (SinchService.SinchBinder) service;
            mSinchService = binder.getService();
            mBound = true;

            // Use start so service doesn't stop when binding is released
            Intent intent = new Intent(getActivity(), SinchService.class);
            getActivity().startService(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mSinchService = null;
            getActivity().finish();
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_USERNAME, mUsername);
        outState.putString(ARG_CONTACT_NAME, mContactName);
        outState.putString(ARG_CONTACT_NUMBER, mContactNumber);

        super.onSaveInstanceState(outState);
    }
}
