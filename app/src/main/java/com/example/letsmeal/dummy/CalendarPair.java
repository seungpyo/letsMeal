package com.example.letsmeal.dummy;

import android.util.Log;

import java.util.Calendar;

public class CalendarPair implements Comparable<CalendarPair> {
    public Calendar start, end;
    private final static String TAG = "CalendarPair";
    public CalendarPair(Calendar start, Calendar end) {
        if (start.compareTo(end) > 0) {
            Log.d(TAG, "Wrong argument for CalendarPair constructor!");
        }
        this.start = start;
        this.end = end;
    }

    public static CalendarPair getInterval(CalendarPair c1, CalendarPair c2) {
        if (c1.compareTo(c2) > 0) {
            // c1 < c2 must hold.
            CalendarPair tmp = c2;
            c2 = c1;
            c1 = tmp;
        }
        if (c1.end.compareTo(c2.start) >= 0) {
            // No interval between two CalendarPairs.
            return null;
        }
        return new CalendarPair(c1.end, c2.start);
    }

    @Override
    public int compareTo(CalendarPair that) {
        if (this.start.equals(that.start)) {
            return this.end.compareTo(that.end);
        } else {
            return this.start.compareTo(that.start);
        }
    }

    @Override
    public String toString() {
        String msg = ""
            + this.start.get(Calendar.YEAR) + "/"
            + this.start.get(Calendar.MONTH) + "/"
            + this.start.get(Calendar.DAY_OF_MONTH) + "/"
            + this.start.get(Calendar.HOUR_OF_DAY) + " ~ "
            + this.end.get(Calendar.YEAR) + "/"
            + this.end.get(Calendar.MONTH) + "/"
            + this.end.get(Calendar.DAY_OF_MONTH) + "/"
            + this.end.get(Calendar.HOUR_OF_DAY);
        return msg;
    }
}
