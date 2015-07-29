package com.artincodes.miniera;


import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.media.RemoteController;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.artincodes.miniera.utils.music.RemoteControlService;
import com.melnykov.fab.FloatingActionButton;
import com.pkmmte.view.CircularImageView;



/**
 * A simple {@link Fragment} subclass.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class MusicFragment extends Fragment {

    private TextView textTitle;
    private TextView textAlbumTitle;
    private TextView textArtist;
    private FloatingActionButton buttonPlay;
    private FloatingActionButton buttonPrevious;
    private FloatingActionButton buttonNext;
    private CircularImageView imageAlbumArt;
    private SeekBar musicSeekBar;
    RelativeLayout musicLayout;
    Bitmap artworkBitmap;

    MediaSessionCompat mediaSessionCompat;


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
            if(mScrubbingSupported) {
                //if scrubbing is supported, we shoul let user use scrubbing SeekBar
                musicSeekBar.setEnabled(true);
                //start SeekBar updater
                mHandler.post(mUpdateSeekBar);
            } else {
                //disabling SeekBar and SeekBar updater
                musicSeekBar.setEnabled(false);
                mHandler.removeCallbacks(mUpdateSeekBar);
            }
        }

        @Override
        public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {
            switch(state) {
                case RemoteControlClient.PLAYSTATE_PLAYING:
                    //if music started playing, we should start updating our SeekBar position
                    //also, update the play/pause icon
                    if(mScrubbingSupported) mHandler.post(mUpdateSeekBar);
                    mIsPlaying = true;
                    buttonPlay.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                default:
                    //if music isn't playing, we should stop updating of SeekBar
                    mHandler.removeCallbacks(mUpdateSeekBar);
                    mIsPlaying = false;
                    buttonPlay.setImageResource(android.R.drawable.ic_media_play);
                    break;
            }

            musicSeekBar.setProgress((int) (currentPosMs * musicSeekBar.getMax() / mSongDuration));
        }

        @Override
        public void onClientPlaybackStateUpdate(int state) {
            switch(state) {
                case RemoteControlClient.PLAYSTATE_PLAYING:
                    if(mScrubbingSupported) mHandler.post(mUpdateSeekBar);
                    mIsPlaying = true;
//                    buttonPlay.setImageResource(android.R.drawable.ic_media_pause);
                    break;
                default:
                    mHandler.removeCallbacks(mUpdateSeekBar);
                    mIsPlaying = false;
//                    buttonPlay.setImageResource(android.R.drawable.ic_media_play);
                    break;
            }
        }


        @Override
        public void onClientMetadataUpdate(RemoteController.MetadataEditor editor) {

            //some players write artist name to METADATA_KEY_ALBUMARTIST instead of METADATA_KEY_ARTIST, so we should double-check it
            textArtist.setText(editor.getString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
                    editor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST, "unknown")
            ));

            textTitle.setText(editor.getString(MediaMetadataRetriever.METADATA_KEY_TITLE, "unknown"));
            textAlbumTitle.setText(editor.getString(MediaMetadataRetriever.METADATA_KEY_ALBUM,"unknown"));

            mSongDuration = editor.getLong(MediaMetadataRetriever.METADATA_KEY_DURATION, 1);
            imageAlbumArt.setImageBitmap(editor.getBitmap(RemoteController.MetadataEditor.BITMAP_KEY_ARTWORK, null));
        }

        @Override
        public void onClientChange(boolean clearing) {

        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.buttonPrev:
                    if(mBound) {
                        mRCService.sendPreviousKey();
                    }
                    break;
                case R.id.buttonNext:
                    if(mBound) {
                        mRCService.sendNextKey();
                    }
                    break;
                case R.id.buttonPlay:
                    if(mBound) {
                        if(mIsPlaying) {
                            mRCService.sendPauseKey();
                        } else {
                            mRCService.sendPlayKey();
                        }
                    }
                    break;
            }
        }
    };
    
    

    public MusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);

        textTitle=(TextView)rootView.findViewById(R.id.textTitle);
        textAlbumTitle = (TextView)rootView.findViewById(R.id.textAlbum);
        textArtist = (TextView)rootView.findViewById(R.id.textArtist);
        musicLayout = (RelativeLayout) rootView.findViewById(R.id.music_layout);
        imageAlbumArt = (CircularImageView)rootView.findViewById(R.id.imageArtWork);
        buttonPlay = (FloatingActionButton)rootView.findViewById(R.id.buttonPlay);
        buttonPrevious = (FloatingActionButton)rootView.findViewById(R.id.buttonPrev);
        buttonNext = (FloatingActionButton)rootView.findViewById(R.id.buttonNext);
        musicSeekBar = (SeekBar)rootView.findViewById(R.id.music_seek);



        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mBound && fromUser) {
                    mRCService.seekTo(mSongDuration * progress/seekBar.getMax());
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
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mBound) {
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
//            mRCService.setRemoteControllerEnabled();
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
            if(mBound) {
                //if service is bound to our activity, we update our position seekbar
                musicSeekBar.setProgress((int) (mRCService.getEstimatedPosition() * musicSeekBar.getMax() / mSongDuration));
                mHandler.postDelayed(this, 1000); //setting up update event after one second
            }
        }
    };

}
