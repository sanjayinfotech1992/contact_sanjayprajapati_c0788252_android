package com.contact;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.contact.adapter.ContactListAdapter;
import com.contact.data.Contact;

public class ContactListFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fab).setOnClickListener(view1 -> NavHostFragment.findNavController(ContactListFragment.this)
                .navigate(R.id.action_ContactListFragment_to_ContactRegisterFragment));


        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ContactListAdapter adapter = new ContactListAdapter(new ContactListAdapter.ItemClickListener() {
            @Override
            public void onEditClicked(Contact item, int pos) {
                Bundle bundle = new Bundle();
                bundle.putInt("cid", item.cid);
                NavHostFragment.findNavController(ContactListFragment.this)
                        .navigate(R.id.action_ContactListFragment_to_ContactRegisterFragment, bundle);
            }

            @Override
            public void onDeleteClicked(Contact item, int pos) {
                new Thread(() -> ContactApp.getInstance().getDataBase().contactDao().delete(item)).start();
            }

            @Override
            public void onItemLongPress(Contact item, int pos) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose an option");
                String[] options = {"Call", "Message", "Email"};
                builder.setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
                            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1001);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.phone));
                                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            }
                            break;
                        case 1:

                            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + item.phone));
                            if (smsIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(smsIntent);
                            }
                            break;
                        case 2:
                            composeEmail(new String[]{item.email}, "");
                            break;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        recyclerView.setAdapter(adapter);

        ((EditText) view.findViewById(R.id.search)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter.getContactList() != null && adapter.getContactList().size() > 0) {
                    adapter.getFilter().filter(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        ContactApp.getInstance().getDataBase().contactDao().getAll().observe(getViewLifecycleOwner(), contacts -> adapter.setData(contacts));
    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}