package com.android.voyce.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.model.Goal;
import com.android.voyce.utils.AppExecutors;
import com.android.voyce.data.local.UserFollowingMusicianDao;
import com.android.voyce.data.model.Proposal;
import com.android.voyce.data.model.User;
import com.android.voyce.data.model.UserFollowingMusician;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.onesignal.OneSignal;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;



public class MusicianDetailsRepository {
    private static final String TAG = SearchRepository.class.getSimpleName();

    private static MusicianDetailsRepository sInstance;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private CollectionReference mUsersCollectionReference = mDb.collection("users");
    private CollectionReference mFollowingCollectionReference = mDb.collection("user_following");
    private CollectionReference mFollowersCollectionReference = mDb.collection("user_followers");

    private final Executor mDiskExecutor;
    private final UserFollowingMusicianDao mUserFollowingMusicianDao;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsFollowing = new MutableLiveData<>();

    private boolean mBolIsFollowing;
    private UserFollowingMusician mUserFollowingMusician;
    private MutableLiveData<Goal> mGoal = new MutableLiveData<>();

    private MusicianDetailsRepository(Executor diskExecutor, UserFollowingMusicianDao userFollowingMusicianDao) {
        mDiskExecutor = diskExecutor;
        mUserFollowingMusicianDao = userFollowingMusicianDao;
    }

    public static MusicianDetailsRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new MusicianDetailsRepository(
                    AppExecutors.getInstance().getDiskIO(),
                    AppDatabase.getInstance(application).userFollowingMusicianDao());
        }
        return sInstance;
    }

    public void setUserFollowingMusician(UserFollowingMusician userFollowingMusician) {
        mUserFollowingMusician = userFollowingMusician;
    }

    public LiveData<Goal> getGoal() {
        return mGoal;
    }

    public LiveData<Boolean> getIsLoading() {
        return mIsLoading;
    }

    public LiveData<User> getMusician() {
        final DocumentReference reference = mUsersCollectionReference.document(mUserFollowingMusician.getId());

        final MutableLiveData<User> userLiveData = new MutableLiveData<>();

        mIsLoading.setValue(true);

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    User musician = documentSnapshot.toObject(User.class);
                    userLiveData.setValue(musician);

                    Goal goal = hashToGoalObject(documentSnapshot.get("goal"));
                    mGoal.setValue(goal);
                }
            }
        });

        return userLiveData;
    }

    public LiveData<List<Proposal>> getProposals() {
        final DocumentReference reference = mUsersCollectionReference.document(mUserFollowingMusician.getId());
        CollectionReference proposalsCollectionReference = reference.collection("proposals");

        final MutableLiveData<List<Proposal>> liveData = new MutableLiveData<>();

        proposalsCollectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    List<Proposal> proposals = queryDocumentSnapshots.toObjects(Proposal.class);
                    liveData.setValue(proposals);
                }
            }
        });

        return liveData;
    }

    private Goal hashToGoalObject(Object hashMap) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(hashMap);
        return gson.fromJson(jsonElement, Goal.class);
    }

    public void handleFollower(String signalId, String name) {
        if (!mBolIsFollowing) {
            Map<String, Object> hashMapMusician = new HashMap<>();
            hashMapMusician.put("image", mUserFollowingMusician.getImage());
            hashMapMusician.put("name", mUserFollowingMusician.getName());

            mFollowingCollectionReference
                    .document(mUserFollowingMusician.getFollower_id())
                    .collection("users")
                    .document(mUserFollowingMusician.getId()).set(hashMapMusician);

            Map<String, Object> hashMapUser = new HashMap<>();
            hashMapUser.put("id", mUserFollowingMusician.getFollower_id());

            mFollowersCollectionReference
                    .document(mUserFollowingMusician.getId())
                    .collection("users")
                    .document(mUserFollowingMusician.getFollower_id()).set(hashMapUser);

            mDiskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mUserFollowingMusicianDao.insertUserFollowingMusician(mUserFollowingMusician);
                }
            });

            try {
                JSONObject notificationContent = new JSONObject("{'contents': {'en': '" + name + " começou a te seguir' }," +
                        "'include_player_ids': ['" + signalId + "'], " +
                        "'headings': {'en': 'Novo Seguidor'}}");

                OneSignal.postNotification(notificationContent, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIsFollowing.setValue(true);
            mBolIsFollowing = true;
        } else {
            mFollowingCollectionReference
                    .document(mUserFollowingMusician.getFollower_id())
                    .collection("users")
                    .document(mUserFollowingMusician.getId()).delete();

            mFollowersCollectionReference
                    .document(mUserFollowingMusician.getId())
                    .collection("users")
                    .document(mUserFollowingMusician.getFollower_id()).delete();

            mDiskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mUserFollowingMusicianDao.deleteById(mUserFollowingMusician.getId());
                }
            });

            mIsFollowing.setValue(false);
            mBolIsFollowing = false;
        }
        final DocumentReference reference = mUsersCollectionReference.document(mUserFollowingMusician.getId());
        CollectionReference followersReference = mFollowersCollectionReference.document(mUserFollowingMusician.getId()).collection("users");
        followersReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    final long followersNumber = queryDocumentSnapshots.size();
                    Map<String, Object> map = new HashMap<>();
                    map.put("followers", followersNumber);
                    reference.update(map);
                }
            }
        });
    }


    public LiveData<Boolean> getIsFollowing() {
        DocumentReference reference = mFollowingCollectionReference
                .document(mUserFollowingMusician.getFollower_id()).collection("users").document(mUserFollowingMusician.getId());

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    UserFollowingMusician userFollowingMusician = documentSnapshot.toObject(UserFollowingMusician.class);
                    if (userFollowingMusician != null) {
                        mIsFollowing.setValue(true);
                        mBolIsFollowing = true;
                    } else {
                        mIsFollowing.setValue(false);
                        mBolIsFollowing = false;
                    }
                }
                mIsLoading.setValue(false);
            }
        });

        return mIsFollowing;
    }

}
