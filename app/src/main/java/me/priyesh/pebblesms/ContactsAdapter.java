package me.priyesh.pebblesms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import me.priyesh.pebblesms.model.Contact;

public class ContactsAdapter extends ArrayAdapter<Contact> {

    private ArrayList<Contact> mContacts;

    public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
        this.mContacts = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item, parent, false);

            holder = new ViewHolder();
            holder.name = ButterKnife.findById(convertView, R.id.title);
            holder.phone = ButterKnife.findById(convertView, R.id.subtitle);
            holder.checkBox = ButterKnife.findById(convertView, R.id.checkbox);

            holder.checkBox.setOnCheckedChangeListener(checkBoxListener);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setOnClickListener(v -> holder.checkBox.toggle());

        final Contact contact = getItem(position);

        holder.name.setText(contact.name);

        if (contact.phone == null || contact.phone.equals("")) {
            holder.phone.setVisibility(View.GONE);
        } else {
            holder.phone.setVisibility(View.VISIBLE);
            holder.phone.setText(contact.phone);
        }

        holder.checkBox.setTag(contact);
        holder.checkBox.setChecked(contact.isSelected);

        return convertView;
    }

    private CompoundButton.OnCheckedChangeListener checkBoxListener = (view, checked) -> {
        final Contact contact = (Contact) view.getTag();
        contact.isSelected = checked;
        notifyDataSetChanged();
    };

    private static class ViewHolder {
        TextView name;
        TextView phone;
        CheckBox checkBox;
    }

    public List<Contact> getSelectedContacts() {
        List<Contact> selectedContacts = new ArrayList<>();

        for (Contact c : mContacts)
            if (c.isSelected) selectedContacts.add(c);

        return selectedContacts;
    }

}