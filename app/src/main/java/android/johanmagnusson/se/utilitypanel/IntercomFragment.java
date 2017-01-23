package android.johanmagnusson.se.utilitypanel;

import android.content.Context;
import android.johanmagnusson.se.utilitypanel.constant.Firebase;
import android.johanmagnusson.se.utilitypanel.model.Contact;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class IntercomFragment extends Fragment {
    public interface OnContactSelectedListener {
        void onContactSelected(Contact contact, View animationView);
    }

    public static final String TAG = IntercomFragment.class.getSimpleName();

    public static final String ARG_DEVICE_KEY = "device-key";

    private OnContactSelectedListener mListener;
    private DatabaseReference mDatabaseIntercomContacts;

    private MyFirebaseRecyclerAdapter<Contact, ContactViewHolder> mAdapter;
    private MyRecyclerView mRecyclerView;
    private LinearLayoutManager mManager;

    private String mDeviceKey;

    public static IntercomFragment newInstance(String deviceKey) {
        Bundle args = new Bundle();
        args.putString(ARG_DEVICE_KEY, deviceKey);

        IntercomFragment fragment = new IntercomFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public IntercomFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            Bundle args = getArguments();

            mDeviceKey = args.getString(ARG_DEVICE_KEY);
        }
        else {
            mDeviceKey = savedInstanceState.getString(ARG_DEVICE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact_list, container, false);

        mDatabaseIntercomContacts = FirebaseDatabase.getInstance().getReference().child(Firebase.NODE_INTERCOM_CONTACTS);

        mRecyclerView = (MyRecyclerView) root.findViewById(R.id.contact_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setEmptyView(root.findViewById(R.id.empty_contact_list));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mManager);

        Query contactQuery = mDatabaseIntercomContacts.child("1").orderByChild(Firebase.PROPERTY_NAME);

        // Removed in onDestroy()
        mAdapter = new MyFirebaseRecyclerAdapter<Contact, ContactViewHolder>(Contact.class, R.layout.contact_item, ContactViewHolder.class, contactQuery) {
            @Override
            protected void populateViewHolder(final ContactViewHolder viewHolder, final Contact model, int position) {
                final DatabaseReference ref = getRef(position);

                viewHolder.bindToModel(model);

                // Item click listener
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.onContactSelected(model, viewHolder.nameView);
                    }
                });
            }
        };

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_DEVICE_KEY, mDeviceKey);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Set in onActivityCreated()
        if(mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the Listener so we can send events to the host
            mListener = (OnContactSelectedListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement OnContactSelectedListener");
        }
    }
}
