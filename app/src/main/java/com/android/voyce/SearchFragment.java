package com.android.voyce;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    RecyclerView mMusiciansGridRecyclerView;

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
        mMusiciansAdapter = new MusiciansAdapter();

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mMusiciansGridRecyclerView.setLayoutManager(layoutManager);

        mMusiciansGridRecyclerView.setHasFixedSize(true);
        mMusiciansGridRecyclerView.setAdapter(mMusiciansAdapter);

        String[] musicians = new String[] {"joao", "vituxo", "leo", "biri"};

        mMusiciansAdapter.setData(musicians);

        return view;
    }

}
