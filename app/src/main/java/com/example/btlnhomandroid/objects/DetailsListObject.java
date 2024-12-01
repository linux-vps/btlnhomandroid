package com.example.btlnhomandroid.objects;

/**
 * Object defining which user should pay which user how much in Balances.
 */
public class DetailsListObject {
    private String owerName;
    private String oweeName;
    private long owedAmount;
    private Group.Currency currency;

    public DetailsListObject(String owerName, String oweeName, long owedAmount, Group.Currency currency) {
        this.owerName = owerName;
        this.oweeName = oweeName;
        this.owedAmount = owedAmount;
        this.currency = currency;
    }

    public String getOwerName() {
        return owerName;
    }

    public String getOweeName() {
        return oweeName;
    }

    /**
     * Getter for owed amount as a formatted string.
     * @return Formatted owed amount as a string.
     */
    public String getOwedAmountAsString() {
        StringBuilder sb = new StringBuilder(Long.toString(this.owedAmount));
        if (sb.length() > 2) {
            sb.insert(sb.length() - 2, '.');
        } else {
            sb.insert(0, "0.");
        }

        sb.append(" " + currency.toString());
        return sb.toString();
    }

    public long getOwedAmount() {
        return owedAmount;
    }

    public Group.Currency getCurrency() { return currency; }

    public void setOwerName(String owerName) {
        this.owerName = owerName;
    }

    public void setOweeName(String oweeName) {
        this.oweeName = oweeName;
    }

    public void setOwedAmount(long owedAmount) {
        this.owedAmount = owedAmount;
    }

    public void setCurrency(Group.Currency currency) { this.currency = currency; }
}
