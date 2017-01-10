package android.johanmagnusson.se.utilitypanel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static android.johanmagnusson.se.utilitypanel.CallFragment.ARG_CONTACT_NAME;
import static android.johanmagnusson.se.utilitypanel.CallFragment.ARG_CONTACT_NUMBER;

public class CallActivity extends AppCompatActivity {

    public final static String TAG = CallActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call);

        if(savedInstanceState == null) {
            String contactName = getIntent().getStringExtra(ARG_CONTACT_NAME);
            String contactNumber = getIntent().getStringExtra(ARG_CONTACT_NUMBER);

            CallFragment callFragment = CallFragment.newInstance(contactName, contactNumber);
            getSupportFragmentManager().beginTransaction().add(R.id.call_container, callFragment).commit();
        }

        getSupportActionBar().setElevation(0f);
    }
}
