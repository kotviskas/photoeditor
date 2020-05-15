package com.serezha2001.photoeditor.ui.BNWFragment;

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

public class BNWFragment extends Fragment {

    public TextView coefView;
    public SeekBar seekBar;
    public double CoefBnw;
    public Bitmap prevBitmap;
    public ProgressBar progressBar;
    LinearLayout btnsLayout;
    Button applyBtn, cancelBtn;
    Asynced task;

    class Asynced extends AsyncTask<Void, Void, Void> {
        Bitmap redactBitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            seekBar.setVisibility(View.INVISIBLE);
            coefView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            redactBitmap = bnw(CoefBnw);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            // Coef.setVisibility(View.VISIBLE);
            // coefView.setVisibility(View.VISIBLE);
            if (redactBitmap != null) {
                MainActivity.mainImage.setImageBitmap(redactBitmap);
            }
            btnsLayout.setVisibility(View.VISIBLE);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bnw, container, false);

        progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        coefView = (TextView)root.findViewById(R.id.coefView);
        seekBar = (SeekBar)root.findViewById(R.id.seekBar);
        prevBitmap = null;

        seekBar.setMax(300);
        seekBar.setProgress(50);
        coefView.setText("None");

        btnsLayout = (LinearLayout)root.findViewById(R.id.processBtnsLayout);
        btnsLayout.setVisibility(View.INVISIBLE);
        applyBtn = (Button)root.findViewById(R.id.applyBtn);
        cancelBtn = (Button)root.findViewById(R.id.cancelBtn);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                coefView.setText(String.valueOf(seekBar.getProgress() + 50) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (prevBitmap == null) {
                    prevBitmap = ((BitmapDrawable)MainActivity.mainImage.getDrawable()).getBitmap();
                }
                CoefBnw = (double)(seekBar.getProgress() + 50) / 100;
                coefView.setText(String.valueOf((int)(CoefBnw * 100)) + "%");
                Asynced task = new Asynced();
                task.execute();
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
                seekBar.setProgress(50);
                coefView.setText("None");
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
           // Toast.makeText(getContext(), ""+e, Toast.LENGTH_LONG).show();
        }
    }

    Bitmap bnw(final double coef) {
        final Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final double separator = 255 / coef / 2 * 3;
        Thread thread = new Thread(){
            public void run(){
                for (int x = 0; x < (Integer) redactBitmap.getWidth()/3; x++) {
                    for (int y = 0; y < redactBitmap.getHeight(); y++) {
                        int prevBitmapPixel = prevBitmap.getPixel(x, y);
                        if (Color.red(prevBitmapPixel) + Color.green(prevBitmapPixel) + Color.blue(prevBitmapPixel) > separator) {
                            redactBitmap.setPixel(x, y, Color.rgb(255, 255, 255));
                        }
                        else {
                            redactBitmap.setPixel(x, y, Color.rgb(0, 0, 0));
                        }
                    }
                }
            }
        };
        Thread thread2 = new Thread(){
            public void run(){
                for (int x = (Integer) redactBitmap.getWidth()/3; x < (Integer) redactBitmap.getWidth()/3*2; x++) {
                    for (int y = 0; y < redactBitmap.getHeight(); y++) {
                        int prevBitmapPixel = prevBitmap.getPixel(x, y);
                        if (Color.red(prevBitmapPixel) + Color.green(prevBitmapPixel) + Color.blue(prevBitmapPixel) > separator) {
                            redactBitmap.setPixel(x, y, Color.rgb(255, 255, 255));
                        }
                        else {
                            redactBitmap.setPixel(x, y, Color.rgb(0, 0, 0));
                        }
                    }
                }
            }
        };
        thread.start();
        thread2.start();

        for (int x = (Integer) redactBitmap.getWidth()/3*2; x < redactBitmap.getWidth(); x++) {
            for (int y = 0; y < redactBitmap.getHeight(); y++) {
                int prevBitmapPixel = prevBitmap.getPixel(x, y);
                if (Color.red(prevBitmapPixel) + Color.green(prevBitmapPixel) + Color.blue(prevBitmapPixel) > separator) {
                    redactBitmap.setPixel(x, y, Color.rgb(255, 255, 255));
                }
                else {
                    redactBitmap.setPixel(x, y, Color.rgb(0, 0, 0));
                }
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        return redactBitmap;
        //MainActivity.mainImage.setImageBitmap(redactBitmap);
    }
}
