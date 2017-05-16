package ru.urfu.taskmanager.task_manager.editor.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import ru.urfu.taskmanager.R;

public final class ImageLoader
{
    public static Loader into(ImageView view) {
        return new Loader(view);
    }

    public static class Loader extends HandlerThread
    {
        Handler mWorkerHandler;

        URL mImageUrl;
        Bitmap mBitmap;
        ImageView mView;

        private Loader(ImageView view){
            super(view.toString());
            this.mView = view;
            this.start();
        }

        @Override
        protected void onLooperPrepared() {
            if (mWorkerHandler == null)
                this.mWorkerHandler = new Handler(getLooper());
        }

        public void from(String url) {
            onLooperPrepared();

            mWorkerHandler.post(() -> {
                try {
                    mImageUrl = new URL(url);
                    mBitmap = BitmapFactory.decodeStream(mImageUrl.openConnection().getInputStream());
                    mView.post(() -> mView.setImageBitmap(mBitmap));
                } catch (IOException e) {
                    mView.post(() -> Toast.makeText(mView.getContext(),
                            mView.getResources().getString(R.string.could_not_load_image), Toast.LENGTH_SHORT).show());
                } finally {
                    quit();
                }
            });
        }
    }
}
