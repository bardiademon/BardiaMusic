package com.bardiademon.MusicPlayer;

import com.bardiademon.MusicPlayer.bardiademon.ConvertDuration;
import com.bardiademon.BardiaJlayer.javazoom.jl.decoder.JavaLayerException;

public interface On extends OnInfo
{
    void onProgress (final int progress);

    void onMusicTime (final ConvertDuration.Time time);

    void onTime (final ConvertDuration.Time time);

    void onStart ();

    void onStop ();

    void onPause ();

    void onCompleted ();

    void onError (final Exception e);

    void onJavaLayerException (final JavaLayerException e);

    void onDie ();

}
