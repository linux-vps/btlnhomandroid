package com.example.btlnhomandroid.balances;

import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlnhomandroid.R;
import com.example.btlnhomandroid.objects.DetailsListObject;
import com.example.btlnhomandroid.objects.SummaryListObject;

import java.util.ArrayList;

/**
 * Adapts balances data to RecyclerView format.
 */
public class BalancesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SUMMARY_LIST_HEADER_VIEW = 1;
    private static final int SUMMARY_LIST_ITEM_VIEW = 2;
    private static final int DETAILS_LIST_HEADER_VIEW = 3;
    private static final int DETAILS_LIST_ITEM_VIEW = 4;

    private ArrayList<SummaryListObject> summaryList = new ArrayList<SummaryListObject>();
    private ArrayList<DetailsListObject> detailsList = new ArrayList<DetailsListObject>();

    public BalancesAdapter() { }

    public void setSummaryList(ArrayList<SummaryListObject> summaryList) {
        this.summaryList = summaryList;
    }

    public void setDetailsList(ArrayList<DetailsListObject> detailsList) {
        this.detailsList = detailsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // list items of summary list
        private TextView participantName;
        private TextView participantSum;

        // list items of details list
        private TextView owerName;
        private TextView oweeName;
        private TextView owedAmount;

        public ViewHolder(final View itemView) {
            super(itemView);

            // get views of elements of summary list
            participantName = (TextView) itemView.findViewById(R.id.participantName);
            participantSum = (TextView) itemView.findViewById(R.id.participantSum);

            // get views of elements of details list
            owerName = (TextView) itemView.findViewById(R.id.owerName);
            oweeName = (TextView) itemView.findViewById(R.id.oweeName);
            owedAmount = (TextView) itemView.findViewById(R.id.owedAmount);
        }

        public void bindViewDetailsList(int pos) {
            if (summaryList == null) pos = pos - 1;
            else {
                if (summaryList.size() == 0) pos = pos - 1;
                else pos = pos - summaryList.size() - 2;
            }

            final String owerName = detailsList.get(pos).getOwerName();
            final String oweeName = detailsList.get(pos).getOweeName();
            final String owedAmount = detailsList.get(pos).getOwedAmountAsString();

            this.owerName.setText(owerName);
            this.oweeName.setText(oweeName);
            this.owedAmount.setText(owedAmount);
        }

        public void bindViewSummaryList(int pos) {
            pos = pos - 1;

            final String participantName = summaryList.get(pos).getParticipantName();
            final String participantSum = summaryList.get(pos).getParticipantSumAsString();

            this.participantName.setText(participantName);
            this.participantSum.setText(participantSum);

            if (summaryList.get(pos).getParticipantSum() < 0) {
                this.participantSum.setTextColor(ContextCompat.getColor(this.participantSum.getContext(), android.R.color.holo_red_dark));
            } else if (summaryList.get(pos).getParticipantSum() == 0) {
                this.participantSum.setTextColor(ContextCompat.getColor(this.participantSum.getContext(), R.color.light_grey));
            }
        }
    }

    public class SummaryListHeaderViewHolder extends ViewHolder {
        public SummaryListHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class SummaryListItemViewHolder extends ViewHolder {
        public SummaryListItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class DetailsListHeaderViewHolder extends ViewHolder {
        public DetailsListHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class DetailsListItemViewHolder extends ViewHolder {
        public DetailsListItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        switch (viewType) {
            case SUMMARY_LIST_HEADER_VIEW:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_list_header, parent, false);
                SummaryListHeaderViewHolder slhvh = new SummaryListHeaderViewHolder(v);
                return slhvh;
            case SUMMARY_LIST_ITEM_VIEW:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_list_item, parent, false);
                SummaryListItemViewHolder slivh = new SummaryListItemViewHolder(v);
                return slivh;
            case DETAILS_LIST_HEADER_VIEW:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_list_header, parent, false);
                DetailsListHeaderViewHolder dlhvh = new DetailsListHeaderViewHolder(v);
                return dlhvh;
            case DETAILS_LIST_ITEM_VIEW:
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.details_list_item, parent, false);
                DetailsListItemViewHolder dlivh = new DetailsListItemViewHolder(v);
                return dlivh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof SummaryListHeaderViewHolder) {
                SummaryListHeaderViewHolder vh = (SummaryListHeaderViewHolder) holder;
            } else if (holder instanceof SummaryListItemViewHolder) {
                SummaryListItemViewHolder vh = (SummaryListItemViewHolder) holder;
                vh.bindViewSummaryList(position);
            } else if (holder instanceof DetailsListHeaderViewHolder) {
                DetailsListHeaderViewHolder vh = (DetailsListHeaderViewHolder) holder;
            } else if (holder instanceof DetailsListItemViewHolder) {
                DetailsListItemViewHolder vh = (DetailsListItemViewHolder) holder;
                vh.bindViewDetailsList(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int summaryListSize = 0;
        int detailsListSize = 0;

        if (detailsList == null && summaryList == null) return 0;

        if (detailsList != null)
            detailsListSize = detailsList.size();
        if (summaryList != null)
            summaryListSize = summaryList.size();

        int itemCount = 0;

        if (detailsListSize > 0 && summaryListSize > 0)
            itemCount = 1 + summaryListSize + 1 + detailsListSize;
        else if (detailsListSize > 0 && summaryListSize == 0)
            itemCount = 1 + detailsListSize;
        else if (detailsListSize == 0 && summaryListSize > 0)
            itemCount = 1 + summaryListSize;
        else itemCount = 0;

        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        int summaryListSize = 0;
        int detailsListSize = 0;

        if (detailsList == null && summaryList == null)
            return super.getItemViewType(position);

        if (detailsList != null)
            detailsListSize = detailsList.size();
        if (summaryList != null)
            summaryListSize = summaryList.size();

        if (detailsListSize > 0 && summaryListSize > 0) {
            if (position == 0) {
                return SUMMARY_LIST_HEADER_VIEW;
            } else if (position == summaryListSize + 1) {
                return DETAILS_LIST_HEADER_VIEW;
            } else if (position > summaryListSize + 1) {
                return DETAILS_LIST_ITEM_VIEW;
            } else {
                return SUMMARY_LIST_ITEM_VIEW;
            }
        } else if (detailsListSize > 0 && summaryListSize == 0) {
            if (position == 0) return DETAILS_LIST_HEADER_VIEW;
            else return DETAILS_LIST_ITEM_VIEW;
        } else if (detailsListSize == 0 && summaryListSize > 0) {
            if (position == 0) return SUMMARY_LIST_HEADER_VIEW;
            else return SUMMARY_LIST_ITEM_VIEW;
        }

        return super.getItemViewType(position);
    }
}
