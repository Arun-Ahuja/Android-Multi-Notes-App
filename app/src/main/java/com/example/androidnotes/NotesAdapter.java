package com.example.androidnotes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends RecyclerView.Adapter<NotesHolder> {

    private final String TAG = getClass().getSimpleName();
    private final MainActivity mainActivity;
    private final ArrayList<Notes> noteList;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("EEE MMM d, h:mm a", Locale.getDefault());

    public NotesAdapter(MainActivity mainActivity, ArrayList<Notes> noteList) {
        this.mainActivity = mainActivity;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_entry, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new NotesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        Notes note = noteList.get(position);
        holder.titletxt.setText(note.getTitle());

        String full = note.getDes();
        if (full.length() > 80){
            String trime = full.substring(0,79);
            trime += "...";
            holder.contenttxt.setText(trime);
        }
        else {holder.contenttxt.setText(note.getDes());}
        holder.datetxt.setText(sdf.format(new Date(note.getDate())));
    }
    @Override
    public int getItemCount() {return noteList.size();}
}
