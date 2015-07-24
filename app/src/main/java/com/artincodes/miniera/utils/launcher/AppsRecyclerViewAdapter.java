package com.artincodes.miniera.utils.launcher;

/**
 * Created by jayadeep on 22/7/15.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.AppPack;

import java.util.ArrayList;
import java.util.List;


public class AppsRecyclerViewAdapter extends RecyclerView.Adapter<AppsRecyclerViewAdapter.SectionAppsHolder> {


    List<List<AppPack>>fullAppsList = new ArrayList<>();
    Context mContext;

    public AppsRecyclerViewAdapter(Context mContext,List<List<AppPack>>fullAppsList) {
        this.fullAppsList = fullAppsList;
        this.mContext = mContext;
    }



    @Override
    public SectionAppsHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.app_section_card_layout, parent, false);


//        itemView.setCar
        return new SectionAppsHolder(itemView);

    }

    @Override
    public void onBindViewHolder(SectionAppsHolder holder, int position) {

//        ContactInfo ci = contactList.get(i);
//        contactViewHolder.vName.setText(ci.name);
//        contactViewHolder.vSurname.setText(ci.surname);
//        contactViewHolder.vEmail.setText(ci.email);
//        contactViewHolder.vTitle.setText(ci.name + " " + ci.surname);

        List<AppPack> sectionApps = fullAppsList.get(position);
        holder.sectionGridView.setAdapter(new DrawerAdapter(mContext, sectionApps));

        DrawerClickListener drawerClickListener = new DrawerClickListener(mContext,sectionApps);
        DrawerLongClickListener drawerLongClickListener = new DrawerLongClickListener(mContext,sectionApps);
        holder.sectionGridView.setOnItemClickListener(drawerClickListener);
        holder.sectionGridView.setOnItemLongClickListener(drawerLongClickListener);

        holder.sectionHeader.setText(sectionApps.get(0).label.substring(0,1).toUpperCase());

    }

    @Override
    public int getItemCount() {
        return fullAppsList.size();
    }

    public static class SectionAppsHolder extends RecyclerView.ViewHolder {
        protected TextView sectionHeader;
        protected StaticGridView sectionGridView;

        public SectionAppsHolder(View v) {
            super(v);
            sectionHeader =  (TextView) v.findViewById(R.id.section_header);
            sectionGridView = (StaticGridView)  v.findViewById(R.id.section_grid);
        }
    }


}
