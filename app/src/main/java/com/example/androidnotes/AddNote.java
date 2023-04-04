package com.example.androidnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class AddNote extends AppCompatActivity {
    private static final String TAG = "AddNote";
    private EditText title, des;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        title = findViewById(R.id.editTitle);
        des = findViewById(R.id.editContent);


        if (getIntent().hasExtra("EDIT_NOTE")) {
            Notes n = (Notes) getIntent().getSerializableExtra("EDIT_NOTE");
            title.setText(n.getTitle());
            des.setText(n.getDes());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String tlt = title.getText().toString();
        String con = des.getText().toString();
        if (item.getItemId() == R.id.addnote) {
            if (tlt.trim().isEmpty() & con.trim().isEmpty()){
                finish();
            }
            else if (tlt.trim().isEmpty()){
                builder.setPositiveButton("OK", (dialog, which) ->  {
                    finish();
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setTitle("Alert");
                builder.setMessage("Note will not save Without title");
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            else {
            Notes n = doDataReturn();
            if (n != null) {
                Intent intent = new Intent();
                if (getIntent().hasExtra("EDIT_NOTE"))
                    intent.putExtra("EDIT_NOTE", n);
                else
                    intent.putExtra("NEW_NOTE", n);
                setResult(RESULT_OK, intent);
            }
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Notes doDataReturn() {

        String titleStr = title.getText().toString();
        if (titleStr.trim().isEmpty()) {
            Log.d(TAG, "onOptionsItemSelected: Name Empty");
            return null;
        }
        String desStr = des.getText().toString();
        if (desStr.trim().isEmpty()) {
            Log.d(TAG, "onOptionsItemSelected: Description Empty");
            return null;
        }
        return new Notes(titleStr, desStr, System.currentTimeMillis());
    }



    @Override
    public void onBackPressed() {
        String txt1 = title.getText().toString();
        String txt2 = des.getText().toString();
        if(getIntent().hasExtra("EDIT_NOTE")){
            Notes note = (Notes) getIntent().getSerializableExtra("EDIT_NOTE");
            if(note.getTitle().equals(title.getText().toString())  & note.getDes().equals(des.getText().toString())  ){
                super.onBackPressed();
                finish();
            }
        }
        if (txt1.trim().isEmpty() & txt2.trim().isEmpty()){
            super.onBackPressed();
        }

        if (txt1.trim().isEmpty() & !txt2.trim().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Ok", (dialog, id) -> {
                super.onBackPressed();
            });
            builder.setNegativeButton("Cancel", (dialog, id) ->  {
               /* @Override
                public void onClick(DialogInterface dialog, int which) {
                }*/
            });
            builder.setTitle("Alert");
            builder.setMessage("Note will not save Without title");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("YES", (dialog, id) -> {
            Notes n = doDataReturn();
                if (n != null) {
                    Intent intent = new Intent();
                    if (getIntent().hasExtra("EDIT_NOTE"))
                        intent.putExtra("EDIT_NOTE", n);
                    else
                        intent.putExtra("NEW_NOTE", n);
                    setResult(RESULT_OK, intent);
                }
            super.onBackPressed();
        });
            builder.setNegativeButton("NO", (dialog, id) -> super.onBackPressed());
            builder.setTitle("Your note is not saved!");
            builder.setMessage("Save note '"+ title.getText().toString()+"'?");
            AlertDialog dialog = builder.create();
            dialog.show();
    }
    }
}