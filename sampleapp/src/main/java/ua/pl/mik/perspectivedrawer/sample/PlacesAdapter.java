package ua.pl.mik.perspectivedrawer.sample;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlacesAdapter extends ArrayAdapter<Place> {
    public PlacesAdapter(Context context) {
        super(context, 0);
        for (Place place : Place.values()) {
            add(place);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            view = View.inflate(getContext(), R.layout.place_item, null);

            viewHolder = new ViewHolder();
            viewHolder.placeImage = (ImageView) view.findViewById(R.id.place_image);
            viewHolder.placeTitle = (TextView) view.findViewById(R.id.place_title);
            viewHolder.placeDescription = (TextView) view.findViewById(R.id.place_description);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        Place place = getItem(position);
        viewHolder.placeTitle.setText(place.getTextId());

        ImageDownloadTask imageDownloadTask = new ImageDownloadTask(viewHolder.placeImage, getContext());
        viewHolder.placeImage.setTag(imageDownloadTask);
        viewHolder.placeImage.setImageDrawable(null);
        imageDownloadTask.execute(place);

        return view;
    }

    private static class ViewHolder {
        private ImageView placeImage;
        private TextView placeTitle;
        private TextView placeDescription;
    }
}
