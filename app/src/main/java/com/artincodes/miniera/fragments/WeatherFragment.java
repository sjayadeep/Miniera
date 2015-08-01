package com.artincodes.miniera.fragments;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.gmailcontract.GmailContract;
import com.artincodes.miniera.utils.weather.WeatherDBHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {


    private TextView temperatureText;
    private TextView temperatureDes;
    private TextView locationText;
    private TextView callCountText;
    private TextView smsCountText;
    private TextView gmailCountText;

    public int countUnread = 0;
    String un;


    String TAG = "WEATHER FRAGMENT";

//    private CircularProgressBar circularProgressBar;


    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);


        temperatureText = (TextView) rootView.findViewById(R.id.temperature);
        temperatureDes = (TextView) rootView.findViewById(R.id.temperature_desc);
        locationText = (TextView) rootView.findViewById(R.id.location);
        callCountText = (TextView) rootView.findViewById(R.id.call_count);
        smsCountText = (TextView) rootView.findViewById(R.id.sms_count);
        gmailCountText = (TextView)rootView.findViewById(R.id.mail_count);
        Typeface robotoCond = Typeface.createFromAsset(getActivity().getAssets(), "roboto_thin.ttf");

        temperatureText.setTypeface(robotoCond);

        getGmailUnreadCount();
//        circularProgressBar = (CircularProgressBar) rootView.findViewById(R.id.progress_weather);
//        circularProgressBar.setVisibility(View.INVISIBLE);

//        un

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        WeatherDBHelper weatherDBHelper = new WeatherDBHelper(getActivity());
        Cursor cursor = weatherDBHelper.getWeather();
        cursor.moveToFirst();
        try {

            temperatureText.setText(cursor.getString(0) + (char) 0x00B0);
            temperatureDes.setText(cursor.getString(1));
            locationText.setText(cursor.getString(2));

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        cursor.close();

        callCountText.setText("" + getMissedCallCount());
        smsCountText.setText("" + getUnreadSMSCount());



    }


    public int getMissedCallCount() {
        String[] projection = {CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.TYPE};
        String where = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE + " AND NEW = 1";
        Cursor c = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, where, null, CallLog.Calls.DATE + " DESC");
        c.moveToFirst();
        //Log.d("CALL", "" + c.getCount()); //do some other operation
        ///Toast.makeText(getActivity(), c.getCount() + ",", Toast.LENGTH_SHORT).show();
        return c.getCount();
    }

    public int getUnreadSMSCount() {
        final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

        Cursor c = getActivity().getContentResolver().query(SMS_INBOX, null, "read = 0", null, null);
        return c.getCount();
    }

    public void getGmailUnreadCount() {


        final String ACCOUNT_TYPE_GOOGLE = "com.google";
        final String[] FEATURES_MAIL = {
                "service_mail"
        };
        AccountManager.get(getActivity()).getAccountsByTypeAndFeatures(ACCOUNT_TYPE_GOOGLE, FEATURES_MAIL,
                new AccountManagerCallback<Account[]>() {
                    @Override
                    public void run(AccountManagerFuture<Account[]> future) {
                        Account[] accounts = null;
                        try {
                            accounts = future.getResult();
                            if (accounts != null && accounts.length > 0) {
                                String selectedAccount = accounts[0].name;
                                Cursor c = getActivity().getContentResolver().query
                                        (GmailContract.Labels.getLabelsUri(selectedAccount),
                                                null, null, null, null);
                                if (c != null) {
                                    final String inboxCanonicalName = GmailContract.Labels.
                                            LabelCanonicalNames.CANONICAL_NAME_INBOX;
                                    final int canonicalNameIndex = c.getColumnIndexOrThrow
                                            (GmailContract.Labels.CANONICAL_NAME);

//                                    c.moveToFirst();

                                    while (c.moveToNext()) {


                                        Log.i(TAG,inboxCanonicalName +" : " + c.getString(canonicalNameIndex));


                                        if (inboxCanonicalName.equals(c.getString(canonicalNameIndex))||"^sq_ig_i_personal".equals(c.getString(canonicalNameIndex))) {

                                            Log.i(TAG,"inside gmail function");


                                            int count = c.getColumnIndexOrThrow(GmailContract.Labels.
                                                    NUM_UNREAD_CONVERSATIONS);
                                            un = c.getString(count);
//                                            Toast.makeText(getActivity(), un,
//                                                    Toast.LENGTH_LONG).show();
                                            gmailCountText.setText(un);

//                                            countUnread = Integer.parseInt(un);

                                        }
                                    }
                                }

                            }
                        } catch (OperationCanceledException oce) {
                            Log.i(TAG,"OCE");
                            //Handling
                        } catch (IOException ioe) {
                            //handling
                            Log.i(TAG,"ioe");

                        } catch (AuthenticatorException ae) {
                            //handle
                            Log.i(TAG,"ae");

                        }catch (NullPointerException e){
                            Log.i(TAG,"NPE");
                        }

                    }
                }, null);
//        return un;
    }


}
