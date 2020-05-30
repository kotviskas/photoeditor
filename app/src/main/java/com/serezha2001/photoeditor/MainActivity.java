package com.serezha2001.photoeditor;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.serezha2001.photoeditor.ui.HomeFragment.HomeFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static ImageView mainImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainImage = findViewById(R.id.mainImage);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        toolbar.setTitleTextColor(Color.WHITE);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.eight, R.id.nav_gallery, R.id.nav_slideshow, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.nine, R.id.ten, R.id.eleven, R.id.twelve)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_image:
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    } else {
                        saveImage(((BitmapDrawable) MainActivity.mainImage.getDrawable()).getBitmap(), "redactedImage");
                        Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error! " + e, Toast.LENGTH_LONG).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public static float[] getBitmapPositionInsideImageView() {
        float[] crdnts = new float[7];
        if (mainImage == null || mainImage.getDrawable() == null)
            return crdnts;

        float[] f = new float[9];
        int dens = ((BitmapDrawable) mainImage.getDrawable()).getBitmap().getDensity();
        float scaleRatio = (float) dens / 160;
        if (scaleRatio == 0) {
            scaleRatio = 1;
        }
        mainImage.getImageMatrix().getValues(f);
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        final Drawable d = mainImage.getDrawable();
        final int origW = (int) (d.getIntrinsicWidth() / scaleRatio);
        final int origH = (int) (d.getIntrinsicHeight() / scaleRatio);

        final int actW = Math.round(origW * scaleX * scaleRatio);
        final int actH = Math.round(origH * scaleY * scaleRatio);

        int imgViewW = mainImage.getWidth();
        int imgViewH = mainImage.getHeight();

        int top = (int) (imgViewH - actH) / 2;
        int left = (int) (imgViewW - actW) / 2;
        crdnts[0] = left;
        crdnts[1] = top;
        crdnts[2] = actW + left;
        crdnts[3] = actH + top;
        crdnts[4] = scaleX;
        crdnts[5] = scaleY;
        crdnts[6] = scaleRatio;
        return crdnts;
    }

    private void saveImage(Bitmap bitmap, @NonNull String name) throws IOException { // saving image
        OutputStream fOs;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = this.getApplicationContext().getContentResolver();
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
            MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        }
        fOs.flush();
        fOs.close();
    }
}
