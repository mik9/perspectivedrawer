package ua.pl.mik.perspectivedrawer.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class TileSources {
    private static List<WeakReference<TilesOverlay>> overlays = new ArrayList<WeakReference<TilesOverlay>>();

    public enum Sources {
        GOOGLE_ORIGINAL("Google-Original", "https://mts0.google.com/vt/lyrs=m@275314532&rlbl=1&", 3, 18, null);

        private final String name;
        private final String baseUrl;
        private final int minZoomLevel;
        private final int maxZoomLevel;
        private final Sources overlay;

        Sources(String name, String baseUrl, int minZoomLevel, int maxZoomLevel, Sources overlay) {
            this.name = name;
            this.baseUrl = baseUrl;
            this.minZoomLevel = minZoomLevel;
            this.maxZoomLevel = maxZoomLevel;
            this.overlay = overlay;
        }
    }

    private TileSources() {
    }

    public static OnlineTileSourceBase createTileSource(Sources sourceId, int mapVersion, final double scale) {
        String sourceName = sourceId.name;
        String baseUrl = String.format(sourceId.baseUrl, mapVersion);

        return new OnlineTileSourceBase(sourceName, ResourceProxy.string.unknown,
                sourceId.minZoomLevel, sourceId.maxZoomLevel, (int) (256 * scale), ".png", new String[] {baseUrl}) {
            @Override
            public String getTileURLString(MapTile aTile) {
                return getBaseUrl() + "x=" + aTile.getX() + "&y=" + aTile.getY() + "&z=" + aTile.getZoomLevel() +
                        "&scale=" + scale;
            }
        };
    }

    public static void setUpTileSource(MapView mapView, Sources source, Context context) {
        // Clear overlays from old tile source
        List<WeakReference<TilesOverlay>> toDelete = new ArrayList<WeakReference<TilesOverlay>>();
        for (WeakReference<TilesOverlay> oldOverlayReference : overlays) {
            TilesOverlay oldOverlay = oldOverlayReference.get();
            if (oldOverlay == null) {
                toDelete.add(oldOverlayReference);
                continue;
            }
            if (mapView.getOverlayManager().contains(oldOverlay)) {
                mapView.getOverlayManager().remove(oldOverlay);
                toDelete.add(oldOverlayReference);
            }
        }
        overlays.removeAll(toDelete);

        final int mapVersion = 157;
        final boolean applySoftScale = true;
        final double scale = applySoftScale ? context.getResources().getDisplayMetrics().density : 1.0;
        mapView.setTileSource(createTileSource(source, mapVersion, scale));
        if (source.overlay != null) {
            MapTileProviderBasic tileProviderBasic = new MapTileProviderBasic(context);
            tileProviderBasic.setTileSource(TileSources.createTileSource(source.overlay, mapVersion, scale));
            TilesOverlay overlay = new TilesOverlay(tileProviderBasic, context);
            mapView.getOverlayManager().add(overlay);

            overlays.add(new WeakReference<TilesOverlay>(overlay));
        }
    }
}
