package net.osmtracker.layoutsdesigner.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import net.osmtracker.layoutsdesigner.OsmtrackerLayoutsDesigner;
import net.osmtracker.layoutsdesigner.R;
import net.osmtracker.layoutsdesigner.activity.MainActivity;
import net.osmtracker.layoutsdesigner.utils.CustomGridItemAdapter;
import net.osmtracker.layoutsdesigner.utils.LayoutButtonGridItem;

import java.util.ArrayList;
import java.util.Calendar;

public class Editor extends AppCompatActivity {

    private TextView txtLayoutName;
    private String contextTag = OsmtrackerLayoutsDesigner.Preferences.TAG + ".Editor";
    private GridView gvLayoutEditor;
    private CustomGridItemAdapter gridAdapter;
    private ArrayList<LayoutButtonGridItem> gridItemsArray;
    private int columnsNum = 0;
    private int rownsNum = 0;
    private String layoutName;
    private Button btnCancel;
    private Button btnAccept;
    private Uri currentUri;

    // Code returned by the startActivityForResult() to the onActionResult() method
    // Indicates that the user choose correctly to select an image from the gallery
    private int SELECT_IMAGE = 10;

    private String IMAGE_PATH;
    private TextView sample_url;
    private ImageButton image_button;

    private View current_new_button_popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = this.getIntent().getExtras();

        //get the extras from the intent and check if exist and has values to initialize the variable to create the editor view
        if(extras != null){
            columnsNum = Integer.parseInt(extras.getString(OsmtrackerLayoutsDesigner.Preferences.EXTRA_COLUMNS_NAME, "3"));
            rownsNum = Integer.parseInt(extras.getString(OsmtrackerLayoutsDesigner.Preferences.EXTRA_ROWS_NAME, "4"));

            layoutName = extras.getString(OsmtrackerLayoutsDesigner.Preferences.EXTRA_NEW_LAYOUT_NAME, getResources().getString(R.string.empty_layout_name).replace("{0}",  Calendar.getInstance().getTime().toString()));
            if(layoutName.equals("") || layoutName.isEmpty()){
                layoutName = getResources().getString(R.string.empty_layout_name).replace("{0}", Calendar.getInstance().getTime().toString());
            }
            txtLayoutName = (TextView) findViewById(R.id.txt_layout_name);
            txtLayoutName.setText(layoutName);

            boolean notesCheckbox = extras.getBoolean(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_NOTES, false);
            boolean cameraCheckbox = extras.getBoolean(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_CAMERA, false);
            boolean voiceRecorderCheckbox = extras.getBoolean(OsmtrackerLayoutsDesigner.Preferences.EXTRA_CHECKBOX_VOICE_RECORDER, false);
            gridItemsArray = new ArrayList<LayoutButtonGridItem>();
            int amountToSubstract = checkIfNeedsDefaultButtons(notesCheckbox, cameraCheckbox, voiceRecorderCheckbox);
            int totalItems = (columnsNum * rownsNum) - amountToSubstract;
            //TODO: VERIFY IF THE DEFAULT BUTTONS ARE CHECKED AND CREATE THEM IN THE ARRAY
            //set the total items created by default in the array
            for(int i = 0; i < totalItems; i++){
                gridItemsArray.add(new LayoutButtonGridItem(""));
            }

            gvLayoutEditor = (GridView) findViewById(R.id.grid_view_editor);
            //pass the num columns assigned by the user
            gvLayoutEditor.setNumColumns(columnsNum);
            gridAdapter = new CustomGridItemAdapter(this, gridItemsArray);
            gvLayoutEditor.setAdapter(gridAdapter);
            gvLayoutEditor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO: MAKES THE FUNCTIONALITY TO OPEN THE POP UP AND SET NAME AND ICON TO THE ITEM
                    LayoutButtonGridItem currentGridItem =  gridItemsArray.get(position);
                    if (currentGridItem.getDefaultIcon() != null){
                        //The selected button is an default button chosen before the editor activity was open, the user can't edit this button
                        Toast.makeText(getApplicationContext(), R.string.can_not_edit_button_message, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        showPopUp(currentGridItem);
                    }
                    //Toast.makeText(getApplicationContext(), "You press " + position, Toast.LENGTH_SHORT).show();
                }
            });

            btnCancel = (Button) findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Editor.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                    builder.setTitle(R.string.cancelling_creation)
                            .setMessage(R.string.cancel_verification)
                            .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            })
                            .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                    .setCancelable(true)
                    .create().show();
                }
            });
            btnAccept = (Button) findViewById(R.id.btn_accept);
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: SAVE THE LAYOUT
                }
            });

        }
        else{
            Toast.makeText(getApplicationContext(), R.string.editor_intent_extras_error, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private int checkIfNeedsDefaultButtons(boolean notesCheckbox, boolean cameraCheckbox, boolean voiceRecorderCheckbox) {
        int i = 0;
        if(voiceRecorderCheckbox){
            gridItemsArray.add(new LayoutButtonGridItem(getResources().getString(R.string.default_button_voice), getResources().getDrawable(R.drawable.voice_32x32)));
            i++;
        }
        if(cameraCheckbox){
            gridItemsArray.add(new LayoutButtonGridItem(getResources().getString(R.string.default_button_camera), getResources().getDrawable(R.drawable.camera_32x32)));
            i++;
        }
        if(notesCheckbox){
            gridItemsArray.add(new LayoutButtonGridItem(getString(R.string.default_button_text_note), getResources().getDrawable(R.drawable.text_32x32)));
            i++;
        }
        return i;
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

    private void showPopUp(final LayoutButtonGridItem currentGridItem){
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        current_new_button_popup = inflater.inflate(R.layout.new_button_popup, null);

        final EditText buttonName = current_new_button_popup.findViewById(R.id.editTextButtonName);
        final TextView imagePathTextView = current_new_button_popup.findViewById(R.id.url_text_view);

        image_button = (ImageButton)current_new_button_popup.findViewById(R.id.imageButton);

        final AlertDialog.Builder builder = new AlertDialog.Builder(Editor.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle(R.string.new_button_pop_up_title)
                .setView(current_new_button_popup)
                .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Checking if the user changed the name or the image of the item
                        if( !currentGridItem.getItemName().equals(buttonName.getText().toString()) || currentGridItem.getImagePath() != imagePathTextView.getText().toString()) {

                            Log.e("#", "Current name: "+ currentGridItem.getItemName() + " nombre en el edit text: "+ buttonName.getText().toString());
                            currentGridItem.setItemName(buttonName.getText().toString());
                            currentGridItem.setImagePath(imagePathTextView.getText().toString());
                            currentGridItem.setImageURI(currentUri);

                            gridAdapter.notifyDataSetChanged();
                            gvLayoutEditor.setAdapter(gridAdapter);
                            currentUri = null;
                        }


                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        //Check if the current gridItem has name or image, and setting the popup with that name/image
        if(currentGridItem.getItemName() != "" || currentGridItem.getImageURI() != null){
            buttonName.setText(currentGridItem.getItemName());
            imagePathTextView.setText(currentGridItem.getImagePath());
            image_button.setMaxWidth(100);
            image_button.setMaxHeight(100);
            image_button.setPadding(9,9,9,9);
            image_button.setImageURI(currentGridItem.getImageURI());
        }

        final AlertDialog dialog = builder.create();


        buttonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Button btOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if(charSequence.length() >=0) {
                    try {

                        if(charSequence.length() == 0){
                            btOk.setEnabled(false);
                        }
                        else{
                            btOk.setEnabled(true);
                        }

                    }catch (Exception e){
                        Log.e(contextTag, "Error editing the button name");

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialog.show();


    }

    // This method throws the intent to open the gallery so the user can choose an icon for the new button in the layout
    // Is called when the user presses the Icon in the pop up
    public void selectImage(View view){

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_IMAGE); // Start the activity waiting for a code as a response (SELECT_IMAGE)
    }

    // This method is called when the intent to open the gallery is finished or closed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == SELECT_IMAGE){ // This is to indentIfy who returned the original requestCode
            if (resultCode == Activity.RESULT_OK) { // If no error happened in the process
                if(current_new_button_popup != null) showPathAndIcon(data.getData());
            }
        }
    }

    // This method writes the image path on the pop up window and shows the icon selected by the user
    private void showPathAndIcon(Uri selectedImage){
        currentUri = selectedImage;
        sample_url = (TextView)current_new_button_popup.findViewById(R.id.url_text_view);
        image_button = (ImageButton)current_new_button_popup.findViewById(R.id.imageButton);

        if (selectedImage != null && selectedImage.getPath() != null){

            IMAGE_PATH = getPath(selectedImage);
            sample_url.setText(IMAGE_PATH);
            Log.e("Path", IMAGE_PATH);
        }

        // Set the correct properties so the image is shown in the right way
        image_button.setMaxWidth(100);
        image_button.setMaxHeight(100);
        image_button.setPadding(9,9,9,9);
        image_button.setImageURI(selectedImage);
    }

    // This method returns the path of an Uri object as a string
    private String getPath(Uri uri) {
        String[] projection = { android.provider.MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
