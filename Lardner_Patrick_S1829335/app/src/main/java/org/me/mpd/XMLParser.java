//Patrick Lardner S1829335

package org.me.mpd;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class XMLParser {
    ArrayList<Item> items = null;
    String rawXML = "";

    public ArrayList<Item> getItems() {

        while (items == null) {
            //wait for items
        }

        return items;
    }

    public void startProgress(String sourceURL, String itemType) {

            new Thread(new Task(sourceURL, itemType)).start();

    }

    private class Task implements Runnable {

        private final String url;
        private final String itemType;

        public Task(String sourceURL, String type) {
            url = sourceURL;
            itemType = type;
        }

        @Override
        public void run() {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            try {
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                StringBuilder sb = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    rawXML = sb.append(inputLine).toString();
                }
                in.close();
            } catch (IOException ae) {
                Log.e("MyTag", "ioexception in run" + ae.toString());
            }

            items = XMLParser.parseXML(rawXML, itemType);
        }

    }

    private static ArrayList<Item> parseXML(String rawXML, String itemType) {
        XmlPullParserFactory factory;
        ArrayList<Item> items = new ArrayList<Item>();

        try {

            factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(rawXML));
            int eventType = parser.getEventType();

            Item i = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                //Look for start tags
                if (eventType == XmlPullParser.START_TAG) {
                    switch (parser.getName().toLowerCase()) {
                        case "item":
                            i = new Item();
                            break;
                        case "title":
                            if (i == null) break;
                            i.setTitle(parser.nextText());
                            break;
                        case "description":
                            if (i == null) break;
                            i.setDescription(parser.nextText());
                            break;
                        case "link":
                            if (i == null) break;
                            i.setLink(parser.nextText());
                            break;
                        case "georss:point":
                            if (i == null) break;
                            String[] coords = parser.nextText().split(" ");
                            i.setLatitude(coords[0]);
                            i.setLongitude(coords[1]);
                            break;
                        case "pubdate":
                            if (i == null) break;
                            String dateTimeString = parser.nextText();
                            String dateOnlyString = dateTimeString.substring(5, dateTimeString.length() - 13);
                            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                            Date date = formatter.parse(dateOnlyString);
                            i.setDate(date);
                            break;
                        default:
                            break;
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.getName().equalsIgnoreCase("item")) {
                        if (i.getTitle() == null) {
                            i.setTitle("Delay");
                        }
                        i.setType(itemType);
                        items.add(i);
                    }
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException | ParseException e) {
            e.printStackTrace();
        }

        return items;

    }
}