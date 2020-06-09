package com.FredrikLohmann.MusicPlayer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.FredrikLohmann.MusicPlayer.HelperClasses.Song;
import com.FredrikLohmann.MusicPlayer.R;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    private Context context;
    private List<Song> songList;

    public SongAdapter(Context context, List<Song> songList){
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.song, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {
        ImageView thumbnail = holder.thumbnail;
        TextView title = holder.title;
        TextView artist = holder.artist;

        holder.itemView.setTag(position);

        thumbnail.setImageBitmap(songList.get(position).getThumbnail());
        title.setText(songList.get(position).getTitle());
        artist.setText(songList.get(position).getArtist());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView thumbnail;
        TextView title;
        TextView artist;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.titleTxt);
            artist = itemView.findViewById(R.id.artistTxt);
        }
    }
}
