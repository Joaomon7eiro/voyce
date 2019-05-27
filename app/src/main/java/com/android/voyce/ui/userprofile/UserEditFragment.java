package com.android.voyce.ui.userprofile;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.voyce.R;
import com.android.voyce.databinding.FragmentUserEditBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class UserEditFragment extends Fragment {
    private static final int RC_PHOTO_PICKER = 2;
    private Uri mSelectedImageUri;
    private StorageReference mProfileImagesStorage;
    private boolean mSaveEnabled = false;
    private FragmentUserEditBinding mBinding;

    public UserEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_edit, container, false);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.toolbarEdit);

        mBinding.toolbarEdit.setNavigationIcon(R.drawable.ic_action_back);
        mBinding.toolbarEdit.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(mBinding.getRoot()).popBackStack();
            }
        });

        mProfileImagesStorage = FirebaseStorage.getInstance().getReference().child("profile_images");

        mBinding.editCancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSaveEnabled = false;
                getActivity().invalidateOptionsMenu();
                mSelectedImageUri = null;
                mBinding.editImage.setVisibility(View.GONE);
                mBinding.editCancelImage.setVisibility(View.GONE);
            }
        });

        mBinding.editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                getActivity().startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_edit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mSaveEnabled) {
            menu.findItem(R.id.save_edit_changes).setEnabled(true);
        } else {
            menu.findItem(R.id.save_edit_changes).setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_edit_changes) {
            if (mSelectedImageUri != null) {
                final StorageReference imageRef = mProfileImagesStorage.child(mSelectedImageUri.getLastPathSegment());
                mBinding.editProgressBar.setVisibility(View.VISIBLE);
                imageRef.putFile(mSelectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mBinding.editProgressBar.setVisibility(View.GONE);
                                        if (uri != null) {
                                            saveChanges(uri.toString());
                                        }
                                    }
                                });
                            }
                        });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChanges(String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("image", url);
        Task<Void> task = FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getUid()).update(map);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Navigation.findNavController(mBinding.getRoot()).popBackStack();
            }
        });
    }

    public void setImageUri(Uri uri) {
        mSelectedImageUri = uri;
        mSaveEnabled = true;
        getActivity().invalidateOptionsMenu();
        Glide.with(mBinding.getRoot()).load(mSelectedImageUri).into(mBinding.editImage);
        mBinding.editImage.setVisibility(View.VISIBLE);
        mBinding.editCancelImage.setVisibility(View.VISIBLE);
    }
}
