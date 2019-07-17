package com.example.letsmeal.support;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

public class HelperLibrary {
    private volatile static HelperLibrary instance;
    private HelperLibrary(){}
    public static HelperLibrary getInstance(){
        if(instance == null){
            synchronized (HelperLibrary.class) {
                if(instance == null){
                    instance = new HelperLibrary();
                }
            }
        }
        return instance;
    }
    public void getAppKeyHash(Context ct) {
        try {
            PackageInfo info = ct.getPackageManager().getPackageInfo(ct.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
}
