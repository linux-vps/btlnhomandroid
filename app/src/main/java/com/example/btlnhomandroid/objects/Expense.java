package com.example.btlnhomandroid.objects;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Class defining the Expense Parse Object.
 */
@ParseClassName("Expense")
public class Expense extends ParseObject implements Comparable<Expense> {
    // KEYS
    public static final String KEY_TITLE = "title";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DATE = "date";
    public static final String KEY_PAYER = "payer";
    public static final String KEY_PAYEES = "payees";
    public static final String KEY_OWED_AMOUNTS = "owedAmounts";
    public static final String KEY_GROUP = "group";

    /**
     * Default constructor.
     */
    public Expense() { super(); }

    /**
     * Constructor with arguments.
     * @param title         Title of the new expense.
     * @param amount        Amount of the new expense.
     * @param date          Date of the new expense.
     * @param payer         Who pays for the new expense.
     * @param payees        Who is paid for by the new expense.
     * @param owedAmounts   Amount owed per payee.
     * @param group         The group the new expense belongs to.
     */
    public Expense(String title, Long amount, Date date, String payer, List<String> payees, List<Long> owedAmounts, Group group) {
        setTitle(title);
        setAmount(amount);
        setDate(date);
        setPayer(payer);
        setPayees(payees);
        setOwedAmounts(owedAmounts);
        setGroup(group);
    }

    @Override
    public int compareTo(Expense o) {
        return getDate().compareTo(o.getDate());
    }

    // GETTER SETTERS
    public String getTitle() { return getString(KEY_TITLE); }

    public void setTitle(String title) { put(KEY_TITLE, title); }

    public Long getAmount() { return getLong(KEY_AMOUNT); }

    public void setAmount(Long amount) { put(KEY_AMOUNT, amount); }

    public Date getDate() { return getDate(KEY_DATE); }

    public void setDate(Date date) { put(KEY_DATE, date); }

    public String getPayer() { return getString(KEY_PAYER); }

    public void setPayer(String payer) { put(KEY_PAYER, payer); }

    public List<String> getPayees() { return getList(KEY_PAYEES); }

    public void setPayees(List<String> payees) { put(KEY_PAYEES, payees); }

    // TODO: figure out solution to following problem
    // owedAmounts is saved as List of Long
    // but when retrieved from backend, it's actually a List of Integer
    // cf. BalancesActivity.java
    public List<Integer> getOwedAmounts() { return getList(KEY_OWED_AMOUNTS); }

    public void setOwedAmounts(List<Long> owedAmounts) { put(KEY_OWED_AMOUNTS, owedAmounts); }

    public Group getGroup() { return (Group) getParseObject(KEY_GROUP); }

    public void setGroup(Group group) { put(KEY_GROUP, group); }
}
