package com.example.facepaint;

import static com.example.facepaint.MainActivity.imgUri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.module.ManifestParser;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.facepaint.Adapters.Adapter;
import com.example.facepaint.Models.Model;
import com.example.facepaint.databinding.ActivityPaintBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class PaintActivity extends AppCompatActivity {
    ActivityPaintBinding binding;
    ArrayList<Model> list;
    Adapter adapter;
    int type;
    int image;
    String internet="https://www.google.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPaintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageView.setImageURI(getIntent().getData());

        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PaintActivity.this,EditActivity.class);
                startActivityForResult(intent,30);
            }
        });

        binding.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(PaintActivity.this)
                        .galleryOnly()
                        .start(20);
            }
        });

        binding.expression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragment bottomSheetFragment=new BottomSheetFragment();
                FragmentManager fragmentManager=getSupportFragmentManager();
                bottomSheetFragment.show(fragmentManager,bottomSheetFragment.getTag());
            }
        });

        binding.chrome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri=Uri.parse(internet);
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(PaintActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    saveToGallery();
                }
                else{
                    ActivityCompat.requestPermissions(PaintActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },1);
                }
            }
        });
/*
        type= getIntent().getIntExtra("type",0);
        if (type==1){
            Intent intent=getIntent();
            image=intent.getIntExtra("image",0);
            binding.paintImageView.setImageResource(image);
        }

 */

        list=new ArrayList<>();
        list.add(new Model(R.drawable.af,"Afghanistan"));
        list.add(new Model(R.drawable.au,"Australia"));
        list.add(new Model(R.drawable.bd,"Bangladesh"));
        list.add(new Model(R.drawable.gb,"England"));
        list.add(new Model(R.drawable.in,"India"));
        list.add(new Model(R.drawable.ie,"Ireland"));
        list.add(new Model(R.drawable.nz,"New Zealand"));
        list.add(new Model(R.drawable.pk,"Pakistan"));
        list.add(new Model(R.drawable.za,"South Africa"));
        list.add(new Model(R.drawable.sl,"Sri Lanka"));
        list.add(new Model(R.drawable.wi,"West Indies"));
        list.add(new Model(R.drawable.zw,"Zimbabwe"));

        adapter=new Adapter(list,this);
        binding.recyclerView.setAdapter(adapter);

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,4);
        binding.recyclerView.setLayoutManager(gridLayoutManager);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==30){
            try {
                imgUri = data.getData();
                if (!imgUri.equals("")){
                    startActivity(new Intent(PaintActivity.this,EditActivity.class));
                }
            }
            catch (Exception e){

            }
        }
        if (requestCode == 20){
            Uri uri=data.getData();
            binding.paintImageView.setImageURI(uri);
        }
    }

    private void filter(String newText) {
        ArrayList<Model> li = new ArrayList<>();
        for (Model item : list) {
            if (item.getName().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))) {
                li.add(item);
            }
        }
        adapter.searchBox(li);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                saveToGallery();
            }
            else{
                Toast.makeText(this, "Please provide granted permission.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void saveToGallery(){
        Uri images;
        ContentResolver contentResolver=getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            images = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }
        else{
            images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*");
        Uri uri = contentResolver.insert(images,contentValues);
        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.imageView.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, outputStream);
            Objects.requireNonNull(outputStream);
            Toast.makeText(this, "Images is saved.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, "Images is not saved.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}