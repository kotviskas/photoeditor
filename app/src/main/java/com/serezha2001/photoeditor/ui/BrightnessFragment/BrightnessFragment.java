package com.serezha2001.photoeditor.ui.BrightnessFragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

public class BrightnessFragment extends Fragment {

    public TextView coefView;
    public SeekBar seekBar;
    public double Coef;
    public Bitmap prevBitmap;
    public ProgressBar progressBar;
    LinearLayout btnsLayout;
    Button applyBtn, cancelBtn;
    Asynced task;

    class Asynced extends AsyncTask<Double, Void, Void> {
        Bitmap redactBitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            seekBar.setVisibility(View.INVISIBLE);
            coefView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Double... coef) {
            redactBitmap = bright(coef[0]);
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_brightness, container, false);

        progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        seekBar = (SeekBar)root.findViewById(R.id.brightSeekbar);
        coefView = (TextView)root.findViewById(R.id.coefView);
        prevBitmap = null;
        btnsLayout = (LinearLayout)root.findViewById(R.id.processBtnsLayout);
        btnsLayout.setVisibility(View.INVISIBLE);
        applyBtn = (Button)root.findViewById(R.id.applyBtn);
        cancelBtn = (Button)root.findViewById(R.id.cancelBtn);

        seekBar.setMax(1000);
        seekBar.setProgress(100);
        coefView.setText("100%");

        btnsLayout = (LinearLayout)root.findViewById(R.id.processBtnsLayout);
        btnsLayout.setVisibility(View.INVISIBLE);
        applyBtn = (Button)root.findViewById(R.id.applyBtn);
        cancelBtn = (Button)root.findViewById(R.id.cancelBtn);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                coefView.setText(String.valueOf(seekBar.getProgress())+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (prevBitmap == null) {
                    prevBitmap = ((BitmapDrawable)MainActivity.mainImage.getDrawable()).getBitmap();
                }
                Coef = (double)(seekBar.getProgress()) / 100;
                coefView.setText(String.valueOf((int)(Coef * 100))+"%");
                Asynced task = new Asynced();
                task.execute(Coef);

            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                btnsLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                seekBar.setVisibility(View.VISIBLE);
                coefView.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.mainImage.setImageBitmap(prevBitmap);
                seekBar.setProgress(100);
                coefView.setText("100%");
                btnsLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                seekBar.setVisibility(View.VISIBLE);
                coefView.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            task.cancel(true);
        } catch (Exception e) {
            //Toast.makeText(getContext(), ""+e, Toast.LENGTH_LONG).show();
        }
    }

    Bitmap bright(final double coef) {
        final Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Thread thread = new Thread(){
            public void run(){
                for (int x = 0; x < (Integer) redactBitmap.getWidth()/3; x++) {
                    for (int y = 0; y < redactBitmap.getHeight(); y++) {
                        int prevBitmapPixel = prevBitmap.getPixel(x, y);
                        double red = Color.red(prevBitmapPixel) * coef;
                        red = Math.min(255, Math.max(0, red));
                        double green = Color.green(prevBitmapPixel) * coef;
                        green = Math.min(255, Math.max(0, green));
                        double blue = Color.blue(prevBitmapPixel) * coef;
                        blue = Math.min(255, Math.max(0, blue));
                        int newPixel = Color.rgb((int) red, (int) green, (int) blue);
                        redactBitmap.setPixel(x, y, newPixel);
                    }
                }
            }
        };
        Thread thread2 = new Thread(){
            public void run(){
                for (int x = (Integer) redactBitmap.getWidth()/3; x < (Integer) redactBitmap.getWidth()/3*2; x++) {
                    for (int y = 0; y < redactBitmap.getHeight(); y++) {
                        int prevBitmapPixel = prevBitmap.getPixel(x, y);
                        double red = Color.red(prevBitmapPixel) * coef;
                        red = Math.min(255, Math.max(0, red));
                        double green = Color.green(prevBitmapPixel) * coef;
                        green = Math.min(255, Math.max(0, green));
                        double blue = Color.blue(prevBitmapPixel) * coef;
                        blue = Math.min(255, Math.max(0, blue));
                        int newPixel = Color.rgb((int) red, (int) green, (int) blue);
                        redactBitmap.setPixel(x, y, newPixel);
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer) redactBitmap.getWidth()/3*2; x < redactBitmap.getWidth(); x++) {
            for (int y = 0; y < redactBitmap.getHeight(); y++) {
                int prevBitmapPixel = prevBitmap.getPixel(x, y);
                double red = Color.red(prevBitmapPixel) * coef;
                red = Math.min(255, Math.max(0, red));
                double green = Color.green(prevBitmapPixel) * coef;
                green = Math.min(255, Math.max(0, green));
                double blue = Color.blue(prevBitmapPixel) * coef;
                blue = Math.min(255, Math.max(0, blue));
                int newPixel = Color.rgb((int) red, (int) green, (int) blue);
                redactBitmap.setPixel(x, y, newPixel);
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        return redactBitmap;
        //MainActivity.mainImage.setImageBitmap(redactBitmap);
    }

}
