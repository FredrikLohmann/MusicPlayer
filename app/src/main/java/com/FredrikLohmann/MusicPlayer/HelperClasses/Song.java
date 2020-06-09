package com.FredrikLohmann.MusicPlayer.HelperClasses;

import android.graphics.Bitmap;

public class Song {

    private long id;
    private String title;
    private String artist;
    private Bitmap thumbnail;

    public Song(long id, String title, String artist, Bitmap thumbnail){
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.thumbnail = thumbnail;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
