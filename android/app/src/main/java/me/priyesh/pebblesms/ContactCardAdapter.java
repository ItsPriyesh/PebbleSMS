package me.priyesh.pebblesms;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import me.priyesh.pebblesms.model.Contact;

public class ContactCardAdapter extends RecyclerView.Adapter<ContactCardAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mNameView;
        public final TextView mPhoneView;
        public final ImageView mLetterView;
        public final Button mRemoveButton;
        public final Button mEditButton;

        public ViewHolder(View v) {
            super(v);
            mNameView = ButterKnife.findById(v, R.id.name_label);
            mPhoneView = ButterKnife.findById(v, R.id.phone_label);
            mLetterView = ButterKnife.findById(v, R.id.letter_view);
            mRemoveButton = ButterKnife.findById(v, R.id.remove_button);
            mEditButton = ButterKnife.findById(v, R.id.edit_button);
        }
    }

    public interface ContactCardClickListener {
        void onContactEditButtonClicked(Contact contact);
        void onContactRemoveButtonClicked(Contact contact);
    }

    private final List<Contact> mContacts;

    public ContactCardAdapter(List<Contact> contacts) {
        mContacts = contacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.contact_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Contact contact = mContacts.get(position);
        holder.mNameView.setText(contact.name);
        holder.mPhoneView.setText(contact.phone);
        holder.mLetterView.setImageDrawable(LetterDrawable.get(contact.name));
        holder.mRemoveButton.setOnClickListener(v -> removeContact(position));
        holder.mEditButton.setOnClickListener(v -> editContact(position, v.getContext()));
    }

    private void removeContact(int position) {
        mContacts.remove(position);
        notifyDataSetChanged();
    }

    private void editContact(int position, Context context) {
        new ContactEditorDialog(context, mContacts.get(position),
                () -> notifyItemChanged(position)).show();
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

}
