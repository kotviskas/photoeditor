package com.serezha2001.photoeditor.ui.ColorFragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;


import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;


public class ColorFragment extends Fragment {

    private Switch redSwitch, greenSwitch, blueSwitch;
    private Bitmap prevBitmap, red, green, blue;
    private ProgressBar progressBar;
    private Asynced task;

    class Asynced extends AsyncTask<Integer, Void, Void> {
        Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            redSwitch.setVisibility(View.INVISIBLE);
            greenSwitch.setVisibility(View.INVISIBLE);
            blueSwitch.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... color) {
            if (color[0] == 0) {
                redactBitmap.setPixels(red(), 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
            } else if (color[0] == 1) {
                redactBitmap.setPixels(green(), 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
            } else if (color[0] == 2) {
                redactBitmap.setPixels(blue(), 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            redSwitch.setVisibility(View.VISIBLE);
            greenSwitch.setVisibility(View.VISIBLE);
            blueSwitch.setVisibility(View.VISIBLE);
            if (redactBitmap != null) {
                MainActivity.mainImage.setImageBitmap(redactBitmap);

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_color, container, false);
        prevBitmap = ((BitmapDrawable) MainActivity.mainImage.getDrawable()).getBitmap();
        MainActivity.mainImage.setVisibility(View.VISIBLE);
        redSwitch = (Switch) root.findViewById(R.id.redSwitch);
        greenSwitch = (Switch) root.findViewById(R.id.greenSwitch);
        blueSwitch = (Switch) root.findViewById(R.id.blueSwitch);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        redSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    greenSwitch.setChecked(false);
                    blueSwitch.setChecked(false);
                    task = new Asynced();
                    task.execute(0);
                } else {
                    MainActivity.mainImage.setImageBitmap(prevBitmap);
                }
            }
        });
        greenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    redSwitch.setChecked(false);
                    blueSwitch.setChecked(false);
                    task = new Asynced();
                    task.execute(1);
                } else {
                    MainActivity.mainImage.setImageBitmap(prevBitmap);
                }
            }
        });
        blueSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    redSwitch.setChecked(false);
                    greenSwitch.setChecked(false);
                    task = new Asynced();
                    task.execute(2);
                } else {
                    MainActivity.mainImage.setImageBitmap(prevBitmap);
                }
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        return root;
    }

    private int[] blue() {
        //final Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final int[] pixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        final int[] newPixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        prevBitmap.getPixels(pixels, 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
        Thread thread = new Thread() {
            public void run() {
                for (int x = 0; x < (Integer) prevBitmap.getWidth() / 3; x++) {
                    for (int y = 0; y < prevBitmap.getHeight(); y++) {
                        newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(0, 0, Color.blue(pixels[prevBitmap.getWidth() * y + x]));
                    }
                }
            }
        };
        Thread thread2 = new Thread() {
            public void run() {
                for (int x = (Integer) prevBitmap.getWidth() / 3; x < (Integer) prevBitmap.getWidth() / 3 * 2; x++) {
                    for (int y = 0; y < prevBitmap.getHeight(); y++) {
                        newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(0, 0, Color.blue(pixels[prevBitmap.getWidth() * y + x]));
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer) prevBitmap.getWidth() / 3 * 2; x < prevBitmap.getWidth(); x++) {
            for (int y = 0; y < prevBitmap.getHeight(); y++) {
                newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(0, 0, Color.blue(pixels[prevBitmap.getWidth() * y + x]));
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        return newPixels;
    }

    private int[] green() {
        //final Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final int[] pixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        final int[] newPixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        prevBitmap.getPixels(pixels, 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
        Thread thread = new Thread() {
            public void run() {
                for (int x = 0; x < (Integer) prevBitmap.getWidth() / 3; x++) {
                    for (int y = 0; y < prevBitmap.getHeight(); y++) {
                        newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(0, Color.green(pixels[prevBitmap.getWidth() * y + x]), 0);
                        //redactBitmap.setPixel(x, y, Color.rgb(0, Color.green(prevBitmap.getPixel(x, y)) ,0));
                    }
                }
            }
        };
        Thread thread2 = new Thread() {
            public void run() {
                for (int x = (Integer) prevBitmap.getWidth() / 3; x < (Integer) prevBitmap.getWidth() / 3 * 2; x++) {
                    for (int y = 0; y < prevBitmap.getHeight(); y++) {
                        newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(0, Color.green(pixels[prevBitmap.getWidth() * y + x]), 0);
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer) prevBitmap.getWidth() / 3 * 2; x < prevBitmap.getWidth(); x++) {
            for (int y = 0; y < prevBitmap.getHeight(); y++) {
                newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(0, Color.green(pixels[prevBitmap.getWidth() * y + x]), 0);
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        return newPixels;
    }

    private int[] red() {
        //final Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final int[] pixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        final int[] newPixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        prevBitmap.getPixels(pixels, 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
        Thread thread = new Thread() {
            public void run() {
                for (int x = 0; x < (Integer) prevBitmap.getWidth() / 3; x++) {
                    for (int y = 0; y < prevBitmap.getHeight(); y++) {
                        newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(Color.red(pixels[prevBitmap.getWidth() * y + x]), 0, 0);
                        //redactBitmap.setPixel(x, y, Color.rgb(Color.red(prevBitmap.getPixel(x, y)), 0 ,0));
                    }
                }
            }
        };
        Thread thread2 = new Thread() {
            public void run() {
                for (int x = (Integer) prevBitmap.getWidth() / 3; x < (Integer) prevBitmap.getWidth() / 3 * 2; x++) {
                    for (int y = 0; y < prevBitmap.getHeight(); y++) {
                        newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(Color.red(pixels[prevBitmap.getWidth() * y + x]), 0, 0);
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer) prevBitmap.getWidth() / 3 * 2; x < prevBitmap.getWidth(); x++) {
            for (int y = 0; y < prevBitmap.getHeight(); y++) {
                newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(Color.red(pixels[prevBitmap.getWidth() * y + x]), 0, 0);
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        return newPixels;
    }
}
