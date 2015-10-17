package me.priyesh.pebblesms;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ContactPickerFragment.ContactLoaderListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 0xbeef;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        if (isMarshmallow()) {
            if (canReadContacts()) loadContactPickerFragment();
            else requestReadContactsPermission();
        } else {
            loadContactPickerFragment();
        }

        mFab.setOnClickListener(v -> {

        });
    }

    private void loadContactPickerFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new ContactPickerFragment())
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS_PERMISSION) {
            new Handler().post(this::loadContactPickerFragment);
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

    @Override
    public void onContactsLoaded() {
        mFab.show();
    }

    @Override
    public void onNoContactsFound() {
        mFab.hide();
    }
}
