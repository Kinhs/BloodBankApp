package com.group8.bloodbank.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group8.bloodbank.R;
import com.group8.bloodbank.models.DonorData;

import java.util.List;

public class SearchDonorAdapter extends RecyclerView.Adapter<SearchDonorAdapter.PostHolder> {


    private List<DonorData> postLists;

    public class PostHolder extends RecyclerView.ViewHolder
    {
        TextView Name, Address, contact, posted, totaldonate;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            Name = itemView.findViewById(R.id.donorName);
            contact = itemView.findViewById(R.id.donorContact);
            totaldonate = itemView.findViewById(R.id.totaldonate);
            Address = itemView.findViewById(R.id.donorAddress);
            posted = itemView.findViewById(R.id.lastdonate);

        }
    }

    public SearchDonorAdapter(List<DonorData> postLists)
    {
        this.postLists = postLists;
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View listitem = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.search_donor_item, viewGroup, false);

        return new PostHolder(listitem);
    }

    @Override
    public void onBindViewHolder(PostHolder postHolder, int i) {

        if(i%2==0)
        {
            postHolder.itemView.setBackgroundColor(Color.parseColor("#C13F31"));
        }
        else
        {
            postHolder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        DonorData donorData = postLists.get(i);
        postHolder.Name.setText("Name: "+donorData.getName());
        postHolder.contact.setText(donorData.getContact());
        postHolder.Address.setText("Address: "+donorData.getAddress());
        postHolder.totaldonate.setText("Total Donation: "+donorData.getTotalDonate()+" times");
        postHolder.posted.setText("Last Donation: "+donorData.getLastDonate());


    }

    @Override
    public int getItemCount() {
        return postLists.size();
    }
}
