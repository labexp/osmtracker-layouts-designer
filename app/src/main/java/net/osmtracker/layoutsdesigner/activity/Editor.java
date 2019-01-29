package net.osmtracker.layoutsdesigner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import net.osmtracker.layoutsdesigner.R;
import net.osmtracker.layoutsdesigner.activity.MainActivity;
import net.osmtracker.layoutsdesigner.utils.CustomGridItemAdapter;
import net.osmtracker.layoutsdesigner.utils.LayoutButtonGridItem;

import java.util.ArrayList;

public class Editor extends AppCompatActivity {

    private GridView gvLayoutEditor;
    private CustomGridItemAdapter gridAdapter;
    private ArrayList<LayoutButtonGridItem> gridItemsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridItemsArray = new ArrayList<LayoutButtonGridItem>();
        //TODO: GET ROWS AND COLUMNS FROM THE POP UP OF THE MAIN SCREEN

        for(int i = 0; i < 3; i++){
            gridItemsArray.add(new LayoutButtonGridItem("Item " + i, null));
        }

        gvLayoutEditor = (GridView) findViewById(R.id.grid_view_editor);
        gridAdapter = new CustomGridItemAdapter(this, gridItemsArray);
        gvLayoutEditor.setAdapter(gridAdapter);

    }

    @Override
    public void onBackPressed() {
        //TODO: push a dialog notifying that this action delete the current layout without save it
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
