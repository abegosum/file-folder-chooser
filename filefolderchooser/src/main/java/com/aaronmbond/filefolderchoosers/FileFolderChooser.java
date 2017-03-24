package com.aaronmbond.filefolderchoosers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aaronmbond.filefolderchoosers.ui.FileFolderRecyclerViewAdapter;
import com.aaronmbond.filefolderchoosers.ui.OnFilesystemItemTouchListener;
import com.aaronmbond.filefolderchoosers.util.PermissionHelper;

import java.io.File;
import java.util.List;

public class FileFolderChooser extends AppCompatActivity
    implements OnFilesystemItemTouchListener{

    public static final String SELECTED_PATH_KEY = "SelectedPath";
    public static final String STARTING_PATH_KEY = "StartingPath";
    public static final String FOLDER_SELECT_KEY = "IsFolderSelect";
    public static final String FILE_SELECT_MULTIPLE_KEY = "FileSelectMultiple";

    public static final int RESULT_CANCELLED = -1;

    public static final String DEBUG_TAG = "ActFileFolderChsr";

    private RecyclerView rcvFileListing;
    private MenuItem mnuSelectFolderFile;
    private Boolean folderSelect;
    private Boolean fileSelectMultiple;
    private TextView lblGoUpOneLevel;
    private TextView lblFullCurrentPath;

    private boolean isFolderSelect() {
        if (folderSelect == null) {
            Intent startingIntent = this.getIntent();
            folderSelect = startingIntent.getBooleanExtra(FOLDER_SELECT_KEY, false);
        }
        return folderSelect.booleanValue();
    }

    private boolean isFileSelectMultiple() {
        if (fileSelectMultiple == null) {
            if (isFolderSelect()) {
                fileSelectMultiple = false;
            } else {
                Intent startingIntent = this.getIntent();
                fileSelectMultiple = startingIntent.getBooleanExtra(FILE_SELECT_MULTIPLE_KEY, false);
            }
        }
        return fileSelectMultiple;
    }

    private File currentLocation;
    private File getCurrentLocation() {
        if (currentLocation == null) {
            Intent startingIntent = this.getIntent();
            String path = startingIntent.getStringExtra(STARTING_PATH_KEY);
            if (path != null) {
                currentLocation = new File(path);
            } else {
                currentLocation =
                        Environment.getExternalStorageDirectory();
            }
        }
        return currentLocation;
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void setupUiElements() {
        if (isFolderSelect()) {
            setTitle("Select a folder");
        } else {
            if (isFileSelectMultiple()) {
                setTitle("Select files");
            } else {
                setTitle("Select a file");
            }
        }
        rcvFileListing = (RecyclerView) findViewById(R.id.rcvFileListing);
        lblGoUpOneLevel = (TextView) findViewById(R.id.lblGoUpOneLevel);
        lblFullCurrentPath = (TextView) findViewById(R.id.lblFullCurrentPath);
    }

    private void goUpOneLevel() {
        FileFolderRecyclerViewAdapter adapter =
                (FileFolderRecyclerViewAdapter) rcvFileListing.getAdapter();
        File currentPath = adapter.getCurrentPath();
        File upOneLevelPath = currentPath.getParentFile();
        File[] upOneLeveListing = upOneLevelPath.listFiles();
        if (upOneLevelPath != null && upOneLeveListing != null) {
            adapter.setPath(upOneLevelPath, true);
            setPathLabel(upOneLevelPath.getAbsolutePath());
        } else {
            Toast.makeText(this, "Can't go to parent folder", Toast.LENGTH_LONG).show();
        }
    }

    private void setupFolderDisplay() {
        setupFolderDisplay(getCurrentLocation());
    }

    private void setupFolderDisplay(File path) {
        setupUiElements();
        if (PermissionHelper.checkExternalWritePermissionsEnabled(this)) {
            Log.d(DEBUG_TAG, "Attempt to setup file/folder chooser for location " +
                    getCurrentLocation().getAbsolutePath());
            FileFolderRecyclerViewAdapter adapter =
                    new FileFolderRecyclerViewAdapter(path, this);
            adapter.setFolderSelect(isFolderSelect());
            adapter.setMultiSelect(isFileSelectMultiple());
            rcvFileListing.setHasFixedSize(true);
            rcvFileListing.setLayoutManager(new LinearLayoutManager(this));
            rcvFileListing.setAdapter(adapter);
            lblGoUpOneLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goUpOneLevel();
                }
            });
            setPathLabel(getCurrentLocation().getAbsolutePath());
        } else {
            Toast.makeText(this,
                    "Need permission to use external storage before we can continue",Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void returnFileAsResult(File file) {
        Intent resultData = new Intent();
        resultData.putExtra(SELECTED_PATH_KEY, file.getAbsolutePath());
        setResult(RESULT_OK, resultData);
        finish();
    }

    private void returnMultipleFilesAsResult(File[] files) {
        Intent resultData = new Intent();
        String[] resultPaths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            resultPaths[i] = files[i].getAbsolutePath();
        }
        resultData.putExtra(SELECTED_PATH_KEY, resultPaths);
        setResult(RESULT_OK, resultData);
        finish();
    }

    private void setPathLabel(String absolutePath) {
        setupUiElements();
        lblFullCurrentPath.setText("Path: " + absolutePath);
    }

    private void returnCurrentPathAsResult() {
        FileFolderRecyclerViewAdapter adapter =
                (FileFolderRecyclerViewAdapter) rcvFileListing.getAdapter();
        returnFileAsResult(adapter.getCurrentPath());
    }

    private void returnMultiSelectAsResult() {
        FileFolderRecyclerViewAdapter adapter =
                (FileFolderRecyclerViewAdapter) rcvFileListing.getAdapter();
        List<File> selected = adapter.getSelected();
        File[] result = selected.toArray(new File[selected.size()]);
        returnMultipleFilesAsResult(result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_folder_chooser);
        setupFolderDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_file_folder_chooser, menu);
        mnuSelectFolderFile = menu.findItem(R.id.mnuSelectFolderFile);
        mnuSelectFolderFile.setEnabled(isFolderSelect() || isFileSelectMultiple());
        mnuSelectFolderFile.setVisible(isFolderSelect() || isFileSelectMultiple());
        mnuSelectFolderFile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (isFolderSelect()) {
                    returnCurrentPathAsResult();
                } else if (isFileSelectMultiple()) {
                    returnMultiSelectAsResult();
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.EXTERNAL_WRITE_PERMISSIONS_REQUEST_CODE:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupFolderDisplay();
                } else {
                    Toast.makeText(this,
                            "Unable to continue without proper permissions",
                            Toast.LENGTH_LONG)
                            .show();
                    setResult(RESULT_CANCELLED);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        FileFolderRecyclerViewAdapter adapter =
                (FileFolderRecyclerViewAdapter) rcvFileListing.getAdapter();
        String currentPath = adapter.getCurrentPath().getPath();
        outState.putString(this.STARTING_PATH_KEY, currentPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        String currentPath = savedInstanceState.getString(STARTING_PATH_KEY);
        if (currentPath != null) {
            File startingFile = new File(currentPath);
            setupFolderDisplay(startingFile);
            setPathLabel(currentPath);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onFilesystemItemTouch(File file, int adapterPotition) {
        FileFolderRecyclerViewAdapter adapter =
                (FileFolderRecyclerViewAdapter) rcvFileListing.getAdapter();
        if (file.isDirectory()) {
            adapter.setPath(file, true);
            setPathLabel(file.getAbsolutePath());
        } else {
            if (isFolderSelect()) {
                Toast.makeText(this, "Please select a folder", Toast.LENGTH_LONG).show();
                return;
            }
            if (isFileSelectMultiple()) {
                adapter.getSelected().add(file);
                adapter.notifyItemChanged(adapterPotition);
            } else {
                returnFileAsResult(file);
            }
        }
    }
}
