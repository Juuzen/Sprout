package com.hcifedii.sprout.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.hcifedii.sprout.R;

public class TitleFragment extends Fragment {

    private TextView title;
    private TextInputLayout layout;

    public TitleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_title, container, false);

        title = view.findViewById(R.id.titleField);
        layout = view.findViewById(R.id.titleLayout);

        return view;
    }

    public String getTitle() {
        return title.getText().toString();
    }

    public void setErrorMessage(String message) {
        layout.setError(message);
    }

}