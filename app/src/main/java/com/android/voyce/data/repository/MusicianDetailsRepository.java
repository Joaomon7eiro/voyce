package com.android.voyce.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.voyce.data.local.AppDatabase;
import com.android.voyce.data.local.UserPostDao;
import com.android.voyce.data.local.UserSponsoringDao;
import com.android.voyce.data.model.Goal;
import com.android.voyce.data.model.Post;
import com.android.voyce.data.model.UserSponsoringProposal;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.onesignal.OneSignal;


import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public class MusicianDetailsRepository {

    private static MusicianDetailsRepository sInstance;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private CollectionReference mUsersCollectionReference = mDb.collection("users");
    private CollectionReference mFollowingCollectionReference = mDb.collection("user_following");
    private CollectionReference mFollowersCollectionReference = mDb.collection("user_followers");
    private CollectionReference mSponsoringCollectionReference = mDb.collection("user_sponsoring");
    private CollectionReference mProposalUsersCollectionReference = mDb.collection("proposal_users");

    private final Executor mDiskExecutor;
    private final UserFollowingMusicianDao mUserFollowingMusicianDao;
    private final UserSponsoringDao mUserSponsoringDao;
    private MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsProposalLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsFollowing = new MutableLiveData<>();

    private boolean mBolIsFollowing;
    private UserFollowingMusician mUserFollowingMusician;
    private MutableLiveData<Goal> mGoal = new MutableLiveData<>();
    private boolean mBolIsSponsoring;
    private MutableLiveData<Boolean> mIsSponsoring = new MutableLiveData<>();

    private MusicianDetailsRepository(Executor diskExecutor,
                                      UserFollowingMusicianDao userFollowingMusicianDao,
                                      UserSponsoringDao userSponsoringDao) {
        mDiskExecutor = diskExecutor;
        mUserFollowingMusicianDao = userFollowingMusicianDao;
        mUserSponsoringDao = userSponsoringDao;
    }

    public static MusicianDetailsRepository getInstance(Application application) {
        if (sInstance == null) {
            sInstance = new MusicianDetailsRepository(
                    AppExecutors.getInstance().getDiskIO(),
                    AppDatabase.getInstance(application).userFollowingMusicianDao(),
                    AppDatabase.getInstance(application).userSponsoringDao());
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
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                    for (int i = 0; i < proposals.size(); i++) {
                        proposals.get(i).setId(documents.get(i).getId());
                    }

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

            addPostsOnFeed();

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

            removePostsOnFeed(mUserFollowingMusician.getId());

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

    private void removePostsOnFeed(final String id) {
        final CollectionReference reference = mDb.collection("feed").document(mUserFollowingMusician.getFollower_id())
                .collection("posts");

        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot query) {
                if (query != null && query.size() > 0) {
                    List<Post> posts = query.toObjects(Post.class);
                    for (Post post: posts) {
                        if (post.getUser_id().equals(id)) {
                            reference.document(post.getId()).delete();
                        }
                    }
                }
            }
        });
    }

    private void addPostsOnFeed() {
        long nowTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        long twoDaysInMillis = TimeUnit.DAYS.toMillis(3);
        long dayAgoTimestamp = nowTimestamp - twoDaysInMillis;

        final List<Post> posts = new ArrayList<>();

        mDb.collection("user_posts")
                .document(mUserFollowingMusician.getId()).collection("posts")
                .whereGreaterThan("timestamp", dayAgoTimestamp)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots != null
                                        && queryDocumentSnapshots.size() > 0) {
                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                        Post post = snapshot.toObject(Post.class);
                                        post.setId(snapshot.getId());
                                        post.setCurrent_user_id(mUserFollowingMusician.getFollower_id());
                                        posts.add(post);
                                    }
                                }
                                for (Post post : posts) {
                                    mDb.collection("feed").document(mUserFollowingMusician.getFollower_id())
                                            .collection("posts").document(post.getId()).set(post);
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

    public void handleSponsor(String signalId, String userName, final Proposal proposal) {
        if (!mBolIsSponsoring) {
            Map<String, Object> hashMapSponsoring = new HashMap<>();
            hashMapSponsoring.put("user_image", mUserFollowingMusician.getImage());
            hashMapSponsoring.put("user_name", mUserFollowingMusician.getName());
            hashMapSponsoring.put("price", proposal.getPrice());
            hashMapSponsoring.put("name", proposal.getName());

            mSponsoringCollectionReference
                    .document(mUserFollowingMusician.getFollower_id())
                    .collection("sponsoring")
                    .document(proposal.getId()).set(hashMapSponsoring);

            Map<String, Object> hashMapProposalUser = new HashMap<>();
            hashMapProposalUser.put("id", mUserFollowingMusician.getFollower_id());

            mProposalUsersCollectionReference
                    .document(proposal.getId())
                    .collection("users")
                    .document(mUserFollowingMusician.getFollower_id()).set(hashMapProposalUser);

            mDiskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    UserSponsoringProposal userSponsoringProposal = new UserSponsoringProposal();
                    userSponsoringProposal.setId(proposal.getId());
                    userSponsoringProposal.setName(proposal.getName());
                    userSponsoringProposal.setPrice(proposal.getPrice());
                    userSponsoringProposal.setSponsor_id(mUserFollowingMusician.getFollower_id());
                    userSponsoringProposal.setUser_image(mUserFollowingMusician.getImage());
                    userSponsoringProposal.setUser_name(mUserFollowingMusician.getName());

                    mUserSponsoringDao.insertUserSponsoringProposal(userSponsoringProposal);
                }
            });

            try {
                JSONObject notificationContent = new JSONObject("{'contents': {'en': '" + userName + " assinou seu plano: " + proposal.getName() + "' }," +
                        "'include_player_ids': ['" + signalId + "'], " +
                        "'headings': {'en': 'Novo Patrocinador'}}");

                OneSignal.postNotification(notificationContent, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIsSponsoring.setValue(true);
            mBolIsSponsoring = true;
        } else {
            mSponsoringCollectionReference
                    .document(mUserFollowingMusician.getFollower_id())
                    .collection("sponsoring")
                    .document(proposal.getId()).delete();

            mProposalUsersCollectionReference
                    .document(proposal.getId())
                    .collection("users")
                    .document(mUserFollowingMusician.getFollower_id()).delete();

            mDiskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mUserSponsoringDao.deleteById(proposal.getId());
                }
            });

            mIsSponsoring.setValue(false);
            mBolIsSponsoring = false;
        }
    }

    public LiveData<Boolean> getProposalLoading() {
        return mIsProposalLoading;
    }

    public LiveData<Boolean> getIsSponsoring(String proposalId) {
        mIsProposalLoading.setValue(true);
        DocumentReference reference = mSponsoringCollectionReference
                .document(mUserFollowingMusician.getFollower_id()).collection("sponsoring").document(proposalId);

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    UserSponsoringProposal userSponsoringProposal = documentSnapshot.toObject(UserSponsoringProposal.class);
                    if (userSponsoringProposal != null) {
                        mIsSponsoring.setValue(true);
                        mBolIsSponsoring = true;
                    } else {
                        mIsSponsoring.setValue(false);
                        mBolIsSponsoring = false;
                    }
                }
                mIsProposalLoading.setValue(false);
            }
        });
        return mIsSponsoring;
    }
}
