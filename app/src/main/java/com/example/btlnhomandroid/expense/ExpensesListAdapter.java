package com.example.btlnhomandroid.expense;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlnhomandroid.Nanuda;
import com.example.btlnhomandroid.R;
import com.example.btlnhomandroid.objects.Expense;
import com.example.btlnhomandroid.objects.Group;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExpensesListAdapter extends RecyclerView.Adapter<ExpensesListAdapter.ExpenseViewHolder> {

    private ArrayList<Expense> expenses;
    private Group group;
    private Context context;

    public ExpensesListAdapter(Context ct, ArrayList<Expense> expenses, Group group) {
        context = ct;
        this.expenses = expenses;
        this.group = group;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.expenses_list_item,parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.expenseText.setText(expenses.get(position).getTitle());
        holder.paidByText.setText(expenses.get(position).getPayer());

        StringBuilder sb = new StringBuilder(Long.toString(expenses.get(position).getAmount()));
        if (sb.length() > 2) {
            sb.insert(sb.length() - 2, '.');
        } else {
            sb.insert(0, "0.");
        }
        sb.append(" " + group.getCurrency().toString());

        holder.amountText.setText(sb.toString());

        String dateFormat = "dd-MM-yyyy";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        String date = simpleDateFormat.format(expenses.get(position).getDate());

        holder.dateText.setText(date);

        holder.expenseListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditExpenseActivity.class);
                intent.putExtra(Nanuda.EXTRA_GROUP, group);
                intent.putExtra(Nanuda.EXTRA_EXPENSES, expenses);
                intent.putExtra(Nanuda.EXTRA_EXPENSE, expenses.get(position));
                ((ExpensesListActivity)context).startActivityForResult(intent, ExpensesListActivity.EDIT_EXPENSE_REQUEST_CODE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder{

        TextView expenseText, paidByText, amountText, dateText;
        ConstraintLayout expenseListLayout;

        public ExpenseViewHolder(@NonNull View itemView) {

            super(itemView);
            expenseText = itemView.findViewById(R.id.expense_name);
            paidByText = itemView.findViewById(R.id.paid_by);
            amountText = itemView.findViewById(R.id.amount);
            dateText = itemView.findViewById(R.id.date);
            expenseListLayout = itemView.findViewById(R.id.expenseListLayout);

        }
    }
}
