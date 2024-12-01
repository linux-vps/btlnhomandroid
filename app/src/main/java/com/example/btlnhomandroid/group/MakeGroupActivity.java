package com.example.btlnhomandroid.group;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlnhomandroid.R;
import com.example.btlnhomandroid.objects.Group;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class MakeGroupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int groupId;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button copyLink;
    Button save;
    Button save1;
    List<String> addArray = new ArrayList<String>();
    EditText txt;
    ListView show;
    private EditText descEditText;
    int participantNmb = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Make a new group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_make_group);
        txt = (EditText) findViewById(R.id.ParticipantInput);
        descEditText = (EditText) findViewById(R.id.makeGroupDescEditText);

        Spinner mySpinner = findViewById(R.id.addExpensePayerSpinner);
        ArrayAdapter<CharSequence> myAdapter = ArrayAdapter.createFromResource(this, R.array.currency, android.R.layout.simple_spinner_item);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(this);

        show = (ListView) findViewById(R.id.participantsListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MakeGroupActivity.this, android.R.layout.simple_list_item_1, addArray);
        show.setAdapter(adapter);

        save = (Button) findViewById(R.id.addParticipantButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getInput = txt.getText().toString();

                if (addArray.contains(getInput)) {
                    Toast.makeText(getBaseContext(), "Participant already added ", Toast.LENGTH_LONG).show();
                } else if (getInput.equals("") || getInput.trim().equals("")) {
                    Toast.makeText(getBaseContext(), "Invalid Name ", Toast.LENGTH_LONG).show();
                } else if (participantNmb == 20) {
                    Toast.makeText(getBaseContext(), "Maximum participants number reached ", Toast.LENGTH_LONG).show();
                } else {
                    addArray.add(getInput);
                    adapter.notifyDataSetChanged();
                    ((EditText) findViewById(R.id.ParticipantInput)).setText(" ");

                }
            }
        });
        EditText groupName = (EditText) findViewById(R.id.groupName);

        save1 = (Button) findViewById(R.id.makeANewGroupButton);
        save1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = groupName.getText().toString();
                String desc = descEditText.getText().toString();
                Group.Currency currency = Group.Currency.valueOf(mySpinner.getSelectedItem().toString());
                List<String> participants = addArray;

                Group group;

                if (desc.equals("")) {
                    group = new Group(name, currency, participants);
                } else {
                    group = new Group(name, desc, currency, participants);
                }

                try {
                    group.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                GroupsListActivity.addGroupId(group.getObjectId(), MakeGroupActivity.this);

                AlertDialog dialog = new AlertDialog.Builder(MakeGroupActivity.this).
                        setTitle("Group saved!").
                        setMessage("Group ID: " + group.getObjectId()).
                        setNeutralButton("Copy ID", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Group ID", group.getObjectId());
                                clipboard.setPrimaryClip(clip);

                                Context context = getApplicationContext();
                                Toast.makeText(context, "Group ID copied!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
        super.onBackPressed();
    }
}