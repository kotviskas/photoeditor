package com.serezha2001.photoeditor.ui.RotateFragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;


public class RotateFragment extends Fragment {

    public Button Undo;
    public SeekBar angle;
    public TextView angleView;
    public Bitmap prevBitmap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_rotate, container, false);
        Undo = (Button)root.findViewById(R.id.undo);
        Undo.setEnabled(false);
        angle = (SeekBar)root.findViewById(R.id.angleSeekbar);
        angleView = (TextView)root.findViewById(R.id.angleView);
        angle.setMax(360);
        angle.setProgress(180);
        angleView.setText("0 deg");

        angle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                angleView.setText(String.valueOf(angle.getProgress() - 180)+" deg");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (prevBitmap == null) {
                    prevBitmap = ((BitmapDrawable)MainActivity.mainImage.getDrawable()).getBitmap();
                }
                int anglefactor = (int)(angle.getProgress() - 180);
                angleView.setText(String.valueOf(anglefactor)+" deg");
                Undo.setEnabled(true);
                rotateImage(anglefactor);
            }
        });
        Undo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.mainImage.setImageBitmap(prevBitmap);
                angle.setProgress(180);
                angleView.setText("0 deg");
                Undo.setEnabled(false);
            }
        });

        return root;
    }

    private void rotateImage(int angle) {
        final Bitmap rotatedBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final double sin = Math.sin(Math.toRadians(angle)), cos = Math.cos(Math.toRadians(angle)), x0 = 0.5 * (prevBitmap.getWidth() - 1), y0 = 0.5 * (prevBitmap.getHeight() - 1);
        Thread thread1 = new Thread() {
            public void run() {
                for (int x = 0; x < (Integer) rotatedBitmap.getWidth() / 3; x++) {
                    for (int y = 0; y < rotatedBitmap.getHeight(); y++) {
                        double a = x - x0, b = y - y0;
                        int xx = (int) (+a * cos - b * sin + x0), yy = (int) (+a * sin + b * cos + y0);
                        if (xx >= 0 && xx < prevBitmap.getWidth() && yy >= 0 && yy < prevBitmap.getHeight()) {
                            rotatedBitmap.setPixel(x, y, prevBitmap.getPixel(xx, yy));
                        }
                    }
                }
            }
        };
        Thread thread2 = new Thread() {
            public void run() {
                for (int x = (Integer) rotatedBitmap.getWidth() / 3; x < (Integer) rotatedBitmap.getWidth() / 3 * 2; x++) {
                    for (int y = 0; y < rotatedBitmap.getHeight(); y++) {
                        double a = x - x0, b = y - y0;
                        int xx = (int) (+a * cos - b * sin + x0), yy = (int) (+a * sin + b * cos + y0);
                        if (xx >= 0 && xx < prevBitmap.getWidth() && yy >= 0 && yy < prevBitmap.getHeight()) {
                            rotatedBitmap.setPixel(x, y, prevBitmap.getPixel(xx, yy));
                        }
                    }
                }
            }
        };
        thread1.start();
        thread2.start();
        for (int x = (Integer) rotatedBitmap.getWidth() / 3 * 2; x < rotatedBitmap.getWidth(); x++) {
            for (int y = 0; y < rotatedBitmap.getHeight(); y++) {
                double a = x - x0, b = y - y0;
                int xx = (int) (+a * cos - b * sin + x0), yy = (int) (+a * sin + b * cos + y0);
                if (xx >= 0 && xx < prevBitmap.getWidth() && yy >= 0 && yy < prevBitmap.getHeight()) {
                    rotatedBitmap.setPixel(x, y, prevBitmap.getPixel(xx, yy));
                }
            }
        }
        while (thread1.isAlive() || thread2.isAlive()) {
        }
        MainActivity.mainImage.setImageBitmap(rotatedBitmap);
    }

}
