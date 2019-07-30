package com.example.letsmeal.dummy;

import android.util.Log;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;

public class TimeRecommendation {

    private static final String TAG = "TimeRecommendation";

    private ArrayList<CalendarPair> busyTimes;


    public TimeRecommendation(ArrayList<CalendarPair> busyTimes) {
        this.busyTimes = busyTimes;
    }


    public ArrayList<CalendarPair> recommendTime() {
        ArrayList<CalendarPair> result = new ArrayList<>();
        Collections.sort(busyTimes);
        for (int i = 0; i < busyTimes.size() - 1; i++) {
            CalendarPair interval = CalendarPair.getInterval(
                    busyTimes.get(i), busyTimes.get(i+1));
            if (interval != null) {
                result.add(interval);
                Log.d(TAG, "Interval: " + interval.toString());
            }
        }
        return result;
    }

}
