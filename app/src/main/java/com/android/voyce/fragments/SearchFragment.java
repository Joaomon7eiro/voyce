package com.android.voyce.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.voyce.Musician;
import com.android.voyce.adapters.MusiciansAdapter;
import com.android.voyce.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements MusiciansAdapter.ListItemClickListener {

    RecyclerView mMusiciansGridRecyclerView;

    ArrayList<Musician> mMusicianArrayList = new ArrayList<>();
    MusiciansAdapter mMusiciansAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mMusiciansGridRecyclerView = view.findViewById(R.id.rv_search_artists_grid);
        mMusiciansAdapter = new MusiciansAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mMusiciansGridRecyclerView.setLayoutManager(layoutManager);

        mMusiciansGridRecyclerView.setHasFixedSize(true);
        mMusiciansGridRecyclerView.setAdapter(mMusiciansAdapter);

        mMusicianArrayList.add(new Musician(R.drawable.juliana, "Juliana Maximo", "texto", 1252, 258, 4));
        mMusicianArrayList.add(new Musician(R.drawable.musician, "Clairo", "texto", 2240300, 152224, 1244));
        mMusicianArrayList.add(new Musician(R.drawable.blackdays, "Black Days", "texto", 32002, 4902, 70));
        mMusicianArrayList.add(new Musician(R.drawable.stonedrunk, "StoneDrunk", "texto", 3202, 750, 35));
        mMusicianArrayList.add(new Musician(R.drawable.ingrime, "Ingrime", "texto", 855, 157, 4));
        mMusicianArrayList.add(new Musician(R.drawable.o_terno, "O Terno", "texto", 182644, 24399, 433));
        mMusicianArrayList.add(new Musician(R.drawable.juliana, "Juliana Maximo", "texto", 1252, 258, 4));
        mMusicianArrayList.add(new Musician(R.drawable.musician, "Clairo", "texto", 2240300, 152224, 1244));
        mMusicianArrayList.add(new Musician(R.drawable.blackdays, "Black Days", "texto", 32002, 4902, 70));
        mMusicianArrayList.add(new Musician(R.drawable.stonedrunk, "StoneDrunk", "texto", 3202, 750, 35));
        mMusicianArrayList.add(new Musician(R.drawable.ingrime, "Ingrime", "texto", 855, 157, 4));
        mMusicianArrayList.add(new Musician(R.drawable.o_terno, "O Terno", "texto", 182644, 24399, 433));
        mMusicianArrayList.add(new Musician(R.drawable.juliana, "Juliana Maximo", "texto", 1252, 258, 4));
        mMusicianArrayList.add(new Musician(R.drawable.musician, "Clairo", "texto", 2240300, 152224, 1244));
        mMusicianArrayList.add(new Musician(R.drawable.blackdays, "Black Days", "texto", 32002, 4902, 70));
        mMusicianArrayList.add(new Musician(R.drawable.stonedrunk, "StoneDrunk", "texto", 3202, 750, 35));
        mMusicianArrayList.add(new Musician(R.drawable.ingrime, "Ingrime", "texto", 855, 157, 4));
        mMusicianArrayList.add(new Musician(R.drawable.o_terno, "O Terno", "texto", 182644, 24399, 433));
        mMusicianArrayList.add(new Musician(R.drawable.juliana, "Juliana Maximo", "texto", 1252, 258, 4));
        mMusicianArrayList.add(new Musician(R.drawable.musician, "Clairo", "texto", 2240300, 152224, 1244));
        mMusicianArrayList.add(new Musician(R.drawable.blackdays, "Black Days", "texto", 32002, 4902, 70));
        mMusicianArrayList.add(new Musician(R.drawable.stonedrunk, "StoneDrunk", "texto", 3202, 750, 35));
        mMusicianArrayList.add(new Musician(R.drawable.ingrime, "Ingrime", "texto", 855, 157, 4));
        mMusicianArrayList.add(new Musician(R.drawable.o_terno, "O Terno", "texto", 182644, 24399, 433));

        mMusiciansAdapter.setData(mMusicianArrayList);

        return view;
    }

    @Override
    public void onListItemClick(int index) {
        Bundle bundle = new Bundle();

        Musician musician = mMusicianArrayList.get(index);

        bundle.putInt("image_resource_id", musician.getProfileImageResourceId());
        bundle.putString("name", musician.getName());

        MusicianFragment musicianFragment = new MusicianFragment();
        musicianFragment.setArguments(bundle);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragments_container, musicianFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
