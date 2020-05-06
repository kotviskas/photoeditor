package com.serezha2001.photoeditor.ui.NegSepFragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

public class NegSepFragment extends Fragment {
    
    public Switch negSwitch, sepiaSwitch;
    public Bitmap prevBitmap, negBitmap, sepiaBitmap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_neg_sep, container, false);

        prevBitmap = ((BitmapDrawable)MainActivity.mainImage.getDrawable()).getBitmap();
        final Thread thread = new Thread(){
            public void run(){
                Thread threadNeg = new Thread(){
                    public void run(){
                        negBitmap = negative();
                    }
                };
                threadNeg.start();
                sepiaBitmap = sepia();
                while (threadNeg.isAlive()){

                }
            }
        };
        thread.start();

        negSwitch = (Switch)root.findViewById(R.id.negSwitch);
        sepiaSwitch = (Switch)root.findViewById(R.id.sepiaSwitch);

        negSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sepiaSwitch.setChecked(false);
                    while (thread.isAlive()){

                    }
                    MainActivity.mainImage.setImageBitmap(negBitmap);
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
                    while (thread.isAlive()){

                    }
                    MainActivity.mainImage.setImageBitmap(sepiaBitmap);
                }
                else {
                    MainActivity.mainImage.setImageBitmap(prevBitmap);
                }
            }
        });

        return root;
    }

    private Bitmap negative() {
        final Bitmap negBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Thread thread = new Thread(){
            public void run(){
                for (int x = 0; x < (Integer)negBitmap.getWidth()/3; x++){
                    for (int y = 0; y < negBitmap.getHeight(); y++){
                        int prevBitmapPixel = prevBitmap.getPixel(x, y);
                        int newPixel= Color.rgb(255 - Color.red(prevBitmapPixel), 255 - Color.green(prevBitmapPixel), 255 - Color.blue(prevBitmapPixel));
                        negBitmap.setPixel(x, y, newPixel);
                    }
                }
            }
        };
        Thread thread2 = new Thread(){
            public void run(){
                for (int x = (Integer)negBitmap.getWidth()/3; x < (Integer)negBitmap.getWidth()/3*2; x++){
                    for (int y = 0; y < negBitmap.getHeight(); y++){
                        int prevBitmapPixel = prevBitmap.getPixel(x, y);
                        int newPixel= Color.rgb(255 - Color.red(prevBitmapPixel), 255 - Color.green(prevBitmapPixel), 255 - Color.blue(prevBitmapPixel));
                        negBitmap.setPixel(x, y, newPixel);
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer)negBitmap.getWidth()/3*2; x < negBitmap.getWidth(); x++){
            for (int y = 0; y < negBitmap.getHeight(); y++){
                int prevBitmapPixel = prevBitmap.getPixel(x, y);
                int newPixel= Color.rgb(255 - Color.red(prevBitmapPixel), 255 - Color.green(prevBitmapPixel), 255 - Color.blue(prevBitmapPixel));
                negBitmap.setPixel(x, y, newPixel);
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        //MainActivity.mainImage.setImageBitmap(negBitmap);
        return negBitmap;
    }

    private Bitmap sepia() {
        final Bitmap negBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Thread thread = new Thread(){
            public void run(){
                for (int x = 0; x < (Integer)negBitmap.getWidth()/3; x++){
                    for (int y = 0; y < negBitmap.getHeight(); y++){
                        int prevBP = prevBitmap.getPixel(x, y);
                        int red = Math.min((int)(Color.red(prevBP) * 0.393 + Color.green(prevBP) * 0.769 + Color.blue(prevBP) * 0.189), 255);
                        int green = Math.min((int)(Color.red(prevBP) * 0.349 + Color.green(prevBP) * 0.686 + Color.blue(prevBP) * 0.168), 255);
                        int blue = Math.min((int)(Color.red(prevBP) * 0.272 + Color.green(prevBP) * 0.534 + Color.blue(prevBP) * 0.131), 255);
                        negBitmap.setPixel(x, y, Color.rgb(red, green, blue));
                    }
                }
            }
        };
        Thread thread2 = new Thread(){
            public void run(){
                for (int x = (Integer)negBitmap.getWidth()/3; x < (Integer)negBitmap.getWidth()/3*2; x++){
                    for (int y = 0; y < negBitmap.getHeight(); y++){
                        int prevBP = prevBitmap.getPixel(x, y);
                        int red = Math.min((int)(Color.red(prevBP) * 0.393 + Color.green(prevBP) * 0.769 + Color.blue(prevBP) * 0.189), 255);
                        int green = Math.min((int)(Color.red(prevBP) * 0.349 + Color.green(prevBP) * 0.686 + Color.blue(prevBP) * 0.168), 255);
                        int blue = Math.min((int)(Color.red(prevBP) * 0.272 + Color.green(prevBP) * 0.534 + Color.blue(prevBP) * 0.131), 255);
                        negBitmap.setPixel(x, y, Color.rgb(red, green, blue));
                    }
                }
            }
        };
        thread.start();
        thread2.start();
        for (int x = (Integer)negBitmap.getWidth()/3*2; x < negBitmap.getWidth(); x++){
            for (int y = 0; y < negBitmap.getHeight(); y++){
                int prevBP = prevBitmap.getPixel(x, y);
                int red = Math.min((int)(Color.red(prevBP) * 0.393 + Color.green(prevBP) * 0.769 + Color.blue(prevBP) * 0.189), 255);
                int green = Math.min((int)(Color.red(prevBP) * 0.349 + Color.green(prevBP) * 0.686 + Color.blue(prevBP) * 0.168), 255);
                int blue = Math.min((int)(Color.red(prevBP) * 0.272 + Color.green(prevBP) * 0.534 + Color.blue(prevBP) * 0.131), 255);
                negBitmap.setPixel(x, y, Color.rgb(red, green, blue));
            }
        }
        while (thread.isAlive() || thread2.isAlive()) {
        }
        //MainActivity.mainImage.setImageBitmap(negBitmap);
        return negBitmap;
    }
}
