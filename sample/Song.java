
public class Song
{
    private int songID;
    private String title;
    private String artist;
    private String album;
    private int length;

    public int getSongID() {
        return songID;
    }

    public String getTitle() {
        return title;
    }
    public String getArtist() {
        return artist;
    }
    public String getAlbum() {
        return album;
    }
    public int getLength() { return length; }

    public Song(int songID, String title, String artist, String album, int length)
    {
        this.songID = songID;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.length = length;
    }
}

