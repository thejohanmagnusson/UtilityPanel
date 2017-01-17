package android.johanmagnusson.se.utilitypanel;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.johanmagnusson.se.utilitypanel.service.SinchService;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CallFragment extends Fragment {

    public final static String TAG = CallFragment.class.getSimpleName();

    public final static String ARG_USERNAME = "username";
    public final static String ARG_CONTACT_NAME = "contact-name";
    public final static String ARG_CONTACT_PHONE_NUMBER = "contact-phone-number";

    private boolean mBound;
    private SinchService mSinchService;

    private String mUsername;
    private String mContactName;
    private String mContactPhoneNumber;

    private TextView mContactNameTextView;
    private TextView mCallStateTextView;
    private FloatingActionButton mHangUpFab;

    public CallFragment() {
        mBound = false;
    }

    public static CallFragment newInstance(String username, String contactName, String contactNumber) {
        CallFragment fragment = new CallFragment();

        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_CONTACT_NAME, contactName);
        args.putString(ARG_CONTACT_PHONE_NUMBER, contactNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUsername = getArguments().getString(ARG_USERNAME);
            mContactName = getArguments().getString(ARG_CONTACT_NAME);
            mContactPhoneNumber = getArguments().getString(ARG_CONTACT_PHONE_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_call, container, false);

        mContactNameTextView = (TextView) root.findViewById(R.id.contact_name);
        mContactNameTextView.setText(mContactName);

        mCallStateTextView = (TextView) root.findViewById(R.id.call_state);
        mCallStateTextView.setText(getResources().getString(R.string.call_state_calling));

        mHangUpFab = (FloatingActionButton) root.findViewById(R.id.hang_up);
        mHangUpFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSinchService != null) {
                    mSinchService.hangup();
                    getActivity().finish();
                }
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        AudioManager audioManager =  (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
        audioManager.setSpeakerphoneOn(true);

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
    public void onStop() {
        super.onStop();

        if(mSinchService.isCallInProgress()) {
            mSinchService.hangup();
        }

        getActivity().setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        AudioManager audioManager =  (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.USE_DEFAULT_STREAM_TYPE);
        audioManager.setSpeakerphoneOn(false);

        unbindUnregisterService();
    }

    private void unbindUnregisterService(){
        if(mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    private SinchService.OnCallListener mCallListener = new SinchService.OnCallListener() {
        @Override
        public void onCallEstablished() {
            mCallStateTextView.setText("¯\\_(ツ)_/¯");
        }

        @Override
        public void onCallEnded() {
            getActivity().finish();

        }
    };

    // Handle connection to service and setup listener
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            // Get service and set bound
            SinchService.SinchBinder binder = (SinchService.SinchBinder) service;
            mSinchService = binder.getService();
            mSinchService.setOnCallListener(mCallListener);
            mBound = true;

            // Use start so service doesn't stop when binding is released.
            // Binding will be released for inactivity before we are done if not started.
            Intent intent = new Intent(getActivity(), SinchService.class);
            getActivity().startService(intent);

            mSinchService.callPhoneNumber(mUsername, mContactPhoneNumber);
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
        outState.putString(ARG_CONTACT_PHONE_NUMBER, mContactPhoneNumber);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        Intent intent = new Intent(getActivity(), SinchService.class);
        getActivity().stopService(intent);

        super.onDestroyView();
    }
}
