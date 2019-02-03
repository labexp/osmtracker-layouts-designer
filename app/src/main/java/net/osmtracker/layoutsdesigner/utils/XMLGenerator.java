package net.osmtracker.layoutsdesigner.utils;

import android.content.Context;
import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.io.File;

public class XMLGenerator {

    private Context context;

    public static void generateXML(Context context, ArrayList<LayoutButtonGridItem> gridItemsArray, String layoutName) throws IOException {


        XmlSerializer ser = Xml.newSerializer();

        OutputStreamWriter fOut = new OutputStreamWriter(context.openFileOutput("pr22.xml",context.MODE_PRIVATE));

        StringWriter writer = new StringWriter();

        ser.setOutput(writer);
        ser.startDocument("UTF-8",true);
        ser.startTag("", "layouts");
        ser.startTag("", "layout");
        ser.attribute("","name","root");
        ser.endTag("","layout");
        ser.endTag("","layouts");
        ser.endDocument();

        fOut.write(writer.toString());
        fOut.close();


    }
}
