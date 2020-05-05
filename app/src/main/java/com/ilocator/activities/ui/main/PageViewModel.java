package com.ilocator.activities.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ilocator.models.Contact;

import java.util.ArrayList;

public class PageViewModel extends ViewModel {
    ArrayList<Contact> contactArrayList;
    MutableLiveData<ArrayList<Contact>> contactLiveData;
    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {

            return "Hello world from section: " + input;
        }
    });

    public PageViewModel () {
        contactLiveData = new MutableLiveData<>();
        init();
    }

    public MutableLiveData<ArrayList<Contact>> getContactMutableLiveData() {
        return contactLiveData;
    }

    public void init(){
        populateList();
        contactLiveData.setValue(contactArrayList);
    }

    public void populateList(){

        Contact user = new Contact();
       user.setName("Emmanuelle");

        contactArrayList = new ArrayList<>();
        contactArrayList.add(user);
        contactArrayList.add(user);
        contactArrayList.add(user);
        contactArrayList.add(user);
        contactArrayList.add(user);
        contactArrayList.add(user);
    }

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
}