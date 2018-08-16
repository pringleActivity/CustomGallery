package com.example.vadym.galleryapp.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vadym.galleryapp.R;
import com.example.vadym.galleryapp.ui.adapter.AdapterRecyclerImages;
import com.example.vadym.galleryapp.ui.fragment.GridFragment;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements AdapterRecyclerImages.OnRecyclerListener, EasyPermissions.PermissionCallbacks {

    public static final int COUNT_GRID = 9; //max 10

    private boolean needReplace = false;
    private int positionReplace;

//    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyPagerAdapter pagerAdapter;

    public interface OnRemoteFragmentListener {
        void addImage(String uri);

        void deleteImage(int position);

        void replaceImage(String uri, int position);

        void startSlideshow(int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pagerAdapter);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission();
            }
        });
    }

    ///-------------------------------
    //EASY RUNTIME PERMISSIONS LIBRARY
    ///-------------------------------
    private void askPermission() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            fetchImageFromFileSystem();
        } else {
            EasyPermissions.requestPermissions(this, "This lets GridMemoryPhoto store and access information like photos on your phone and its SD card.",
                    123, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        fetchImageFromFileSystem();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
    ///----------------------------
    ///----------------------------

    ///----------------------------
    //NATIVE RUNTIME PERMISSIONS---
    ///----------------------------

//    private void askPermission() {
//        if (hasPermissions()){
//            fetchImageFromFileSystem();
//        }
//        else {
//            rationaleOfNeedForPermission();
//        }
//    }
//
//    private boolean hasPermissions(){
//        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
//
//        for (String perms : permissions){
//            int res = checkCallingOrSelfPermission(perms);
//            if (!(res == PackageManager.PERMISSION_GRANTED)){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public void rationaleOfNeedForPermission() {
//        //if user recently clicked "deny"
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            final String message = "Storage permission is needed to show files count";
//            Snackbar.make(MainActivity.this.findViewById(R.id.root_container), message, Snackbar.LENGTH_LONG)
//                    .setAction("GRANT", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            requestPerms();
//                        }
//                    })
//                    .show();
//        } else {
//            requestPerms();
//        }
//    }
//
//    private void requestPerms(){
//        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            requestPermissions(permissions, 123);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        boolean allowed = true;
//
//        switch (requestCode){
//            case 123:
//
//                for (int res : grantResults){
//                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
//                }
//
//                break;
//            default:
//                allowed = false;
//                break;
//        }
//
//        if (allowed){
//            fetchImageFromFileSystem();
//        }
//        else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
//                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();
//
//                } else { //if user clicked "never ask again"
//                    showNoStoragePermissionSnackbar();
//                }
//            }
//        }
//    }
//
//    public void showNoStoragePermissionSnackbar() {
//        Snackbar.make(MainActivity.this.findViewById(R.id.root_container), "Storage permission isn't granted" , Snackbar.LENGTH_LONG)
//                .setAction("SETTINGS", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        openApplicationSettings();
//
//                        Toast.makeText(getApplicationContext(),
//                                "Open Permissions and grant the Storage permission",
//                                Toast.LENGTH_SHORT)
//                                .show();
//                    }
//                })
//                .show();
//    }
//
//    public void openApplicationSettings() {
//        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                Uri.parse("package:" + getPackageName()));
//        startActivityForResult(appSettingsIntent, 123);
//    }



    private void fetchImageFromFileSystem() {

        if (Build.VERSION.SDK_INT >= 23){
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 0);
        } else {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 0);
        }

//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 0);

//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uriAsObject = data.getData();
            String uri = uriAsObject.toString();
            if (!needReplace) {
                OnRemoteFragmentListener onRemoteFragmentListener = pagerAdapter.getCurrentFragment();
                onRemoteFragmentListener.addImage(uri);
            } else {
                OnRemoteFragmentListener onRemoteFragmentListener = pagerAdapter.getCurrentFragment();
                onRemoteFragmentListener.replaceImage(uri, this.positionReplace);
                needReplace = false;
            }
        }
    }

    private void showActionDialog(final int position) {
        String choice[] = new String[]{getResources().getString(R.string.replace), getResources().getString(R.string.delete)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: //replace
                        needReplace = true;
                        MainActivity.this.positionReplace = position;
                        fetchImageFromFileSystem();
                        break;
                    case 1: //delete
                        OnRemoteFragmentListener onRemoteFragmentListener = pagerAdapter.getCurrentFragment();
                        onRemoteFragmentListener.deleteImage(position);
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void onClickRecyclerItem(int position) {
        OnRemoteFragmentListener onRemoteFragmentListener = pagerAdapter.getCurrentFragment();
        onRemoteFragmentListener.startSlideshow(position);
    }

    @Override
    public void onLongClickRecyclerItem(int position) {
        showActionDialog(position);
    }



    private class MyPagerAdapter extends FragmentPagerAdapter {

        private GridFragment currentFragment;

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public GridFragment getCurrentFragment() {
            return this.currentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                currentFragment = ((GridFragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            return GridFragment.newInstance(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.title_tab_layout1);
                case 1:
                    return getResources().getString(R.string.title_tab_layout2);
                case 2:
                    return getResources().getString(R.string.title_tab_layout3);
                case 3:
                    return getResources().getString(R.string.title_tab_layout4);
                case 4:
                    return getResources().getString(R.string.title_tab_layout5);
                case 5:
                    return getResources().getString(R.string.title_tab_layout6);
                case 6:
                    return getResources().getString(R.string.title_tab_layout7);
                case 7:
                    return getResources().getString(R.string.title_tab_layout8);
                case 8:
                    return getResources().getString(R.string.title_tab_layout9);
                case 9:
                    return getResources().getString(R.string.title_tab_layout10);
            }
            return "";
        }

        @Override
        public int getCount() {
            return COUNT_GRID;
        }
    }
}