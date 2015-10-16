package me.priyesh.pebblesms.model;

public class Contact {

    public final String name;
    public final String phone;
    public boolean isSelected;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
