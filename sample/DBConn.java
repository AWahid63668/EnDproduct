
import java.sql.*;
import java.util.ArrayList;

public class DBConn
{
    private Connection connection;

    public DBConn() { connect(); }


    private void connect()
    {
        try { connection = DriverManager.getConnection("jdbc:sqlite:" + Main.HOME_FOLDER + "/NikolaisVersion.db"); }
        catch (Exception e) { e.printStackTrace(); System.out.println("failed to connect in db constructor :("); }
    }

    @Override public void finalize() { close(); }

    private ResultSet resultSetFromQuery(String query) throws SQLException
    {
        if (connection.isClosed()) connect();
        return connection.createStatement().executeQuery(query);
    }
    public void executeChangeQuery(String query) { try { connection.createStatement().executeUpdate(query); } catch (Exception e) { e.printStackTrace(); } }
    public void close()
    {
        try { connection.close(); }
        catch (Exception e) { e.printStackTrace(); }
    }

    public ArrayList<Song> songsFromPlaylist(Playlist playlist)
    {
        ResultSet resultSet;
        try
        {
            resultSet = resultSetFromQuery("SELECT * FROM Songs " +
                "JOIN Playlists_Songs ON Songs.songID = Playlists_Songs.songID " +
                "WHERE Playlists_Songs.playlistName = '" + playlist.getPlaylistName() + "';");
        }
        catch (Exception e) { e.printStackTrace(); return null; }
        final ArrayList<Song> songs = new ArrayList<>();
        try
        {
            while (resultSet.next())
            {
                songs.add(new Song(
                        resultSet.getInt("songID"),
                        resultSet.getString("title"),
                        resultSet.getString("album"),
                        resultSet.getString("artist"),
                        resultSet.getInt("length")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); return null; }
        return songs;
    }

    public ArrayList<String> songNamesFromPlaylist(Playlist playlist)
    {
        final ArrayList<String> songNames = new ArrayList<>();
        for (Song song : songsFromPlaylist(playlist)) { songNames.add(song.getTitle()); }
        return songNames;
    }

    public ArrayList<Song> allSongs()
    {
        final ArrayList<Song> songs = new ArrayList<>();
        ResultSet resultSet;
        try
        {
            resultSet = resultSetFromQuery("SELECT * FROM Songs;");
        }
        catch (Exception e) { e.printStackTrace(); System.out.println("error here");return null; }
        try
        {
            while (resultSet.next())
            {
                songs.add(new Song(
                        resultSet.getInt("songID"),
                        resultSet.getString("title"),
                        resultSet.getString("album"),
                        resultSet.getString("artist"),
                        resultSet.getInt("length")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); return null; }
        return songs;

    }

    public ArrayList<String> allSongNames()
    {
        final ArrayList<String> allNames = new ArrayList<>();
        for (Song song : allSongs()) {
            allNames.add(song.getTitle());
        }
        return allNames;
    }

    public ArrayList<Playlist> allPlaylistsOwnedBy(User user)
    {
        ResultSet resultSet;
        try { resultSet = resultSetFromQuery("SELECT * FROM Playlists WHERE Playlists.username = '" + user.getUsername() + "';"); }
        catch (Exception e) { e.printStackTrace(); return null; }

        final ArrayList<Playlist> playlists = new ArrayList<>();
        try
        {
            while (resultSet.next())
            {
                playlists.add(new Playlist(
                        resultSet.getString("playlistName"),
                        resultSet.getString("username"),
                        resultSet.getBoolean("isUserEditable")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); return null; }
        return playlists;
    }

    public ArrayList<String> allPlaylistNamesOwnedBy(User user)
    {
        final ArrayList<String> playlistNames = new ArrayList<>();
        allPlaylistsOwnedBy(user).forEach(playlist -> playlistNames.add(playlist.getPlaylistName()));
        return playlistNames;
    }

    public void addSongToPlaylist(int songID, String playlistName)
    {
        int matchupID;
        try { matchupID = resultSetFromQuery("SELECT matchupID FROM Playlists_Songs ORDER BY matchupID DESC LIMIT 1;").getInt("matchupID") + 1; }
        catch (Exception e) { matchupID = 1; } // if there is no matchupID in there yet

        executeChangeQuery("INSERT INTO Playlists_Songs VALUES (" + matchupID + ", \'" + playlistName + "\', " + songID + ");");
    }

    public void removeSongFromPlaylist(int songID, String playlistName)
    {
        executeChangeQuery("DELETE FROM Playlists_Songs WHERE songID = " + songID + " AND playlistName = \'" + playlistName + "\';");
    }

    public void addNewPlaylist(String playlistName, String userName)
    {
        try { if (connection.isClosed()) connect(); } catch (Exception e) { e.printStackTrace(); }
        executeChangeQuery("INSERT INTO Playlists (playlistName, username) VALUES (\'" + playlistName + "\', \'" + userName + "\');");
    }

    public void deletePlaylist(String playlistName, String username)
    {
        try { if (connection.isClosed()) connect(); } catch (Exception e) { e.printStackTrace(); }
        executeChangeQuery("DELETE FROM Playlists WHERE playlistName = \'" + playlistName + "\' AND username = \'" + username + "\';");
    }
}
