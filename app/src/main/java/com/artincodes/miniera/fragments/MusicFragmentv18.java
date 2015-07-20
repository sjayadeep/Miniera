package com.artincodes.miniera.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.artincodes.miniera.MainActivity;
import com.artincodes.miniera.R;
import com.melnykov.fab.FloatingActionButton;
import com.pkmmte.view.CircularImageView;
import com.woodblockwithoutco.remotemetadataprovider.v18.media.RemoteMetadataProvider;
import com.woodblockwithoutco.remotemetadataprovider.v18.media.enums.MediaCommand;
import com.woodblockwithoutco.remotemetadataprovider.v18.media.enums.PlayState;
import com.woodblockwithoutco.remotemetadataprovider.v18.media.listeners.OnArtworkChangeListener;
import com.woodblockwithoutco.remotemetadataprovider.v18.media.listeners.OnMetadataChangeListener;
import com.woodblockwithoutco.remotemetadataprovider.v18.media.listeners.OnPlaybackStateChangeListener;

public  class MusicFragmentv18 extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String NO_CLIENT = "Client state: NO CLIENT";
    protected static final String CLIENT_ACTIVE = "Client state: ACTIVE";
    private TextView textTitle;
    private TextView textAlbumTitle;
    private TextView textArtist;
    //private TextView mAlbumTextView;
    private FloatingActionButton buttonPlay;
    private FloatingActionButton buttonPrevious;
    private FloatingActionButton buttonNext;
    private SeekBar musicSeekBar;


    private CircularImageView imageAlbumArt;
    Bitmap blurArtWork;
    private RemoteMetadataProvider mProvider;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MusicFragmentv18 newInstance(int sectionNumber) {
        MusicFragmentv18 fragment = new MusicFragmentv18();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MusicFragmentv18() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);
        textTitle=(TextView)rootView.findViewById(R.id.textTitle);
        textAlbumTitle = (TextView)rootView.findViewById(R.id.textAlbum);
        textArtist = (TextView)rootView.findViewById(R.id.textArtist);
//        textAlbumTitle.setShadowLayer(3, 0, 2, Color.BLACK);
//        textArtist.setShadowLayer(3, 0, 2, Color.BLACK);
        //textAlbumTitle.setTypeface(MyActivity.robotoCond);
       // textSubTitles.setTypeface(MyActivity.robotoCond);
        imageAlbumArt = (CircularImageView)rootView.findViewById(R.id.imageArtWork);
        buttonPlay = (FloatingActionButton)rootView.findViewById(R.id.buttonPlay);
        buttonPrevious = (FloatingActionButton)rootView.findViewById(R.id.buttonPrev);
        buttonNext = (FloatingActionButton)rootView.findViewById(R.id.buttonNext);
        musicSeekBar = (SeekBar)rootView.findViewById(R.id.music_seek);
        musicSeekBar.setVisibility(View.GONE);

        mProvider = RemoteMetadataProvider.getInstance(getActivity());


        mProvider.setOnMetadataChangeListener(new OnMetadataChangeListener() {
            @Override
            public void onMetadataChanged(String artist, String title,
                                          String album, String albumArtist, long duration) {
                //mArtistTextView.setText("ARTIST: "+artist);
                textTitle.setText(title);
                textAlbumTitle.setText(album);
                textArtist.setText(artist);
            }
        });

        imageAlbumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = null;
                try {
                    launchIntent = mProvider.getCurrentClientIntent();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (launchIntent !=null)
                    startActivity(launchIntent);
                else
                    Toast.makeText(getActivity().getApplicationContext(), "Open your Music Player", Toast.LENGTH_SHORT).show();
            }
        });

        mProvider.setOnPlaybackStateChangeListener(new OnPlaybackStateChangeListener() {


            @Override
            public void onPlaybackStateChanged(PlayState playState, long l, float v) {

                if (playState.toString().equals("PLAYING")){
//                    buttonPlay.setImageDrawable(getResources().getDrawable(R.mipmap.ic_delete));
                }else {
//                    buttonPlay.setImageDrawable(getResources().getDrawable(R.mipmap.ic_delete));
                }

            }
        });

        mProvider.setOnArtworkChangeListener(new OnArtworkChangeListener() {
            @Override
            public void onArtworkChanged(Bitmap artwork) {
                imageAlbumArt.setImageBitmap(artwork);
//                blurArtWork = BlurBuilder.fastblur(artwork,12);
            }
        });

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mProvider.sendMediaCommand(MediaCommand.PLAY_PAUSE)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Open your Music Player", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mProvider.sendMediaCommand(MediaCommand.PREVIOUS)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Open your Music Player", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mProvider.sendMediaCommand(MediaCommand.NEXT)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Open your Music Player", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();

        //acquiring remote media controls
        mProvider.acquireRemoteControls();
    }

    @Override
    public void onPause() {
        super.onPause();

        //dropping remote media controls
        mProvider.dropRemoteControls(true);
    }

}