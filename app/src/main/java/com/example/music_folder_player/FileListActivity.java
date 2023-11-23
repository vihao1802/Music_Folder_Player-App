package com.example.music_folder_player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class FileListActivity extends AppCompatActivity {
    ArrayList<AudioModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        Button btn_exit = findViewById(R.id.btn_exit);
        Button btn_play_all = findViewById(R.id.btn_play_all_music);
        Button btn_up = findViewById(R.id.btn_up);

        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        btn_play_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songsList.size()==0){
                    Toast.makeText(FileListActivity.this, "Không có nhạc để phát",Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        //navigate to another activity
                        MyMediaPlayer.getInstance().reset();
                        MyMediaPlayer.currentIndex = 0;
                        Intent intent = new Intent(getApplicationContext(),MusicPlayerActivity.class);
                        intent.putExtra("LIST",songsList);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(FileListActivity.this,"Cannot open the file",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView noFilesText = findViewById(R.id.nofiles_textview);
        TextView pathText = findViewById(R.id.path_current);

//      set up files and folders
        String path = getIntent().getStringExtra("path");
        pathText.setText(path);
        File root = new File(path);
        File[] filesAndFolders = root.listFiles();

        if (filesAndFolders != null) {
            Arrays.sort(filesAndFolders, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
            });
        }

        if(filesAndFolders==null || filesAndFolders.length ==0){
            noFilesText.setVisibility(View.VISIBLE);
            return;
        }

        noFilesText.setVisibility(View.INVISIBLE);


//      set up music
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
//                MediaStore.Audio.Albums.ALBUM_ART
        };

        String selection = MediaStore.Audio.Media.DATA + " like ? AND " + MediaStore.Audio.Media.MIME_TYPE + "=?";
        String[] selectionArgs = new String[]{path + "%","audio/mpeg"};
        String sortOrder = MediaStore.Audio.Media.TITLE.toLowerCase() + " ASC";
//                  Toast.makeText(getApplicationContext(), "Path: " + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Toast.LENGTH_SHORT).show();
//                  Toast.makeText(getApplicationContext(), "Path2: " + uri, Toast.LENGTH_SHORT).show();

        Cursor cursor = null;
        try {
             cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,selectionArgs,sortOrder);

        }catch (Exception ex) {
            Toast.makeText(FileListActivity.this,ex.getMessage(),Toast.LENGTH_SHORT).show();

        }


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
//                    Toast.makeText(getApplicationContext(),"Title: " + title,Toast.LENGTH_SHORT).show();

                    String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));

                    String albumId = "";
                    try {
                        albumId  = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                    }catch (Exception ex) {
//                        Toast.makeText(FileListActivity.this,albumId,Toast.LENGTH_SHORT).show();

                    }
//                    Toast.makeText(getApplicationContext(),"Cannot open the file " + albumId,Toast.LENGTH_SHORT).show();

                    String albumArt = "";

                    if(!Objects.equals(albumId, "")) {
                        albumArt = getAlbumArt(Long.parseLong(albumId));
                    }
                    AudioModel songData = new AudioModel(filePath,title,duration,album,albumArt);

                    if(title.equals("tone")) {
                        continue;
                    };
//                    Toast.makeText(getApplicationContext(), "Album: " + album, Toast.LENGTH_SHORT).show();

                    songsList.add(songData);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),filesAndFolders,songsList));
    }
    private String getAlbumArt(long albumId) {
        String[] projection = {MediaStore.Audio.Albums.ALBUM_ART};
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Audio.Albums._ID + "=?",
                new String[]{String.valueOf(albumId)},
                null
        );

        String albumArt = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                albumArt = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
            }
            cursor.close();
        }

        return albumArt;
    }
}