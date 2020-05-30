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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

public class BNWFragment extends Fragment {

    private TextView coefView;
    private SeekBar seekBar;
    private double CoefBnw;
    private Bitmap prevBitmap;
    private ProgressBar progressBar;
    private LinearLayout btnsLayout;
    private Asynced task;

    class Asynced extends AsyncTask<Void, Void, Void> {
        Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            seekBar.setVisibility(View.INVISIBLE);
            coefView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            redactBitmap.setPixels(bnw(CoefBnw), 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
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
        MainActivity.mainImage.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        coefView = (TextView) root.findViewById(R.id.coefView);
        seekBar = (SeekBar) root.findViewById(R.id.seekBar);
        prevBitmap = null;

        seekBar.setMax(300);
        seekBar.setProgress(50);
        coefView.setText("None");

        btnsLayout = (LinearLayout) root.findViewById(R.id.processBtnsLayout);
        btnsLayout.setVisibility(View.INVISIBLE);
        Button applyBtn = (Button) root.findViewById(R.id.applyBtn);
        Button cancelBtn = (Button) root.findViewById(R.id.cancelBtn);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    prevBitmap = ((BitmapDrawable) MainActivity.mainImage.getDrawable()).getBitmap();
                }
                CoefBnw = (double) (seekBar.getProgress() + 50) / 100;
                coefView.setText(String.valueOf((int) (CoefBnw * 100)) + "%");
                Asynced task = new Asynced();
                task.execute();
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnsLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                seekBar.setVisibility(View.VISIBLE);
                coefView.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
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

    private int[] bnw(final double coef) {
        final double separator = 255 / coef / 2 * 3;
        final int[] pixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        final int[] newPixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        prevBitmap.getPixels(pixels, 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
        Thread thread = new Thread() {
            public void run() {
                for (int x = 0; x < (Integer) prevBitmap.getWidth() / 3; x++) {
                    for (int y = 0; y < prevBitmap.getHeight(); y++) {
                        //int prevBitmapPixel = prevBitmap.getPixel(x, y);
                        int prevBitmapPixel = pixels[prevBitmap.getWidth() * y + x];
                        if (Color.red(prevBitmapPixel) + Color.green(prevBitmapPixel) + Color.blue(prevBitmapPixel) > separator) {
                            newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(255, 255, 255);
                        } else {
                            newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(0, 0, 0);
                        }
                    }
                }
            }
        };
        Thread thread2 = new Thread() {
            public void run() {
                for (int x = (Integer) prevBitmap.getWidth() / 3; x < (Integer) prevBitmap.getWidth() / 3 * 2; x++) {
                    for (int y = 0; y < prevBitmap.getHeight(); y++) {
                        int prevBitmapPixel = pixels[prevBitmap.getWidth() * y + x];
                        if (Color.red(prevBitmapPixel) + Color.green(prevBitmapPixel) + Color.blue(prevBitmapPixel) > separator) {
                            newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(255, 255, 255);
                        } else {
                            newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(0, 0, 0);
                        }
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer) prevBitmap.getWidth() / 3 * 2; x < prevBitmap.getWidth(); x++) {
            for (int y = 0; y < prevBitmap.getHeight(); y++) {
                int prevBitmapPixel = pixels[prevBitmap.getWidth() * y + x];
                if (Color.red(prevBitmapPixel) + Color.green(prevBitmapPixel) + Color.blue(prevBitmapPixel) > separator) {
                    newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(255, 255, 255);
                } else {
                    newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(0, 0, 0);
                }
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        return newPixels;
    }
}
