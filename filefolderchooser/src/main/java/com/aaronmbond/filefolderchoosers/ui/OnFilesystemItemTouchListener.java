package com.aaronmbond.filefolderchoosers.ui;

import java.io.File;

/**
 * Created by Frags on 2/11/2017.
 */

public interface OnFilesystemItemTouchListener {
    void onFilesystemItemTouch(File file, int adapterPotition);
}
