package android.johanmagnusson.se.utilitypanel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.johanmagnusson.se.utilitypanel.constant.Firebase;
import android.johanmagnusson.se.utilitypanel.model.Contact;
import android.johanmagnusson.se.utilitypanel.model.Panel;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static android.johanmagnusson.se.utilitypanel.R.id.fab;

public class MainActivity extends AppCompatActivity
                          implements ContactListFragment.OnContactSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQ_CODE_RECORD_AUDIO = 1;
    private static final int REQ_CODE_READ_PHONE_STATE = 2;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private DatabaseReference mFirebasePanelDetails;

    private boolean mHasIntercomFeature;
    private boolean mHasAccessCodeFeature;
    private String mAccessCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mFab = (FloatingActionButton) findViewById(fab);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ContactListFragment(), ContactListFragment.TAG).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO: use device id as key
        mFirebasePanelDetails = FirebaseDatabase.getInstance().getReference(Firebase.NODE_PANEL_DETAILS).child("1");
        // No need to handle removing lister, single event.
        mFirebasePanelDetails.addListenerForSingleValueEvent(mPanelListener);
    }

    private ValueEventListener mPanelListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Panel panel = dataSnapshot.getValue(Panel.class);
            mToolbar.setTitle(panel.getDescription());

            // Handle intercom feature
            mHasIntercomFeature = panel.getHasIntercomFeature();
            setupIntercomFeature(mHasIntercomFeature);

            // Handle access code feature
            mHasAccessCodeFeature = panel.getHasAccessCodeFeature();
            mAccessCode = mHasAccessCodeFeature ? panel.getAccessCode() : null;
            setupAccessCodeFeature(mHasAccessCodeFeature);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // TODO: Show error information
        }
    };

    private void setupIntercomFeature(boolean hasIntercomFeature) {

    }

    private void setupAccessCodeFeature(boolean hasAccessCodeFeature) {
        if(hasAccessCodeFeature) {
            if(mFab.getVisibility() != View.VISIBLE) { mFab.setVisibility(View.VISIBLE); }

            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Access control panel", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        else {
            if(mFab.getVisibility() == View.VISIBLE) { mFab.setVisibility(View.GONE); }

            mFab.setOnClickListener(null);
        }
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
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

        Intent intent = new Intent(this, CallActivity.class);
        // TODO: Implement device ID as username
        intent.putExtra(CallActivity.ARG_USERNAME, Long.toString(Calendar.getInstance().getTimeInMillis()));
        intent.putExtra(CallActivity.ARG_CONTACT_NAME, contact.getName());
        intent.putExtra(CallActivity.ARG_CONTACT_PHONE_NUMBER, contact.getPhonenumber());

        startActivity(intent);
    }
}
