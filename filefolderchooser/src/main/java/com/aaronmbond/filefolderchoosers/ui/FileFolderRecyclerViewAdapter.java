package com.aaronmbond.filefolderchoosers.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.aaronmbond.filefolderchoosers.R;

import java.io.File;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

public class FileFolderRecyclerViewAdapter
        extends RecyclerView.Adapter<FileFolderRecyclerViewAdapter.ViewHolder> {

    private static final String DEBUG_TAG = "RcaFileFolder";

    private File currentPath;
    private boolean folderSelect = false;
    private boolean multiSelect = false;
    private File[] directoryList;
    private OnFilesystemItemTouchListener filesystemItemTouchListener;
    private List<File> selected;

    public File getCurrentPath() {
        return currentPath;
    }

    public void setFolderSelect(boolean folderSelect) {
        this.folderSelect = folderSelect;
    }

    public boolean getFolderSelect() {
        return folderSelect;
    }

    public List<File> getSelected() {
        if (selected == null) {
            selected = new ArrayList<File>();
        }
        return selected;
    }

    public FileFolderRecyclerViewAdapter(File currentPath) {
        this(currentPath, null);
    }

    public FileFolderRecyclerViewAdapter(File currentPath,
                                         OnFilesystemItemTouchListener filesystemItemTouchListener) {
        setPath(currentPath, false);
        this.filesystemItemTouchListener = filesystemItemTouchListener;
    }

    public void setPath(File pathToView, boolean refreshView) {
        this.currentPath = pathToView;
        Log.d(DEBUG_TAG, "Attempting to set current path to " + pathToView.getAbsolutePath());
        if (!pathToView.exists() || !pathToView.isDirectory()) {
            throw new IllegalArgumentException("FileFolderChooser viewing paths must be directories that exist");
        }
        directoryList = pathToView.listFiles();
        sortFiles(directoryList);
        Log.d(DEBUG_TAG, "Set path contains " + directoryList.length + " files");
        if (refreshView) {
            this.notifyDataSetChanged();
        }
        currentPath = pathToView;
    }

    private void sortFiles(File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int result = 0;
                // files are of same type, compare names
                if ((o1.isDirectory() && o2.isDirectory()) ||
                        !o1.isDirectory() && !o2.isDirectory()) {
                    result = o1.getName().compareTo(o2.getName());
                } else if (o1.isDirectory()) { //directories are "less than" (come first)
                    result = -1;
                } else {
                    result = 1;
                }
                return result;
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_folder_item,
                parent,
                false);
        ViewHolder holder = new ViewHolder(view);
        holder.setFolderSelect(folderSelect);
        holder.setMutiSelect(multiSelect);
        holder.setFilesystemItemTouchListener(filesystemItemTouchListener);
        view.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setFile(directoryList[position]);
        //holder.setFolderSelect(getFolderSelect());
        holder.setSelected(getSelected());
        holder.dataBindView();
    }

    @Override
    public int getItemCount() {
        return directoryList.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
        File file;
        boolean folderSelect;
        boolean mutiSelect;
        TextView lblFileFolderName;
        TextView lblFileFolderDetails;
        CheckBox chkFileSelected;
        SimpleDateFormat modifiedDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        OnFilesystemItemTouchListener filesystemItemTouchListener;
        List<File> selected;

        public void setSelected(List<File> selected) {
            this.selected = selected;
        }

        public void setFilesystemItemTouchListener(OnFilesystemItemTouchListener filesystemItemTouchListener) {
            this.filesystemItemTouchListener = filesystemItemTouchListener;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public boolean getFolderSelect() { return this.folderSelect; }

        public void setFolderSelect(boolean folderSelect) { this.folderSelect = folderSelect; }

        public boolean isMutiSelect() {
            return mutiSelect;
        }

        public void setMutiSelect(boolean mutiSelect) {
            this.mutiSelect = mutiSelect;
        }

        public ViewHolder(View itemView) {
            super(itemView);
        }

        private void setupUiElements() {
            lblFileFolderName = (TextView) itemView.findViewById(R.id.lblFileFolderName);
            lblFileFolderDetails = (TextView) itemView.findViewById(R.id.lblFileFolderDetails);
            chkFileSelected = (CheckBox) itemView.findViewById(R.id.chkFileSelected);
        }

        public void dataBindView() {
            setupUiElements();
            lblFileFolderName.setText(file.getName());
            Date modifiedDate = new Date(file.lastModified());
            String details = ", Last Modified: " + modifiedDateFormat.format(modifiedDate);
            if (file.isDirectory()) {
                int dirColor = ContextCompat.getColor(itemView.getContext(), R.color.folder_color);
                lblFileFolderName.setTextColor(dirColor);
                details = "folder" + details;
                chkFileSelected.setVisibility(View.GONE);
            } else {
                int fileColor = 0;
                if (folderSelect) {
                    fileColor = ContextCompat.getColor(itemView.getContext(), R.color.unselectable);
                } else {
                    fileColor = ContextCompat.getColor(itemView.getContext(), R.color.file_color);
                }
                lblFileFolderName.setTextColor(fileColor);
                details = "file" + details;
                if (!folderSelect && mutiSelect) {
                    chkFileSelected.setVisibility(View.VISIBLE);
                }
            }
            chkFileSelected.setChecked(selected != null && selected.contains(file));
            lblFileFolderDetails.setText(details);
            Log.d(DEBUG_TAG, "DataBinding for " + file.getName());
        }

        @Override
        public void onClick(View view) {
            if (filesystemItemTouchListener != null) {
                filesystemItemTouchListener.onFilesystemItemTouch(file, getAdapterPosition());
            }
        }
    }
}
