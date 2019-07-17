package com.example.letsmeal;

import java.util.ArrayList;

public class Person {

    long id;
    String name;
    String profileImage;
    String phoneNum;
    String email;
    String encryptedPasswd;
    ArrayList<Schedule> schedules;

    Person(String name) {
        this.name = name;
    }

}
