package com.jaison.app_android.filestore;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.jaison.app_android.BaseActivity;
import com.jaison.app_android.Hasura;
import com.jaison.app_android.R;
import com.jaison.app_android.auth.AuthActivity;
import com.jaison.bsimagepicker.BottomSheetImagePicker;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FilestoreActivity extends BaseActivity {

    public static void startActivity(Activity startingActivity) {
        startingActivity.startActivity(new Intent(startingActivity, FilestoreActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filestore);

        findViewById(R.id.uploadButton_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get an image from the user's camera or gallery
                BottomSheetImagePicker
                        .getInstance()
                        .showImagePicker(
                                FilestoreActivity.this,
                                (BottomSheetLayout) findViewById(R.id.bottomsheet),
                                new BottomSheetImagePicker.Listener() {
                                    @Override
                                    public void onImageArrived(Uri selectedImageUri) {
                                        uploadImage(selectedImageUri);
                                    }
                                });
            }
        });

        findViewById(R.id.uploadButton_random).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] file = "This is a random byte array".getBytes();
                uploadFile(file);
            }
        });


    }

    /**
     * This method demonstrates uploading a file using the Hasura filestore apis. The filestore APIs also allow us to set permissions on who can upload and download files.
     * Currently, the permission set is
     * Public: Anybody can read, but only logged in users can upload.
     * Hence, an authorization token (received after signup/login) is sent as a header while uploading files.
     * @param file
     */
    private void uploadFile(byte[] file) {
        if (Hasura.getSessionToken(FilestoreActivity.this) == null) {
            showToast("You need to be logged in to upload files. You can login/signup by clicking on the button named \"Authentication\" in the home page.");
            return;
        }
        String url = Hasura.Config.FILESTORE_URL + "file";

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("image/*");
        RequestBody body = RequestBody.create(mediaType, file);
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + Hasura.getSessionToken(FilestoreActivity.this))
                .build();
        showProgressDialog("Uploading file");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                dismissProgressDialog();
                showToast("File upload failed: " + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                dismissProgressDialog();
                String jsonString = response.body().string();
                if (response.isSuccessful()) {
                    showToast("File upload successful: " + jsonString);
                } else {
                    showToast("File upload failed: " + jsonString);
                }
            }
        });
    }

    private void uploadImage(Uri imageUri) {
        try {
            byte[] file = FilestoreHelper.getBytes(this, imageUri);
            uploadFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            showToast("File upload failed. Failed to convert image uri to byte[]");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        BottomSheetImagePicker.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BottomSheetImagePicker.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BottomSheetImagePicker.getInstance().destroy();
    }
}
