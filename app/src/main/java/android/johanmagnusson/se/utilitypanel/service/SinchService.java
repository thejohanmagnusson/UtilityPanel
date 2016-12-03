package android.johanmagnusson.se.utilitypanel.service;

import android.app.Service;
import android.content.Intent;
import android.johanmagnusson.se.utilitypanel.BuildConfig;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SinchService extends Service {

    private static final String APP_KEY = BuildConfig.SINCH_APP_KEY;
    private static final String APP_SECRET = BuildConfig.SINCH_APP_SECRET;
    private static final String ENVIRONMENT = BuildConfig.SINCH_ENVIRONMENT;

    private static final String TAG = SinchService.class.getSimpleName();

    public static final String CALL_ID = "CALL_ID";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
