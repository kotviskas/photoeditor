package com.serezha2001.photoeditor.ui.HomeFragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HomeFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1, SELECT_FILE = 0, SAVE_PIC = 2;
    private String curImagePath = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity.mainImage.setVisibility(View.VISIBLE);
        FloatingActionButton choosePic = root.findViewById(R.id.choosePic);
        FloatingActionButton camButton = root.findViewById(R.id.takePhoto);
        FloatingActionButton saveBtn = root.findViewById(R.id.savePic);

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SELECT_FILE);
                    } else {
                        choosePic();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error! " + e, Toast.LENGTH_LONG).show();
                }
            }
        });
        camButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
                } else {
                    openCamera();
                }
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, SAVE_PIC);
                    } else {
                        saveImage(((BitmapDrawable) MainActivity.mainImage.getDrawable()).getBitmap(), "redactedImage");
                        Toast.makeText(getContext(), "Done!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error! " + e, Toast.LENGTH_LONG).show();
                }
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
        if (requestCode == SAVE_PIC) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    saveImage(((BitmapDrawable) MainActivity.mainImage.getDrawable()).getBitmap(), "redactedImage");
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error! " + e, Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (requestCode == SELECT_FILE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePic();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { // getting new image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    MainActivity.mainImage.setImageURI(Uri.parse(curImagePath));
                    //rotateImage();
                } catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Error! " + e, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == SELECT_FILE) {
                MainActivity.mainImage.setImageURI(data.getData());
            }
        }
    }

    /*private void rotateImage() {
        try {
            Bitmap temp = BitmapFactory.decodeFile(curImagePath);
            ExifInterface exifInterface = new ExifInterface(curImagePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Matrix  matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(270);
                    break;
                default:
            }
            MainActivity.mainImage.setImageBitmap(Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    private void saveImage(Bitmap bitmap, @NonNull String name) throws IOException { // saving image
        OutputStream fOs;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/");
            fOs = resolver.openOutputStream(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOs);
        } else {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator, name + ".png");
            fOs = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOs);
            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        }
        fOs.flush();
        fOs.close();
    }

    private File getImageFile() throws IOException {
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("photo_", ".png", storageDir);
    }

    private void choosePic() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Error! " + e, Toast.LENGTH_SHORT).show();
            }
            if (imageFile != null) {
                curImagePath = imageFile.getAbsolutePath();
                Uri imagePath = FileProvider.getUriForFile(getContext(), "com.serezha2001.photoeditor.fileprovider", imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
                curImagePath = imagePath.toString();
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                Toast.makeText(getContext(), "Error! ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}