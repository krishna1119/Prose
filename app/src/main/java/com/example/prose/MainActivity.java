package com.example.prose;



import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;


import com.canhub.cropper.CropImage.*;
import com.canhub.cropper.CropImageView;
import com.canhub.cropper.CropImageActivity;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.prose.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private static final int CAMERA_PIC_REQUEST = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        Button btn = findViewById(R.id.extract_button);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},CAMERA_PIC_REQUEST );
        }


        btn.setOnClickListener(view -> onCameraButtonClicked());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onCameraButtonClicked(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
            initializeCameraLauncher();
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }


    }

    private void initializeCameraLauncher() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                // Get the captured image URI
                Intent data = result.getData();
                Uri capturedImageUri = data.getData();

                // Launch the cropping activity with the captured image URI
                Bitmap resultAfterCrop = cropImage(capturedImageUri);
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap cropImage(Uri imageUri) {
        Bitmap bitmap;
//        CropImage.activity(imageUri)
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .start(this);

        //after get data, then we gong to do the ocr part
        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
        return bitmap;
    }

    private void gettextFromImaages(Bitmap bitmap){
        TextRecognizer recogonizer = new TextRecognizer.Builder(this).build();
        if(recogonizer.isOperational()){
            Toast.makeText(this, "ErrorOccured", Toast.LENGTH_SHORT).show();
        }
        else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock>  textBlock = recogonizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<textBlock.size();i++){
                TextBlock tb = textBlock.valueAt(i);
                stringBuilder.append(tb.getValue());
                stringBuilder.append("\n");  //for line keeping as the frame


            }
            String resultText = new String(stringBuilder.toString());


        }
    }

}