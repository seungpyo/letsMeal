package com.example.letsmeal.dummy;

import java.util.ArrayList;


public class Person {
    /**
     * @param uid A UID used for identification in FireBase.
     */
    public String uid;
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
