package ru.urfu.colorpicker.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public class BitmapManager
{
    public static Bitmap getHueBitmap(int width, int height)
    {

        Bitmap hueBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        int[] pixels = new int[width * height];

        for (int x = 0; x < width; x++) {
            float hue = 0;
            if (width > height)
                hue = (x * 360f) / width;
            for (int y = 0; y < height; y++) {
                if (width <= height)
                    hue = (y * 360f) / height;
                float[] hsv = new float[]{hue, 1, 1};

                pixels[y * width + x] = Color.HSVToColor(hsv);
            }
        }

        hueBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return hueBitmap;
    }

    public static Bitmap brightness(Bitmap bitmap, float value) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < pixels.length; i++) {
            float[] hsv = new float[3];
            Color.colorToHSV(pixels[i], hsv);
            hsv[2] = value;
            pixels[i] = Color.HSVToColor(hsv);
        }

        bitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return bitmap;
    }
}
