package com.example.letsmeal;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letsmeal.support.Glide4Engine;
import com.example.letsmeal.support.PermissionRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.ArrayList;

public class MediaPickerActivity extends AppCompatActivity {

    static private final int REQUEST_CODE_CHOOSE = 1;

    private ArrayList<Uri> path = new ArrayList<>();

    private ImageView imgMain;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MediaAdapter imageAdapter;
    private ImageController mainController;
    private FloatingActionButton button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
            path = savedInstanceState.getParcelableArrayList("selections");


        setContentView(R.layout.activity_picker);

        imgMain = findViewById(R.id.img_main);
        recyclerView = findViewById(R.id.image_rv);
        button = findViewById(R.id.confirm_pick_button);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mainController = new ImageController(imgMain);
        imageAdapter = new MediaAdapter(this, mainController, path);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(imageAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!path.isEmpty())
                    setResult(RESULT_OK, new Intent().putParcelableArrayListExtra("selections", path));
                else
                    setResult(RESULT_CANCELED);
                finish();
            }
        });

        PermissionRequest.with(this).needExternalStorage().request();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK){
            path = (ArrayList<Uri>) Matisse.obtainResult(data);
            imageAdapter.changePath(path);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("selections", path);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionRequest.REQUEST_CODE){
            if(grantResults.length == 2 && PermissionRequest.allGranted(grantResults)){
                loadPicker();
            }else{
                /**
                 * @TODO 권한 획득 실패시 처리할 로직 작성하기
                 */
            }
        }
    }

    private void loadPicker(){
        Matisse.from(MediaPickerActivity.this)
                .choose(MimeType.ofAll())
                .countable(true)
                .maxSelectable(9)
                .theme(R.style.Matisse_Dracula)
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE);
    }
}
