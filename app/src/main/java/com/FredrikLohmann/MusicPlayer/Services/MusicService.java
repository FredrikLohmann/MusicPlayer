package com.FredrikLohmann.MusicPlayer.Services;

import android.app.Activity;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;

import com.FredrikLohmann.MusicPlayer.Fragments.MainFragment;
import com.FredrikLohmann.MusicPlayer.HelperClasses.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    private final IBinder musicBinder = new MusicBinder();
    private MainFragment mainFragment;
    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songList;
    private int songPosition;
    private boolean hasPlayedSong;
    private boolean repeatSong;
    private boolean loopingPart;
    private long min;
    private long max;

    @Override
    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
        hasPlayedSong = false;
        repeatSong = false;
        loopingPart = false;
    }

    private void initMusicPlayer(){
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public void setSongList(ArrayList<Song> songList){
        this.songList = songList;
    }

    public void setFragment (MainFragment fragment){
        this.mainFragment = fragment;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    public void playSong(){
        hasPlayedSong = true;
        mediaPlayer.reset();

        Song playSong = songList.get(songPosition);
        long currentSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);

        try{
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    public void prepareSong(){
        Song playSong = songList.get(songPosition);
        long currentSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);

        try{
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAndPlaySong(int position){
        songPosition = position;
        playSong();
    }

    public void seekTo(int msec){
        mediaPlayer.seekTo(msec);
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void start(){
        mediaPlayer.start();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void setPlaybackRate(float rate){
        mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(rate));
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public Song getCurrentSong(){
        return songList.get(songPosition);
    }

    public int getCurrentSongPosition() {
        return songPosition;
    }

    public boolean hasPlayedSong(){
        return hasPlayedSong;
    }

    public void repeatSong(boolean b){
        repeatSong = b;
    }

    public boolean isRepeatingSong(){
        return repeatSong;
    }

    public void loopPart(boolean b){
        loopingPart = b;
    }

    public boolean isLoopingPart(){
        return loopingPart;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min){
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max){
        this.max = max;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (repeatSong){
            mediaPlayer.start();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        // Uppdatera mini spelaren
        mainFragment.updateCurrentSong();
    }


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}
