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

public class SinchService extends Service {

    private static final String TAG = SinchService.class.getSimpleName();

    private static final String APP_KEY = BuildConfig.SINCH_APP_KEY;
    private static final String APP_SECRET = BuildConfig.SINCH_APP_SECRET;
    private static final String ENVIRONMENT = BuildConfig.SINCH_ENVIRONMENT;

    public static final String TEST_NUMBER = "+46000000000";
    public static final String CALL_ID = "CALL_ID";

    private final IBinder mBinder = new SinchBinder();
    private SinchClient mSinchClient;
    private String mUserId;

    // Binder/Interface
    public class SinchBinder extends Binder {

        public SinchService getService() {
            return SinchService.this;
        }
    }


    public SinchService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null && mSinchClient.isStarted()) {
            mSinchClient.terminate();
        }

        super.onDestroy();
    }

    public String getUserName() {
        return mUserId;
    }

    public void StartClient(String userName) {
        if (mSinchClient == null) {
            mUserId = userName;

            mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userName)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT).build();

            mSinchClient.setSupportCalling(true);

            mSinchClient.addSinchClientListener(new MySinchClientListener());
            mSinchClient.start();
        }
    }

    public void stopClient() {
        if (mSinchClient != null) {
            mSinchClient.terminate();
            mSinchClient = null;
        }
    }

    //// TODO: handle exceptions
    public void callPhoneNumber(String phoneNumber) {
        Call call = mSinchClient.getCallClient().callPhoneNumber(phoneNumber);
        call.addCallListener(new CallListener() {
            @Override
            public void onCallProgressing(Call call) {
                //CALL_PROGRESSING
            }

            @Override
            public void onCallEstablished(Call call) {
                //CALL_ESTABLISHED
            }

            @Override
            public void onCallEnded(Call call) {
                //CALL_ENDED
            }

            @Override
            public void onShouldSendPushNotification(Call call, List<PushPair> list) {
                // Can't reach receivers device, need to wake up with a push notification.
            }
        });
    }

    public void hangup() {

    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientStarted(SinchClient sinchClient) {
            Log.d(TAG, "--------- SinchClient started");
        }

        @Override
        public void onClientStopped(SinchClient sinchClient) {
            Log.d(TAG, "--------- SinchClient stopped");
        }

        @Override
        public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
            Log.d(TAG, "--------- SinchClient failed: " + sinchError.getMessage());

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
    }
}
