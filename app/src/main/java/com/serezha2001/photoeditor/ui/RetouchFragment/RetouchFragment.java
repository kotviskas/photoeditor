package com.serezha2001.photoeditor.ui.RetouchFragment;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;


public class RetouchFragment extends Fragment {
    public Bitmap prevBitmap, redactBitmap;
    public TextView brushSizeVal, coefVal;
    public SeekBar brushSizeSeekbar, coefSeekbar;
    public int brushSize = 1;
    public double coef = 0.1;
    public Button undoBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_retouch, container, false);
        prevBitmap = ((BitmapDrawable)MainActivity.mainImage.getDrawable()).getBitmap();
        redactBitmap = prevBitmap;
        redactBitmap = redactBitmap.copy(redactBitmap.getConfig(), true);
        brushSizeVal = (TextView)root.findViewById(R.id.brushSize);
        coefVal = (TextView)root.findViewById(R.id.coef);
        brushSizeSeekbar = (SeekBar)root.findViewById(R.id.brushSizeSeekbar);
        coefSeekbar = (SeekBar)root.findViewById(R.id.coefSeekbar);
        undoBtn = (Button)root.findViewById(R.id.undoBtn);
        brushSizeVal.setText("Brush: 1");
        coefVal.setText("Coef: 0.1");

        brushSizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushSizeVal.setText("Brush: " + String.valueOf(seekBar.getProgress()));
                brushSize = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                brushSizeVal.setText("Brush: " + String.valueOf(seekBar.getProgress()));
                brushSize = seekBar.getProgress();
            }
        });

        coefSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                coefVal.setText("Coef: " + String.valueOf((double)seekBar.getProgress()/10));
                coef = (double)seekBar.getProgress()/10;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                coefVal.setText("Coef: " + String.valueOf((double)seekBar.getProgress()/10));
                coef = (double)seekBar.getProgress()/10;
            }
        });

        undoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.mainImage.setImageBitmap(prevBitmap);
                redactBitmap = prevBitmap;
                redactBitmap = redactBitmap.copy(redactBitmap.getConfig(), true);
            }
        });

        MainActivity.mainImage.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float arr[] = MainActivity.getBitmapPositionInsideImageView();
                float x = event.getX();
                float y = event.getY();
                float scaleFactorX = (arr[2] - arr[0]) / (prevBitmap.getWidth() / arr[6]), scaleFactorY = (arr[3] - arr[1]) / (prevBitmap.getHeight() / arr[6]);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        retouch((int)((x-arr[0])/scaleFactorX), (int)((y-arr[1])/scaleFactorY), arr[6]);
                        Toast.makeText(getContext(), (int)((x-arr[0])/scaleFactorX)+" "+(int)((y-arr[1])/scaleFactorY)+" "+arr[6], Toast.LENGTH_LONG).show();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        retouch((int)((x-arr[0])/scaleFactorX), (int)((y-arr[1])/scaleFactorY), arr[6]);
                        break;
                }
                return true;
            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.mainImage.setOnTouchListener(null);
    }

    private void retouch(int x, int y, float scaleRatio) {
        int averageRed = 0, averageGreen = 0, averageBlue = 0, cnt = 0;
        for (int i = -brushSize; i < brushSize + 1 && ((int)(x*scaleRatio) + i) < redactBitmap.getWidth() && ((int)(x*scaleRatio) + i) >=0; i++){
            for (int j = -brushSize; j < brushSize + 1 && ((int)(y*scaleRatio) + j) < redactBitmap.getHeight() && ((int)(y*scaleRatio) + j) >= 0; j++){
                int prevPixel = redactBitmap.getPixel(((int)(x*scaleRatio) + i), ((int)(y*scaleRatio) + j));
                averageRed += Color.red(prevPixel);
                averageGreen += Color.green(prevPixel);
                averageBlue += Color.blue(prevPixel);
                cnt++;
            }
        }
        for (int i = -brushSize; i < brushSize + 1 && ((int)(x*scaleRatio) + i) < redactBitmap.getWidth() && ((int)(x*scaleRatio) + i) >=0; i++){
            for (int j = -brushSize; j < brushSize + 1 && ((int)(y*scaleRatio) + j) < redactBitmap.getHeight() && ((int)(y*scaleRatio) + j) >= 0; j++){
                int prevPixel = redactBitmap.getPixel(((int)(x*scaleRatio) + i), ((int)(y*scaleRatio) + j));
                redactBitmap.setPixel(((int)(x*scaleRatio) + i), ((int)(y*scaleRatio) + j), Color.rgb((int)(Color.red(prevPixel) + ((averageRed/cnt - Color.red(prevPixel)) * coef)), (int)(Color.green(prevPixel) + ((averageGreen/cnt - Color.green(prevPixel)) * coef)), (int)(Color.blue(prevPixel) + ((averageBlue/cnt - Color.blue(prevPixel)) * coef))));
            }
        }
        MainActivity.mainImage.setImageBitmap(redactBitmap);
    }
}