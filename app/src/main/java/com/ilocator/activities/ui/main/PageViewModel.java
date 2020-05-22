package com.ilocator.activities.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.ilocator.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class PageViewModel extends ViewModel {
    ArrayList<Contact> contactArrayList;
    MutableLiveData<ArrayList<Contact>> contactLiveData;
    private String tab;
    private Context context;
    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
         //   Log.d("ТАБЫ","inPUSSY"+ input);
         //   Log.d("ТАБЫ", String.valueOf(input));
            return "Hello world from section: " + input;
        }
    });

    public PageViewModel (Context context) {
        this.context = context;
        contactLiveData = new MutableLiveData<>();
        init();
    }








    public MutableLiveData<ArrayList<Contact>> getContactMutableLiveData() {
        return contactLiveData;
    }

    public void init(){



        contactArrayList = new ArrayList<>();
       populateList();
        contactLiveData.setValue(contactArrayList);

    }

    public void populateList(){



        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if ((cursor != null ? cursor.getCount() : 0) > 0) {
            while (cursor.moveToNext()) {

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //String photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Contact contact = new Contact();

                contact.setNumber(phoneNo);
                contact.setName(name);
                Log.e("contact", "getAllContacts: " + name + " " + phoneNo);
                contactArrayList.add(contact);
                Log.d("ПО ОДНОМУ",contact.getName());

            }
        }
        if (cursor != null) {
            cursor.close();
        }

    }

    public void setIndex(int index) {
       // Log.d("ТАБЫ", String.valueOf(index));
        mIndex.setValue(index);

    }

    public LiveData<String> getText() {
        return mText;
    }
}