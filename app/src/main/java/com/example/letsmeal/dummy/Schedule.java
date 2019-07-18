package com.example.letsmeal.dummy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Schedule implements Serializable {

    public long id;
    public String organizerUid;
    public String title;
    public Calendar calendar;

    /**
     * These two coordinates should be written in GeoPoint for Firebase
     */
    public double latitude;
    public double longitude;
    public String place;

    /**
     * We store participants as a list of Person.ID.
     */
    public String participantsAsString;
    public ArrayList<Long> participants;
    public HashMap<Long, Boolean> participates;

    public String description;

    /**
     * TODO:  Media fields.
     */

    public Schedule(String organizerUid) {
        this.organizerUid = organizerUid;
        this.calendar = Calendar.getInstance();
        this.description = "";
    }

    @Override
    public String toString() {
        String str;
        str = "Schedule ID : " + id;
        str += ", organizerUID = " + organizerUid;
        str += ", title = " + title;
        str += ", date = " + Schedule.getDateString(this.calendar);
        str += ", time = " + Schedule.getTimeString(this.calendar);
        str += ", description: \n";
        str += description;
        return str;
    }

    public void setTitle(String title) {this.title = title;}
    public String getTitle() {return this.title;}

    public void setCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        this.calendar.set(this.calendar.YEAR, year);
        this.calendar.set(this.calendar.MONTH, month);
        this.calendar.set(this.calendar.DAY_OF_MONTH, dayOfMonth);
        this.calendar.set(this.calendar.HOUR_OF_DAY, hourOfDay);
        this.calendar.set(this.calendar.MINUTE, minute);
    }

    public Calendar getCalendar() {return this.calendar;}
    public long getTimeInMills() {return this.calendar.getTimeInMillis();}


    public void setLatitude(double latitude) {this.latitude = latitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return this.description;}

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

    /**
     * TODO: IMPLEMENT!!!
     * I am not even sure what is the right signature for this function.
     */
    public void setParticipants(String participantsString) {
        return;
    }

    public void setParticipantsAsString(String participantsAsString) {
        this.participantsAsString = participantsAsString;
    }

    public String getParticipantsAsString() {
        return participantsAsString;
    }

    public String getOrganizerUid() {
        return organizerUid;
    }
}
