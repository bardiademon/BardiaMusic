package com.bardiademon.MusicPlayer;

import com.bardiademon.MusicPlayer.bardiademon.ConvertDuration;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.bardiademon.BardiaJlayer.javazoom.jl.decoder.JavaLayerException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.InputStream;

public final class Main
{
    public static void main (final String[] args)
    {
        if (args != null && args.length == 1)
        {
            final File file = new File (args[0]);
            if (file.exists () && FilenameUtils.getExtension (file.getName ()).equals ("mp3"))
            {
                final BardiaPlayer player = new BardiaPlayer ();

                final TerminalPrint terminalPrint = new TerminalPrint ();
                terminalPrint.setPath (file.getAbsolutePath ());

                player.setMusic (file.getAbsolutePath () , new On ()
                {
                    @Override
                    public void onProgress (final int progress)
                    {
                        terminalPrint.setProgress (progress);
                    }

                    @Override
                    public void onMusicTime (final ConvertDuration.Time time)
                    {
                        terminalPrint.setMusicTime (time.toString ());
                    }

                    @Override
                    public void onTime (final ConvertDuration.Time time)
                    {
                        terminalPrint.setTime (time.toString ());
                    }

                    @Override
                    public void onStart ()
                    {

                    }

                    @Override
                    public void onStop ()
                    {

                    }

                    @Override
                    public void onPause ()
                    {

                    }

                    @Override
                    public void onCompleted ()
                    {

                    }

                    @Override
                    public void onName (final String name)
                    {
                        terminalPrint.setName (name);
                    }

                    @Override
                    public void onMetadata (final ID3v1 metadata)
                    {
                        terminalPrint.setAlbum (metadata.getAlbum ());
                        terminalPrint.setTitle (metadata.getTitle ());
                    }

                    @Override
                    public void onMetadata (final ID3v2 metadata)
                    {
                        terminalPrint.setAlbum (metadata.getAlbum ());
                        terminalPrint.setTitle (metadata.getTitle ());
                    }

                    @Override
                    public void onMetadataError ()
                    {

                    }

                    @Override
                    public void onAlbumImage (final InputStream stream)
                    {

                    }

                    @Override
                    public void onAlbumImageError ()
                    {

                    }

                    @Override
                    public void onCompleteInfo ()
                    {

                    }

                    @Override
                    public void onError (final Exception e)
                    {
                        terminalPrint.setError (e.getMessage ());
                    }

                    @Override
                    public void onJavaLayerException (final JavaLayerException e)
                    {

                    }

                    @Override
                    public void onDie ()
                    {

                    }
                });

                player.start ();
                return;
            }
        }

        PrintAbout ();
    }

    private static final class TerminalPrint
    {
        private int progress = 0;
        private String path, name;
        private String musicTime, time = "";
        private String album, title;

        private boolean normalPrint = false;

        public void setProgress (int progress)
        {
            this.progress = progress;
            printTime ();
        }

        public void setPath (String path)
        {
            this.path = path;
            print ();
        }

        public void setName (String name)
        {
            this.name = name;
            print ();
        }

        public void setMusicTime (String musicTime)
        {
            this.musicTime = musicTime;
            print ();
        }

        public void setTime (String time)
        {
            this.time = time;
            printTime ();
        }

        public void setAlbum (String album)
        {
            this.album = album;
            print ();
        }

        public void setTitle (String title)
        {
            this.title = title;
            print ();
        }

        public void setError (String error)
        {
            System.out.println (error);
        }

        private void print ()
        {
            if (isEmpty (name) || isEmpty (title) || isEmpty (album) || isEmpty (musicTime)) return;

            normalPrint = true;

            System.out.printf ("\n\nPath: %s\n\nName: %s\n\nTitle: %s\n\nAlbum: %s\n\nMusic time: %s\n\n" ,
                    path , name , title , album , musicTime);
        }

        private boolean isEmpty (final String val)
        {
            return (val == null || val.isEmpty ());
        }

        private void printTime ()
        {
            if (normalPrint)
                System.out.printf ("\rProgress: %d%% :: Time: %s" , progress , time);
        }
    }

    public static void PrintAbout ()
    {
        System.out.println (Default.POWERED_BY);
        System.out.println (Default._V);
    }
}
