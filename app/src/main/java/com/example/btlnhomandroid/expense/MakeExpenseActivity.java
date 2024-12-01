package com.example.btlnhomandroid.expense;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btlnhomandroid.Nanuda;
import com.example.btlnhomandroid.R;
import com.example.btlnhomandroid.objects.Expense;
import com.example.btlnhomandroid.objects.Group;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Handles behaviour for the Make Expense Activity.
 */
public class MakeExpenseActivity extends AppCompatActivity {

    private List<PayeeListItemHolder> payeeListItemHolders;
    private Group group;
    private ArrayList<Expense> expenses;

    private int checkedCheckBoxes = 0;
    private int manuallySetCheckedCheckedBoxes = 0;
    private long totalDebt = 0;
    private long manuallySetTotalDebt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_expense);

        // set up Action Bar
        getSupportActionBar().setTitle("Add Expense");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpActivity(getIntent());
    }

    /**
     * Sets up the Activity's UI and functionality.
     * @param intent    The intent this Activity received.
     */
    private void setUpActivity(Intent intent) {
        // get group data
        group = intent.getParcelableExtra(Nanuda.EXTRA_GROUP);
        expenses = intent.getParcelableArrayListExtra(Nanuda.EXTRA_EXPENSES);

        // sets up On Focus Change Listener on the amount input
        EditText amountEditText = (EditText) findViewById(R.id.addExpenseAmountEditText);
        amountEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // if no longer in focus
                if (!hasFocus) {
                    // if user inputted something i.e. not empty
                    if (!amountEditText.getText().toString().equalsIgnoreCase("")) {
                        // process input
                        StringBuilder sb = new StringBuilder(amountEditText.getText());

                        int dotIndex = sb.indexOf(".");

                        if (dotIndex > -1) {
                            sb.deleteCharAt(dotIndex);
                        } else {
                            sb.append("00");
                        }

                        totalDebt = Long.parseLong(sb.toString());
                        // reset all payee data when user re-inputs amount
                        manuallySetCheckedCheckedBoxes = 0;
                        manuallySetTotalDebt = 0;
                        calculateDebtDistribution(true);
                    }
                }
            }
        });

        // set up default date (today)
        EditText dateEditText = (EditText) findViewById(R.id.addExpenseDateEditText);
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        String date = simpleDateFormat.format(new Date());
        dateEditText.setText(date);

        // sets up the currency label
        TextView currencyTextView = (TextView) findViewById(R.id.addExpenseCurrencyLabel);
        currencyTextView.setText(group.getCurrency().toString());

        // sets up the payer spinner
        Spinner payerSpinner = (Spinner) findViewById(R.id.addExpensePayerSpinner);

        List<String> participants = group.getParticipants();
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, participants);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payerSpinner.setAdapter(spinnerArrayAdapter);

        // sets up the scroll view
        payeeListItemHolders = new ArrayList<PayeeListItemHolder>();

        LinearLayout payeeListLinearLayout = (LinearLayout) findViewById(R.id.payeeListLinearLayout);

        int participantsSize = participants.size();

        /*
        Payee List Item Structure:
        - Linear Layout
        -- CheckBox
        -- Linear Layout
        ---- EditText
         */
        for (int i = 0; i < participantsSize; i++) {
            LinearLayout payeeListItemLinearLayout = new LinearLayout(this);

            payeeListItemLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            payeeListItemLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            checkBox.setText(participants.get(i));
            checkBox.setChecked(true);
            checkedCheckBoxes++;

            LinearLayout paidForAmountEditTextLinearLayout = new LinearLayout(this);
            paidForAmountEditTextLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            paidForAmountEditTextLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            paidForAmountEditTextLinearLayout.setWeightSum(2f);
            paidForAmountEditTextLinearLayout.setGravity(Gravity.END);

            EditText paidForAmountEditText = new EditText(this);
            paidForAmountEditText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.25f));
            paidForAmountEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

            paidForAmountEditTextLinearLayout.addView(paidForAmountEditText);

            payeeListItemLinearLayout.addView(checkBox);
            payeeListItemLinearLayout.addView(paidForAmountEditTextLinearLayout);

            payeeListLinearLayout.addView(payeeListItemLinearLayout);

            payeeListItemHolders.add(new PayeeListItemHolder(checkBox, paidForAmountEditText));
        }

        calculateDebtDistribution();
    }

    /**
     * Creates a new Expense and saves it in the database.
     * @param view
     */
    public void createNewExpense(View view) {
        EditText newExpenseNameInput = (EditText) findViewById(R.id.newExpenseName);
        EditText newDateInput = (EditText) findViewById(R.id.addExpenseDateEditText);
        Spinner newWhoPaidSpinner = (Spinner) findViewById(R.id.addExpensePayerSpinner);

        String newExpenseName = newExpenseNameInput.getText().toString();
        Long newAmount = totalDebt;
        Date newDate = null;
        try {
            newDate = new SimpleDateFormat("dd/MM/yyyy").parse(newDateInput.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newWhoPaid = newWhoPaidSpinner.getSelectedItem().toString();
        List<String> newPayees = new ArrayList<String>();

        int payeeListItemHoldersSize = payeeListItemHolders.size();
        List<Long> newOwedAmounts = new ArrayList<Long>();

        for (int i = 0; i < payeeListItemHoldersSize; i++) {
            if (payeeListItemHolders.get(i).getCheckBox().isChecked()) {
                newPayees.add(payeeListItemHolders.get(i).getCheckBox().getText().toString());
                newOwedAmounts.add(payeeListItemHolders.get(i).getDebt());
            }
        }

        Expense newExpense = new Expense(newExpenseName, newAmount, newDate, newWhoPaid, newPayees, newOwedAmounts, group);

        try {
            newExpense.save();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        // finish activity
        Intent intent = setUpBackIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Calculates debt distribution and resets all manually set data.
     */
    private void calculateDebtDistribution() {
        calculateDebtDistribution(false);
    }

    /**
     * Calculates debt distribution
     * @param resetManuallySetData  If true all manually set data is reset, otherwise not.
     */
    private void calculateDebtDistribution(boolean resetManuallySetData) {
        // calculate debt distribution
        double totalDebt = ((double) (this.totalDebt - manuallySetTotalDebt)) / 100.0;
        int payeeNum = checkedCheckBoxes - manuallySetCheckedCheckedBoxes;
        double debtPerPayee = totalDebt / payeeNum;

        int payeeListItemHoldersSize = payeeListItemHolders.size();

        PayeeListItemHolder tempPayeeListItemHolder;

        // set debt
        for (int i = 0; i < payeeListItemHoldersSize; i++) {
            tempPayeeListItemHolder = payeeListItemHolders.get(i);

            // if participant is a payee and debt is not manually set
            if (tempPayeeListItemHolder.getCheckBox().isChecked() && !tempPayeeListItemHolder.hasBeenSetManually()) {
                tempPayeeListItemHolder.setDebt(debtPerPayee);
            // if participant is a payee and debt is manually set
            } else if (tempPayeeListItemHolder.getCheckBox().isChecked() && tempPayeeListItemHolder.hasBeenSetManually()) {
                // reset all data if true
                if (resetManuallySetData) {
                    tempPayeeListItemHolder.setDebt(debtPerPayee);
                    tempPayeeListItemHolder.setHasBeenSetManually(false);
                    tempPayeeListItemHolder.setHasBeenAddedToTotal(false);
                // otherwise skip
                } else {
                    continue;
                }
            // if participant is not a payee then set to 0
            } else {
                tempPayeeListItemHolder.setDebt(0);
            }
        }
    }

    /**
     * Class to hold Payee List Items and its operations.
     */
    private class PayeeListItemHolder {
        private final CheckBox checkBox;
        private final EditText editText;
        private long debt;
        private long prevDebt;
        private boolean hasBeenSetManually;
        private boolean hasBeenAddedToTotal;

        public PayeeListItemHolder(CheckBox checkBox, EditText editText) {
            this.checkBox = checkBox;
            this.editText = editText;
            this.hasBeenSetManually = false;
            this.hasBeenAddedToTotal = false;

            // set On Checked Change Listener to checkbox
            this.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedCheckBoxes++;
                    } else {
                        checkedCheckBoxes--;
                        // if checkbox had been set manually, reset everything
                        if (hasBeenSetManually) {
                            hasBeenSetManually = false;
                            hasBeenAddedToTotal = false;
                            manuallySetTotalDebt -= debt;
                            manuallySetCheckedCheckedBoxes--;
                        }
                    }
                    calculateDebtDistribution();
                }
            });

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // if set by user tag is null, otherwise it has value "temp"
                    if (editText.getTag() == null) {
                        hasBeenSetManually = true;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            };

            this.editText.addTextChangedListener(textWatcher);

            // set On Focus Change Listener to the edit text
            this.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // if no longer focused and was set manually by user
                    if (!hasFocus && hasBeenSetManually) {
                        // process input
                        StringBuilder sb = new StringBuilder(editText.getText());

                        int dotIndex = sb.length() - 3;

                        if (sb.charAt(dotIndex) == '.') {
                            sb.deleteCharAt(dotIndex);
                        } else {
                            sb.append("00");
                        }

                        long newDebt = Long.parseLong(sb.toString());
                        // if condition to check if it's a new value and does needs recalculation
                        if (newDebt != debt) {
                            prevDebt = debt;
                            debt = newDebt;

                            // use tag "temp" to signal that this was set programmatically and not trigger On Checked Change Listener
                            editText.setTag("temp");
                            DecimalFormat df = new DecimalFormat("0.00");
                            editText.setText(df.format((double) newDebt / 100));
                            editText.setTag(null);

                            // if this checkbox's value has been added to the total manually set debt before
                            if (hasBeenSetManually && hasBeenAddedToTotal) {
                                // subtract it first
                                manuallySetTotalDebt -= prevDebt;
                            } else {
                                manuallySetCheckedCheckedBoxes++;
                                hasBeenAddedToTotal = true;
                            }

                            manuallySetTotalDebt += newDebt;
                            calculateDebtDistribution();
                        }
                    }
                }
            });
        }

        public CheckBox getCheckBox() { return checkBox; }

        public EditText getEditText() { return editText; }

        public long getDebt() { return debt; }

        public boolean hasBeenSetManually() { return hasBeenSetManually; }

        public void setDebt(double debt) {
            this.debt = (long) (debt * 100);

            DecimalFormat df = new DecimalFormat("0.00");

            editText.setTag("temp");
            editText.setText(df.format(debt));
            editText.setTag(null);
        }

        public void setHasBeenSetManually(boolean hasBeenSetManually) { this.hasBeenSetManually = hasBeenSetManually;}

        public void setHasBeenAddedToTotal(boolean hasBeenAddedToTotal) { this.hasBeenAddedToTotal = hasBeenAddedToTotal; }
    }

    /**
     * Sets up the intent to go back to the parent activity.
     * @return  Set up intent.
     */
    private Intent setUpBackIntent() {
        Intent intent = new Intent();

        intent.putExtra(Nanuda.EXTRA_GROUP, group);
        intent.putParcelableArrayListExtra(Nanuda.EXTRA_EXPENSES, expenses);

        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = setUpBackIntent();
            setResult(RESULT_CANCELED, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = setUpBackIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
        super.onBackPressed();
    }
}