package com.bardiademon.MusicPlayer;

import com.bardiademon.MusicPlayer.bardiademon.ConvertDuration;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.ID3v2;
import com.bardiademon.BardiaJlayer.javazoom.jl.decoder.JavaLayerException;
import com.bardiademon.BardiaJlayer.javazoom.jl.player.Player;
import org.apache.commons.io.FilenameUtils;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;

public final class BardiaPlayer implements MusicHandler
{
    private boolean stop;

    private boolean pause;

    private On on;

    private BMPlayer player;

    private long pauseLocation = 0;

    private InputStream stream;

    private long lenMusic;

    private String pathMusic;

    private final ConvertDuration convertDuration = new ConvertDuration ();

    private long allPosition;

    private int progress = 0;

    private InputStream inputStream;

    /**
     * baraye ke agar next zade shod ta zamani ke next anjam nashode amaliyat digeiy anjam nashe
     */
    private boolean controlBlock = false;

    @Override
    public void stop ()
    {
        if (isNotControlBlock ()) _stop ();
    }

    private void _stop ()
    {
        if (!stop && player != null)
        {
            setTrueControlBlock ();
            on.onStop ();
            player.close ();
            stop = true;
            System.gc ();
            setFalseControlBlock ();
        }
    }

    private void setTrueControlBlock ()
    {
        controlBlock = true;
    }

    private void setFalseControlBlock ()
    {
        controlBlock = false;
    }

    private boolean isNotControlBlock ()
    {
        return !controlBlock;
    }

    @Override
    public void start ()
    {
        if (isNotControlBlock ()) _start ();
    }

    private void _start ()
    {
        try
        {
            setTrueControlBlock ();
            if (pauseLocation > 0) stream.skip (pauseLocation);

            if (check () == null)
            {
                metadata ();
                startMusic ();
            }
            else setFalseControlBlock ();
        }
        catch (final IOException e)
        {
            on.onError (e);
        }
    }

    @Override
    public void close ()
    {
        if (isNotControlBlock ()) _close ();
    }

    private void _close ()
    {
        setTrueControlBlock ();
        progress = 0;
        if (player != null) player.close ();
        pause = false;
        pauseLocation = 0;
        stop = false;
        if (stream != null) streamClose ();
        setFalseControlBlock ();
    }

    public JavaLayerException check ()
    {
        if (_setMusic ())
        {
            try
            {
                _stop ();
                player = new BMPlayer (stream);
            }
            catch (final JavaLayerException e)
            {
                if (on != null)
                    on.onJavaLayerException (e);

                return e;
            }
        }
        return null;
    }


    @Override
    public void pause ()
    {
        if (isNotControlBlock ()) _pause ();
    }

    private void _pause ()
    {
        if (!pause && !stop && player != null)
        {
            setTrueControlBlock ();
            pauseLocation = progressCalculate (progressCalculate ());
            progress = progressCalculate ();
            pause = true;
            _stop ();
            on.onPause ();
        }
        else if (pause)
        {
            setTrueControlBlock ();
            pause = false;
            stop = false;
            _setProgress (progress);
        }
    }

    @Override
    public void setProgress (final int Progress)
    {
        if (isNotControlBlock ()) _setProgress (Progress);
    }

    private void _setProgress (final int Progress)
    {
        if (Progress >= 0 && Progress <= 100)
        {
            setMusic (pathMusic , on);
            setTrueControlBlock ();
            pauseLocation = progressCalculate (Progress);
            this.progress = Progress;

            _start ();
        }
    }

    @Override
    public void setMusic (final String Path , final On _On)
    {
        _setMusic (null , Path , _On);
    }

    @Override
    public void setMusic (final InputStream _InputStream , final String Path , final On _On)
    {
        _setMusic (_InputStream , Path , _On);
    }

    @Override
    public void setMusic (final InputStream _InputStream , final On _On)
    {
        _setMusic (_InputStream , null , _On);
    }

    private void _setMusic (final InputStream inputStream , final String path , final On _On)
    {
        setTrueControlBlock ();
        if (stream != null && player != null) _stop ();
        _close ();
        this.on = _On;

        if (inputStream != null) this.inputStream = inputStream;
        if (path != null) pathMusic = path;

        setStream ();
        pauseLocation = 0;
        stop = false;
        pause = false;
        player = null;
        setFalseControlBlock ();
    }

    @Override
    public void die ()
    {
        setTrueControlBlock ();
        _close ();
        player = null;
        stream = null;
        stop = true;
        progress = 0;
        pauseLocation = 0;
        pathMusic = null;

        if (inputStream != null)
        {
            try
            {
                inputStream.close ();
                inputStream = null;
            }
            catch (final IOException e)
            {
                on.onError (e);
            }
        }

        on.onDie ();
        on = null;
        System.gc ();
        setFalseControlBlock ();
    }

    @Override
    public void setVolume (float volume)
    {
        if (player != null && !isStop () && !isPause ())
        {
            if (volume >= -80.0) player.setVolume (volume);
        }
    }

    @Override
    public void getMetadata ()
    {
        metadata ();
    }

    public void onInfo (final String musicPath , final OnInfo onInfo)
    {
        metadata (onInfo , musicPath);
    }

    private void setStream ()
    {
        try
        {
            _stop ();
            if (inputStream != null) stream = inputStream;
            else
            {
                stream = new FileInputStream (pathMusic);
                if (on != null) on.onName (FilenameUtils.getName (pathMusic));
            }
            lenMusic = stream.available ();
        }
        catch (final IOException e)
        {
            on.onError (e);
        }
    }

    private void metadata ()
    {
        metadata (null , pathMusic);
    }

    private void metadata (final OnInfo onInfo , final String pathMusic)
    {
        new Thread (() ->
        {
            try
            {
                if (pathMusic != null)
                {
                    final Mp3File mp3File = new Mp3File (pathMusic);

                    if (onInfo != null) onInfo.onName (FilenameUtils.getName (pathMusic));

                    if (onInfo == null) on.onMusicTime (convertDuration.convert (mp3File.getLengthInSeconds ()));
                    else onInfo.onMusicTime (convertDuration.convert (mp3File.getLengthInSeconds ()));

                    allPosition = mp3File.getLengthInMilliseconds ();

                    if (mp3File.hasId3v2Tag ())
                    {
                        final ID3v2 id3v2Tag = mp3File.getId3v2Tag ();
                        if (onInfo == null) on.onMetadata (id3v2Tag);
                        else onInfo.onMetadata (id3v2Tag);

                        final byte[] albumImage = id3v2Tag.getAlbumImage ();
                        if (albumImage != null && albumImage.length > 0)
                        {
                            if (onInfo == null) on.onAlbumImage (new ByteArrayInputStream (albumImage));
                            else onInfo.onAlbumImage (new ByteArrayInputStream (albumImage));
                        }
                        else
                        {
                            if (onInfo == null) on.onAlbumImageError ();
                            else onInfo.onAlbumImageError ();
                        }
                    }
                    else if (mp3File.hasId3v1Tag ())
                    {
                        if (onInfo == null)
                        {
                            on.onAlbumImageError ();
                            on.onMetadata (mp3File.getId3v1Tag ());
                        }
                        else
                        {
                            onInfo.onAlbumImageError ();
                            onInfo.onMetadata (mp3File.getId3v1Tag ());
                        }
                    }
                    else
                    {
                        if (onInfo == null)
                        {
                            on.onAlbumImageError ();
                            on.onMetadataError ();
                        }
                        else
                        {
                            onInfo.onAlbumImageError ();
                            onInfo.onMetadataError ();
                        }
                    }
                }
                else
                {
                    on.onMetadataError ();
                    on.onAlbumImageError ();
                }
            }
            catch (final Exception e)
            {
                if (on != null)
                {
                    on.onError (e);
                    die ();
                }
            }
            finally
            {
                System.gc ();
                if (onInfo == null)
                {
                    if (on != null) on.onCompleteInfo ();
                }
                else onInfo.onCompleteInfo ();
            }
        }).start ();
    }

    private int progressCalculate ()
    {
        if (stream != null && player != null && allPosition > 0)
            return (int) (((player.getPosition () * 100) / allPosition) + progress);
        return 0;
    }

    private int progressCalculate (final int progress)
    {
        return Math.abs ((int) ((progress * lenMusic) / 100));
    }

    private void streamClose ()
    {
        if (stream != null)
        {
            setTrueControlBlock ();
            try
            {
                stream.close ();
                System.gc ();
            }
            catch (final IOException e)
            {
                if (on != null) on.onError (e);
            }
            setFalseControlBlock ();
        }
    }

    private void startMusic ()
    {
        Main.PrintAbout ();
        new Thread (() ->
        {
            try
            {
                on.onStart ();
                player.play ();
                if (player != null && player.isComplete ())
                {
                    _stop ();
                    on.onCompleted ();
                }
            }
            catch (final JavaLayerException e)
            {
                _stop ();
                on.onJavaLayerException (e);
            }
            finally
            {
                streamClose ();
            }
        }).start ();

        new Thread (() ->
        {
            while (!stop && !pause && (player != null && !player.isComplete ()))
            {
                try
                {
                    on.onTime (convertDuration.convert2 (timeCalculate ()));
                    on.onProgress (progressCalculate ());
                }
                catch (final NullPointerException e)
                {
                    if (on == null) return;
                }
            }
        }).start ();
        setFalseControlBlock ();
    }

    public boolean isPause ()
    {
        return pause;
    }

    public boolean isStop ()
    {
        return stop;
    }

    private long timeCalculate ()
    {
        if (player == null) return 0;
        return (Math.abs ((progress * allPosition) / 100)) + player.getPosition ();
    }

    public boolean isControlBlock ()
    {
        return controlBlock;
    }

    private boolean _setMusic ()
    {
        return ((pathMusic != null && !pathMusic.isEmpty ()) || inputStream != null);
    }

    private static final class BMPlayer extends Player
    {
        public BMPlayer (InputStream stream) throws JavaLayerException
        {
            super (stream);
            decodeFrame ();
        }
    }
}
