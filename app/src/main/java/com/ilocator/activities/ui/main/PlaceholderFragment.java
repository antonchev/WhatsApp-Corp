package com.ilocator.activities.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ilocator.R;
import com.ilocator.models.Contact;
import com.ilocator.utils.ContactsApdapter;
import com.ilocator.utils.SpeedyLinearLayoutManager;

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
    private int index;
    private static final int REQUEST_CODE_READ_CONTACTS=1;
    private static boolean READ_CONTACTS_GRANTED =false;
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

        pageViewModel = new PageViewModel(getContext());
        index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);

        }
        pageViewModel.setIndex(index);
       // pageViewModel.getText();
        Log.d("ТАБЫ","PUSSY"+ index);



    }

    Observer<ArrayList<Contact>> userListUpdateObserver = new Observer<ArrayList<Contact>>() {
        @Override
        public void onChanged(ArrayList<Contact> userArrayList) {



            mAdapter = new ContactsApdapter(getContext(),userArrayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            if (index==1) return; else
            recyclerView.setAdapter(mAdapter);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        switch (requestCode){
            case REQUEST_CODE_READ_CONTACTS:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    READ_CONTACTS_GRANTED = true;
                }
        }
        if(READ_CONTACTS_GRANTED){
         //   loadContacts();
            pageViewModel.getContactMutableLiveData().observe((LifecycleOwner) getContext(), userListUpdateObserver);
            Log.d("Заебомба","Контакты");
        }
        else{
            Log.d("Запрещено","Контакты");
           // Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_message, container, false);

        recyclerView=root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        contactArrayList = new ArrayList<>();

        int hasReadContactPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        // если устройство до API 23, устанавливаем разрешение
        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            READ_CONTACTS_GRANTED = true;
        }
        else{
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED){
            pageViewModel.getContactMutableLiveData().observe((LifecycleOwner) getContext(), userListUpdateObserver);
        }


      //  pageViewModel.getContactMutableLiveData().observe((LifecycleOwner) getContext(), userListUpdateObserver);

        return root;
    }
}