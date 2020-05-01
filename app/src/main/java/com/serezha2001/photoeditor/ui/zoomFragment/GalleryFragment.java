package com.serezha2001.photoeditor.ui.zoomFragment;

import android.os.Bundle;
import android.view.Gravity;
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

public class GalleryFragment extends Fragment {


    public static Button Apply, Undo;
    public static EditText Coef;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_two, container, false);

        Apply = (Button) root.findViewById(R.id.zoomApply);
        Undo = (Button) root.findViewById(R.id.zoomUndo);
        Coef = (EditText) root.findViewById(R.id.zoomCoef);

        Apply.setOnClickListener(new View.OnClickListener(){
            public void onClick (View view){

                try {
                    int scale = Integer.parseInt(Coef.getText().toString());
                    MainActivity.mainImage.setScaleX(scale);
                    MainActivity.mainImage.setScaleY(scale);
                } catch (NumberFormatException e) {
                    Toast toast = Toast.makeText(getContext(), "Enter coefficient", 1000);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
            }
        });
        Undo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.mainImage.setScaleX(1);
                MainActivity.mainImage.setScaleY(1);
            }
        });

        return root;
    }
}
