package com.example.music_folder_player;

import java.io.Serializable;

public class AudioModel implements Serializable {
    String path;
    String title;
    String duration;

    String album;
    String albumArt;

    public AudioModel(String path, String title, String duration,String album,String albumArt) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.album = album;
        this.albumArt = albumArt;

    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }
    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}