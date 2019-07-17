package com.example.letsmeal;

import com.example.letsmeal.dummy.Schedule;

import java.util.Calendar;

public class ItemCard implements Comparable<ItemCard> {

    private static final String TAG = "ItemCard";

    String title;
    String date;
    String time;
    String place;
    String participants; // A list of participants are marshaled into a single String
    String description;

    Calendar calendar;
    long timestamp;

    ItemCard(Schedule schedule) {
        this.title = schedule.getTitle();
        this.calendar = schedule.getCalendar();
        this.date = Schedule.getDateString(this.calendar);
        this.time = Schedule.getTimeString(this.calendar);
        this.timestamp = this.calendar.getTimeInMillis();
        this.place = schedule.getPlace();
        this.participants = schedule.getParticipantsAsString();
        this.description = schedule.getDescription();
   }

    public String getTitle() {
        return this.title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return this.calendar.toString();
    }

    @Override
    public int compareTo(ItemCard that) {
        if (this.timestamp > that.getTimestamp())
            return 1;
        else if (this.timestamp < that.getTimestamp())
            return -1;
        else
            return 0;
    }
}
