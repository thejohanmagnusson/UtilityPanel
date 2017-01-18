package android.johanmagnusson.se.utilitypanel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.johanmagnusson.se.utilitypanel.model.Contact;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
                          implements ContactListFragment.OnContactSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQ_CODE_RECORD_AUDIO = 1;
    private static final int REQ_CODE_READ_PHONE_STATE = 2;

    private boolean mMissingPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ContactListFragment(), ContactListFragment.TAG).commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Access control panel", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check "Dangerous" permissions
        handlePermission(MainActivity.this, Manifest.permission.RECORD_AUDIO, REQ_CODE_RECORD_AUDIO);
        handlePermission(MainActivity.this, Manifest.permission.RECORD_AUDIO, REQ_CODE_READ_PHONE_STATE);
    }

    private void handlePermission(Activity activity, String permission, int requestCode) {
        if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            // Show explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // TODO: Show explanation
            }
            else {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                // Callback gets result of the request.
            }
        }
    }

    // TODO: Handle permissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                } else {
                    // permission denied
                    // Disable functionality that depends on the permission.
                }
                return;
            }
            case REQ_CODE_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                } else {
                    // permission denied
                    // Disable functionality that depends on the permission.
                }
                return;
            }
        }
    }


    // ContactListFragment.OnContactSelectedListener
    @Override
    public void onContactSelected(Contact contact, View animationView) {

        if(mMissingPermission) {
            Toast.makeText(this, "Missing permission", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CallActivity.class);
        // TODO: Implement device ID as username
        intent.putExtra(CallActivity.ARG_USERNAME, Long.toString(Calendar.getInstance().getTimeInMillis()));
        intent.putExtra(CallActivity.ARG_CONTACT_NAME, contact.getName());
        intent.putExtra(CallActivity.ARG_CONTACT_PHONE_NUMBER, contact.getPhone());

        // Animate contact name
        Pair contactNameTrans = new Pair<>(animationView, getString(R.string.transition_contact_name));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, contactNameTrans);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
}
