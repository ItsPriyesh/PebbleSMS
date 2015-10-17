package me.priyesh.pebblesms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butterknife.ButterKnife;
import me.priyesh.pebblesms.model.Contact;

public class ContactPickerFragment extends Fragment {

    public static final String TAG = ContactPickerFragment.class.getSimpleName();

    private ContactLoaderListener mListener;
    private ArrayList<Contact> mContacts;
    private ContactsAdapter mContactsAdapter;
    private ListView mContactsListView;
    private ProgressBar mProgressBar;

    private final Handler mHandler = new Handler();
    private final Runnable mContactsQueryComplete = this::onContactsQueryComplete;

    public interface ContactLoaderListener {
        void onContactsLoaded();
        void onNoContactsFound();
    }

    public ContactPickerFragment() { /* Required empty public constructor */ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startContactsQuery();
        mListener = (ContactLoaderListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_contact_picker, container, false);
        mContactsListView = ButterKnife.findById(view, R.id.list_view);
        mProgressBar = ButterKnife.findById(view, R.id.progress_bar);
        return view;
    }

    public List<Contact> getSelectedContacts() {
        return mContactsAdapter.getSelectedContacts();
    }

    private void startContactsQuery() {
        new Thread() {
            public void run() {
                mContacts = fetchContacts();
                mHandler.post(mContactsQueryComplete);
            }
        }.start();
    }

    private void onContactsQueryComplete() {
        mProgressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_out_top));
        new Handler().postDelayed(() -> {
            mProgressBar.setVisibility(View.GONE);
            if (mContacts.size() != 0) {
                mListener.onContactsLoaded();
                mContactsAdapter = new ContactsAdapter(getActivity(), mContacts);
                mContactsListView.setAdapter(mContactsAdapter);
                mContactsListView.setVisibility(View.VISIBLE);
            } else {
                mListener.onNoContactsFound();
            }
        }, getResources().getInteger(android.R.integer.config_mediumAnimTime));
    }

    private ArrayList<Contact> fetchContacts() {
        HashSet<Contact> contactsSet = new HashSet<>();

        ContentResolver resolver = getActivity().getContentResolver();

        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        final int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        final int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        final int hasPhoneIndex = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            String id = cursor.getString(idIndex);
            String phone = "";

            if (Integer.parseInt(cursor.getString(hasPhoneIndex)) > 0) {
                Cursor phones = resolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                if (phones.moveToNext()) {
                    phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
            }

            if (!(phone == null || phone.equals(""))) {
                // Only add contacts that have at least a phone
                contactsSet.add(new Contact(name, phone));
            }
        }
        cursor.close();

        ArrayList<Contact> contacts = new ArrayList<>(contactsSet.size());
        contacts.addAll(contactsSet);
        Collections.sort(contacts, (c1, c2) -> c1.name.compareTo(c2.name));

        return contacts;
    }
}
