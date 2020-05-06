package com.serezha2001.photoeditor.ui.NegativeFragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

public class NegativeFragment extends Fragment {

    public Button Apply, Undo;
    Bitmap prevBitmap, negBitmap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_negative, container, false);

        Apply = (Button)root.findViewById(R.id.negativeApply);
        Undo = (Button)root.findViewById(R.id.negativeUndo);
        Undo.setEnabled(false);

        Apply.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                prevBitmap = ((BitmapDrawable) MainActivity.mainImage.getDrawable()).getBitmap();
                Undo.setEnabled(true);
                negative();
            }
        });
        Undo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                MainActivity.mainImage.setImageBitmap(prevBitmap);
                Undo.setEnabled(false);
            }
        });

        return root;
    }

    private void negative() {
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
        MainActivity.mainImage.setImageBitmap(negBitmap);
    }
}
