package com.serezha2001.photoeditor.ui.CropFragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

public class CropFragment extends Fragment {


    //public Button Apply, Undo;
    public SeekBar Coef;
    public TextView coefView;
    public Bitmap prevBitmap;
    public ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_crop, container, false);

            progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            Coef = (SeekBar) root.findViewById(R.id.zoomCoef);
            Coef.setMax(19);
            Coef.setProgress(0);
            coefView = (TextView)root.findViewById(R.id.coefView);
            coefView.setText("1x");

        class Asynced extends AsyncTask<Integer, Void, Void> {
            Bitmap redactBitmap;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Coef.setVisibility(View.INVISIBLE);
                coefView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Integer... angle) {
                redactBitmap = imageCrop(angle[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                progressBar.setVisibility(View.INVISIBLE);
                Coef.setVisibility(View.VISIBLE);
                coefView.setVisibility(View.VISIBLE);
                if (redactBitmap != null) {
                    MainActivity.mainImage.setImageBitmap(redactBitmap);
                }
            }
        }

            Coef.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    coefView.setText(String.valueOf(Coef.getProgress() + 1)+"x");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (prevBitmap == null) {
                        prevBitmap = ((BitmapDrawable)MainActivity.mainImage.getDrawable()).getBitmap();
                        // Undo.setEnabled(true);
                    }
                    int scale = (int)(Coef.getProgress() + 1);
                    coefView.setText(String.valueOf(scale)+"x");
                    Asynced task = new Asynced();
                    task.execute(scale);
                    //imageCrop(scale);
                }
            });

        return root;
    }

    private Bitmap imageCrop(int scale) {
        try {
            Bitmap croppedBitmap = Bitmap.createBitmap(prevBitmap.getWidth(), prevBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            for (int x = 0, prevx = 0; x < croppedBitmap.getWidth(); x += scale, prevx++){
                for (int y = 0, prevy = 0; y < croppedBitmap.getHeight(); y += scale, prevy++){
                    int prevBitmapPixel = prevBitmap.getPixel(prevBitmap.getWidth()/2 - prevBitmap.getWidth()/scale/2 + prevx,prevBitmap.getHeight()/2 - prevBitmap.getHeight()/scale/2 + prevy);
                    int newPixel= Color.argb(Color.alpha(prevBitmapPixel), Color.red(prevBitmapPixel), Color.green(prevBitmapPixel), Color.blue(prevBitmapPixel));
                    for (int i = 0; i < scale && x + i< croppedBitmap.getWidth(); i++){
                        for (int j = 0; j < scale && y + j < croppedBitmap.getHeight(); j++){
                            croppedBitmap.setPixel(x+i, y+j, newPixel);
                        }
                    }
                }
            }
            return croppedBitmap;
            //MainActivity.mainImage.setImageBitmap(croppedBitmap);
            //Undo.setEnabled(true);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error! " + e, Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
