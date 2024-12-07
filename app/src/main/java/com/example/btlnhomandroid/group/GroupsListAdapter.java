package com.example.btlnhomandroid.group;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.btlnhomandroid.Nanuda;
import com.example.btlnhomandroid.R;
import com.example.btlnhomandroid.expense.ExpensesListActivity;
import com.example.btlnhomandroid.objects.Group;

import java.util.List;

public class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.ViewHolder> {

    private final List<Group> groupsList;
    private final Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private final LinearLayout linearLayout;
        private final TextView nameTextView;
        private final TextView descTextView;
        private final LinearLayout infoLinearLayout;
        private final LinearLayout editGroupImageButtonLinearLayout;
        private final ImageButton editGroupImageButton;

        public ViewHolder(View view) {
            super(view);

            this.view = view;
            linearLayout = (LinearLayout) view.findViewById(R.id.groupsListItemLinearLayout);
            nameTextView = (TextView) view.findViewById(R.id.groupsListItemName);
            descTextView = (TextView) view.findViewById(R.id.groupsListItemDesc);
            infoLinearLayout = (LinearLayout) view.findViewById(R.id.groupsListItemInfoLinearLayout);
            editGroupImageButtonLinearLayout = (LinearLayout) view.findViewById(R.id.editGroupImageButtonLinearLayout);
            editGroupImageButton = (ImageButton) view.findViewById(R.id.editGroupImageButton);
        }

        public View getView() { return view; }

        public LinearLayout getLinearLayout() { return linearLayout; }

        public TextView getNameTextView() { return nameTextView; }

        public TextView getDescTextView() { return descTextView; }

        public LinearLayout getInfoLinearLayout() { return infoLinearLayout; }

        public LinearLayout getEditGroupImageButtonLinearLayout() { return editGroupImageButtonLinearLayout; }

        public ImageButton getEditGroupImageButton() { return editGroupImageButton; }
    }

    public GroupsListAdapter(Context context, List<Group> groupsList) {
        this.context = context;
        this.groupsList = groupsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.groups_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        viewHolder.getNameTextView().setText(groupsList.get(position).getName());
        if (groupsList.get(position).getDesc() != null) {
            viewHolder.getDescTextView().setText(groupsList.get(position).getDesc());
        } else {
            viewHolder.getDescTextView().setText("");
        }

        viewHolder.getInfoLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ExpensesListActivity.class);
                intent.putExtra(Nanuda.EXTRA_GROUP, groupsList.get(position));
                ((GroupsListActivity)context).startActivityForResult(intent, GroupsListActivity.EXPENSE_REQUEST_CODE);
            }
        });

        viewHolder.getEditGroupImageButtonLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditGroupActivity.class);
                intent.putExtra(Nanuda.EXTRA_GROUP, groupsList.get(position));
                ((GroupsListActivity)context).startActivityForResult(intent, GroupsListActivity.EDIT_GROUP_REQUEST_CODE);
            }
        });

        viewHolder.getEditGroupImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditGroupActivity.class);
                intent.putExtra(Nanuda.EXTRA_GROUP, groupsList.get(position));
                ((GroupsListActivity)context).startActivityForResult(intent, GroupsListActivity.EDIT_GROUP_REQUEST_CODE);
            }
        });

        viewHolder.getInfoLinearLayout().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this group?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Group groupToDelete = groupsList.get(position);
                                groupsList.remove(groupToDelete);
                                GroupsListAdapter.this.notifyDataSetChanged();
                                GroupsListActivity.removeGroupId(groupToDelete.getObjectId(), context);
                                groupToDelete.deleteInBackground();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }
}
