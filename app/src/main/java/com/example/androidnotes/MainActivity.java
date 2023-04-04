package com.example.androidnotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private final String TAG = getClass().getSimpleName();
    private final ArrayList<Notes> noteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Notes currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);

        notesAdapter = new NotesAdapter(this, noteList);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadDataFromFile();



        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
    }

    public void handleResult(ActivityResult result) {
        if (result == null || result.getData() == null) {
            Log.d(TAG, "handleResult: NULL ActivityResult received");
            return;
        }
        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK) {
            if (data.hasExtra("NEW_NOTE")) {
                Notes n = (Notes) data.getSerializableExtra("NEW_NOTE");
                noteList.add(n);
                Collections.sort(noteList);
                notesAdapter.notifyItemRangeChanged(0, noteList.size());
            } else if (data.hasExtra("EDIT_NOTE")) {
                Notes n = (Notes) data.getSerializableExtra("EDIT_NOTE");
                currentNote.setTitle(n.getTitle());
                currentNote.setDes(n.getDes());
                currentNote.setDate(n.getDate());
                Collections.sort(noteList);
                notesAdapter.notifyItemRangeChanged(0, noteList.size());
            }
        }
        setTitle(getString(R.string.app_name) + " [" + noteList.size() + "]");

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            Intent intent = new Intent(this, AddNote.class);
            activityResultLauncher.launch(intent);
        } else if (item.getItemId() == R.id.menu_info) {
            Intent intent = new Intent(this, AboutNote.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDataToFile();
    }

    private void saveDataToFile() {
        Log.d(TAG, "saveDataToFile: " + noteList.size());

        // Make JSONArray
        JSONArray jsonArray = new JSONArray();
        for (Notes n : noteList) {
            try {
                jsonArray.put(n.toJSON());
            } catch (JSONException e) {
                Log.d(TAG, "saveDataToFile: " + e.getMessage());
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput("JSONText.json", MODE_PRIVATE);
            PrintWriter pr = new PrintWriter(fos);
            pr.println(jsonArray);
            pr.close();
            fos.close();
        } catch (Exception e) {
            Log.d(TAG, "saveDataToFile: " + e.getMessage());
            e.printStackTrace();
        }
        Log.w(TAG, "saveDataToFile: " + jsonArray);
    }

    private void loadDataFromFile() {
        FileInputStream fis;
        try {
            fis = getApplicationContext().openFileInput("JSONText.json");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "loadDataFromFile: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        StringBuilder fileContent = new StringBuilder();

        try {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }
            JSONArray jsonArray = new JSONArray(fileContent.toString());
            Log.d(TAG, "readFromFile: ");
            for (int i = 0; i < jsonArray.length(); i++) {
                noteList.add(Notes.createFromJSON(jsonArray.getJSONObject(i)));
            }
        } catch (Exception e) {
            Log.d(TAG, "loadDataFromFile: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        Log.d(TAG, "loadDataFromFile: " + noteList.size());
        setTitle(getString(R.string.app_name) + " [" + noteList.size() + "]");
    }
    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        currentNote = noteList.get(pos);
        Intent intent = new Intent(this, AddNote.class);
        intent.putExtra("EDIT_NOTE", currentNote);
        activityResultLauncher.launch(intent);
    }
    @Override
    public boolean onLongClick(View view) {
        NotesHolder n = new NotesHolder(view);
        String str = n.titletxt.getText().toString();
        int pos = recyclerView.getChildLayoutPosition(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Yes", (dialog, which) ->  {
                noteList.remove(pos);
                notesAdapter.notifyItemRemoved(pos);
                setTitle(getString(R.string.app_name) + " [" + noteList.size() + "]");
                Toast.makeText(this, "Note "+str+" Deleted", Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
            }
        });
        builder.setTitle("Delete Note '"+ str+"'?");
       // builder.setMessage("Do you want to delete it ");
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        super.onBackPressed();
    }
}