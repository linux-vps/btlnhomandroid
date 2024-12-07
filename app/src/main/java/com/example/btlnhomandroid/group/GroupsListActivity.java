package com.example.btlnhomandroid.group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlnhomandroid.R;
import com.example.btlnhomandroid.objects.Group;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GroupsListActivity extends AppCompatActivity {
    public static final int EXPENSE_REQUEST_CODE = 100;
    public static final int MAKE_GROUP_REQUEST_CODE = 101;
    public static final int EDIT_GROUP_REQUEST_CODE = 102;

    private static final String GROUPS_FILE_PATH = "groups.txt";
    private static ArrayList<String> groupIds = new ArrayList<String>();
    private List<Group> groupsList = new ArrayList<Group>();
    private GroupsListAdapter groupsListAdapter;

    static ArrayList<String> groupNames = new ArrayList<>();
    public static ArrayAdapter<String> arrayAdapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button addGroupButton;
    private EditText link;
    private Button joinButtonPopup;

    private SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);

        getSupportActionBar().setTitle("Groups");

        setUpGroupIds();

        setUpGroupsList();

        addGroupButton = findViewById(R.id.addGroupButton);
        addGroupButton.setOnClickListener(v -> {
            AddGroupDialog addGroupDialog = new AddGroupDialog(GroupsListActivity.this);
            addGroupDialog.show();
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.join_group_menu, menu);
    }

    private void setUpGroupsList() {
        if (groupIds.size() > 0) {
            ParseQuery<Group> query = ParseQuery.getQuery("Group");
            query.whereContainedIn(Group.KEY_OBJECT_ID, groupIds);
            query.findInBackground((groupsList, e) -> {
                if (e == null) {
                    GroupsListActivity.this.groupsList = groupsList;
                    int groupsListSize = GroupsListActivity.this.groupsList.size();

                    for (int i = 0; i < groupsListSize; i++) {
                        groupNames.add(groupsList.get(i).getName());
                    }
                } else {
                    Log.d("Groups List", "Error: " + e.getMessage());
                }
                setUpRecyclerView(GroupsListActivity.this.groupsList);
            });
        } else {
            setUpRecyclerView(GroupsListActivity.this.groupsList);
        }
    }

    /**
     * Sets up the groups list recycler view.
     */
    private void setUpRecyclerView(List<Group> groups) {
        // set up recycler view
        RecyclerView recyclerView = findViewById(R.id.groupsListRecyclerView);

        groupsListAdapter = new GroupsListAdapter(this, groups);
        Log.i("Groups List", "Adapter initialised!");
        recyclerView.setAdapter(groupsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Sets up the Groups ID array list.
     */
    private void setUpGroupIds() {
        prefs = getSharedPreferences(getApplicationContext().getPackageName(), MODE_PRIVATE);

        File groupIdsFile = new File(getFilesDir() + "/" + GROUPS_FILE_PATH);
        if (groupIdsFile.exists()) {
            groupIds = readGroupIds(this);
        } else {
            if (prefs.getBoolean("firstRun", true)) {
                prefs.edit().putBoolean("firstRun", false).apply();
                List<String> sampleParticipants = new ArrayList<String>();
                sampleParticipants.add("James");
                sampleParticipants.add("Mary");
                sampleParticipants.add("Robert");
                Group sampleGroup = new Group("Sample Group", "This is a sample group.", Group.Currency.KRW, sampleParticipants);
                try {
                    sampleGroup.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                groupIds.add(sampleGroup.getObjectId());
                writeGroupIds(groupIds, this);
            } else {
                writeGroupIds(groupIds, this);
            }
        }
    }

    /**
     * Write group IDs to the groups.txt file.
     *
     * @param groupIds Group IDs as a list of strings.
     * @param context
     */
    private static void writeGroupIds(ArrayList<String> groupIds, Context context) {
        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(GROUPS_FILE_PATH, MODE_PRIVATE);

            StringBuilder data = new StringBuilder();

            for (String s : groupIds) {
                data.append(s);
                data.append("\n");
            }

            fos.write(data.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Reads group IDs from the groups.txt file.
     *
     * @param context
     * @return Group IDs as a list of strings.
     */
    private static ArrayList<String> readGroupIds(Context context) {
        ArrayList<String> groupIds = new ArrayList<String>();

        try {
            InputStream inputStream = context.openFileInput(GROUPS_FILE_PATH);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ((receiveString = bufferedReader.readLine()) != null) {
                    groupIds.add(receiveString);
                }

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(context.toString(), "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(context.toString(), "Cannot read file: " + e.toString());
        }

        return groupIds;
    }

    public static void addGroupId(String groupId, Context context) {
        groupIds.add(groupId);
        writeGroupIds(groupIds, context);
    }

    public static void removeGroupId(String groupId, Context context) {
        groupIds.remove(groupIds.indexOf(groupId));
        writeGroupIds(groupIds, context);
    }

    public void updateGroupsList(Group newGroup) {
        groupsList.add(newGroup);
        addGroupId(newGroup.getObjectId(), this);
        groupsListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MAKE_GROUP_REQUEST_CODE
                || requestCode == EDIT_GROUP_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    setUpGroupsList();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}