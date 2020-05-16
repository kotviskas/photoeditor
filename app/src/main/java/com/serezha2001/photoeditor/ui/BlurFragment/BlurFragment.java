package com.serezha2001.photoeditor.ui.BlurFragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;


public class BlurFragment extends Fragment {

    public SeekBar radius;
    public TextView radiusView;
    public Bitmap prevBitmap = null;
    public ProgressBar progressBar;
    LinearLayout btnsLayout;
    Button applyBtn, cancelBtn;
    Asynced task;

    class Asynced extends AsyncTask<Integer, Void, Void> {
        Bitmap redactBitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            radius.setVisibility(View.INVISIBLE);
            radiusView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... angle) {
            // redactBitmap = rotateImage(angle[0]);
            redactBitmap = boxBlur(Bitmap.createBitmap(prevBitmap), (int)(radius.getProgress()) );
            redactBitmap = boxBlur(redactBitmap, (int)(radius.getProgress()));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.INVISIBLE);
            if (redactBitmap != null) {
                MainActivity.mainImage.setImageBitmap(redactBitmap);
            }
            btnsLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_blur, container, false);
        progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        radius = (SeekBar)root.findViewById(R.id.radiusSeekbar);
        radiusView = (TextView)root.findViewById(R.id.radiusView);
        radius.setMax(50);
        radius.setProgress(0);
        radiusView.setText("radius : 0");
        btnsLayout = (LinearLayout)root.findViewById(R.id.processBtnsLayout);
        btnsLayout.setVisibility(View.INVISIBLE);
        applyBtn = (Button)root.findViewById(R.id.applyBtn);
        cancelBtn = (Button)root.findViewById(R.id.cancelBtn);

        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusView.setText("radius : " + String.valueOf(radius.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (prevBitmap == null) {
                    prevBitmap = ((BitmapDrawable)MainActivity.mainImage.getDrawable()).getBitmap();
                }
                int currentRadius = (int)(radius.getProgress());
                radiusView.setText("radius : " + String.valueOf(currentRadius));
                Asynced task = new Asynced();
                task.execute(currentRadius);
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                btnsLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                radius.setVisibility(View.VISIBLE);
                radiusView.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                MainActivity.mainImage.setImageBitmap(prevBitmap);
                radius.setProgress(0);
                radiusView.setText("radius : 0");
                btnsLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                radius.setVisibility(View.VISIBLE);
                radiusView.setVisibility(View.VISIBLE);
            }
        });

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

    public static Bitmap boxBlur(Bitmap bmp, int range) {

        Bitmap blurred = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(blurred);

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        boxBlurHorizontal(pixels, w, h, range / 2);
        boxBlurVertical(pixels, w, h, range / 2);

        c.drawBitmap(pixels, 0, w, 0.0F, 0.0F, w, h, true, null);

        return blurred;
    }

    private static void boxBlurHorizontal(int[] pixels, int w, int h, int halfRange) {
        int index = 0;
        int[] newColors = new int[w];

        for (int y = 0; y < h; y++) {
            int hits = 0;
            long r = 0;
            long g = 0;
            long b = 0;
            for (int x = -halfRange; x < w; x++) {
                int oldPixel = x - halfRange - 1;
                if (oldPixel >= 0) {
                    int color = pixels[index + oldPixel];
                    if (color != 0) {
                        r -= Color.red(color);
                        g -= Color.green(color);
                        b -= Color.blue(color);
                    }
                    hits--;
                }

                int newPixel = x + halfRange;
                if (newPixel < w) {
                    int color = pixels[index + newPixel];
                    if (color != 0) {
                        r += Color.red(color);
                        g += Color.green(color);
                        b += Color.blue(color);
                    }
                    hits++;
                }

                if (x >= 0) {
                    newColors[x] = Color.argb(0xFF, (int) (r / hits), (int) (g / hits), (int) (b / hits));
                }
            }

            for (int x = 0; x < w; x++) {
                pixels[index + x] = newColors[x];
            }

            index += w;
        }
    }

    private static void boxBlurVertical(int[] pixels, int w, int h, int halfRange) {

        int[] newColors = new int[h];
        int oldPixelOffset = -(halfRange + 1) * w;
        int newPixelOffset = (halfRange) * w;

        for (int x = 0; x < w; x++) {
            int hits = 0;
            long r = 0;
            long g = 0;
            long b = 0;
            int index = -halfRange * w + x;
            for (int y = -halfRange; y < h; y++) {
                int oldPixel = y - halfRange - 1;
                if (oldPixel >= 0) {
                    int color = pixels[index + oldPixelOffset];
                    if (color != 0) {
                        r -= Color.red(color);
                        g -= Color.green(color);
                        b -= Color.blue(color);
                    }
                    hits--;
                }

                int newPixel = y + halfRange;
                if (newPixel < h) {
                    int color = pixels[index + newPixelOffset];
                    if (color != 0) {
                        r += Color.red(color);
                        g += Color.green(color);
                        b += Color.blue(color);
                    }
                    hits++;
                }

                if (y >= 0) {
                    newColors[y] = Color.argb(0xFF, (int) (r / hits), (int) (g / hits), (int) (b / hits));
                }

                index += w;
            }

            for (int y = 0; y < h; y++) {
                pixels[y * w + x] = newColors[y];
            }
        }
    }
}