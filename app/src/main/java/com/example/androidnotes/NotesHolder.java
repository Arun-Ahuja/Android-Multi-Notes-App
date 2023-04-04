package com.example.androidnotes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotesHolder extends RecyclerView.ViewHolder {

    TextView titletxt, contenttxt,datetxt;

    public NotesHolder(@NonNull View itemView){
        super(itemView);

        titletxt = itemView.findViewById(R.id.noteTitle);
        contenttxt = itemView.findViewById(R.id.noteContent);
        datetxt = itemView.findViewById(R.id.editDate);

    }


}
