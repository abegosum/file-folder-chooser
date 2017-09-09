package com.aaronmbond.filefolderchoosertestharness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aaronmbond.filefolderchoosers.FileFolderChooser;

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

public class FileFolderChooserTestActivity extends AppCompatActivity {

    public static final int SINGLE_FILE_CODE = 0;
    public static final int MULTI_FILE_CODE = 1;
    public static final int FOLDER_CODE = 2;

    private Button btnSingleFile;
    private Button btnMultiFile;
    private Button btnFolder;

    private void startFileFolderChooserForSingleFile() {
        Intent startActivity = new Intent(this, FileFolderChooser.class);
        // setting this is redundant - false is default
        startActivity.putExtra(FileFolderChooser.FOLDER_SELECT_KEY, false);
        // setting this is redundant - false is default
        startActivity.putExtra(FileFolderChooser.FILE_SELECT_MULTIPLE_KEY, false);
        startActivityForResult(startActivity, SINGLE_FILE_CODE);
    }

    private void startFileFolderChooserForMultiFile() {
        Intent startActivity = new Intent(this, FileFolderChooser.class);
        // setting this is redundant - false is default
        startActivity.putExtra(FileFolderChooser.FOLDER_SELECT_KEY, false);
        // specifying that we want multiple-select mode
        startActivity.putExtra(FileFolderChooser.FILE_SELECT_MULTIPLE_KEY, true);
        startActivityForResult(startActivity, MULTI_FILE_CODE);
    }

    private void startFileFolderChooserForFolder() {
        Intent startActivity = new Intent(this, FileFolderChooser.class);
        // specify we want to select a folder
        startActivity.putExtra(FileFolderChooser.FOLDER_SELECT_KEY, true);
        // setting this is redundant - false is default and multi select is ignored in folder select mode
        startActivity.putExtra(FileFolderChooser.FILE_SELECT_MULTIPLE_KEY, false);
        startActivityForResult(startActivity, FOLDER_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_folder_chooser_test);
        btnSingleFile = (Button) findViewById(R.id.btnSingleFile);
        btnMultiFile = (Button) findViewById(R.id.btnMultiFile);
        btnFolder = (Button) findViewById(R.id.btnFolder);
        btnSingleFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileFolderChooserForSingleFile();
            }
        });
        btnMultiFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileFolderChooserForMultiFile();
            }
        });
        btnFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFileFolderChooserForFolder();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SINGLE_FILE_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    String absolutePath = data.getStringExtra(FileFolderChooser.SELECTED_PATH_KEY);
                    Toast.makeText(this, "Selected: " + absolutePath, Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Data came back null", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                }
                break;
            case MULTI_FILE_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    String[] resultPaths =
                            data.getStringArrayExtra(FileFolderChooser.SELECTED_PATH_KEY);
                    StringBuilder resultMessage = new StringBuilder();
                    resultMessage.append("Selected:");
                    resultMessage.append("\n");
                    for (String path : resultPaths) {
                        resultMessage.append(path);
                        resultMessage.append("\n");
                    }
                    Toast.makeText(this, resultMessage.toString(), Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Data came back null", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                }
                break;
            case FOLDER_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    String absolutePath = data.getStringExtra(FileFolderChooser.SELECTED_PATH_KEY);
                    Toast.makeText(this, "Selected: " + absolutePath, Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Data came back null", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
