package android.johanmagnusson.se.utilitypanel;

import android.content.Intent;
import android.johanmagnusson.se.utilitypanel.model.Contact;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity
                          implements ContactListFragment.OnContactSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

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

    // ContactListFragment.OnContactSelectedListener
    @Override
    public void onContactSelected(Contact contact) {
        Intent intent = new Intent(this, CallActivity.class);
        // TODO: Implement device ID as username
        intent.putExtra(CallFragment.ARG_USERNAME, "1234567890");
        intent.putExtra(CallFragment.ARG_CONTACT_NAME, contact.getName());
        intent.putExtra(CallFragment.ARG_CONTACT_NUMBER, contact.getPhone());

        startActivity(intent);
    }
}
