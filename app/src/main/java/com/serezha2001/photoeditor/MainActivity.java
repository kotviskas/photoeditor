package com.serezha2001.photoeditor;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.eight, R.id.nav_gallery, R.id.nav_slideshow, R.id.four,R.id.five,R.id.six,R.id.seven,R.id.nine, R.id.ten,R.id.eleven,R.id.twelve)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        int dens = ((BitmapDrawable)mainImage.getDrawable()).getBitmap().getDensity();
        float scaleRatio = (float)dens / 160;
        if (scaleRatio == 0) {
            scaleRatio = 1;
        }
        mainImage.getImageMatrix().getValues(f);
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        final Drawable d = mainImage.getDrawable();
        final int origW = (int)(d.getIntrinsicWidth() / scaleRatio);
        final int origH = (int)(d.getIntrinsicHeight() / scaleRatio);

        final int actW = Math.round(origW * scaleX * scaleRatio);
        final int actH = Math.round(origH * scaleY * scaleRatio);

        int imgViewW = mainImage.getWidth();
        int imgViewH = mainImage.getHeight();

        int top = (int) (imgViewH - actH)/2;
        int left = (int) (imgViewW - actW)/2;
        crdnts[0] = left;
        crdnts[1] = top;
        crdnts[2] = actW + left;
        crdnts[3] = actH + top;
        crdnts[4] = scaleX;
        crdnts[5] = scaleY;
        crdnts[6] = scaleRatio;
        return crdnts;
    }
}
