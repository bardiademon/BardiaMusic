package com.bardiademon.MusicPlayer;

import java.io.InputStream;

public interface MusicHandler
{
    void stop ();

    void start ();

    void pause ();

    void close ();

    void setProgress (final int Progress);

    void setMusic (final String Path , final On _On);

    void setMusic (final InputStream _InputStream , final String Path , final On _On);

    void setMusic (final InputStream _InputStream , final On _On);

    void die ();

    void setVolume (float volume);

    void getMetadata ();
}
