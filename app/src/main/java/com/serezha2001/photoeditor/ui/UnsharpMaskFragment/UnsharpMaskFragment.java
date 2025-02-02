package com.serezha2001.photoeditor.ui.UnsharpMaskFragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.serezha2001.photoeditor.MainActivity;
import com.serezha2001.photoeditor.R;

public class UnsharpMaskFragment extends Fragment {

    private float amount = 0;
    private int radius = 1, threshold = 1;

    private SeekBar radiusInput;
    private TextView radiusView;
    private SeekBar amountInput;
    private TextView amountView;
    private SeekBar thresholdInput;
    private TextView thresholdView;

    private Button applyButton;

    private Bitmap srcBitmap;
    private ProgressBar progressBar;
    private LinearLayout btnsLayout;

    private Asynced task;

    class Asynced extends AsyncTask<Float, Void, Void> {
        Bitmap redactBitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            amountInput.setVisibility(View.INVISIBLE);
            amountView.setVisibility(View.INVISIBLE);
            radiusInput.setVisibility(View.INVISIBLE);
            radiusView.setVisibility(View.INVISIBLE);
            thresholdInput.setVisibility(View.INVISIBLE);
            thresholdView.setVisibility(View.INVISIBLE);
            applyButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Float... coef) {
            redactBitmap = usm(coef[0], coef[1], Math.round(coef[2]));
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

        View root = inflater.inflate(R.layout.fragment_unsharp_mask, container, false);
        MainActivity.mainImage.setVisibility(View.VISIBLE);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        srcBitmap = ((BitmapDrawable) MainActivity.mainImage.getDrawable()).getBitmap();

        amountInput = (SeekBar) root.findViewById(R.id.amountInput);
        amountView = (TextView) root.findViewById(R.id.amountView);
        radiusInput = (SeekBar) root.findViewById(R.id.radiusInput);
        radiusView = (TextView) root.findViewById(R.id.radiusView);
        thresholdInput = (SeekBar) root.findViewById(R.id.thresholdInput);
        thresholdView = (TextView) root.findViewById(R.id.thresholdView);
        radiusInput.setMax(50);
        radiusInput.setProgress(0);
        amountInput.setMax(50);
        amountInput.setProgress(0);
        thresholdInput.setMax(50);
        thresholdInput.setProgress(0);
        radiusView.setText("Radius: 0");
        amountView.setText("Amount: 0");
        thresholdView.setText("Threshold: 0");
        applyButton = (Button) root.findViewById(R.id.applyButton);

        btnsLayout = (LinearLayout) root.findViewById(R.id.processBtnsLayout);
        btnsLayout.setVisibility(View.INVISIBLE);
        Button applyBtn = (Button) root.findViewById(R.id.applyBtn);
        Button cancelBtn = (Button) root.findViewById(R.id.cancelBtn);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                threshold = thresholdInput.getProgress();
                radius = radiusInput.getProgress();
                amount = amountInput.getProgress();

                Asynced task = new Asynced();
                task.execute(amount, (float) threshold, (float) radius);
            }
        });

        radiusInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusView.setText("Radius: " + String.valueOf(radiusInput.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        amountInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amountView.setText("Amount: " + String.valueOf(amountInput.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        thresholdInput.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                thresholdView.setText("Threshold: " + String.valueOf(thresholdInput.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnsLayout.setVisibility(View.INVISIBLE);
                amountInput.setVisibility(View.VISIBLE);
                amountView.setVisibility(View.VISIBLE);
                radiusInput.setVisibility(View.VISIBLE);
                radiusView.setVisibility(View.VISIBLE);
                thresholdInput.setVisibility(View.VISIBLE);
                thresholdView.setVisibility(View.VISIBLE);
                applyButton.setVisibility(View.VISIBLE);

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mainImage.setImageBitmap(srcBitmap);
                btnsLayout.setVisibility(View.INVISIBLE);
                amountInput.setVisibility(View.VISIBLE);
                amountView.setVisibility(View.VISIBLE);
                radiusInput.setVisibility(View.VISIBLE);
                radiusView.setVisibility(View.VISIBLE);
                thresholdInput.setVisibility(View.VISIBLE);
                thresholdView.setVisibility(View.VISIBLE);
                applyButton.setVisibility(View.VISIBLE);
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        return root;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            task.cancel(true);
        } catch (Exception e) {
        }
    }

    private Bitmap boxBlur(Bitmap bmp, int range) {

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

    private void boxBlurHorizontal(int[] pixels, int w, int h, int halfRange) {
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

    private Bitmap usm(final float amount, final float threshold, final int radius) {

        Bitmap usmBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Bitmap blurBitmap = Bitmap.createBitmap(srcBitmap);

        blurBitmap = boxBlur(blurBitmap, radius * 2);

        int srcRed, srcGreen, srcBlue, blurRed, blurGreen, blurBlue, usmPixel;

        for (int x = 0; x < srcBitmap.getWidth(); x++) {
            for (int y = 0; y < srcBitmap.getHeight(); y++) {

                int srcPixel = srcBitmap.getPixel(x, y);
                int blurPixel = blurBitmap.getPixel(x, y);

                srcRed = Color.red(srcPixel);
                srcGreen = Color.green(srcPixel);
                srcBlue = Color.blue(srcPixel);

                blurRed = Color.red(blurPixel);
                blurGreen = Color.green(blurPixel);
                blurBlue = Color.blue(blurPixel);

                if (Math.abs(srcRed - blurRed) >= threshold) {
                    srcRed = (int) (amount * (srcRed - blurRed) + srcRed);
                    if (srcRed > 255) {
                        srcRed = 255;
                    }
                    if (srcRed < 0) {
                        srcRed = 0;
                    }
                }

                if (Math.abs(srcGreen - blurGreen) >= threshold) {
                    srcGreen = (int) (amount * (srcGreen - blurGreen) + srcGreen);
                    if (srcGreen > 255) {
                        srcGreen = 255;
                    }
                    if (srcGreen < 0) {
                        srcGreen = 0;
                    }
                }

                if (Math.abs(srcBlue - blurBlue) >= threshold) {
                    srcBlue = (int) (amount * (srcBlue - blurBlue) + srcBlue);
                    if (srcBlue > 255) {
                        srcBlue = 255;
                    }
                    if (srcBlue < 0) {
                        srcBlue = 0;
                    }
                }

                usmPixel = Color.rgb(srcRed, srcGreen, srcBlue);
                usmBitmap.setPixel(x, y, usmPixel);
            }
        }

        return usmBitmap;
    }
}
