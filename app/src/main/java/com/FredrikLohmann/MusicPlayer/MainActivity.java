package com.FredrikLohmann.MusicPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;

import com.FredrikLohmann.MusicPlayer.Fragments.MainFragment;
import com.FredrikLohmann.MusicPlayer.Fragments.MusicPlayerFragment;
import com.FredrikLohmann.MusicPlayer.HelperClasses.Song;
import com.FredrikLohmann.MusicPlayer.Services.MusicService;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MusicPlayerFragment.OnFragmentClosedListener {

    public static MusicService musicService;
    public static ArrayList<Song> songList;

    private Intent playIntent;
    private boolean musicBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, MainFragment.newInstance("",""),"MAIN_FRAGMENT").commit();

        songList = getSongList();
    }

    /**
     * Hämtar en lista med sånger från mappen Music i enhetens lagringsutrymme
     */
    private ArrayList<Song> getSongList(){
        ArrayList<Song> songs = new ArrayList<>();
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()){
            // Hämtar kolumner
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int pathColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            // Lägger till sånger till listan

            while (musicCursor.moveToNext()) {
                long songId = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String url = musicCursor.getString(pathColumn);

                // Tar endast filer från mappen Music
                if (url.contains("Music")) {

                    Uri trackUri = ContentUris.withAppendedId(
                            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            songId);
                    try {
                        Bitmap thumbnail = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            thumbnail = getContentResolver().loadThumbnail(trackUri, new Size(500,500), null);
                        }
                        songs.add(new Song(songId, title, artist, thumbnail));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Stänger ner pekaren
        if (musicCursor != null)
            musicCursor.close();
        return songs;
    }

    public void songPicked(View view){
        int position = Integer.parseInt(view.getTag().toString());
        if (openPlayingSongFragment(position)){
            // Spela låten
            musicService.setAndPlaySong(position);

        }
    }

    private boolean openPlayingSongFragment(int position){
        Fragment fragment = MusicPlayerFragment.newInstance(songList.get(position));
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Kontrollerar först att inte några fragments är öppna
        if (fragmentManager.getBackStackEntryCount() <= 0) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.pull_up_from_bottom, R.anim.drag_down_from_top, R.anim.pull_up_from_bottom, R.anim.drag_down_from_top);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.fragment_container, fragment, "MUSIC_PLAYER_FRAGMENT").commit();

            return true;
        }
        return false;
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
            musicService = binder.getService();
            musicService.setSongList(songList);
            musicService.setFragment((MainFragment) getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT"));

            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent == null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService=null;
        super.onDestroy();
    }

    public void miniPlayerPressed(View view) {
        if (MainActivity.musicService.hasPlayedSong()){
            openPlayingSongFragment(musicService.getCurrentSongPosition());
        }
        else {
            musicService.prepareSong();
            openPlayingSongFragment(musicService.getCurrentSongPosition());
        }
    }

    @Override
    public void onFragmentClosed() {
        MainFragment mf = (MainFragment) getSupportFragmentManager().findFragmentByTag("MAIN_FRAGMENT");
        mf.updateButtonsUi();
    }
}
