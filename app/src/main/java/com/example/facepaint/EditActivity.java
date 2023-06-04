package com.example.facepaint;
import static com.example.facepaint.MainActivity.imgUri;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.facepaint.databinding.ActivityEditBinding;

import java.io.File;

import okhttp3.ResponseBody;

public class EditActivity extends AppCompatActivity implements ApiService.ApiCallback {
    private ApiService apiService;
    ActivityEditBinding binding;
    int type1;
    int image;
    private Uri imgUri2;

    public static ImageView person_img;
    public static ImageView flag_img;
    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        person_img = (ImageView) findViewById(R.id.editImage);
        flag_img = (ImageView) findViewById(R.id.editPaintImage);
        done = (Button) findViewById(R.id.done);

        // Create an instance of ApiService and pass the callback
        apiService = new ApiService(this);

        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the file paths of the images
                String imagePath = getPathFromUri(EditActivity.this, imgUri); // Use the appropriate method to get the file path from the Uri
                String flagPath = "image"; // Replace with the actual file path of the flag image

                // Call the method to upload the images
                apiService.uploadImages(imagePath, flagPath);
            }
        });

         type1= getIntent().getIntExtra("type",0);
        if (type1==1){
            Intent intent=getIntent();
            image=intent.getIntExtra("image",0);
            flag_img.setImageResource(image);
        }


        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
        dsPhotoEditorIntent.setData(imgUri);
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Face_Paint");
        startActivityForResult(dsPhotoEditorIntent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    imgUri2 = data.getData();
                    person_img.setImageURI(imgUri);
                    break;
            }
        }
    }

    // Helper method to get the file path from the Uri
    private String getPathFromUri(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    @Override
    public void onSuccess(ResponseBody responseBody) {
        // Handle the API response containing the resulting image
        // For example, you can decode the response body and display it in an ImageView
        // Bitmap bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
        // imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onError(int statusCode, String errorMessage) {
        // Handle the error response
        // ...
    }

    @Override
    public void onFailure(String failureMessage) {
        // Handle the network failure
        // ...
    }
}
