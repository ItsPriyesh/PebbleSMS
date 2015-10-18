package me.priyesh.pebblesms;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import me.priyesh.pebblesms.model.Contact;

public class ContactEditorDialog extends AlertDialog {

    public interface ContactEditListener {
        void onContactEdited();
    }

    private final Contact mContact;
    private final ContactEditListener mListener;

    private TextView mNameView;
    private TextView mPhoneView;

    public ContactEditorDialog(Context context, Contact contact, ContactEditListener listener) {
        super(context);
        mContact = contact;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final View view = View.inflate(getContext(), R.layout.dialog_contact_editor, null);
        setView(view);
        setTitle("Edit contact");

        mNameView = ButterKnife.findById(view, R.id.name_view);
        mPhoneView = ButterKnife.findById(view, R.id.phone_view);

        mNameView.setText(mContact.name);
        mPhoneView.setText(mContact.phone);

        setButton(BUTTON_POSITIVE, "OK", (dialog, which) -> dispatchContactEdited());
        setButton(BUTTON_NEGATIVE, "Cancel", (dialog, which) -> dismiss());

        super.onCreate(savedInstanceState);
    }

    private void dispatchContactEdited() {
        mContact.name = mNameView.getText().toString();
        mContact.phone = mPhoneView.getText().toString();
        mListener.onContactEdited();
    }
}
