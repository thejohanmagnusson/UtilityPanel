package android.johanmagnusson.se.utilitypanel.service;

import android.app.Service;
import android.content.Intent;
import android.johanmagnusson.se.utilitypanel.BuildConfig;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class SinchService extends Service
                          implements SinchClientListener, CallListener {

    private static final String TAG = SinchService.class.getSimpleName();

    private static final String APP_KEY = BuildConfig.SINCH_APP_KEY;
    private static final String APP_SECRET = BuildConfig.SINCH_APP_SECRET;
    private static final String ENVIRONMENT = BuildConfig.SINCH_ENVIRONMENT;

    public static final String TEST_NUMBER = "+46000000000";

    private static final int CLIENT_NOT_CONNECTED = 0;
    private static final int CLIENT_CONNECTED = 1;
    private static final int PLACE_CALL = 2;
    private static final int ONGOING_CALL = 3;

    private final IBinder mBinder = new SinchBinder();
    private OnCallListener mCallListener;
    private SinchClient mSinchClient;
    private int mState;
    private String mPhoneNumber;
    private String mCallId;

    // Binder
    public class SinchBinder extends Binder {

        public SinchService getService() {
            return SinchService.this;
        }
    }

    // Interface
    public interface OnCallListener {
        void onCallEstablished();
        void onCallEnded();
    }

    public SinchService() {
        mState = CLIENT_NOT_CONNECTED;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setOnCallListener(OnCallListener listener) {
        mCallListener = listener;
    }

    public void callPhoneNumber(String userName, String phoneNumber) {
        mPhoneNumber = phoneNumber;

        if(!isStarted()) {
            mState = PLACE_CALL;
            startClient(userName);
        }
        else {
            placePhoneCall(phoneNumber);
        }
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    //region SinchClient
    private void startClient(String userName) {
        mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userName)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT).build();

        mSinchClient.setSupportCalling(true);
        mSinchClient.addSinchClientListener(this);
        mSinchClient.start();
    }

    @Override
    public void onClientStarted(SinchClient sinchClient) {
        Log.d(TAG, "--------- SinchClient started");

        if(mState == PLACE_CALL) {
            placePhoneCall(mPhoneNumber);
        }
        else {
            mState = CLIENT_CONNECTED;
        }
    }

    @Override
    public void onClientStopped(SinchClient sinchClient) {
        Log.d(TAG, "--------- SinchClient stopped");
        mState = CLIENT_NOT_CONNECTED;
    }

    @Override
    public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
        Log.d(TAG, "--------- SinchClient failed: " + sinchError.getMessage());
        mState = CLIENT_NOT_CONNECTED;
        mSinchClient.terminate();
        mSinchClient = null;
    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {
        Log.d(TAG, "--------- SinchClient: credentials required");

    }

    @Override
    public void onLogMessage(int i, String s, String s1) {

    }
    //endregion

    //region Phone call
    private void placePhoneCall(String phoneNumber) {
        Call call = mSinchClient.getCallClient().callPhoneNumber(phoneNumber);
        call.addCallListener(this);
        mCallId = call.getCallId();
    }

    @Override
    public void onCallProgressing(Call call) {
        mState = ONGOING_CALL;
        // This callback is never called ?!
    }

    @Override
    public void onCallEstablished(Call call) {
        mState = ONGOING_CALL;
        if(mCallListener != null) {
            mCallListener.onCallEstablished();
        }
    }

    @Override
    public void onCallEnded(Call call) {
        mState = CLIENT_CONNECTED;
        mCallId = null;

        if(mCallListener != null) {
            mCallListener.onCallEnded();
        }
    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> list) {
    }
    //endregion

    public boolean isCallInProgress() {
        return mSinchClient != null && mSinchClient.getCallClient().getCall(mCallId) != null;
    }

    public void hangup() {
        if(mSinchClient != null) {
            Call call = mSinchClient.getCallClient().getCall(mCallId);

            if(call != null) {
                call.hangup();
                mCallId = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null) {
            mSinchClient.terminate();
            mSinchClient = null;
        }

        super.onDestroy();
    }
}
