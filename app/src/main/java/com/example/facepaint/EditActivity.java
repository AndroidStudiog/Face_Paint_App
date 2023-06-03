package com.example.facepaint;

import static com.example.facepaint.MainActivity.imgUri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.facepaint.databinding.ActivityEditBinding;

public class EditActivity extends AppCompatActivity {
    ActivityEditBinding binding;
    int type1;
    int image;

    public static ImageView person_img;

    public static ImageView flag_img;

    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        person_img = (ImageView) findViewById(R.id.editImage);

        flag_img = (ImageView) findViewById(R.id.editPaintImage);

        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent submit=new Intent(EditActivity.this,SubmitActivity.class);
                startActivity(submit);
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

                    Uri outputUri = data.getData();

                    // handle the result uri as you want, such as display it in an imageView;

                    person_img.setImageURI(outputUri);

                    break;

            }
        }
    }
}