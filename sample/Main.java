
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.control.ProgressBar;

import java.awt.*;
import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;

public class Main extends Application
{
    //private static final String LOGO_LOCATION = "C:/Users/Nikolai/Pictures/ahga.PNG"; // U:\Logo.png
    private ListView listView;
    public static final String HOME_FOLDER = System.getProperty("user.dir");
    private SongMode songMode = SongMode.LISTEN;
    private VBox boxOfButtons;
    private DBConn dbConn = new DBConn();
    private MP3Player player;
    private User user = new User("nick", "nick_");

    @Override public void start(Stage stage) throws Exception
    {
        dbConn = new DBConn();
        player  = new MP3Player();
        BorderPane outsideBorderPane = new BorderPane();
        outsideBorderPane.setStyle("-fx-background-color: green;");

        Scene scene = new Scene(outsideBorderPane); //1024, 768);

        stage.setTitle("Hello World");
        stage.setScene(scene);
        stage.show();
        //BorderPane within To
        BorderPane topBorderPane = new BorderPane();
        topBorderPane.setPadding(new Insets(40));
        topBorderPane.setStyle("-fx-background-color: red;");
        outsideBorderPane.setTop(topBorderPane);

        /// /SEARCH BAR


        //LOGO
        final FileInputStream file = new FileInputStream(HOME_FOLDER + "/logo.png");
        final Image image = new Image(file);
        final ImageView imageView = new ImageView(image);
        imageView.setFitHeight(75);
        imageView.setFitWidth(125);
        topBorderPane.setLeft(imageView);

        //LOGOUT BUTTON

        Button logoutButton = new Button("Search");
        logoutButton.setPrefSize(100, 20);
        BorderPane.setAlignment(logoutButton, Pos.CENTER_RIGHT);
        topBorderPane.setRight(logoutButton);

        // LEFT OF MAIN BORDER PANE
        VBox leftHandVBox = new VBox();

        BorderPane LeftBorderPane = new BorderPane();
        LeftBorderPane.setPadding(new Insets(40));
        LeftBorderPane.setStyle("-fx-background-color: blue");
        outsideBorderPane.setLeft(LeftBorderPane);

        Label subheading = new Label( "Playlists");
        subheading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        leftHandVBox.getChildren().add(subheading);


        leftHandVBox.setPadding(new Insets(10));
        leftHandVBox.setSpacing(20);
        leftHandVBox.setStyle("-fx-background-color:purple");

        //leftHandVBox = new VBox(10);

        final Button allSongsButton = new Button("All Songs");
        allSongsButton.setOnAction((ActionEvent ae)
        -> {
           player.pause();
           player.loadAllSongs();
           listView.getItems().clear();
           listView.getItems().addAll(dbConn.allSongNames());
        });
        leftHandVBox.getChildren().add(allSongsButton);

        for (Playlist playlist : dbConn.allPlaylistsOwnedBy(user))
        {
            final Button playlistButton = new Button(playlist.getPlaylistName());
            playlistButton.setOnAction((ActionEvent ae)
            -> {
                player.pause();
                player.loadPlaylist(playlist);
                listView.getItems().clear();
                listView.getItems().addAll(dbConn.songNamesFromPlaylist(playlist));
            });
            leftHandVBox.getChildren().add(playlistButton);
        }
        outsideBorderPane.setLeft(leftHandVBox);

        // RIGHT OF MAIN BORDER PANE

        VBox rightHandVBox = new VBox();
        rightHandVBox.setPadding(new Insets(10));
        rightHandVBox.setSpacing(20);
        rightHandVBox.setStyle("-fx-background-color: Pink;");

        subheading = new Label("Customise");
        subheading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        rightHandVBox.getChildren().add(subheading);

        boxOfButtons = new VBox(20);
        Button[] controlButtons = new Button[6];

        controlButtons[0] = new Button("Add To playlist");
        controlButtons[0].setOnAction(dontNeedThis -> songMode = SongMode.ADD_TO_PLAYLIST);
        controlButtons[0].setPrefSize(200, 10);

        controlButtons[1] = new Button("Remove From Playlist");
        controlButtons[1].setOnAction(dontNeedThis -> songMode = SongMode.REMOVE_FROM_PLAYLIST);
        controlButtons[1].setPrefSize(200, 10);

        controlButtons[2] = new Button("Music Video");
        controlButtons[2].setOnAction(dontNeedThis -> songMode = SongMode.WATCH_MUSIC_VIDEO);
        controlButtons[2].setPrefSize(200, 10);

        controlButtons[3] = new Button("Song Lyrics");
        controlButtons[3].setOnAction((ActionEvent ae) -> songMode = SongMode.SEE_LYRICS);
        controlButtons[3].setPrefSize(200, 10);

        controlButtons[4] = new Button("Create blank playlist");
        controlButtons[4].setOnAction((dontNeedThis) -> {
            final TextInputDialog playlistNameDialogue = new TextInputDialog();
            playlistNameDialogue.setTitle("New Playlist");
            playlistNameDialogue.setHeaderText("You'll have to reload the program for the new playlist to appear");
            playlistNameDialogue.setContentText("Choose a new playlist name:\t");

            final Optional<String> result = playlistNameDialogue.showAndWait();
            if (result.isPresent()) dbConn.addNewPlaylist(result.get(), user.getUsername());
        });
        controlButtons[4].setPrefSize(200, 10);

        controlButtons[5] = new Button("Delete playlist");
        controlButtons[5].setOnAction((dontNeedThis) -> {
            final ChoiceDialog playlistNameDialogue = new ChoiceDialog("", dbConn.allPlaylistNamesOwnedBy(user));
            playlistNameDialogue.setTitle("Delete Playlist");
            playlistNameDialogue.setHeaderText("You'll have to reload the program for the new playlist to disappear");
            playlistNameDialogue.setContentText("Choose a playlist to delete: \t");

            final Optional<String> result = playlistNameDialogue.showAndWait();
            if (result.isPresent()) dbConn.deletePlaylist(result.get(), user.getUsername());
        });
        controlButtons[5].setPrefSize(200, 10);

        boxOfButtons.getChildren().addAll(controlButtons);

        rightHandVBox.getChildren().add(boxOfButtons);

        outsideBorderPane.setRight(rightHandVBox);

        //BorderPane within Centre
        BorderPane centreBorderPane = new BorderPane();
        centreBorderPane.setPadding(new Insets(40));
        centreBorderPane.setStyle("-fx-background-color: yellow;");
        outsideBorderPane.setCenter(centreBorderPane);

        subheading = new Label("PLAYLIST 1");
        subheading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        centreBorderPane.setTop(subheading);

        listView = new ListView<String>();
        player.loadAllSongs();
        System.out.println(dbConn.allSongNames() == null);
        listView.getItems().addAll(dbConn.allSongNames());
        listView.getSelectionModel().selectedIndexProperty().addListener( (dontNeedThis, dontNeedThisEither, index) -> {
            final int indexAsInt = (int) index;
            switch (songMode)
            {
                case LISTEN:
                    if (indexAsInt >= 0 && indexAsInt < listView.getItems().size()) player.skipTo(indexAsInt);
                    break;
                case SEE_LYRICS: {
                    final String songName = listView.getItems().get(indexAsInt).toString();
                    final URI linkToShow = URI.create("https://genius.com/search?q=" + songName.replace(" ", "%20"));
                    try { Desktop.getDesktop().browse(linkToShow); } catch (Exception e) { e.printStackTrace(); }

                    songMode = SongMode.LISTEN;
                    break;
                }
                case WATCH_MUSIC_VIDEO: {
                    String songName = listView.getItems().get(indexAsInt).toString();
                    final URI linkToShow = URI.create("https://www.youtube.com/results?search_query=" + songName.replace(" ", "+"));
                    try { Desktop.getDesktop().browse(linkToShow); } catch (Exception e) { e.printStackTrace(); }

                    songMode = SongMode.LISTEN;
                    break;
                }
                case ADD_TO_PLAYLIST: {
                    final ChoiceDialog playlistDialogue = new ChoiceDialog<>("", dbConn.allPlaylistNamesOwnedBy(user));
                    playlistDialogue.setTitle("Choose a playlist");
                    playlistDialogue.setContentText("Choose a playlist to add the song to:\t");

                    final Optional<String> result = playlistDialogue.showAndWait();
                    result.ifPresent(playlistName -> dbConn.addSongToPlaylist(indexAsInt + 1, playlistName));

                    songMode = SongMode.LISTEN;
                    break;
                }
                case REMOVE_FROM_PLAYLIST: {
                    final ChoiceDialog playlistDialogue = new ChoiceDialog<>("", dbConn.allPlaylistNamesOwnedBy(user));
                    playlistDialogue.setTitle("Choose a playlist");
                    playlistDialogue.setContentText("Choose a playlist to remove the song from:\t");

                    final Optional<String> result = playlistDialogue.showAndWait();
                    result.ifPresent(playlistName -> { dbConn.removeSongFromPlaylist(indexAsInt + 1, playlistName); System.out.println("donezo"); });

                    songMode = SongMode.LISTEN;
                    break;
                }
            }
        });
        centreBorderPane.setCenter(listView);

        TextField searchbar = new TextField();
        searchbar.setPromptText("Search album, song or album");
        searchbar.setPrefSize(100, 20);
        final ObservableList<String> allSongTitles = FXCollections.observableArrayList(dbConn.allSongNames());

        searchbar.textProperty().addListener(((dontNeedThis, dontNeedThisEither, searchText) -> {
            final String searchTextTrimmed = searchText.trim().toLowerCase();
            if (!searchTextTrimmed.isEmpty()) // if they typed something new in
            {
                listView.getItems().clear();
                for (String title : allSongTitles) if (title.toLowerCase().contains(searchTextTrimmed)) listView.getItems().add(title); // adding title if matches search
            }
            else { listView.setItems(FXCollections.observableArrayList(dbConn.allSongNames())); } // search text is blank. resetting
        }));
        BorderPane.setAlignment(searchbar, Pos.CENTER);
        topBorderPane.setCenter(searchbar);

        //Creating BottomBorderPane
        BorderPane bottomBorderPane = new BorderPane();
        bottomBorderPane.setPadding(new Insets(40));
        bottomBorderPane.setStyle("-fx-background-color: orange;");
        outsideBorderPane.setBottom(bottomBorderPane);

        //Creating Pause/ play etc etc Buttons
        HBox bottomHandHBox = new HBox();
        bottomHandHBox.setPadding(new Insets(10));
        bottomHandHBox.setSpacing(20);
        bottomHandHBox.setStyle("-fx-background-color: orange;");

        final HBox playbackButtonsHBox = new HBox(10);

        Button[] playbackButtons = new Button[6];

        playbackButtons[0] = new Button("Shuffle");
        playbackButtons[0].setOnAction((ActionEvent ae) -> player.shuffle());
        playbackButtons[0].setPrefSize(50, 50);

        playbackButtons[1] = new Button("Last Song");
        playbackButtons[1].setOnAction((ActionEvent ae) -> player.skipBackward());
        playbackButtons[1].setPrefSize(50, 50);

        playbackButtons[2] = new Button("Pause");
        playbackButtons[2].setOnAction((ActionEvent ae) -> player.pause());
        playbackButtons[2].setPrefSize(50, 50);

        playbackButtons[3] = new Button("Play");
        playbackButtons[3].setOnAction((ActionEvent ae) -> player.play());
        playbackButtons[3].setPrefSize(50, 50);

        playbackButtons[4] = new Button("Next Song");
        playbackButtons[4].setOnAction((ActionEvent ae) -> player.skipForward());
        playbackButtons[4].setPrefSize(50, 50);

        playbackButtons[5] = new Button("Repeat");
        playbackButtons[5].setOnAction((ActionEvent ae) -> player.setRepeating());
        playbackButtons[5].setPrefSize(50, 50);

        playbackButtonsHBox.getChildren().addAll(playbackButtons);
        bottomHandHBox.getChildren().add(playbackButtonsHBox);
        outsideBorderPane.setBottom(bottomHandHBox);

        //slider for song length
        Slider songlengthslider = new Slider();
        songlengthslider.setMin(0);
        songlengthslider.setMax(100);
        songlengthslider.setValue(50);
        // songlengthslider.valueProperty().addListener(() -> ); DO THIS IN A BIT

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefSize(400, 30);
        progressBar.setProgress(0.5);

        songlengthslider.valueProperty().addListener(
                (observable, old_value, new_value) -> progressBar.setProgress(new_value.doubleValue() / 100));

       bottomHandHBox.getChildren().add(songlengthslider);
        dbConn.close();
    }


    public static void main(String[] args) {
        launch(args);
    }}