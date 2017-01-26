package android.johanmagnusson.se.utilitypanel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.johanmagnusson.se.utilitypanel.Util.AnimUtils;
import android.johanmagnusson.se.utilitypanel.constant.Feature;
import android.johanmagnusson.se.utilitypanel.constant.Firebase;
import android.johanmagnusson.se.utilitypanel.model.Contact;
import android.johanmagnusson.se.utilitypanel.model.Panel;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.johanmagnusson.se.utilitypanel.R.id.fab;

public class MainActivity extends AppCompatActivity
                          implements IntercomFragment.OnContactSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQ_CODE_RECORD_AUDIO = 1;
    private static final int REQ_CODE_READ_PHONE_STATE = 2;

    private Toolbar mToolbar;
    private FloatingActionButton mFab;

    private DatabaseReference mFirebasePanelDetails;

    private String mDefaultFeature;
    private List<String> mFeatures;
    private String mAccessCode;

    private String mCurrentFragmentTag;

    public MainActivity() {
        mFeatures = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Hide FAB until a fragment is added.
        mFab = (FloatingActionButton) findViewById(fab);
        mFab.setAlpha(0f);
        mFab.setScaleX(0f);
        mFab.setScaleY(0f);

        if(savedInstanceState == null) {
            // TODO: show loading fragment
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

            // Check for unsupported features
            List<String> supportedFeatures = Feature.getSupported();

            for(String feature : panel.getFeatures()) {
                if(!supportedFeatures.contains(feature)) {
                    Log.i(TAG, "----- Unsupported panel feature: " + feature);
                }
            }

            // Setup panel features
            mFeatures = panel.getFeatures();

            if(mFeatures.size() > 0) {
                mDefaultFeature = panel.getDefaultFeature();

                if(mFeatures.contains(mDefaultFeature)) {
                    setFeatureView(mDefaultFeature);
                }
                else {
                    setFeatureView(mFeatures.get(0));
                }
            }
            else {
                // TODO: Set UI for Error
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // TODO: Set UI for Error
        }
    };

    private void setFeatureView(String feature) {
        if(feature.equals(Feature.ACCESS_CODE)) {
            setAccessCodeView();
        }
        else if(feature.equals(Feature.INTERCOM)) {
            setIntercomView();
        }
    }

    private void setAccessCodeView() {
        // Todo: Use device id as key
        AccessCodeFragment fragment = new AccessCodeFragment().newInstance("1");
        setFragment(fragment, fragment.TAG);

        showFab(R.drawable.ic_call_end_white_24dp);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFab();
                setIntercomView();
            }
        });
    }

    private void setIntercomView() {
        // Todo: Use device id as key
        IntercomFragment fragment = new IntercomFragment().newInstance("1");
        setFragment(fragment, fragment.TAG);
        showFab(R.drawable.ic_dialpad_white_24dp);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFab();
                setAccessCodeView();
            }
        });
    }

    private void setFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Add fragment if container is empty
        if(TextUtils.isEmpty(mCurrentFragmentTag)) {
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment, fragmentTag).commit();
            mCurrentFragmentTag = fragmentTag;
        }
        // ...Replace fragment
        else {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, fragmentTag).commit();
            mCurrentFragmentTag = fragmentTag;
        }
    }

    private void showFab(@android.support.annotation.DrawableRes int iconResId) {
        mFab.setAlpha(0f);
        mFab.setScaleX(0f);
        mFab.setScaleY(0f);
        mFab.setImageResource(iconResId);
        mFab.setTranslationY(mFab.getHeight() / 2);
        mFab.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(300L)
                .setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(this))
                .start();
    }

    private void hideFab() {
        mFab.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(300L)
                .setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(this))
                .start();
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

    // IntercomFragment.OnContactSelectedListener
    @Override
    public void onContactSelected(Contact contact, View animationView) {
        hideFab();

        Intent intent = new Intent(this, CallActivity.class);
        // TODO: Implement device ID as username
        intent.putExtra(CallActivity.ARG_USERNAME, Long.toString(Calendar.getInstance().getTimeInMillis()));
        intent.putExtra(CallActivity.ARG_CONTACT_NAME, contact.getName());
        intent.putExtra(CallActivity.ARG_CONTACT_PHONE_NUMBER, contact.getPhonenumber());

        startActivity(intent);
    }
}
