# BardiaMusic
Music Player
> version 1.4.0

![bardia-music](https://www.bardiademon.com/public/github/img-musicplayer.png "Bardia Player")

## How to use
> New Java Project 1.8 and above</br>
> Added "MusicPlayer.jar" file to project</br>

```java

final BardiaPlayer player = new BardiaPlayer ();

player.setMusic ("MUSIC PATH" , new On ()
{
    @Override
    public void onProgress (final int progress)
    {
         // progress = An integer from one to 100
    }

    @Override
    public void onMusicTime (final ConvertDuration.Time time)
    {
        // time.toString() => 00:00:00
    }

    @Override
    public void onTime (final ConvertDuration.Time time)
    {
        // time.toString() => 00:00:00
    }

    @Override
    public void onStart ()
    {
        // The moment music starts
    }

    @Override
    public void onStop ()
    {
        // The moment music stop
    }

    @Override
    public void onPause ()
    {
         // The moment music pause
    }

    @Override
    public void onCompleted ()
    {
         // The moment of the end of the music
    }

    @Override
    public void onName (final String name)
    {
        // Music name => Name.mp3
    }

    @Override
    public void onMetadata (final ID3v1 metadata)
    {
        // Music information
    }

    @Override
    public void onMetadata (final ID3v2 metadata)
    {
        // Music information
    }

    @Override
    public void onMetadataError ()
    {

    }

    @Override
    public void onAlbumImage (final InputStream stream)
    {
         // Music image
    }

    @Override
    public void onAlbumImageError ()
    {

    }

    @Override
    public void onCompleteInfo ()
    {
        // End of geting information
    }

    @Override
    public void onError (final Exception e)
    {
        // Any errors during the program
    }

    @Override
    public void onJavaLayerException (final JavaLayerException e)
    {
        // Error playing music
    }

    @Override
    public void onDie ()
    {
        
    }
});

player.start (); // Play music and call the onStart method

```
