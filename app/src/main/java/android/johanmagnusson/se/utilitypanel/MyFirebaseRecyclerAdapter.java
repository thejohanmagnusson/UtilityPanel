package android.johanmagnusson.se.utilitypanel;

import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public abstract class MyFirebaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends com.firebase.ui.database.FirebaseRecyclerAdapter<T, VH> {

    public interface OnEmptyListChangedListener {
        void onEmptyListChanged(boolean isEmpty);
    }

    private OnEmptyListChangedListener mEmptyListChangedListener;
    private Query ref;

    public MyFirebaseRecyclerAdapter(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);

        this.ref = ref;
        registerAdapterDataObserver(observer);
        registerValueEventListener();
    }

    public MyFirebaseRecyclerAdapter(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass, DatabaseReference ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);

        this.ref = (Query) ref;
        registerAdapterDataObserver(observer);
        registerValueEventListener();
    }

    final private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);

            // Remove listener when first item is inserted
            if (positionStart == 0) {
                registerValueEventListener();
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);

            // add listener when last item is removed to let ValueEventListener handle all emptyListChanged events
            if (positionStart == 0) {
                registerValueEventListener();
            }
        }
    };

    final private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // remove listener when synchronizing is done.
            unregisterValueEventListener();

            onEmptyListChanged(dataSnapshot.getChildrenCount() == 0);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // remove listener on error
            unregisterValueEventListener();
        }
    };

    private boolean isValueEventListenerRegistered;

    private void registerValueEventListener() {
        if (!isValueEventListenerRegistered) {
            isValueEventListenerRegistered = true;
            ref.addValueEventListener(mValueEventListener);
        }
    }

    private void unregisterValueEventListener() {
        if (isValueEventListenerRegistered) {
            ref.removeEventListener(mValueEventListener);
            isValueEventListenerRegistered = false;
        }
    }

    public void registerEmptyListChangedListener(OnEmptyListChangedListener listener) {
        mEmptyListChangedListener = listener;
    }

    public void unregisterEmptyListChangedListener() {
        mEmptyListChangedListener = null;
    }

    private void onEmptyListChanged(boolean isEmpty) {
        if (mEmptyListChangedListener != null) {
            mEmptyListChangedListener.onEmptyListChanged(isEmpty);
        }
    }

    @Override
    public void cleanup() {
        unregisterAdapterDataObserver(observer);
        unregisterValueEventListener();
        unregisterEmptyListChangedListener();
        this.ref = null;

        super.cleanup();
    }
}