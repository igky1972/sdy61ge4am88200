package com.igkyrtis.sdy61ge4am88200;

import com.google.android.gms.drive.CreateFileActivityOptions;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.igkyrtis.sdy61ge4am88200.tasks.EditDriveFileAsyncTask;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.support.annotation.NonNull;
import android.util.Log;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class HomeActivity extends BaseDriveActivity {
//public class HomeActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    /**
     * Request code for file creator activity.
     */
    private static final int REQUEST_CODE_CREATOR = NEXT_AVAILABLE_REQUEST_CODE;

    /**
     * Request code for the file opener activity.
     */
    private static final int REQUEST_CODE_OPENER = NEXT_AVAILABLE_REQUEST_CODE + 1;

    /**
     * Text file MIME type.
     */
    private static final String MIME_TYPE_TEXT = "text/plain";

    /**
     * Title edit text field.
     */
     private EditText mTitleEditText;

    /**
     * Body edit text field.
     */
    private EditText mContentsEditText;

    /**
     * Save button. Invokes the upsert tasks on click.
     */
    private Button mSaveButton;

    /**
     * Open button. Invokes the upsert tasks on click.
     */
    private Button mOpenButton;


    /**
     * Create button. Invokes the upsert tasks on click.
     */
    private Button mCreateButton;



    /**
     * TabView button. Invokes the upsert tasks on click.
     */
    private Button mTabsActivityStart;

    /**
     * Drive ID of the currently opened Drive file.
     */
    private DriveId mCurrentDriveId;

    /**
     * Currently opened file's metadata.
     */
    private Metadata mMetadata;

    /**
     * Currently opened file's contents.
     */
    private DriveContents mDriveContents;

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        DisplayToast("Your app has just started!!!");


		// the TextBox to enter our text
		// EditText textBox = (EditText) findViewById(R.id.txtEditTextBox);
        mTitleEditText = (EditText) findViewById(R.id.txtEditTextTitle);
		mContentsEditText = (EditText)  findViewById(R.id.txtEditTextBox);
		mCreateButton = (Button) findViewById(R.id.btnCreate);
		mSaveButton = (Button) findViewById(R.id.btnSave);
        mOpenButton = (Button) findViewById(R.id.btnOpen);
        mTabsActivityStart = (Button) findViewById(R.id.btnTabs);


		//---Button view Internet Button---
        Button btnInternet = (Button) findViewById(R.id.btnInternet);
        btnInternet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null){
                    //display - no net
                    DisplayToast("You have clicked the Internet button, but there's no connection");
                }
                else{
                    //is connected or not?
                    if (networkInfo.isConnected()){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.eap.gr"));
                        startActivity(browserIntent);
                    }
                }


            }
        });




        //---Button view Create Button---
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DisplayToast("You have clicked the Create button");
                createDriveFile();
            }
        });



        //---Button view Open Button---
        mOpenButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DisplayToast("You have clicked the Open button");
                openDriveFile();
            }
        });





        //---Button view Save Button---
        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
				DisplayToast("You have clicked the Save button");
				save();
            }
        });


        //---Button Tabs View---

        mTabsActivityStart.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                DisplayToast("You have clicked the Tabs button");
                startTabsActivity();
            }
        });
		
        refreshUiFromCurrentFile();
    }
	
	
	// Displaying toast messages
    private void DisplayToast (String msg) {
        Toast.makeText(getBaseContext(), msg,
                Toast.LENGTH_SHORT).show();
    }

    private void startTabsActivity(){
        DisplayToast("Tabs activity started");
        Intent intentTabs = new Intent(this, TabsActivity.class);
        startActivity(intentTabs);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CREATOR:
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    mCurrentDriveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    loadCurrentFile();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }





    /**
     * Refreshes the main content view with the current activity state.
     */
    private void refreshUiFromCurrentFile() {
        Log.d(TAG, "Refreshing...");
        if (mCurrentDriveId == null) {
            mSaveButton.setEnabled(false);
            return;
        }
        mSaveButton.setEnabled(true);

        if (mMetadata == null || mDriveContents == null) {
            return;
        }

        mTitleEditText.setText(mMetadata.getTitle());
        try {
            String contents = Utils.readFromInputStream(mDriveContents.getInputStream());
            mContentsEditText.setText(contents);
        } catch (IOException e) {
            Log.e(TAG, "IOException while reading from contents input stream", e);
            showToast(R.string.msg_errreading);
            mSaveButton.setEnabled(false);
        }
    }




    /**
     * Retrieves the currently selected Drive file's metadata and contents.
     */
    private void loadCurrentFile() {
        Log.d(TAG, "Retrieving...");
        final DriveFile file = mCurrentDriveId.asDriveFile();

        // Retrieve and store the file metadata and contents.
        mDriveResourceClient.getMetadata(file)
                .continueWithTask(new Continuation<Metadata, Task<DriveContents>>() {
                    @Override
                    public Task<DriveContents> then(@NonNull Task<Metadata> task) {
                        if (task.isSuccessful()) {
                            mMetadata = task.getResult();
                            return mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
                        } else {
                            return Tasks.forException(task.getException());
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DriveContents>() {
            @Override
            public void onSuccess(DriveContents driveContents) {
                mDriveContents = driveContents;
                refreshUiFromCurrentFile();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to retrieve file metadata and contents.", e);
            }
        });
    }





    /**
     * Saves metadata and content changes.
     */
    // Called when button save pressed or clicked....
    private void save() {

        Log.d(TAG, "Saving...");

        if (mCurrentDriveId == null) {
            return;
        }

        new EditDriveFileAsyncTask(mDriveResourceClient) {
            @Override
            public Changes edit(DriveContents driveContents) {
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setTitle(mTitleEditText.getText().toString())
                        .build();
                try {
                    byte[] body = mContentsEditText.getText().toString().getBytes();
                    driveContents.getOutputStream().write(body);
                } catch (IOException e) {
                    Log.e(TAG, "IOException while reading from driveContents output stream", e);
                }
                return new Changes(metadataChangeSet, driveContents);
            }

            @Override
            protected void onPostExecute(Boolean isSuccess) {
                if (isSuccess) {
                    showToast(R.string.msg_saved);
                } else {
                    showToast(R.string.msg_errsaving);
                }
            }
        }.execute(mCurrentDriveId);

    }


    /**
     * Shows a {@link Toast} with the given message.
     */
    private void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_LONG).show();
    }





    /**
     * Launches an {@link Intent} to create a new Drive file.
     */
    private void createDriveFile() {
        Log.i(TAG, "Create drive file.");

        if (!isSignedIn()) {
            Log.w(TAG, "Failed to create file, user is not signed in.");
            return;
        }

        // Nullify the previous DriveContents and Metadata
        mDriveContents = null;
        mMetadata = null;

        // Build the DriveContents and start a CreateFileActivityIntent.
        mDriveResourceClient.createContents()
                .continueWithTask(new Continuation<DriveContents, Task<IntentSender>>() {
                    @Override
                    public Task<IntentSender> then(@NonNull Task<DriveContents> task) {
                        if (!task.isSuccessful()) {
                            return Tasks.forException(task.getException());
                        }
                        Log.i(TAG, "New contents created.");
                        // Build file metadata options.
                        MetadataChangeSet metadataChangeSet =
                                new MetadataChangeSet.Builder()
                                        .setMimeType(MIME_TYPE_TEXT)
                                        .build();
                        // Build file creation options.
                        CreateFileActivityOptions createFileActivityOptions =
                                new CreateFileActivityOptions.Builder()
                                        .setInitialMetadata(metadataChangeSet)
                                        .setInitialDriveContents(task.getResult())
                                        .build();
                        // Build CreateFileActivityIntent.
                        return mDriveClient.newCreateFileActivityIntentSender(createFileActivityOptions);
                    }
                }).addOnSuccessListener(new OnSuccessListener<IntentSender>() {
            @Override
            public void onSuccess(IntentSender intentSender) {
                Log.i(TAG, "New CreateActivityIntent created.");
                try {
                    // Start CreateFileActivityIntent
                    startIntentSenderForResult(
                            intentSender,
                            REQUEST_CODE_CREATOR,
                            /* fillInIntent= */ null,
                            /* flagsMask= */ 0,
                            /* flagsValues= */ 0,
                            /* extraFlags= */ 0);
                } catch (SendIntentException e) {
                    Log.e(TAG, "Failed to launch file chooser.", e);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to create file.", e);
            }
        });
    }





    /**
     * Launches an {@link Intent} to open an existing Drive file.
     */
    private void openDriveFile() {
        Log.i(TAG, "Open Drive file.");

        if (!isSignedIn()) {
            Log.w(TAG, "Failed to open file, user is not signed in.");
            return;
        }

        // Build activity options.
        final OpenFileActivityOptions openFileActivityOptions =
                new OpenFileActivityOptions.Builder()
                        .setMimeType(Collections.singletonList(MIME_TYPE_TEXT))
                        .build();

        // Start a OpenFileActivityIntent
        mDriveClient.newOpenFileActivityIntentSender(openFileActivityOptions)
                .addOnSuccessListener(new OnSuccessListener<IntentSender>() {
                    @Override
                    public void onSuccess(IntentSender intentSender) {
                        try {
                            startIntentSenderForResult(
                                    intentSender,
                                    REQUEST_CODE_OPENER,
                                    /* fillInIntent= */ null,
                                    /* flagsMask= */ 0,
                                    /* flagsValues= */ 0,
                                    /* extraFlags= */ 0);
                        } catch (SendIntentException e) {
                            Log.w(TAG, "Unable to send intent.", e);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to create OpenFileActivityIntent.", e);
            }
        });
    }




}