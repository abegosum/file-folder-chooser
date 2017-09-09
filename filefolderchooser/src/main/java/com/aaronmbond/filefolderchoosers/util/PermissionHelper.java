package com.aaronmbond.filefolderchoosers.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Copyright 2/5/2017 Aaron M. Bond
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
