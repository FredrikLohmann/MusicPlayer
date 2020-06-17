package com.FredrikLohmann.MusicPlayer.Fragments;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.FredrikLohmann.MusicPlayer.HelperClasses.Song;
import com.FredrikLohmann.MusicPlayer.MainActivity;
import com.FredrikLohmann.MusicPlayer.R;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MusicPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicPlayerFragment extends Fragment {

    private Song song;

    private Handler handler;
    private Handler loopHandler;

    private ImageButton backButton;
    private ImageView imageView;
    private TextView titleView;
    private TextView artistView;

    private TextView songDuration;
    private TextView currentDuration;

    private SeekBar seekBar;
    private CrystalRangeSeekbar rangeSeekBar;
    private Button playBtn;
    private Button speedBtn1;
    private Button speedBtn2;
    private Button speedBtn3;

    private OnFragmentClosedListener mListener;

    public MusicPlayerFragment(Song song) {
        this.song = song;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MusicPlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicPlayerFragment newInstance(Song song) {
        MusicPlayerFragment fragment = new MusicPlayerFragment(song);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);
        initComponents(view);
        initListeners();
        startProgressBarTracking();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentClosedListener) {
            mListener = (OnFragmentClosedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onFragmentClosed();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentClosedListener {
        // TODO: Update argument type and name
        void onFragmentClosed();
    }

    private void initComponents(View view) {
        backButton = view.findViewById(R.id.backBtn);

        imageView = view.findViewById(R.id.imageView);
        imageView.setImageBitmap(song.getThumbnail());

        titleView = view.findViewById(R.id.titleTxtPlayer);
        titleView.setText(song.getTitle());
        artistView = view.findViewById(R.id.artistTxtPlayer);
        artistView.setText(song.getArtist());

        songDuration = view.findViewById(R.id.songDuration);
        String str = getFormattedDuration(MainActivity.musicService.getDuration());
        songDuration.setText(str);
        currentDuration = view.findViewById(R.id.currentDuration);

        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setMax(MainActivity.musicService.getDuration());
        seekBar.setPadding(16,8,16,8);
        rangeSeekBar = view.findViewById(R.id.rangeSeekBar);
        rangeSeekBar.setLeftThumbColor(Color.argb(50,255,255,255));
        rangeSeekBar.setRightThumbColor(Color.argb(50,255,255,255));
        rangeSeekBar.setMaxValue(MainActivity.musicService.getDuration());
        rangeSeekBar.setEnabled(false);
        if(MainActivity.musicService.isLoopingPart()){
            rangeSeekBar.setMinValue(MainActivity.musicService.getMin());
            rangeSeekBar.setMaxValue(MainActivity.musicService.getMax());
            rangeSeekBar.setEnabled(true);
        }
        playBtn = view.findViewById(R.id.playBtn);
        speedBtn1 = view.findViewById(R.id.fiftyPercentBtn);
        speedBtn2 = view.findViewById(R.id.seventyfivePercentBtn);
        speedBtn3 = view.findViewById(R.id.hundredPercentBtn);
    }

    private void initListeners(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // En användare har ändrat på progressbaren
                if(fromUser)
                    MainActivity.musicService.seekTo(progress);

                String str = getFormattedDuration(progress);
                currentDuration.setText(str);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rangeSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                MainActivity.musicService.setMin((long) minValue);
                MainActivity.musicService.setMax((long) maxValue);
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MainActivity.musicService.isPlaying()){
                    MainActivity.musicService.start();
                    playBtn.setText("Pause");
                }
                else {
                    MainActivity.musicService.pause();
                    playBtn.setText("Play");
                }
            }
        });

        if (MainActivity.musicService.isPlaying()){
            playBtn.setText("Pause");
        }
        else if (!MainActivity.musicService.isPlaying()){
            playBtn.setText("Play");
        }

        speedBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!MainActivity.musicService.isLoopingPart()){
                    MainActivity.musicService.loopPart(true);
                    startLoopingPartTracking();
                    rangeSeekBar.setEnabled(true);

                }
                else{
                    MainActivity.musicService.loopPart(false);
                    rangeSeekBar.setEnabled(false);

                }
            }
        });

        speedBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.musicService.setPlaybackRate(0.75f);
            }
        });

        speedBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.musicService.setPlaybackRate(1.00f);
            }
        });
    }

    /**
     * Startar uppdateringen av progressbaren
     */
    private void startProgressBarTracking() {
        handler = new Handler();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(handler != null){
                    // Sätter progressbaren till den position som mediaspelaren har
                    seekBar.setProgress(MainActivity.musicService.getCurrentPosition());
                }
                // Uppdaterar progressbaren varje sekund
                if(handler != null)
                    handler.postDelayed(this, 1000);
            }
        });
    }

    private void startLoopingPartTracking(){
        loopHandler = new Handler();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Används för att kunna spela samma stycke gång efter gång
                if (MainActivity.musicService.isLoopingPart()) {
                    if (MainActivity.musicService.getCurrentPosition() > MainActivity.musicService.getMax()){
                        MainActivity.musicService.seekTo((int)MainActivity.musicService.getMin());
                    }
                    // Uppdaterar 10 gånger i sekunden
                    if(loopHandler != null)
                        loopHandler.postDelayed(this, 100);
                }
                else{
                    loopHandler = null;
                }
            }
        });

    }

    private String getFormattedDuration(long duration){
        Date date = new Date(duration);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());

        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        startProgressBarTracking();
    }
}
