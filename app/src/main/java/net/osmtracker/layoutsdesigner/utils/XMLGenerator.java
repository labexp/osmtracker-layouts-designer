package net.osmtracker.layoutsdesigner.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import net.osmtracker.layoutsdesigner.OsmtrackerLayoutsDesigner;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.io.File;

public class XMLGenerator {

    private Context context;

    public static void generateXML(Context context, ArrayList<LayoutButtonGridItem> gridItemsArray, String layoutName, int rows, int columns) throws IOException {


        layoutName = layoutName + OsmtrackerLayoutsDesigner.Preferences.XML_EXTENSION;

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);
        serializer.startTag("", "layouts");
        serializer.startTag("","layout");
        serializer.attribute("", "name", "root");

        int index = 0;

        for (int i = 0; i < rows ; i++) {
            serializer.startTag("","row");
            for (int j = 0; j <columns ; j++) {
                LayoutButtonGridItem currentItem = gridItemsArray.get(index);

                serializer.startTag("","button");
                serializer.attribute("","type","tag");
                serializer.attribute("","label",currentItem.getItemName());
                serializer.attribute("","icon",currentItem.getImagePath());
                serializer.endTag("","button");

                index++;
            }
            serializer.endTag("","row");
        }

        serializer.endTag("", "layout");
        serializer.endTag("","layouts");
        serializer.endDocument();
        String result = writer.toString();

        String path = Environment.getExternalStorageDirectory() + OsmtrackerLayoutsDesigner.Preferences.VAL_STORAGE_DIR+
                File.separator+OsmtrackerLayoutsDesigner.Preferences.LAYOUTS_SUBDIR + File.separator;

        createDir(path);

        writeToFile(context,path + layoutName, result);

        Toast.makeText(context,result,Toast.LENGTH_SHORT).show();

    }

    private static void writeToFile(Context context, String fileName, String str) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(str.getBytes(), 0, str.length());
        fos.close();
    }

    private static void createDir(String dirPath) {

        //Get File if SD card is present
        File apkStorage = null;
        if (isSDCardPresent()) {
            apkStorage = new File(dirPath);
        }
        //If File is not present create directory
        if (!apkStorage.exists()) {
            apkStorage.mkdirs();
            Log.e("#", "Directory Created.");
        }
    }
    private static boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
