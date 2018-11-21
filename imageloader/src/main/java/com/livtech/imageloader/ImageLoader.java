package com.livtech.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;


import static android.os.Environment.isExternalStorageRemovable;


public class ImageLoader {
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "mobileiron";
    private static ImageLoader loader = null;
    private Context context;
    private OnImageLoadListener loadListener;
    private LruCache<String, Bitmap> mMemoryCache;

    private ImageLoader() {
    }

    public static ImageLoader get() {
        if (loader == null) {
            synchronized (ImageLoader.class) {
                if (loader == null) {
                    loader = new ImageLoader();
                }
            }
        }
        return loader;
    }

    public void initLoader(Context context) {
        this.context = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //initDisk();
                initMemoryCache();
            }
        }).start();
    }


    private void initMemoryCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private void initDisk() {
        synchronized (mDiskCacheLock) {
            try {
                mDiskLruCache = DiskLruCache.open(getDiskCacheDir(), 1, 100, DISK_CACHE_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mDiskCacheStarting = false; // Finished initialization
            mDiskCacheLock.notifyAll(); // Wake any waiting threads
        }
    }

    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
    // but if not mounted, falls back on internal storage.
    private File getDiskCacheDir() {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + DISK_CACHE_SUBDIR);
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            final String imageKey = String.valueOf(params[0]);

            // Check disk cache in background thread
            Bitmap bitmap = getBitmapFromDiskCache(imageKey);

            if (bitmap == null) { // Not found in disk cache
                // Process as normal
                bitmap = ImageDownloader.downLoad(imageKey);
            }

            // Add final bitmap to caches
            addBitmapToCache(imageKey, bitmap);

            return bitmap;
        }
    }

    private void addBitmapToCache(String key, Bitmap bitmap) {
        // Add to memory cache as before
        /*if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }*/

        // Also add to disk cache
        synchronized (mDiskCacheLock) {
            /*if (mDiskLruCache != null && mDiskLruCache.get(key) == null) {
                mDiskLruCache.put(key, bitmap);
            }*/
        }
    }

    private Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                }
            }
            if (mDiskLruCache != null) {
                //return mDiskLruCache.get(key);
            }
        }
        return null;
    }


    public void loadImage(final String url, ImageView imageView) {
        Bitmap bitmap = getBitmapFromMemCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        else {
           ImageLoaderTask task= new ImageLoaderTask(url, imageView, new OnImageLoadListener() {
                @Override
                public void onImageLoad(Bitmap bitmap, ImageView targetView) {
                    if (isMainThread() && bitmap != null && targetView != null)
                        targetView.setImageBitmap(bitmap);
                    addBitmapToMemoryCache(url, bitmap);
                }
            });
           task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
           task.cancel(true);
        }
    }

    private boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }


    public interface OnImageLoadListener {
        void onImageLoad(Bitmap bitmap, ImageView imageView);
    }

    private static class ImageLoaderTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private OnImageLoadListener loadListener;
        private WeakReference<ImageView> imageView;

        private ImageLoaderTask(String url, ImageView imageView, OnImageLoadListener loadListener) {
            this.url = url;
            this.loadListener = loadListener;
            this.imageView = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return ImageDownloader.downLoad(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            loadListener.onImageLoad(bitmap, imageView.get());
        }
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
