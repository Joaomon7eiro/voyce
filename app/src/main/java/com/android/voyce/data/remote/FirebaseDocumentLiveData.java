package com.android.voyce.data.remote;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseDocumentLiveData extends LiveData<QuerySnapshot> {
    public static final String TAG = "FBDocumentLiveData";

    private Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private ListenerRegistration listenerRegistration;

    private boolean listenerRemovePending = false;
    private final Handler handler = new Handler();

    public FirebaseDocumentLiveData(Query query) {
        this.query = query;
    }

    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            listenerRegistration.remove();
            listenerRemovePending = false;
        }
    };

    @Override
    protected void onActive() {
        super.onActive();

        Log.d(TAG, "onActive");
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener);
        }
        else {
            listenerRegistration = query.addSnapshotListener(listener);
        }
        listenerRemovePending = false;
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        Log.d(TAG, "onInactive: ");
        // Listener removal is schedule on a two second delay
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = true;
    }

    private class MyValueEventListener implements EventListener<QuerySnapshot> {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
            if (e != null){
                Log.e(TAG, "Can't listen to query snapshots: " + querySnapshot + ":::" + e.getMessage());
                return;
            }
            setValue(querySnapshot);
        }
    }
}
