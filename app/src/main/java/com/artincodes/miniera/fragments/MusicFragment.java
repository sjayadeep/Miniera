package com.artincodes.miniera.fragments;


import android.annotation.TargetApi;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.media.RemoteController;
import android.media.RemoteController.MetadataEditor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.artincodes.miniera.R;
import com.artincodes.miniera.utils.music.RemoteControlService;
import com.melnykov.fab.FloatingActionButton;
import com.pkmmte.view.CircularImageView;


@TargetApi(Build.VERSION_CODES.KITKAT)

public class MusicFragment extends Fragment {

    public final static String START_REMOTE_CONTROLLER_ACTION = "com.artincodes.miniera.utils.music.START_REMOTE_CONTROLLER";

    //Views in the Activity
    protected FloatingActionButton mPrevButton;
    protected FloatingActionButton mPlayPauseButton;
    protected FloatingActionButton mNextButton;
    protected TextView mArtistText;
    protected TextView mTitleText;
    protected TextView mAlbumText;
    protected SeekBar mScrubBar;
    protected CircularImageView mArtwork;
    RelativeLayout musicLayout;

    String TAG = "MUSIC FRAGMENT";


    protected RemoteControlService mRCService;
    protected boolean mBound = false; //flag indicating if service is bound to Activity

    protected Handler mHandler = new Handler();

    protected boolean mIsPlaying = false; //flag indicating if music is playing
    protected long mSongDuration = 1;

    private RemoteController.OnClientUpdateListener mClientUpdateListener = new RemoteController.OnClientUpdateListener() {

        private boolean mScrubbingSupported = false; //is scrubbing supported?

        private boolean isScrubbingSupported(int flags) {
            //if "flags" bitmask contains certain bits it means that srcubbing is supported
            return (flags & RemoteControlClient.FLAG_KEY_MEDIA_POSITION_UPDATE) != 0;
        }

        @Override
        public void onClientTransportControlUpdate(int transportControlFlags) {
            mScrubbingSupported = isScrubbingSupported(transportControlFlags);
            if (mScrubbingSupported) {
                //if scrubbing is supported, we shoul let user use scrubbing SeekBar
                mScrubBar.setEnabled(true);
                //start SeekBar updater
                mHandler.post(mUpdateSeekBar);
            } else {
                //disabling SeekBar and SeekBar updater
                mScrubBar.setEnabled(false);
                mHandler.removeCallbacks(mUpdateSeekBar);
            }
        }

        @Override
        public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
            switch (state) {
                case RemoteControlClient.PLAYSTATE_PLAYING:
                    //if music started playing, we should start updating our SeekBar position
                    //also, update the play/pause icon
                    if (mScrubbingSupported) mHandler.post(mUpdateSeekBar);
                    mIsPlaying = true;
                    mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                default:
                    //if music isn't playing, we should stop updating of SeekBar
                    mHandler.removeCallbacks(mUpdateSeekBar);
                    mIsPlaying = false;
                    mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    break;
            }

            mScrubBar.setProgress((int) (currentPosMs * mScrubBar.getMax() / mSongDuration));
        }

        @Override
        public void onClientPlaybackStateUpdate(int state) {
            switch (state) {
                case RemoteControlClient.PLAYSTATE_PLAYING:
                    if (mScrubbingSupported) mHandler.post(mUpdateSeekBar);
                    mIsPlaying = true;
                    mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                default:
                    mHandler.removeCallbacks(mUpdateSeekBar);
                    mIsPlaying = false;
                    mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    break;
            }
        }

        @Override
        public void onClientMetadataUpdate(MetadataEditor editor) {

            //some players write artist name to METADATA_KEY_ALBUMARTIST instead of METADATA_KEY_ARTIST, so we should double-check it
            mArtistText.setText(editor.getString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
                            editor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, "unknown"))
            );

            mTitleText.setText(editor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE, "unknown"));
            mAlbumText.setText(editor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUM, "unknown"));

            mSongDuration = editor.getLong(MediaMetadataRetriever.METADATA_KEY_DURATION, 1);
            try {
                mArtwork.setImageBitmap(editor.getBitmap(MetadataEditor.BITMAP_KEY_ARTWORK, null));

            }catch (NullPointerException e){
                Log.i(TAG,"NO ALBUM ART");
            }
        }

        @Override
        public void onClientChange(boolean clearing) {

        }
    };

//    private OnClickListener mClickListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            switch(v.getId()) {
//                case R.id.prev_button:
//                    if(mBound) {
//                        mRCService.sendPreviousKey();
//                    }
//                    break;
//                case R.id.next_button:
//                    if(mBound) {
//                        mRCService.sendNextKey();
//                    }
//                    break;
//                case R.id.play_pause_button:
//                    if(mBound) {
//                        if(mIsPlaying) {
//                            mRCService.sendPauseKey();
//                        } else {
//                            mRCService.sendPlayKey();
//                        }
//                    }
//                    break;
//            }
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);

        mPrevButton = (FloatingActionButton) rootView.findViewById(R.id.buttonPrev);
        mPlayPauseButton = (FloatingActionButton) rootView.findViewById(R.id.buttonPlay);
        mNextButton = (FloatingActionButton) rootView.findViewById(R.id.buttonNext);
        musicLayout = (RelativeLayout) rootView.findViewById(R.id.music_layout);
        mPrevButton.setImageResource(android.R.drawable.ic_media_previous);
        mNextButton.setImageResource(android.R.drawable.ic_media_next);

        mTitleText = (TextView) rootView.findViewById(R.id.textTitle);
        mAlbumText = (TextView) rootView.findViewById(R.id.textAlbum);
        mArtistText = (TextView) rootView.findViewById(R.id.textAlbum);

        mArtwork = (CircularImageView) rootView.findViewById(R.id.imageArtWork);

        mScrubBar = (SeekBar) rootView.findViewById(R.id.music_seek);

        mPrevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    mRCService.sendPreviousKey();
                    }
                }
            }
        );
        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){
                    mRCService.sendNextKey();
                }
            }
        });
        mPlayPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    if (mIsPlaying) {
                        mRCService.sendPauseKey();
                    } else {
                        mRCService.sendPlayKey();
                    }
                }
            }
        });


        mScrubBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mBound && fromUser) {
                    mRCService.seekTo(mSongDuration * progress / seekBar.getMax());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.post(mUpdateSeekBar);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent("com.artincodes.miniera.utils.music.BIND_RC_CONTROL_SERVICE");
        intent.setPackage("com.artincodes.miniera");
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            mRCService.setRemoteControllerDisabled();
        }
        getActivity().unbindService(mConnection);
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            //Getting the binder and activating RemoteController instantly
            RemoteControlService.RCBinder binder = (RemoteControlService.RCBinder) service;
            mRCService = binder.getService();
//            try {
//
//            }catch (SecurityException)
            mRCService.setRemoteControllerEnabled();
            mRCService.setClientUpdateListener(mClientUpdateListener);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private Runnable mUpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mBound) {
                //if service is bound to our activity, we update our position seekbar
                mScrubBar.setProgress((int) (mRCService.getEstimatedPosition() * mScrubBar.getMax() / mSongDuration));
                mHandler.postDelayed(this, 1000); //setting up update event after one second
            }
        }
    };

    @Override
    public void onResume(){
        super.onResume();

//        if (mBound){
//            musicLayout.setVisibility(View.VISIBLE);
//
//        }else {
//            musicLayout.setVisibility(View.GONE);
//        }
//        Toast.makeText(getActivity(),"RESUME",Toast.LENGTH_SHORT).show();
    }

}
