package android.johanmagnusson.se.utilitypanel;


import android.content.Context;
import android.johanmagnusson.se.utilitypanel.model.Contact;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

// Why is it so hard to come up with good names?
public class MyRecyclerView extends RecyclerView {

    private View mEmptyView;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        // Unregister for current adapter first
        final Adapter currentAdapter = getAdapter();
        if (currentAdapter != null) {
            ((MyFirebaseRecyclerAdapter<Contact, ContactViewHolder>) currentAdapter).unregisterEmptyListChangedListener();
        }

        super.setAdapter(adapter);

        // Register for the new adapter
        if (adapter != null) {
            ((MyFirebaseRecyclerAdapter<Contact, ContactViewHolder>) adapter).registerEmptyListChangedListener(mListener);
        }
    }

    MyFirebaseRecyclerAdapter.OnEmptyListChangedListener mListener = new MyFirebaseRecyclerAdapter.OnEmptyListChangedListener() {
        @Override
        public void onEmptyListChanged(boolean isEmpty) {
            if (mEmptyView != null) {
                mEmptyView.setVisibility(isEmpty ? VISIBLE : GONE);
            }
        }
    };

    public void setEmptyView(View mEmptyView) {
        this.mEmptyView = mEmptyView;
    }
}

