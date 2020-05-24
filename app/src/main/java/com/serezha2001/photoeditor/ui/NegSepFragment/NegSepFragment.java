package com.serezha2001.photoeditor.ui.NegSepFragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

public class NegSepFragment extends Fragment {

    private Switch negSwitch, sepiaSwitch;
    private Bitmap prevBitmap;
    private ProgressBar progressBar;
    private Asynced task;

    class Asynced extends AsyncTask<Boolean, Void, Void> {
        Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            negSwitch.setVisibility(View.INVISIBLE);
            sepiaSwitch.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Boolean... isNegative) {
            if (isNegative[0]){
                redactBitmap.setPixels(negative(), 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
            }
            else {
                redactBitmap.setPixels(sepia(), 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            negSwitch.setVisibility(View.VISIBLE);
            sepiaSwitch.setVisibility(View.VISIBLE);
            if (redactBitmap != null) {
                MainActivity.mainImage.setImageBitmap(redactBitmap);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_neg_sep, container, false);

        prevBitmap = ((BitmapDrawable)MainActivity.mainImage.getDrawable()).getBitmap();

        negSwitch = (Switch)root.findViewById(R.id.negSwitch);
        sepiaSwitch = (Switch)root.findViewById(R.id.sepiaSwitch);
        progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        negSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sepiaSwitch.setChecked(false);
                    task = new Asynced();
                    task.execute(true);
                }
                else {
                    MainActivity.mainImage.setImageBitmap(prevBitmap);
                }
            }
        });
        sepiaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    negSwitch.setChecked(false);
                    task = new Asynced();
                    task.execute(false);
                }
                else {
                    MainActivity.mainImage.setImageBitmap(prevBitmap);
                }
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        return root;
    }

    private int[] negative() {
        //final Bitmap negBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final int[] pixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        final int[] newPixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        prevBitmap.getPixels(pixels, 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
        Thread thread = new Thread(){
            public void run(){
                for (int x = 0; x < (Integer)prevBitmap.getWidth()/3; x++){
                    for (int y = 0; y < prevBitmap.getHeight(); y++){
                        int prevBP = prevBitmap.getPixel(x, y);
                        int newPixel= Color.rgb(255 - Color.red(prevBP), 255 - Color.green(prevBP), 255 - Color.blue(prevBP));
                        newPixels[prevBitmap.getWidth() * y + x] = newPixel;
                    }
                }
            }
        };
        Thread thread2 = new Thread(){
            public void run(){
                for (int x = (Integer)prevBitmap.getWidth()/3; x < (Integer)prevBitmap.getWidth()/3*2; x++){
                    for (int y = 0; y < prevBitmap.getHeight(); y++){
                        int prevBP = pixels[prevBitmap.getWidth() * y + x];
                        int newPixel= Color.rgb(255 - Color.red(prevBP), 255 - Color.green(prevBP), 255 - Color.blue(prevBP));
                        newPixels[prevBitmap.getWidth() * y + x] = newPixel;
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer)prevBitmap.getWidth()/3*2; x < prevBitmap.getWidth(); x++){
            for (int y = 0; y < prevBitmap.getHeight(); y++){
                int prevBP = pixels[prevBitmap.getWidth() * y + x];
                int newPixel= Color.rgb(255 - Color.red(prevBP), 255 - Color.green(prevBP), 255 - Color.blue(prevBP));
                newPixels[prevBitmap.getWidth() * y + x] = newPixel;
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        //negBitmap.setPixels(newPixels, 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
        return newPixels;
    }

    private int[] sepia() {
        //final Bitmap sepiaBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final int[] pixels = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        final int newPixels[] = new int[prevBitmap.getWidth() * prevBitmap.getHeight()];
        prevBitmap.getPixels(pixels, 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
        Thread thread = new Thread(){
            public void run(){
                for (int x = 0; x < (Integer)prevBitmap.getWidth()/3; x++){
                    for (int y = 0; y < prevBitmap.getHeight(); y++){
                        int prevBP = pixels[prevBitmap.getWidth() * y + x];
                        //int prevBP = prevBitmap.getPixel(x, y);
                        int red = Math.min((int)(Color.red(prevBP) * 0.393 + Color.green(prevBP) * 0.769 + Color.blue(prevBP) * 0.189), 255);
                        int green = Math.min((int)(Color.red(prevBP) * 0.349 + Color.green(prevBP) * 0.686 + Color.blue(prevBP) * 0.168), 255);
                        int blue = Math.min((int)(Color.red(prevBP) * 0.272 + Color.green(prevBP) * 0.534 + Color.blue(prevBP) * 0.131), 255);
                        //sepiaBitmap.setPixel(x, y, Color.rgb(red, green, blue));
                        newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(red, green, blue);
                    }
                }
            }
        };
        Thread thread2 = new Thread(){
            public void run(){
                for (int x = (Integer)prevBitmap.getWidth()/3; x < (Integer)prevBitmap.getWidth()/3*2; x++){
                    for (int y = 0; y < prevBitmap.getHeight(); y++){
                        int prevBP = pixels[prevBitmap.getWidth() * y + x];
                        //int prevBP = prevBitmap.getPixel(x, y);
                        int red = Math.min((int)(Color.red(prevBP) * 0.393 + Color.green(prevBP) * 0.769 + Color.blue(prevBP) * 0.189), 255);
                        int green = Math.min((int)(Color.red(prevBP) * 0.349 + Color.green(prevBP) * 0.686 + Color.blue(prevBP) * 0.168), 255);
                        int blue = Math.min((int)(Color.red(prevBP) * 0.272 + Color.green(prevBP) * 0.534 + Color.blue(prevBP) * 0.131), 255);
                        //sepiaBitmap.setPixel(x, y, Color.rgb(red, green, blue));
                        newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(red, green, blue);
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer)prevBitmap.getWidth()/3*2; x < prevBitmap.getWidth(); x++){
            for (int y = 0; y < prevBitmap.getHeight(); y++){
                int prevBP = pixels[prevBitmap.getWidth() * y + x];
                //int prevBP = prevBitmap.getPixel(x, y);
                int red = Math.min((int)(Color.red(prevBP) * 0.393 + Color.green(prevBP) * 0.769 + Color.blue(prevBP) * 0.189), 255);
                int green = Math.min((int)(Color.red(prevBP) * 0.349 + Color.green(prevBP) * 0.686 + Color.blue(prevBP) * 0.168), 255);
                int blue = Math.min((int)(Color.red(prevBP) * 0.272 + Color.green(prevBP) * 0.534 + Color.blue(prevBP) * 0.131), 255);
                //sepiaBitmap.setPixel(x, y, Color.rgb(red, green, blue));
                newPixels[prevBitmap.getWidth() * y + x] = Color.rgb(red, green, blue);
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        //sepiaBitmap.setPixels(newPixels, 0, prevBitmap.getWidth(), 0, 0, prevBitmap.getWidth(), prevBitmap.getHeight());
        //MainActivity.mainImage.setImageBitmap(sepiaBitmap);
        return newPixels;
    }
}
