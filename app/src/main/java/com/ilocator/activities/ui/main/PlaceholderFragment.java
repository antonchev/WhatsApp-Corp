package com.ilocator.activities.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ilocator.R;
import com.ilocator.models.Contact;
import com.ilocator.utils.ContactsApdapter;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements LifecycleOwner {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<Contact> contactArrayList;
    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);


    }

    Observer<ArrayList<Contact>> userListUpdateObserver = new Observer<ArrayList<Contact>>() {
        @Override
        public void onChanged(ArrayList<Contact> userArrayList) {
            mAdapter = new ContactsApdapter(getContext(),userArrayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(mAdapter);
        }
    };

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_message, container, false);

        recyclerView=root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        contactArrayList = new ArrayList<>();
        pageViewModel.getContactMutableLiveData().observe((LifecycleOwner) getContext(), userListUpdateObserver);

        return root;
    }
}