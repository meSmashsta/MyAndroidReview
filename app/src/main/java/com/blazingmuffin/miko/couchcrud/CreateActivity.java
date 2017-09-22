package com.blazingmuffin.miko.couchcrud;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.blazingmuffin.miko.couchcrud.utility.HelperUtil;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {
    private LinearLayout mLinearLayout;
    private EditText mTitle;
    private EditText mDescription;
    private Button mSaveButton;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        mLinearLayout = (LinearLayout) findViewById(R.id.activity_form);

        mTitle = (EditText) findViewById(R.id.activity_title);
        mDescription = (EditText) findViewById(R.id.activity_description);
        mSaveButton = (Button) findViewById(R.id.activity_save_button);

        mProgressBar = (ProgressBar) findViewById(R.id.saving_loading_indicator);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitle.getText().toString();
                String desc = mDescription.getText().toString();
                new SaveActivity().execute(title, desc);
            }
        });
    }

    public class SaveActivity extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }
        @Override
        protected String[] doInBackground(String... params) {
            String title = params[0];
            String desc = params[1];
            // create a manager
            Manager manager = null;
            try {
                manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // create or open the database
            Database database = null;
            try {
                database = manager.getDatabase("app");
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

            // the properties that will be saved on the document
            Map<String, Object> properties = new HashMap<>();
            properties.put("title", title);
            properties.put("sdk", desc);

            // create a new document to be saved in the database
            Document document = database.createDocument();
            try {
                document.putProperties(properties);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

            // logging the saved document
            Log.d(HelperUtil.getFullName(getApplicationContext()), String.format("Document ID :: %s", document.getId()));
            Log.d(HelperUtil.getFullName(getApplicationContext()), String.format("Learning %s with %s",
                    (String) document.getProperty("title"),
                    (String) document.getProperty("sdk")));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            doneLoading();
            Intent data = new Intent();
            data.putExtra("createdNewDocument", true);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void showLoading() {
        mLinearLayout.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void doneLoading() {
        mLinearLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
