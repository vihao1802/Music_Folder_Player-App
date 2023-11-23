package com.example.music_folder_player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.squareup.picasso.Picasso;


import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    Context context;
    File[] filesAndFolders;
    ArrayList<AudioModel> songsList;
    int countSongIndex = 0;
    public MyAdapter(Context context, File[] filesAndFolders,ArrayList<AudioModel> songsList) {
        this.context = context;
        this.songsList = songsList;
        this.filesAndFolders = filesAndFolders;
    }


    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //set up for files and folder list
        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());
        if(selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        } else if (selectedFile.isFile() && (selectedFile.getName().toLowerCase().endsWith(".mp3") || selectedFile.getName().toLowerCase().endsWith(".mp4"))) {
//            holder.imageView.setImageResource(R.drawable.baseline_music_video_24);

            AudioModel songData = songsList.get(countSongIndex);

            byte[] image;
            try {
                image = getAlbumArt(songData.getPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (image != null) {
                Glide.with(context).asBitmap().load(image).into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.baseline_music_video_24);
            }
            if(songData.album != null) {
                holder.albumText.setText(songData.album);
            } else {
                holder.albumText.setVisibility(View.INVISIBLE);
            }
            countSongIndex++;
        } else if (selectedFile.isFile() && selectedFile.getName().toLowerCase().endsWith(".ogg")) {
            holder.imageView.setImageResource(R.drawable.baseline_music_note_24);
        }else if (selectedFile.isFile() && selectedFile.getName().toLowerCase().endsWith(".jpg") || selectedFile.isFile() && selectedFile.getName().toLowerCase().endsWith(".png")) {
            holder.imageView.setImageResource(R.drawable.baseline_photo_24);
        }
        else{
            holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        }

        //set up for music list



//        Toast.makeText(context.getApplicationContext(),"index" + position,Toast.LENGTH_SHORT).show();





        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedFile.isDirectory()){
                    Intent intent = new Intent(context, FileListActivity.class);
                    String path = selectedFile.getAbsolutePath();
                    intent.putExtra("path",path);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else if (selectedFile.isFile() && (selectedFile.getName().toLowerCase().endsWith(".mp3") || selectedFile.getName().toLowerCase().endsWith(".mp4") || selectedFile.getName().toLowerCase().endsWith(".ogg"))) {
                    try {
                        int indexSong = 0;
                        int count=0;

                        for (AudioModel item: songsList ) {
                            if(selectedFile.getName().replace(".mp3","").equalsIgnoreCase(item.title.replace(".mp3",""))) {
                                indexSong = count;
                                break;
                            }
                            count++;
                        }
                        //navigate to another activity
//                        AudioModel songData = songsList.get( indexSong);
//                        holder.titleTextView.setText(songData.getTitle());
//                        if(MyMediaPlayer.currentIndex== indexSong){
//                            holder.titleTextView.setTextColor(Color.parseColor("#FF0000"));
//                        }else{
//                            holder.titleTextView.setTextColor(Color.parseColor("#000000"));
//                        }

                        MyMediaPlayer.getInstance().reset();
                        MyMediaPlayer.currentIndex = indexSong;
                        Intent intent = new Intent(context,MusicPlayerActivity.class);
                        intent.putExtra("LIST",songsList);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                        context.startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(context.getApplicationContext(),"Cannot open the file",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    //open the file
                    try {
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        String type = "image/*";
                        intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(context.getApplicationContext(),"Cannot open the file",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {

//                PopupMenu popupMenu = new PopupMenu(context,v);
//                popupMenu.getMenu().add("DELETE");
//                popupMenu.getMenu().add("MOVE");
//                popupMenu.getMenu().add("RENAME");
//
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        if(item.getTitle().equals("DELETE")){
//                            boolean deleted = selectedFile.delete();
//                            if(deleted){
//                                Toast.makeText(context.getApplicationContext(),"DELETED ",Toast.LENGTH_SHORT).show();
//                                v.setVisibility(View.GONE);
//                            }
//                        }
//                        if(item.getTitle().equals("MOVE")){
//                            Toast.makeText(context.getApplicationContext(),"MOVED ",Toast.LENGTH_SHORT).show();
//
//                        }
//                        if(item.getTitle().equals("RENAME")){
//                            Toast.makeText(context.getApplicationContext(),"RENAME ",Toast.LENGTH_SHORT).show();
//
//                        }
//                        return true;
//                    }
//                });

//                popupMenu.show();
//                return true;
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return filesAndFolders.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView,titleTextView,albumText;
        ImageView imageView,iconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            albumText = itemView.findViewById(R.id.albumName);
            textView = itemView.findViewById(R.id.file_name_text_view);
            imageView = itemView.findViewById(R.id.icon_view);
            titleTextView = itemView.findViewById(R.id.song_title);
            iconImageView = itemView.findViewById(R.id.music_icon_big);
        }
    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

}
