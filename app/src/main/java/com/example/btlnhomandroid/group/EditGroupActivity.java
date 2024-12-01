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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlnhomandroid.Nanuda;
import com.example.btlnhomandroid.R;
import com.example.btlnhomandroid.objects.Group;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class EditGroupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int groupId;
    Button save;

    List<String> addArray = new ArrayList<String>();
    EditText txt;
    ListView show;
    int participantNmb = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        getSupportActionBar().setTitle("Edit Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Group currGroup = intent.getParcelableExtra(Nanuda.EXTRA_GROUP);

        EditText nameEditText = (EditText) findViewById(R.id.groupName);
        nameEditText.setText(currGroup.getName());

        EditText descEditText = (EditText) findViewById(R.id.editGroupDescEditText);

        if (currGroup.getDesc() != null) {
            descEditText.setText(currGroup.getDesc());
        }

        Spinner currencySpinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> currencyArrayAdapter = ArrayAdapter.createFromResource(this, R.array.currency, android.R.layout.simple_spinner_item);
        currencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyArrayAdapter);
        currencySpinner.setSelection(currencyArrayAdapter.getPosition(currGroup.getCurrency().toString()));

        txt = (EditText) findViewById(R.id.ParticipantInput);
        show = (ListView) findViewById(R.id.participantsListView);
        save = (Button) findViewById(R.id.addParticipantButton);

        addArray = currGroup.getParticipants();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditGroupActivity.this, android.R.layout.simple_list_item_1, addArray);
        show.setAdapter(adapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getInput = txt.getText().toString();

                if (addArray.contains(getInput)) {
                    Toast.makeText(getBaseContext(), "Participant already added ", Toast.LENGTH_LONG).show();
                } else if (getInput == null || getInput.trim().equals("")) {
                    Toast.makeText(getBaseContext(), "Invalid Name ", Toast.LENGTH_LONG).show();
                } else if (participantNmb == 20) {
                    Toast.makeText(getBaseContext(), "Maximum participants number reached ", Toast.LENGTH_LONG).show();
                } else {
                    addArray.add(getInput);
                    adapter.notifyDataSetChanged();
                    ((EditText) findViewById(R.id.ParticipantInput)).setText("");
                }
            }
        });

        TextView editGroupIdTextView = (TextView) findViewById(R.id.editGroupIdTextView);
        editGroupIdTextView.setText(currGroup.getObjectId());

        Button editGroupCopyIdButton = (Button) findViewById(R.id.editGroupCopyIdButton);
        editGroupCopyIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Group ID", currGroup.getObjectId());
                clipboard.setPrimaryClip(clip);

                Context context = getApplicationContext();
                Toast.makeText(context, "Group ID copied!", Toast.LENGTH_SHORT).show();
            }
        });

        Button saveChangesButton = (Button) findViewById(R.id.editGroupSaveChangesButton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String desc = descEditText.getText().toString();
                Group.Currency currency = Group.Currency.valueOf(currencySpinner.getSelectedItem().toString());
                List<String> participants = addArray;

                currGroup.setName(name);
                currGroup.setDesc(desc);
                currGroup.setCurrency(currency);
                currGroup.setParticipants(participants);

                try {
                    currGroup.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Button deleteButton = (Button) findViewById(R.id.editGroupDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditGroupActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this group?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GroupsListActivity.removeGroupId(currGroup.getObjectId(), getApplicationContext());
                                currGroup.deleteInBackground();

                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
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