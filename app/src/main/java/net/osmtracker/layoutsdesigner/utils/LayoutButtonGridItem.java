package net.osmtracker.layoutsdesigner.utils;

import android.net.Uri;

public class LayoutButtonGridItem {
    private String itemName;
    private Uri imagePath;

    public LayoutButtonGridItem(String name, Uri path){
        this.itemName = name;
        this.imagePath = path;
    }


    public String getItemName() {
        return itemName;
    }

    public Uri getImagePath() {
        return imagePath;
    }

    public int getId(){
        return itemName.hashCode();
    }
}
