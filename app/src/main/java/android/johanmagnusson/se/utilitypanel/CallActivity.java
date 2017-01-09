package android.johanmagnusson.se.utilitypanel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class CallActivity extends AppCompatActivity {

    public final static String TAG = CallActivity.class.getSimpleName();
    public final static String CONTACT_NAME_KEY = "contact-name";
    public final static String CONTACT_NUMBER_KEY = "contact-number";

    private String mContactName;
    private String mContactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call);

        if(savedInstanceState == null) {
            mContactName = getIntent().getStringExtra(CONTACT_NAME_KEY);
            mContactNumber = getIntent().getStringExtra(CONTACT_NUMBER_KEY);
        }

        Toast.makeText(this, "Calling: " + mContactName + ", " + mContactNumber, Toast.LENGTH_LONG).show();
    }
}
