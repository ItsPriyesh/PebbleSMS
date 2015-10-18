package me.priyesh.pebblesms;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.priyesh.pebblesms.model.Contact;

public class MainActivity extends AppCompatActivity implements ContactPickerFragment.OnContactsSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 0xbeef;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private int contactSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        final Preferences preferences = Preferences.getInstance(this);
        contactSize = preferences.getInt(Preferences.Key.CONTACTS_SIZE, 0);

        mFab.setOnClickListener(onFabClickListener);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private View.OnClickListener onFabClickListener = v -> {
        if (contactSize >= ContactPickerFragment.MAX_CONTACTS) {
            Snackbar.make(v, "Contact limit has been reached", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (isMarshmallow()) {
            if (canReadContacts()) showContactsPicker();
            else requestReadContactsPermission();
        } else {
            showContactsPicker();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new Handler().post(this::showContactsPicker);
            } else {
                Snackbar.make(mFab, "Unable to access contacts", Snackbar.LENGTH_LONG)
                        .setAction("Retry", onFabClickListener).show();
            }
        }
    }

    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean canReadContacts() {
        return checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestReadContactsPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
    }

    private void showContactsPicker() {
        new ContactPickerFragment().show(getSupportFragmentManager(), ContactPickerFragment.TAG);
    }

    @Override
    public void onContactsSelected(List<Contact> contacts) {
        mRecyclerView.setAdapter(new ContactCardAdapter(contacts));
    }

    private void showContactEditor(Contact contact) {

    }
}
