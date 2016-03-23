package ua.pl.mik.perspectivedrawer.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageDownloadTask extends AsyncTask<Place, Void, Bitmap> {
    private final Context context;
    private WeakReference<ImageView> imageViewWeakReference;
    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final String URL = "http://maps.googleapis.com/maps/api/staticmap?center=%1$f," +
            "%2$f&zoom=11&size=%3$dx%3$d&scale=%4$f";

    public ImageDownloadTask(ImageView imageView, Context context) {
        this.imageViewWeakReference = new WeakReference<ImageView>(imageView);
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Place... params) {
        Place place = params[0];

        String url = String.format(URL, place.getLat(), place.getLng(),
                context.getResources().getDimensionPixelSize(R.dimen.place_image_size),
                context.getResources().getDisplayMetrics().density);

        Bitmap b = BitmapCache.get(place);
        if (b == null) {
            byte[] bytes = null;
            try {
                Response r = null;
                r = okHttpClient.newCall(new Request.Builder().url(url).build()).execute();
                bytes = r.body().bytes();
            } catch (IOException e) {
                return null;
            }

            b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (b != null) {
                BitmapCache.put(place, b);
            }
        }
        return b;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        ImageView imageView = imageViewWeakReference.get();
        if (bitmap != null && imageView != null && imageView.getTag() == this) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
