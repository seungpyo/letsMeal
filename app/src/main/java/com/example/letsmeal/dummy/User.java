package com.example.letsmeal.dummy;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;


public class User implements Serializable {
    /**
     * @param uid A UID used for identification in FireBase.
     */
    private String uid;
    private String name;

    public final static int UID = 0, NAME = 1, ALL = 2;

    public User(){}

    /**
     * Constructor for User class.
     * @param uid If unknown, just give null.
     * @param name If unknown, just give null.
     */
    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object that) {
        Log.d("우효~", "쵸-럭키wwww");
        if (that instanceof User) {
            return this.uid.equals(((User) that).getUid());
        } else {
            return false;
        }
    }


}
