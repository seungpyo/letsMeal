package com.example.letsmeal.dummy;

import android.util.Log;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;


/**
 * 생각해보기
 *
 * 기존 아이디어는 "각 유저의 캘린더에서 선약을 읽어와서, 공통으로 비는 시간을 추천한다" 였다.
 * 허나, 이를 위해서는 아래의 문제들을 먼저 확실히 해야 할 것이다.
 *
 * 1. 구현 상의 문제
 *  가) 다른 사용자와의 Calendar Consensus?
 *
 * 2. 사용자 경험 상의 문제
 *  가) 캘린더에 있지만 실제로 시간을 잡아먹진 않는 일정들 (e.g. XXX 주간, 공휴일 등)
 *  나) 캘린더에 없지만 실제로 시간을 잡아먹고 있는 일정들 (e.g. 학교수업, 알바, 회사 근무 시간 등)
 *
 * 위 문제들을 해결 할 것인지, 아니면 그냥 시간추천을 없애고 캘린더는 로컬 캘린더에 읽고 쓰기만 할 것인지를 정해야 할 듯.
 *
 */


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
