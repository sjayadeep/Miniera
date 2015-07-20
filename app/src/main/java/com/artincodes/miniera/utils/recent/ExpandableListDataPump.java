package com.artincodes.miniera.utils.recent;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static Context mContext;

    public ExpandableListDataPump(Context context){

        mContext=context;

    }
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> whatsapp = new ArrayList<String>();
        RecentDBHelper recentDBHelper = new RecentDBHelper(mContext);
        Cursor c = recentDBHelper.getWhatsappRecentItems();
        c.moveToFirst();

        while (!c.isAfterLast()){
            whatsapp.add(c.getString(1));
            c.moveToNext();
        }
//        technology.add("Beats sued for noise-cancelling tech");
//        technology.add("Wikipedia blocks US Congress edits");
//        technology.add("Google quizzed over deleted links");
//        technology.add("Nasa seeks aid with Earth-Mars links");
//        technology.add("The Good, the Bad and the Ugly");

        List<String> entertainment = new ArrayList<String>();
        entertainment.add("Goldfinch novel set for big screen");
        entertainment.add("Anderson stellar in Streetcar");
        entertainment.add("Ronstadt receives White House honour");
        entertainment.add("Toronto to open with The Judge");
        entertainment.add("British dancer return from Russia");

        List<String> science = new ArrayList<String>();
        science.add("Eggshell may act like sunblock");
        science.add("Brain hub predicts negative events");
        science.add("California hit by raging wildfires");
        science.add("Rosetta's comet seen in close-up");
        science.add("Secret of sandstone shapes revealed");

        expandableListDetail.put("WhatsApp", whatsapp);
        expandableListDetail.put("Message", entertainment);
        expandableListDetail.put("Calls", science);
        return expandableListDetail;
    }
}
