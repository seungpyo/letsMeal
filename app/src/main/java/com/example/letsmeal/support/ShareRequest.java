package com.example.letsmeal.support;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.letsmeal.R;

import java.util.ArrayList;

public class ShareRequest {
    private Context ct;
    private Intent intent;
    private ArrayList<Uri> data;

    private ShareRequest(Context ct){
        this.ct = ct;
        intent = null;
        data = null;
    };

    public static ShareRequest with(Context ct){
        // Return a new instance of this class.
        return new ShareRequest(ct);
    }

    public ShareRequest shareFiles(final ArrayList<Uri> dataURI){
        // Integrity Check
        if(dataURI == null || dataURI.isEmpty())
            return this;

        data = new ArrayList<Uri>(dataURI);

        final ContentResolver cr = ct.getContentResolver();
        final String title = ct.getResources().getString(R.string.share_chooser_title);

        Intent sendIntent;
        if(cr.getType(data.get(0)).contains("video")){
            // Multiple Videos Selection
            sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, data);
            sendIntent.setType("video/*");
        }else{
            // Multiple Images Selection
            sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE); // MimeTypes might be mixed.
            sendIntent.putExtra(Intent.EXTRA_STREAM, data);
            sendIntent.setType("image/*");
        }

        this.intent = sendIntent;
        return this;
    }

    public ShareRequest shareFile(final Uri datum){
        // Integrity Check
        if(datum == null)
            return this;

        if(data == null)
            data = new ArrayList<>();
        data.add(datum);

        final ContentResolver cr = ct.getContentResolver();
        final String title = ct.getResources().getString(R.string.share_chooser_title);

        Intent sendIntent;
        if(cr.getType(data.get(0)).contains("video")){
            // Multiple Videos Selection
            sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, data);
            sendIntent.setType("video/*");
        }else{
            // Multiple Images Selection
            sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE); // MimeTypes might be mixed.
            sendIntent.putExtra(Intent.EXTRA_STREAM, data);
            sendIntent.setType("image/*");
        }

        this.intent = sendIntent;
        return this;
    }

    // A helper function for starting a chooser intent
    private ShareRequest withChooser(String title){
        // Create intent to show the chooser dialog
        Intent chooser = Intent.createChooser(intent, title);

        // Verify the original intent will resolve to at least one activity
        if (intent.resolveActivity(ct.getPackageManager()) != null) {
            this.intent = chooser;
        }
        return this;
    }

    public ShareRequest request(){
        ct.startActivity(intent);
        return this;
    }
}
