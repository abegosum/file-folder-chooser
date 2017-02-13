package com.aaronmbond.filefolderchoosers.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Frags on 2/5/2017.
 */

public class PermissionHelper {

    public static final int EXTERNAL_WRITE_PERMISSIONS_REQUEST_CODE = 0;

    public static boolean checkExternalWritePermissionsEnabled(Activity currentActivity) {
        boolean result = false;
        if (ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            result = true;
        } else {
            ActivityCompat.requestPermissions(currentActivity,
            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_WRITE_PERMISSIONS_REQUEST_CODE);
        }
        return result;
    }
}
