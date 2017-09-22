package com.blazingmuffin.miko.couchcrud;

import android.content.Intent;
import android.net.nsd.NsdManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blazingmuffin.miko.couchcrud.utility.DatabaseUtil;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    public static int UPDATE_DOCUMENT = 1;

    Document mDocument;

    String mActivityId;
    Database mDatabase;
    TextView mTitle;
    TextView mDescription;

    Button mEdit;
    Button mDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitle = (TextView) findViewById(R.id.tv_activity_title);
        mDescription = (TextView) findViewById(R.id.tv_activity_details);
        mEdit = (Button) findViewById(R.id.btn_edit);
        mDelete = (Button) findViewById(R.id.btn_delete);

        Intent intent = getIntent();
        String activityId = intent.getStringExtra("activityId");
        mActivityId = activityId;

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("activityId", mActivityId);
                startActivityForResult(intent, UPDATE_DOCUMENT);
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteActivityTask().execute();
            }
        });

        loadDetails();
    }

    private void loadDetails() {
        new FetchActivityDetailTask().execute();
    }

    private class FetchActivityDetailTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... params) {
            mDatabase = DatabaseUtil.Instance(getApplicationContext());
            mDocument = mDatabase.getDocument(mActivityId);
            return mDocument;
        }

        @Override
        protected void onPostExecute(Document document) {
            if (document != null) {
                mTitle.setText(document.getProperty("title").toString());
                mDescription.setText(document.getProperty("sdk").toString());
            }
        }
    }

    private class DeleteActivityTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            try {
                mDocument.update(new Document.DocumentUpdater() {
                    @Override
                    public boolean update(UnsavedRevision newRevision) {
                        newRevision.setIsDeletion(true);
                        Map<String, Object> properties = newRevision.getUserProperties();
                        properties.put("deleted_at", System.currentTimeMillis());
                        newRevision.setUserProperties(properties);
                        return true;
                    }
                });
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            Intent data = new Intent();
            data.putExtra("deleteDocument", true);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UPDATE_DOCUMENT) {
            if (data.hasExtra("updated")) {
                loadDetails();
            }
        }
    }
}
