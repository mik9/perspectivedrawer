package ua.pl.mik.perspectivedrawer.sample;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

public class BitmapCache {
    private static final LruCache<Place, Bitmap> mCache = new LruCache<Place, Bitmap>(5 * 1024 * 1024) {
        @Override
        protected int sizeOf(Place key, Bitmap value) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return value.getAllocationByteCount();
            }
            return value.getRowBytes() * value.getHeight();
        }
    };

    public static Bitmap get(Place place) {
        return mCache.get(place);
    }

    public static void put(Place place, Bitmap bitmap) {
        mCache.put(place, bitmap);
    }
}
