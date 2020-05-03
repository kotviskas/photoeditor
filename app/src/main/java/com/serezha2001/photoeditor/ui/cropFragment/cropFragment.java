package com.serezha2001.photoeditor.ui.cropFragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

public class cropFragment extends Fragment {


    public static Button Apply, Undo;
    public static EditText Coef;
    public static Bitmap prevBitmap;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_two, container, false);

        Apply = (Button) root.findViewById(R.id.zoomApply);
        Undo = (Button) root.findViewById(R.id.zoomUndo);
        Undo.setEnabled(false);
        Coef = (EditText) root.findViewById(R.id.zoomCoef);


        Apply.setOnClickListener(new View.OnClickListener(){
            public void onClick (View view){
                try {
                    int scale = Integer.parseInt(Coef.getText().toString());
                    prevBitmap = ((BitmapDrawable)MainActivity.mainImage.getDrawable()).getBitmap();
                    imageCrop(scale);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Enter coefficient", 1000).show();
                }
            }
        });
        Undo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.mainImage.setImageBitmap(prevBitmap);
                Undo.setEnabled(false);
            }
        });
        return root;
    }

    private void imageCrop(int scale) {
        try {
            Bitmap croppedBitmap = Bitmap.createBitmap((Integer)prevBitmap.getWidth(), (Integer)prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            for (int x = 0, prevx = 0; x < (Integer)croppedBitmap.getWidth(); x += scale, prevx++){
                for (int y = 0, prevy = 0; y < (Integer)croppedBitmap.getHeight(); y += scale, prevy++){
                    int prevBitmapPixel = prevBitmap.getPixel((Integer)prevBitmap.getWidth()/2 - (Integer)prevBitmap.getWidth()/scale/2 + prevx,(Integer)prevBitmap.getHeight()/2 - (Integer)prevBitmap.getHeight()/scale/2 + prevy);
                    int newPixel= Color.argb(Color.alpha(prevBitmapPixel), Color.red(prevBitmapPixel), Color.green(prevBitmapPixel), Color.blue(prevBitmapPixel));
                    for (int i = 0; i < scale && x + i< croppedBitmap.getWidth(); i++){
                        for (int j = 0; j < scale && y + j < croppedBitmap.getHeight(); j++){
                            croppedBitmap.setPixel(x+i, y+j, newPixel);
                        }
                    }
                }
            }
            MainActivity.mainImage.setImageBitmap(croppedBitmap);
            Undo.setEnabled(true);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error! " + e, Toast.LENGTH_SHORT).show();
        }
    }
}
