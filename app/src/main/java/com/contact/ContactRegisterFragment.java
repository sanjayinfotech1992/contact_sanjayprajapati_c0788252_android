package com.contact;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.contact.data.Contact;
import com.google.android.material.snackbar.Snackbar;

public class ContactRegisterFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_register, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int cid = getArguments() != null ? getArguments().getInt("cid") : -1;

        EditText firstName = view.findViewById(R.id.firstname);
        EditText lastName = view.findViewById(R.id.lastname);
        EditText phone = view.findViewById(R.id.phone);
        EditText email = view.findViewById(R.id.email);
        EditText address = view.findViewById(R.id.address);
        Button register = view.findViewById(R.id.register);

        if (cid != -1) {
            //Edit
            ContactApp.getInstance().getDataBase().contactDao().findById(cid).observe(getViewLifecycleOwner(), contact -> {
                firstName.setText(contact.firstName);
                lastName.setText(contact.lastName);
                email.setText(contact.email);
                phone.setText(contact.phone);
                address.setText(contact.address);
                register.setText("Update");
            });


        }


        register.setOnClickListener(view1 -> {
            hideKeyboard();

            Contact contact = new Contact();
            contact.firstName = firstName.getText().toString();
            contact.lastName = lastName.getText().toString();
            contact.email = email.getText().toString();
            contact.phone = phone.getText().toString();
            contact.address = address.getText().toString();

            new Thread(() -> {
                if (cid != -1) {
                    contact.cid = cid;
                    ContactApp.getInstance().getDataBase().contactDao().updateAll(contact);
                } else {
                    ContactApp.getInstance().getDataBase().contactDao().insertAll(contact);
                }

                getActivity().runOnUiThread(() -> {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), cid != -1 ? "Contact updated" : "Contact created", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ok", view2 -> NavHostFragment.findNavController(ContactRegisterFragment.this).navigateUp())
                            .setActionTextColor(Color.RED)
                            .show();
                });

            }).start();


        });

    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}