package com.example.letsmeal.dummy;

import android.util.Log;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Schedule implements Serializable {
    /**
     * @param id A distinct ID for each schedule. Can we replace it with ID from Cloud Firestore?
     */
    public long id;
    public String organizerUid;
    public String title;

    // Firebase only supports timestamp.
    public long timestamp;

    // These two coordinates should be written in GeoPoint for Firebase
    public GeoPoint coordinate;
    public String place;

    // We store participants as a list of Person.ID.
    public ArrayList<String> participants;
    public HashMap<String, Boolean> participates;

    public String description;

    private final String TAG = "Schedule Class";

    /**
     * TODO:  Media fields.
     */


    /**
     * Dummy no-argument constructor for FireStore compatibility.
     */
    public Schedule(){
        // Dummy no-argument constructor for FireStore compatibility.
        this.participants = new ArrayList<>();
    }

    public Schedule(String organizerUid) {

        if (organizerUid == null) {
            Log.d(TAG, "organizer uid is null");
            Log.d(TAG, "ORGANIZER UID SHOULD NEVER BE NULL!!!");
        }

        this.organizerUid = organizerUid;
        this.description = "";

        this.participants = new ArrayList<>();
        this.participants.add(organizerUid);

        Log.d(TAG, this.organizerUid);

    }


    // Getters for FireStore compatibility
    public long getId() {
        return id;
    }
    public String getOrganizerUid() {
        return organizerUid;
    }
    public String getTitle() {
        return this.title;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public Calendar timestampToCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(this.timestamp);
        return cal;
    }
    public GeoPoint getCoordinate() {
        return this.coordinate;
    }
    public String getPlace() {
        return place;
    }
    public String getDescription() {
        return this.description;
    }
    public ArrayList<String> getParticipants() {
        return participants;
    }
    public HashMap<String, Boolean> getParticipates() {
        return participates;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp =timestamp;
    }
    public void setTimestamp(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.YEAR, year);
        cal.set(cal.MONTH, month);
        cal.set(cal.DAY_OF_MONTH, dayOfMonth);
        cal.set(cal.HOUR_OF_DAY, hourOfDay);
        cal.set(cal.MINUTE, minute);
        this.timestamp = cal.getTimeInMillis();
    }
    /*
    public void setCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        this.calendar.set(this.calendar.YEAR, year);
        this.calendar.set(this.calendar.MONTH, month);
        this.calendar.set(this.calendar.DAY_OF_MONTH, dayOfMonth);
        this.calendar.set(this.calendar.HOUR_OF_DAY, hourOfDay);
        this.calendar.set(this.calendar.MINUTE, minute);
    }
    */
    public void setCoordinate(double latitude, double longitude) {
        this.coordinate = new GeoPoint(latitude, longitude);
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public void setDescription(String description) {this.description = description;}

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    /**
     * Pretty-print date
     * @param dateCalendar A Calendar instance which contains YEAR, MONTH, DAY_OF_MONTH
     * @return A formatted String of dateCalendar
     */
    public static String getDateString(Calendar dateCalendar) {
        return String.format(Locale.getDefault(),
                "%04d/%02d/%02d (%s)",
                dateCalendar.get(dateCalendar.YEAR),
                dateCalendar.get(dateCalendar.MONTH)+1,
                dateCalendar.get(dateCalendar.DAY_OF_MONTH),
                getKoreanDayOfWeek(dateCalendar));

    }
    private static String getKoreanDayOfWeek(Calendar cal) {
        String [] yoil_vector = {"ERROR", "일", "월", "화", "수", "목", "금", "토"};
        return yoil_vector[cal.DAY_OF_WEEK];
    }

    /**
     * Pretty-print date
     * @param timeCalendar A Calendar instance which contains HOUR_OF_DAY, MINUTE
     * @return A formatted String of timeCalendar
     */
    public static String getTimeString(Calendar timeCalendar) {
        return String.format(Locale.getDefault(),
                "%s %02d:%02d",
                getKoreanAmPm(timeCalendar),
                timeCalendar.get(timeCalendar.HOUR_OF_DAY),
                timeCalendar.get(timeCalendar.MINUTE));
    }
    private static String getKoreanAmPm(Calendar cal) {
        if (cal.get(cal.HOUR_OF_DAY)< 12)
            return "오전";
        return "오후";
    }


    @Override
    public String toString() {
        String str;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(this.timestamp);
        str = "Schedule ID : " + id;
        str += ", organizerUID = " + organizerUid;
        str += ", title = " + title;
        str += ", date = " + Schedule.getDateString(cal);
        str += ", time = " + Schedule.getTimeString(cal);
        str += ", description: \n";
        str += description;
        return str;
    }

}
