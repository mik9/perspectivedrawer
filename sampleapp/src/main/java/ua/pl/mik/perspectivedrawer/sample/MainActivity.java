package ua.pl.mik.perspectivedrawer.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.pl.mik.perspectivedrawer.PerspectiveDrawer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PerspectiveDrawer perspectiveDrawer = (PerspectiveDrawer) findViewById(R.id.drawer);

        final TextView txt_page_content = (TextView) findViewById(R.id.txtview_page_content);
        ListView lv = (ListView) findViewById(R.id.listview_navmenu);
        final ArrayList<String> menu_options = new ArrayList<>();
        menu_options.add("Menu 1");
        menu_options.add("Menu 2");
        menu_options.add("Menu 3");
        menu_options.add("Menu 4");
        menu_options.add("Menu 5");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout
                .simple_list_item_1, menu_options);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txt_page_content.setText("Page Data for " + menu_options.get(i));
                perspectiveDrawer.close();
            }
        });
    }
}
