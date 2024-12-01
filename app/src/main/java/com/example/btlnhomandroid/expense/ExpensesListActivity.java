package com.example.btlnhomandroid.expense;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlnhomandroid.group.GroupsListActivity;
import com.example.btlnhomandroid.Nanuda;
import com.example.btlnhomandroid.R;
import com.example.btlnhomandroid.balances.BalancesActivity;
import com.example.btlnhomandroid.objects.Expense;
import com.example.btlnhomandroid.objects.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpensesListActivity extends AppCompatActivity {

    public static final int MAKE_EXPENSE_REQUEST_CODE = 200;
    public static final int EDIT_EXPENSE_REQUEST_CODE = 201;
    public static final int BALANCES_REQUEST_CODE = 202;

    private Group group = null;
    private ArrayList<Expense> expenses = null;
    private RecyclerView recyclerView;
    private ExpensesListAdapter expensesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_lists);

        setUpActivity(getIntent());
    }

    /**
     * Sets up the expenses list activity.
     * @param intent    Intent received.
     */
    private void setUpActivity(Intent intent) {
        group = intent.getParcelableExtra(Nanuda.EXTRA_GROUP);

        // set up action bar
        getSupportActionBar().setTitle(group.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set up On Tab Selected Listener on the "Balances" tab to go to Balances Activity
        TabLayout tabLayout = (TabLayout) findViewById(R.id.expensesTabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1:
                        Intent intent = setUpNextIntent(BalancesActivity.class);
                        startActivityForResult(intent, BALANCES_REQUEST_CODE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        // set up On Click Listener on the Add Expense floating action button to go to Make Expense Activity
        FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.addExpenseButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = setUpNextIntent(MakeExpenseActivity.class);
                startActivityForResult(intent, MAKE_EXPENSE_REQUEST_CODE);
            }
        });

        setUpExpensesList();
    }

    /**
     * Sets up the expenses list.
     */
    private void setUpExpensesList() {
        ParseQuery<Expense> query = ParseQuery.getQuery("Expense");
        query.whereEqualTo("group", group);
        query.findInBackground(new FindCallback<Expense>() {
            public void done(List<Expense> expensesList, ParseException e) {
                if (e == null) {
                    expenses = (ArrayList<Expense>) expensesList;
                    Collections.sort(expenses, Collections.reverseOrder());
                    setUpRecyclerView();
                } else {
                    Log.d("Expenses List", "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Sets up the expenses list recycler view.
     */
    private void setUpRecyclerView() {
        // set up recycler view
        recyclerView = findViewById(R.id.expenses_recycler);

        expensesListAdapter = new ExpensesListAdapter( this, expenses, group);
        recyclerView.setAdapter(expensesListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Sets up the intent to go back to the parent activity.
     * @return  Set up intent.
     */
    private Intent setUpBackIntent() {
        return setUpBackIntent(false);
    }

    /**
     * Sets up the intent to go back to the parent activity.
     * @param toParentActivity  True if intent has to specify target activity, otherwise false.
     * @return                  Set up intent.
     */
    private Intent setUpBackIntent(boolean toParentActivity) {
        Intent intent;
        if (toParentActivity) {
            intent = new Intent(this, GroupsListActivity.class);
        } else {
            intent = new Intent();
        }

        intent.putExtra(Nanuda.EXTRA_GROUP, group);

        return intent;
    }

    /**
     * Sets up the intent to go to a following activity.
     * @param targetActivity    Following activity to start.
     * @return                  Set up intent.
     */
    private Intent setUpNextIntent(Class targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.putExtra(Nanuda.EXTRA_GROUP, group);
        intent.putParcelableArrayListExtra(Nanuda.EXTRA_EXPENSES, expenses);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MAKE_EXPENSE_REQUEST_CODE
                || requestCode == EDIT_EXPENSE_REQUEST_CODE
                || requestCode == BALANCES_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    group = data.getParcelableExtra(Nanuda.EXTRA_GROUP);
                    expenses = data.getParcelableArrayListExtra(Nanuda.EXTRA_EXPENSES);
                    break;
            }
        }

        if (requestCode == MAKE_EXPENSE_REQUEST_CODE
                || requestCode == EDIT_EXPENSE_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    setUpExpensesList();
                    expensesListAdapter.notifyDataSetChanged();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = setUpBackIntent(true);
            setResult(RESULT_OK, intent);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = setUpBackIntent(true);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}