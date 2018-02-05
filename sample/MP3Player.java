
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MP3Player
{
    private final ArrayList<MediaPlayer> queue = new ArrayList<>();
    private Boolean playing = false;
    private Boolean repeating = false;
    private Boolean isRepeating() { return repeating; }
    private int songIndex = 0;
    public Boolean isPlaying() { return playing; }
    private DBConn dbConn = new DBConn();

    public void play()
    {
        if (!playing)
        {
            queue.get(songIndex).play();
            System.out.println("songIndex is " + songIndex);
            playing = true;
        }
    }

    public void pause()
    {
        if (playing)
        {
            queue.get(songIndex).pause();
            playing = false;
        }
    }

    public void togglePlayPause()
    {
        if (playing) pause();
        else play();
    }

    private MediaPlayer mediaPlayerFromSong(Song song)
    {
        final Media media = new Media(new File(Main.HOME_FOLDER + "/Songs/" + song.getTitle().replace(" ", "_") + ".mp3").toURI().toString());
        final MediaPlayer player = new MediaPlayer(media);
        player.setOnEndOfMedia(() ->
        {
            if (isRepeating()) playFromBeginning();
            else skipForward();
        });
        return player;
    }

    public void loadPlaylist(Playlist playlist)
    {
        final DBConn dbConn = new DBConn();
        final ArrayList<Song> songs = dbConn.songsFromPlaylist(playlist);
        dbConn.close();

        queue.clear();
        for (Song song : songs) queue.add(mediaPlayerFromSong(song));
    }

    public void skipForward()
    {
        pause();
        if (songIndex + 1 < queue.size()) songIndex++;
        else songIndex = 0;
        play();
    }

    public void skipBackward()
    {
        pause();
        if (songIndex - 1 > 0) songIndex--;
        else songIndex = 0;
        play();
    }

    public void shuffle()
    {
        pause();
        Collections.shuffle(queue);
        play();
    }

    public void setRepeating() { repeating = true; }

    private void playFromBeginning()
    {
        pause();
        queue.get(songIndex).seek(new Duration(0.0));
        play();
    }

    public void seekTo(Double percent)
    {
        if (percent >= 0.0 && percent <= 100.0)
        {
            pause();
            final Duration newDuration = queue.get(songIndex).getTotalDuration().multiply(percent / 100.0);
            queue.get(songIndex).seek(newDuration);
            play();
        }
    }

    public void skipTo(int newIndex)
    {
        if (newIndex >= 0 && newIndex < queue.size())
        {
            pause();
            songIndex = newIndex;
            play();
        }
    }

    public void loadAllSongs()
    {
        pause();
        queue.clear();
        for (Song song : dbConn.allSongs()) { queue.add(mediaPlayerFromSong(song)); System.out.println("Added " + song.getTitle()); }
    }


}
