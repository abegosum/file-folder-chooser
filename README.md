# file-folder-chooser

A simple, lightweight file and folder chooser dialogue library for Android

## Features

* Allows selection of a file or a folder
* Clean interface
* Permissions handler to work with marshmallow and later permissions model
* Relies on specific intents
  * Other projects tend to raise general intents
  * Since there has been no standard intent for file and folder manipulation in Android, this leads to unreliable behavior between versions of Android
  * Using a specific call to a library eliminates the ambiguity and ensures consistent behavior across versions
  * The drawback is that this library is meant to be used in an app specifically and cannot be called as a standalone handler for _any_ file intent
* Multi-file select coming soon

## Changelog

### v0.1.0

Initial versioned release.

## Building

To build the project, apply the following steps:

1. Clone the repository and check out a tag of a version you'd like to build (or build from the latest commit, understanding that some features may still be untested)
1. In the directory where you cloned the repository, use the gradle wrapper to run `gradlew assemble`
   1. For Unix-like systems (MacOSX Terminal, Linux, etc), this will be `./gradlew assemble`
   1. For Windows systems using command prompt, this will be `gradlew.bat assemble`
   1. For Windows systems using Powershell, this will be `.\gradlew.bat assemble`
   1. NOTE: If the build fails, ensure that your `JAVA_HOME` environment variable points to the location of a valid Java SDK (this _must_ be the SDK, the JRE will not work)
1. Once the build completes, you can find the resulting Android library file under `filefolderchooser/build/outputs/aar/filefolderchooser-release.aar`

## Including in your Android Studio Project

To include the aar in your project, open your project using Android Studio and follow these steps:

1. Select File -> Project Structure
1. In the top-left of the resulting window, click the `+` button to add a module
1. Select "Import .JAR/.AAR Package"
1. Under "File name," click the Browse button (marked `...`) and select the `filefolderchooser-release.aar` file generated using the build process above
1. "Subproject name" should automatically be set to `filefolderchooser-release`
1. Click "Finish"

This will create a new module with the Android library available to the rest of the project.

## Using the Library

To display a dialogue to a user, you send an intent to the `FileFolderChooser` class.  There are two options that you can add to the intent with names identified by public constants ending in `_KEY` on the `FileFolderChooser` class.  Those two options are:

* `STARTING_PATH` (identifed by the constant `FileFolderChooser.STARTING_PATH_KEY`)
  * The starting path of the dialogue to be displayed to the user.  This can be useful if you would like your app to have a default folder where the user will be working.  If unspecified, this will be set to the default of `android.os.Environment.getExternalStorageDirectory()`
* `FOLDER_SELECT` (identified by the constant `FileFolderChooser.FOLDER_SELECT_KEY`)
  * A boolean.  If true, the dialogue will allow the user to select a folder (files will be listed; but, they will appear in RED to indicate that they cannot be selected).  If false, the dialogue will allow the user to select a file.  If unspecified, this will be false.

Additionally, the resulting intent will come back with the selected file or folder as part of the data on the returning intent.  The selected file data is identified by the constant `FileFolderChooser.SELECTED_PATH_KEY`.

Here's an example of some code from an Android activity that can handle a file selection (note the `onActivityResult` interrogating the resulting data for `FileFolderChooser.SELECTED_PATH_KEY`.

```java
    public static final int SINGLE_FILE_CODE = 0;

    private void startFileFolderChooserForSingleFile() {
        Intent startActivity = new Intent(this, FileFolderChooser.class);
        // setting this is redundant - false is default
        startActivity.putExtra(FileFolderChooser.FOLDER_SELECT_KEY, false);
        startActivityForResult(startActivity, SINGLE_FILE_CODE);
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
        }
    }
```

The `startFileFolderChooserForSingleFile` method may be called in an `onClickListener` for a button or elsewhere in the behavior of the activity.

Once the `FileFolderChooser` activity is finished, `onActivityResult` will be called.  `requestCode` will be the request code identified by our activity when we sent the intent, `resultCode` will be a standard activity finish code and the `data` will be the resulting Intent.

For more comprehensive examples, look in the `com.aaronmbond.filefolderchoosertestharness` app module included with the code.  The `FileFolderChooserTestActivity` class has multiple use cases you can draw from.

## License

This library is released under the Apache License, Version 2.0. 

See the included `LICENSE` file for all terms.
