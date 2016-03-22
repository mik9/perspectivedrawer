package ua.pl.mik.perspectivedrawer.sample;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import ua.pl.mik.perspectivedrawer.ActionBarDrawerToggle;
import ua.pl.mik.perspectivedrawer.DrawerLayout;

public class MyActivity extends AppCompatActivity {
    private PlacesAdapter mAdapter;
    private MapView mMapView;
    private boolean mCloseDrawer;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }


        mMapView = (MapView) findViewById(R.id.map_view);
        TileSources.setUpTileSource(mMapView, TileSources.Sources.GOOGLE_ORIGINAL, this);
        mMapView.getController().setZoom(14);
        mMapView.setMultiTouchControls(true);

        ListView listView = (ListView) findViewById(R.id.places);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer);

        mAdapter = new PlacesAdapter(this);

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = mAdapter.getItem(position);
                mMapView.getController().animateTo(new GeoPoint(place.getLat(), place.getLng()));
                mDrawer.close();
            }
        });

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        mCloseDrawer = pref.getBoolean("close_drawer", false);
        mDrawer.setDimmingEnabled(pref.getBoolean("dimming", true));

        if (savedInstanceState != null) {
            mMapView.getController().setCenter((org.osmdroid.api.IGeoPoint) savedInstanceState.getSerializable("map_center"));
        } else {
            mDrawer.open();
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.drawable.ic_navigation_drawer,
                R.string.action_close_drawer,
                R.string.action_close_drawer);
        mDrawer.setDrawerListener(mDrawerToggle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        MenuItem menuCloseAction = menu.findItem(R.id.action_close_drawer);
        menuCloseAction.setChecked(mCloseDrawer);
        MenuItem menuDimming = menu.findItem(R.id.action_dimming_enabled);
        menuDimming.setChecked(mDrawer.isDimmingEnabled());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_close_drawer:
                item.setChecked(!item.isChecked());
                mCloseDrawer = item.isChecked();
                break;
            case R.id.action_dimming_enabled:
                item.setChecked(!item.isChecked());
                mDrawer.setDimmingEnabled(item.isChecked());
                break;
        }

        mDrawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("map_center", (java.io.Serializable) mMapView.getMapCenter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("close_drawer", mCloseDrawer);
        editor.putBoolean("dimming", mDrawer.isDimmingEnabled());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isOpened()) {
            super.onBackPressed();
        } else {
            mDrawer.open();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
