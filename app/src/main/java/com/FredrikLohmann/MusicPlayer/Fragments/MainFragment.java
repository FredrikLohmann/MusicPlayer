package com.FredrikLohmann.MusicPlayer.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.FredrikLohmann.MusicPlayer.Adapters.SongAdapter;
import com.FredrikLohmann.MusicPlayer.HelperClasses.Song;
import com.FredrikLohmann.MusicPlayer.MainActivity;
import com.FredrikLohmann.MusicPlayer.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private RecyclerView recyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SeekBar miniSeekbar;
    private ImageView miniImage;
    private TextView miniTitle;
    private TextView miniArtist;
    private Button miniBtn;

    private OnFragmentInteractionListener mListener;
    private Handler handler;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initComponents(view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        SongAdapter songAdapter = new SongAdapter(getContext(), MainActivity.songList);
        recyclerView.setAdapter(songAdapter);
        updateUi(0);
        return view;
    }

    private void initComponents(View view) {
        recyclerView = view.findViewById(R.id.songList);
        miniSeekbar = view.findViewById(R.id.miniSeekbar);
        miniSeekbar.setPadding(0, 0, 0, 0);
        miniImage = view.findViewById(R.id.miniImage);
        miniTitle = view.findViewById(R.id.miniTitle);
        miniArtist = view.findViewById(R.id.miniArtist);
        miniBtn = view.findViewById(R.id.miniBtn);
        miniSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!b){

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        miniBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MainActivity.musicService != null){
                    if (!MainActivity.musicService.hasPlayedSong()){
                        MainActivity.musicService.playSong();
                    }
                    else if (MainActivity.musicService.isPlaying()){
                        MainActivity.musicService.pause();
                        miniBtn.setText("Play");
                    }
                    else if (!MainActivity.musicService.isPlaying()){
                        MainActivity.musicService.start();
                        miniBtn.setText("Pause");
                    }

                }
            }
        });
        if(MainActivity.musicService != null && MainActivity.musicService.isPlaying()){
            miniBtn.setText("Pause");
        }
        else {
            miniBtn.setText("Play");
        }
    }

    public void updateButtonsUi(){
        if (MainActivity.musicService.isPlaying()){
            miniBtn.setText("Pause");
        }
        else if (!MainActivity.musicService.isPlaying()){
            miniBtn.setText("Play");
        }
    }

    private void updateUi(int position){
        miniImage.setImageBitmap(MainActivity.songList.get(position).getThumbnail());
        miniTitle.setText(MainActivity.songList.get(position).getTitle());
        miniArtist.setText(MainActivity.songList.get(position).getArtist());
    }

    public void updateCurrentSong(){
        miniSeekbar.setMax(MainActivity.musicService.getDuration());
        miniSeekbar.setProgress(MainActivity.musicService.getCurrentPosition());
        miniImage.setImageBitmap(MainActivity.musicService.getCurrentSong().getThumbnail());
        miniTitle.setText(MainActivity.musicService.getCurrentSong().getTitle());
        miniArtist.setText(MainActivity.musicService.getCurrentSong().getArtist());
        startProgressBarTracking();
    }

    private void startProgressBarTracking() {
        handler = new Handler();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(handler != null){
                    // Sätter progressbaren till den position som mediaspelaren har
                    miniSeekbar.setProgress(MainActivity.musicService.getCurrentPosition());
                }
                // Uppdaterar progressbaren varje sekund
                if(handler != null)
                    handler.postDelayed(this, 1000);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()
              //      + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void miniPlayerPressed(View view) {

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
