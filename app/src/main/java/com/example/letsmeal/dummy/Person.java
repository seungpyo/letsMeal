package com.example.letsmeal.dummy;

import java.util.ArrayList;

// Just to test my git system

public class Person {

    public long id;
    public String name;
    public String profileImage;
    public String phoneNum;
    public String email;
    public String encryptedPasswd;
    public ArrayList<Schedule> schedules;

    public Person(String name) {
        this.name = name;
    }

}
