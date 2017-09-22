package com.blazingmuffin.miko.couchcrud;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.couchbase.lite.Document;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.Map;

/**
 * Created by Miko on 21/09/2017.
 */

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityAdapterViewHolder> {
    QueryEnumerator mDocuments;
    private final ListItemClickListener mListItemClickListener;

    public interface ListItemClickListener {
        void onListItemClick(Document document);
    }

    public class ActivityAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mActivityTextView;
        public ActivityAdapterViewHolder(View view) {
            super(view);
            mActivityTextView = (TextView) view.findViewById(R.id.tv_activity_data);
            mActivityTextView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int itemIndex = getAdapterPosition();
            Document document = mDocuments.getRow(itemIndex).getDocument();

            mListItemClickListener.onListItemClick(document);
        }
    }
    public ActivityAdapter(ListItemClickListener listItemClickListener) {
        mListItemClickListener = listItemClickListener;
    }

    @Override
    public ActivityAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParent = false;
        View view = inflater.inflate(R.layout.activity_list, parent, shouldAttachToParent);
        return new ActivityAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActivityAdapterViewHolder holder, int position) {
        QueryRow queryRow = mDocuments.getRow(position);
        Document document = queryRow.getDocument();
        holder.mActivityTextView.setText(document.getProperty("title").toString());
    }

    @Override
    public int getItemCount() {
        if (null == mDocuments) return 0;
        return mDocuments.getCount();
    }

    public void setDocuments(QueryEnumerator documents) {
        mDocuments = documents;
        notifyDataSetChanged();
    }


}
