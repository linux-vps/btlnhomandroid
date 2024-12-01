package com.example.btlnhomandroid.objects;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

/**
 * Class defining the Group Parse Object.
 */
@ParseClassName("Group")
public class Group extends ParseObject {
    // KEYS
    public static final String KEY_NAME = "name";
    public static final String KEY_DESC = "desc";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_PARTICIPANTS = "participants";

    /**
     * Enum for Currency
     */
    public enum Currency {
        USD, EUR, KRW
    }

    /**
     * Default constructor.
     */
    public Group() { super(); }

    /**
     * Constructor without optional description.
     * @param name Name of the new group.
     * @param currency Currency used by the new group.
     * @param participants List of participants in the new group.
     */
    public Group(String name, Currency currency, List<String> participants) {
        setName(name);
        setCurrency(currency);
        setParticipants(participants);
    }

    /**
     * Constructor with optional description.
     * @param name Name of the new group.
     * @param desc Description of the new group.
     * @param currency Currency used by the new group.
     * @param participants List of participants in the new group.
     */
    public Group(String name, String desc, Currency currency, List<String> participants) {
        setName(name);
        setDesc(desc);
        setCurrency(currency);
        setParticipants(participants);
    }

    // GETTER SETTERS
    public String getName() { return getString(KEY_NAME); }

    public void setName(String name) { put(KEY_NAME, name); }

    public String getDesc() { return getString(KEY_DESC); }

    public void setDesc(String desc) { put(KEY_DESC, desc); }

    public Currency getCurrency() { return Currency.valueOf(getString(KEY_CURRENCY)); }

    public void setCurrency(Currency currency) { put(KEY_CURRENCY, currency.toString()); }

    public List<String> getParticipants() { return getList(KEY_PARTICIPANTS); }

    public void setParticipants(List<String> participants) { put(KEY_PARTICIPANTS, participants); }
}
