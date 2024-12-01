package com.example.btlnhomandroid.objects;

/**
 * Object defining which user should pay which user how much in Balances.
 */
public class SummaryListObject implements Comparable<SummaryListObject> {
    private String participantName;
    private long participantSum;
    private Group.Currency currency;

    public SummaryListObject(String participantName, long participantSum, Group.Currency currency) {
        this.participantName = participantName;
        this.participantSum = participantSum;
        this.currency = currency;
    }

    public String getParticipantName() {
        return participantName;
    }

    /**
     * Getter for participant sum as a formatted string.
     * @return Formatted participant sum as a string.
     */
    public String getParticipantSumAsString() {
        if (this.participantSum != 0) {
            StringBuilder sb = new StringBuilder(Long.toString(this.participantSum));
            sb.insert(sb.length() - 2, '.');
            sb.append(" " + currency.toString());
            return sb.toString();
        } else {
            return "0.00 " + currency.toString();
        }
    }

    public Long getParticipantSum() { return participantSum; }

    public Group.Currency getCurrency() { return currency; }

    public void setParticipantName(String participantName) { this.participantName = participantName; }

    public void setParticipantSum(long participantSum) {
        this.participantSum = participantSum;
    }

    public void setCurrency(Group.Currency currency) { this.currency = currency; }

    @Override
    public int compareTo(SummaryListObject o) {
        return this.getParticipantSum().compareTo(o.getParticipantSum());
    }
}
