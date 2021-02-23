package com.contact.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.contact.R;
import com.contact.data.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactListAdapter extends
        RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> implements Filterable {

    private List<Contact> contactList;
    private List<Contact> filteredContactList;
    private ItemClickListener itemClickListener;

    public ContactListAdapter(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setData(List<Contact> contactList) {
        this.contactList = contactList;
        this.filteredContactList = new ArrayList<>(contactList);
        notifyDataSetChanged();
    }

    public List<Contact> getContactList(){
        return this.contactList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredContactList = (List<Contact>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filteredContactList.clear();
                if (constraint.length() == 0) {
                    filteredContactList.addAll(contactList);
                } else {
                    filteredContactList = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredContactList;
                results.count = filteredContactList.size();
                return results;
            }
        };
    }

    protected List<Contact> getFilteredResults(String constraint) {
        List<Contact> results = new ArrayList<>();
        for (Contact item : contactList) {
            if (item.firstName.toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_contact, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = filteredContactList.get(position);
        holder.name.setText(contact.firstName + " " + contact.lastName);
        holder.email.setText(contact.email);
        holder.phone.setText(contact.phone);
        holder.address.setText(contact.address);
        holder.edit.setOnClickListener(view -> {
            itemClickListener.onEditClicked(contact, position);
        });

        holder.delete.setOnClickListener(view -> {
            itemClickListener.onDeleteClicked(contact, position);
        });
        holder.itemView.setOnLongClickListener(view -> {
            itemClickListener.onItemLongPress(contact, position);
            return false;
        });
    }

    public interface ItemClickListener {

        void onEditClicked(Contact item, int pos);

        void onDeleteClicked(Contact item, int pos);

        void onItemLongPress(Contact item, int pos);
    }

    @Override
    public int getItemCount() {
        return filteredContactList != null ? filteredContactList.size() : 0;
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView email;
        public TextView phone;
        public TextView address;
        public ImageView edit;
        public ImageView delete;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
