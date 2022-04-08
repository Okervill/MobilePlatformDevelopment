//Patrick Lardner S1829335

package org.me.mpd;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Item {

    private String title;
    private String description;
    private String link;
    private String longitude;
    private String latitude;
    private Date date;
    private String type;
    private Date startDate;
    private Date endDate;

    public Item() {

    }

    public Item(String title, String description, String link, String longitude, String latitude, Date date, String type) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String toString() {
        return "Title: " + title + " Description: " + description + " Link: " + link + " Coordinates: " + latitude + ", " + longitude + " Date: " + date.toString();
    }

    public String getType() {
        return this.type;
    }

    public void setType(String itemType) {
        type = itemType;
    }

    public void setStartDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        Date start = new Date();
        try {
            start = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.startDate = start;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        Date end = new Date();
        try {
            end = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.endDate = end;
    }

    public Date getEndDate() {
        return endDate;
    }

    public long getDays() {
        if (startDate != null & endDate != null) {
            long days = endDate.getTime() - startDate.getTime();
            return TimeUnit.DAYS.convert(days, TimeUnit.MILLISECONDS);
        }
        return 0;
    }

}
