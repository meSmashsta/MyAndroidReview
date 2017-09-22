package com.blazingmuffin.miko.couchcrud.utility;

import android.content.Context;

/**
 * Created by Miko on 21/09/2017.
 */

public final class HelperUtil {
    public static final String getFullName(Context context) {
        return context.getClass().getCanonicalName();
    }
}
