package com.example.letsmeal.support;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

/**
 * Request relevant permissions needed by the app.
 *
 * <p>
 *     Usage
 * <pre>
 * <code>
 * if(PermissionRequest.with(this).externalStorage().request()){
 *     ... // The access has been granted.
 * }
 * </code>
 * </pre>
 * </p>
 *
 * Reference: https://developer.android.com/guide/topics/permissions/overview.html#permission-groups
 *
 * @author hgs3896
 * @since 1.0
 */
public class PermissionRequest {

    private Activity activity;
    private ArrayList<String> permissions = new ArrayList<>();
    private int numberRequested = 0;
    private PermissionRequest(Activity activity){
        this.activity = activity;
    }

    public static final int REQUEST_CODE = 99;

    /**
     * @param activity Activity 객체 예를 들면 this 같은걸 넘기면 된다.
     * @return PermissionRequest 객체가 리턴된다. (Factory Pattern)
     */
    public static PermissionRequest with(Activity activity){
        return new PermissionRequest(activity);
    }

    public static boolean allGranted(int[] grantResults){
        for(int result : grantResults){
            if(result == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    public boolean checkPermission(String permission){
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 외부 저장장치에 읽고 쓰는 권한을 요청한다.
     *
     * @return 권한을 이미 획득한 경우 true, 아닌경우 false 이며 권한 호출을 요청한다.
     */
    public PermissionRequest needExternalStorage(){
        if (!checkPermission(Manifest.permission_group.STORAGE)){
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        numberRequested += 2;
        return this;
    }

    /**
     * 휴대폰에서 관리되고 있는 일정 기록을 읽고 쓰는 권한을 요청한다.
     *
     * @return 권한을 이미 획득한 경우 true, 아닌경우 false 이며 권한 호출을 요청한다.
     */
    public PermissionRequest needCalendar(){
        if (!checkPermission(Manifest.permission_group.CALENDAR)){
            permissions.add(Manifest.permission.READ_CALENDAR);
            permissions.add(Manifest.permission.WRITE_CALENDAR);
        }
        numberRequested += 2;
        return this;
    }

    /**
     * 현재 위치를 정확하게 추정할 수 있도록 GPS 데이터에 접근하는 권한을 요청한다.
     *
     * @return 권한을 이미 획득한 경우 true, 아닌경우 false 이며 권한 호출을 요청한다.
     */
    public PermissionRequest needLocation(){
        if (!checkPermission(Manifest.permission_group.LOCATION)){
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        numberRequested += 2;
        return this;
    }

    public PermissionRequest request(){
        String [] permissions = new String[this.permissions.size()];

        int idx = 0;
        for(String permission : this.permissions)
            permissions[idx++] = permission;

        if(permissions.length > 0)
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);

        return this;
    }
}

