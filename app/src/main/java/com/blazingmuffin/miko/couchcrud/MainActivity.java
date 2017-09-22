package com.blazingmuffin.miko.couchcrud;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blazingmuffin.miko.couchcrud.utility.DatabaseUtil;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

public class MainActivity extends AppCompatActivity implements ActivityAdapter.ListItemClickListener {
    public static int CREATE_NEW_DOCUMENT = 1;
    public static int DELETE_DOCUMENT = 2;

    RecyclerView mRecyclerView;
    ActivityAdapter mActivityAdapter;
    ProgressBar mProgressBar;
    Toast mToast;
    Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_activities);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_indicate_loading);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mActivityAdapter = new ActivityAdapter(this);
        mRecyclerView.setAdapter(mActivityAdapter);
        loadActivities();
    }

    private void loadActivities() {
        new FetchActivityTask().execute();
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void doneLoading() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClick(Document document) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MainActivity.this, document.getProperty("title").toString(), Toast.LENGTH_LONG);
        mToast.show();

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("activityId", document.getId());
//        startActivity(intent);
        startActivityForResult(intent, DELETE_DOCUMENT);
    }

    private class FetchActivityTask extends AsyncTask<String, Void, QueryEnumerator> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected QueryEnumerator doInBackground(String... params) {
            mDatabase = DatabaseUtil.Instance(MainActivity.this);
            Query query = mDatabase.createAllDocumentsQuery();
            query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
            query.setDescending(true);
            QueryEnumerator result = null;
            try {
                result = query.run();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(QueryEnumerator documents) {
            doneLoading();
            if (null != documents) {
               mActivityAdapter.setDocuments(documents);
           }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CREATE_NEW_DOCUMENT) {
                if (data.hasExtra("createdNewDocument")) {
                    loadActivities();
                }
            } else if (requestCode == DELETE_DOCUMENT) {
                if (data.hasExtra("deleteDocument")) {
                    loadActivities();
                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItemId = item.getItemId();
        switch (selectedItemId) {
            case R.id.action_create:
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivityForResult(intent, CREATE_NEW_DOCUMENT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
