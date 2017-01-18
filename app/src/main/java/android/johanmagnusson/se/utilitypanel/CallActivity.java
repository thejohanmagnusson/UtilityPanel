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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class CallActivity extends AppCompatActivity {

    public final static String TAG = CallActivity.class.getSimpleName();

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


    public CallActivity() {
        mBound = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        if(savedInstanceState == null) {
            Intent intent = getIntent();

            mUsername = intent.getStringExtra(ARG_USERNAME);
            mContactName = intent.getStringExtra(ARG_CONTACT_NAME);
            mContactPhoneNumber = intent.getStringExtra(ARG_CONTACT_PHONE_NUMBER);
        }
        else {
            mUsername = savedInstanceState.getString(ARG_USERNAME);
            mContactName = savedInstanceState.getString(ARG_CONTACT_NAME);
            mContactPhoneNumber = savedInstanceState.getString(ARG_CONTACT_PHONE_NUMBER);
        }

        mContactNameTextView = (TextView) findViewById(R.id.contact_name);
        mContactNameTextView.setText(mContactName);

        mCallStateTextView = (TextView) findViewById(R.id.call_state);
        mCallStateTextView.setText(getResources().getString(R.string.call_state_calling));

        mHangUpFab = (FloatingActionButton) findViewById(R.id.hang_up);
        mHangUpFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSinchService != null) {
                    mSinchService.hangup();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        AudioManager audioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
        audioManager.setSpeakerphoneOn(true);

        bindStartRegisterService();
    }

    private void bindStartRegisterService(){
        // Bind/create service
        // mBound is set by onServiceConnected in mConnection
        if(!mBound) {
            Intent intent = new Intent(this, SinchService.class);
            bindService(intent, mConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mSinchService.isCallInProgress()) {
            mSinchService.hangup();
        }

        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        AudioManager audioManager =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.USE_DEFAULT_STREAM_TYPE);
        audioManager.setSpeakerphoneOn(false);

        unbindUnregisterService();
    }

    private void unbindUnregisterService(){
        if(mBound) {
            unbindService(mConnection);
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
            mSinchService.setOnCallListener(mCallListener);
            mBound = true;

            // Use start so service doesn't stop when binding is released.
            // Binding will be released for inactivity before we are done if not started.
            Intent intent = new Intent(CallActivity.this, SinchService.class);
            startService(intent);

            mSinchService.callPhoneNumber(mUsername, mContactPhoneNumber);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mSinchService = null;
            finish();
        }
    };

    private SinchService.OnCallListener mCallListener = new SinchService.OnCallListener() {
        @Override
        public void onCallEstablished() {
        }

        @Override
        public void onCallEnded() {
            finish();
        }

        @Override
        public void onCallDurationChanged(int callDuration) {
            mCallStateTextView.setText(Utility.formatCallDuration(callDuration));
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
    public void onDestroy() {
        Intent intent = new Intent(CallActivity.this, SinchService.class);
        stopService(intent);

        super.onDestroy();
    }
}
