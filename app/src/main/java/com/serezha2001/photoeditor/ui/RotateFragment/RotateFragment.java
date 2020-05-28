package com.serezha2001.photoeditor.ui.RotateFragment;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;


public class RotateFragment extends Fragment {

    private SeekBar angle;
    private TextView angleView;
    private Bitmap prevBitmap = null;
    private ProgressBar progressBar;
    private LinearLayout btnsLayout;
    private Asynced task;

    class Asynced extends AsyncTask<Integer, Void, Void> {
        Bitmap redactBitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            angle.setVisibility(View.INVISIBLE);
            angleView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... angle) {
            redactBitmap = Bitmap.createBitmap(prevBitmap, 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight(), rotateImageByMrx(angle[0]), true);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            if (redactBitmap != null) {
                MainActivity.mainImage.setImageBitmap(redactBitmap);
            }
            btnsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_rotate, container, false);
        MainActivity.mainImage.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        angle = (SeekBar)root.findViewById(R.id.angleSeekbar);
        angleView = (TextView)root.findViewById(R.id.angleView);
        angle.setMax(360);
        angle.setProgress(180);
        angleView.setText("0 deg");
        btnsLayout = (LinearLayout)root.findViewById(R.id.processBtnsLayout);
        btnsLayout.setVisibility(View.INVISIBLE);
        Button applyBtn = (Button) root.findViewById(R.id.applyBtn);
        Button cancelBtn = (Button) root.findViewById(R.id.cancelBtn);

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
                Asynced task = new Asynced();
                task.execute(anglefactor);
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                btnsLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                angle.setVisibility(View.VISIBLE);
                angleView.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.mainImage.setImageBitmap(prevBitmap);
                angle.setProgress(180);
                angleView.setText("0 deg");
                btnsLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                angle.setVisibility(View.VISIBLE);
                angleView.setVisibility(View.VISIBLE);
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            task.cancel(true);
        } catch (Exception e) {
           // Toast.makeText(getContext(), ""+e, Toast.LENGTH_LONG).show();
        }
    }

    private Matrix rotateImageByMrx(int angle)
    {
        double radians = (angle  * Math.PI) / 180.0;
        Matrix matrix = new Matrix();

        float[] arr = {(float)Math.cos(radians), (float)-Math.sin(radians), prevBitmap.getWidth() / 2, (float)Math.sin(radians), (float)Math.cos(radians), prevBitmap.getHeight() / 2, 0.0f, 0.0f, 1.0f};
        matrix.setValues(arr);

        return (matrix);
    }

    private Bitmap rotateImage(int angle) {
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
        return rotatedBitmap;
        //MainActivity.mainImage.setImageBitmap(rotatedBitmap);
    }

}
