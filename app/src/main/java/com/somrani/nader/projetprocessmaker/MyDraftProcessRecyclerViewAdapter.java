package com.somrani.nader.projetprocessmaker;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.somrani.nader.projetprocessmaker.DraftProcessFragment.OnListFragmentInteractionListener;
import com.somrani.nader.projetprocessmaker.dummy.DummyContent.DummyItem;
import com.somrani.nader.projetprocessmaker.interfaces.DraftProcessItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDraftProcessRecyclerViewAdapter extends RecyclerView.Adapter<MyDraftProcessRecyclerViewAdapter.ViewHolder> {

    private List<DraftProcessItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyDraftProcessRecyclerViewAdapter(List<DraftProcessItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_draftprocess, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(Integer.toString(position));
        holder.mContentView.setText(mValues.get(position).app_pro_title);
        holder.mTacheView.setText(mValues.get(position).app_tas_title);
        holder.mDateView.setText(mValues.get(position).del_task_due_date);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void updateItems(List<DraftProcessItem> items){
        this.mValues = items;
        System.out.println(mValues);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mTacheView;
        public final TextView mDateView;
        public DraftProcessItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mTacheView = (TextView) view.findViewById(R.id.item_tache);
            mDateView = (TextView) view.findViewById(R.id.item_date);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
