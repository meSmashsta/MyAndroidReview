package com.blazingmuffin.miko.couchcrud.utility;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Miko on 21/09/2017.
 */

public class DatabaseUtil {
    private static Manager mManager;
    private static Database mDatabase;

    private static final String DATABASE_NAME = "app";

    private DatabaseUtil() {}

    public static synchronized Manager Manager(Context context) {
        Manager manager = null;
        try {
            manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manager;
    }

    public static synchronized Database Instance(Context context) {
        Database database = null;
        try {
            database = DatabaseUtil.Manager(context).getDatabase(DATABASE_NAME);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return database;
    }

}
