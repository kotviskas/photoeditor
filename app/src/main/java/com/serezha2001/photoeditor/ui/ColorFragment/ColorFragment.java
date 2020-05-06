package com.serezha2001.photoeditor.ui.ColorFragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;


import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;


public class ColorFragment extends Fragment {

    public Switch redSwitch, greenSwitch, blueSwitch;
    public Bitmap prevBitmap, red, green, blue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_color, container, false);
        prevBitmap = ((BitmapDrawable)MainActivity.mainImage.getDrawable()).getBitmap();
        final Thread thread = new Thread(){
            public void run(){
                Thread threadRed = new Thread(){
                    public void run(){
                        red = red();

                    }
                };
                Thread threadGreen = new Thread(){
                    public void run(){
                        green = green();
                    }
                };
                threadRed.start();
                threadGreen.start();
                blue = blue();
                while (threadGreen.isAlive() || threadRed.isAlive()){

                }
            }
        };
        thread.start();
        redSwitch = (Switch)root.findViewById(R.id.redSwitch);
        greenSwitch = (Switch)root.findViewById(R.id.greenSwitch);
        blueSwitch = (Switch)root.findViewById(R.id.blueSwitch);

        //Toast.makeText(getContext(), "Hey", Toast.LENGTH_LONG).show();


        redSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    greenSwitch.setChecked(false);
                    blueSwitch.setChecked(false);
                    //red();
                    while (thread.isAlive()){

                    }
                    MainActivity.mainImage.setImageBitmap(red);
                }
                else {
                    MainActivity.mainImage.setImageBitmap(prevBitmap);
                }
            }
        });
        greenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    redSwitch.setChecked(false);
                    blueSwitch.setChecked(false);
                    //green();
                    while (thread.isAlive()){

                    }
                    MainActivity.mainImage.setImageBitmap(green);
                }
                else {
                    MainActivity.mainImage.setImageBitmap(prevBitmap);
                }
            }
        });
        blueSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    redSwitch.setChecked(false);
                    greenSwitch.setChecked(false);
                    //blue();
                    while (thread.isAlive()){

                    }
                    MainActivity.mainImage.setImageBitmap(blue);
                }
                else {
                    MainActivity.mainImage.setImageBitmap(prevBitmap);
                }
            }
        });

        return root;
    }

    private Bitmap blue() {
        final Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Thread thread = new Thread(){
            public void run(){
                for (int x = 0; x < (Integer) redactBitmap.getWidth()/3; x++) {
                    for (int y = 0; y < redactBitmap.getHeight(); y++) {
                        redactBitmap.setPixel(x, y, Color.rgb(0, 0 ,Color.blue(prevBitmap.getPixel(x, y))));
                    }
                }
            }
        };
        Thread thread2 = new Thread(){
            public void run(){
                for (int x = (Integer) redactBitmap.getWidth()/3; x < (Integer) redactBitmap.getWidth()/3*2; x++) {
                    for (int y = 0; y < redactBitmap.getHeight(); y++) {
                        redactBitmap.setPixel(x, y, Color.rgb(0, 0 ,Color.blue(prevBitmap.getPixel(x, y))));
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer) redactBitmap.getWidth()/3*2; x < redactBitmap.getWidth(); x++) {
            for (int y = 0; y < redactBitmap.getHeight(); y++) {
                redactBitmap.setPixel(x, y, Color.rgb(0, 0 ,Color.blue(prevBitmap.getPixel(x, y))));
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        //MainActivity.mainImage.setImageBitmap(redactBitmap);
        return redactBitmap;
    }

    private Bitmap green() {
        final Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Thread thread = new Thread(){
            public void run(){
                for (int x = 0; x < (Integer) redactBitmap.getWidth()/3; x++) {
                    for (int y = 0; y < redactBitmap.getHeight(); y++) {
                        redactBitmap.setPixel(x, y, Color.rgb(0, Color.green(prevBitmap.getPixel(x, y)) ,0));
                    }
                }
            }
        };
        Thread thread2 = new Thread(){
            public void run(){
                for (int x = (Integer) redactBitmap.getWidth()/3; x < (Integer) redactBitmap.getWidth()/3*2; x++) {
                    for (int y = 0; y < redactBitmap.getHeight(); y++) {
                        redactBitmap.setPixel(x, y, Color.rgb(0, Color.green(prevBitmap.getPixel(x, y)) ,0));
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer) redactBitmap.getWidth()/3*2; x < redactBitmap.getWidth(); x++) {
            for (int y = 0; y < redactBitmap.getHeight(); y++) {
                redactBitmap.setPixel(x, y, Color.rgb(0, Color.green(prevBitmap.getPixel(x, y)) ,0));
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        //MainActivity.mainImage.setImageBitmap(redactBitmap);
        return redactBitmap;
    }

    private Bitmap red() {
        final Bitmap redactBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Thread thread = new Thread(){
            public void run(){
                for (int x = 0; x < (Integer) redactBitmap.getWidth()/3; x++) {
                    for (int y = 0; y < redactBitmap.getHeight(); y++) {
                        redactBitmap.setPixel(x, y, Color.rgb(Color.red(prevBitmap.getPixel(x, y)), 0 ,0));
                    }
                }
            }
        };
        Thread thread2 = new Thread(){
            public void run(){
                for (int x = (Integer) redactBitmap.getWidth()/3; x < (Integer) redactBitmap.getWidth()/3*2; x++) {
                    for (int y = 0; y < redactBitmap.getHeight(); y++) {
                        redactBitmap.setPixel(x, y, Color.rgb(Color.red(prevBitmap.getPixel(x, y)), 0 ,0));
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer) redactBitmap.getWidth()/3*2; x < redactBitmap.getWidth(); x++) {
            for (int y = 0; y < redactBitmap.getHeight(); y++) {
                redactBitmap.setPixel(x, y, Color.rgb(Color.red(prevBitmap.getPixel(x, y)), 0 ,0));
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        //MainActivity.mainImage.setImageBitmap(redactBitmap);
        return redactBitmap;
    }
}
