package com.blazingmuffin.miko.couchcrud;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blazingmuffin.miko.couchcrud.utility.DatabaseUtil;
import com.blazingmuffin.miko.couchcrud.utility.HelperUtil;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;

import java.util.Map;

public class EditActivity extends AppCompatActivity {

    Document mDocument;

    EditText mTitle;
    EditText mDescription;
    Button mButton;

    Database mDatabase;

    String mActivityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mTitle = (EditText) findViewById(R.id.activity_title);
        mDescription = (EditText) findViewById(R.id.activity_description);
        mButton = (Button) findViewById(R.id.activity_save_button);

        mActivityId = getIntent().getStringExtra("activityId");

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveActivityTask().execute(mTitle.getText().toString(),
                        mDescription.getText().toString());
            }
        });

        loadActivity();
    }

    private void loadActivity() {
        new GetActivityTask().execute(mActivityId);
    }

    private class GetActivityTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            String activityId = params[0];
            mDatabase = DatabaseUtil.Instance(getApplicationContext());
            mDocument = mDatabase.getDocument(activityId);
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mTitle.setText(mDocument.getProperty("title").toString());
            mDescription.setText(mDocument.getProperty("sdk").toString());
        }
    }

    private class SaveActivityTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            final String title = params[0];
            final String description = params[1];
            try {
                mDocument.update(new Document.DocumentUpdater() {
                    @Override
                    public boolean update(UnsavedRevision newRevision) {
                        Map properties = newRevision.getProperties();
                        properties.put("title", title);
                        properties.put("sdk", description);
                        return true;
                    }
                });
            } catch (CouchbaseLiteException e) {
                Log.e(HelperUtil.getFullName(getApplicationContext()), e.toString());
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            Intent data = new Intent();
            data.putExtra("updated", true);
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
