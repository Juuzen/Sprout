package com.hcifedii.sprout.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcifedii.sprout.R;
import com.hcifedii.sprout.adapter.PresetHabitAdapter;

import java.util.LinkedList;
import java.util.List;

import model.Habit;


public class PresetFragment extends Fragment {

    Context context;

    public PresetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preset_list, container, false);
//
        context = view.getContext();

        RecyclerView presetHabitList = view.findViewById(R.id.presetHabitRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        presetHabitList.setLayoutManager(linearLayoutManager);

        presetHabitList.setAdapter(new PresetHabitAdapter(createPresetHabitList()));


//        // Set the adapter
//        if (view instanceof RecyclerView) {
//            Context context = view.getContext();
//            RecyclerView recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
//            recyclerView.setAdapter(new PresetHabitRecyclerViewAdapter(DummyContent.ITEMS));
//        }
        return view;
    }


    private List<Habit> createPresetHabitList(){

        List<Habit> list = new LinkedList<Habit>();

        Habit habit1 = new Habit(), habit2 = new Habit(), habit3 = new Habit();
        habit1.setTitle("Bevi più acqua");
        habit2.setTitle("Fai più sport");
        habit3.setTitle("Fai meditazione");

        list.add(habit1);
        list.add(habit2);
        list.add(habit3);

        return list;
    }
}