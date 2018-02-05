
public class Playlist
{
    private String playlistName;
    private String username;
    private Boolean isEditable;

    public String getPlaylistName() {
        return playlistName;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getEditable() {
        return isEditable;
    }

    public Playlist(String playlistName, String username, Boolean isEditable)
    {
        this.playlistName = playlistName;
        this.username = username;
        this.isEditable = isEditable;
    }
}
