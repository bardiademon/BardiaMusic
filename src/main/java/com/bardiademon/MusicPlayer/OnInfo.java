package com.bardiademon.MusicPlayer;

import com.bardiademon.MusicPlayer.bardiademon.ConvertDuration;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;

import java.io.InputStream;

public interface OnInfo
{
    void onMusicTime (final ConvertDuration.Time time);

    void onName (final String name);

    void onMetadata (final ID3v1 metadata);

    void onMetadata (final ID3v2 metadata);

    void onMetadataError ();

    void onAlbumImage (final InputStream stream);

    void onAlbumImageError ();

    void onCompleteInfo ();
}
