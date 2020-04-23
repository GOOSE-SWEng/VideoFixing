package sugestedFrameVideo;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class VideoController {
	@FXML
	private Button play;
	@FXML
	private MediaView mediaView;
	@FXML
	private BorderPane vidBPane;
	@FXML
	private Slider volSlider;
	@FXML
	private Label subtitleLB;
	@FXML
	private ImageView fulsrcBtImg;
	@FXML
	private ImageView muteBtImg;

	private Bounds vidSubBounds;

	// Play button control
	@FXML
	public void play(ActionEvent event) {

		if (mediaView.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
			mediaView.getMediaPlayer().pause();

		} else if (mediaView.getMediaPlayer().getStatus() != MediaPlayer.Status.PLAYING) {
			mediaView.getMediaPlayer().play();
		}
	}

	// Stop button control
	public void stop(ActionEvent event) {
		mediaView.getMediaPlayer().stop();
		play.setText("Play");
	}

	// Full screen button control
	public void setFullScreen(ActionEvent event) {
		// Retrieve the correct containers
		// TODO: adapt to the main program
		// This only works for this structure
		Scene pane = vidBPane.getScene();
		Stage mainStage = (Stage) pane.getWindow();

		Scene mainScene = mainStage.getScene();
		AnchorPane root = (AnchorPane) mainScene.getRoot();
		Pane slidePane = (Pane) root.getChildren().get(2);
		Pane videoContainerPane = (Pane) slidePane.getChildren().get(0);
		SubScene videoSubscene = (SubScene) videoContainerPane.getChildren().get(0);

		if (mainStage.isFullScreen()) {
			mainStage.setFullScreen(false);
			videoSubscene.setLayoutX(vidSubBounds.getMinX());
			videoSubscene.setLayoutY(vidSubBounds.getMinY());
			videoSubscene.setHeight(vidSubBounds.getHeight());
			videoSubscene.setWidth(vidSubBounds.getWidth());
			mediaView.setFitHeight(vidSubBounds.getHeight() - 50);
			mediaView.setFitWidth(vidSubBounds.getWidth());
			fulsrcBtImg.setImage(new Image(getClass().getResourceAsStream("/graphics/fullscreen.png")));

		} else {
			if (!mainStage.isFullScreen()) {
				vidSubBounds = videoSubscene.getBoundsInParent();
			}
			// Set the main stage to fullscreen
			mainStage.setFullScreen(true);
			// Put the video to the top left corner
			videoSubscene.setLayoutX(0);
			videoSubscene.setLayoutY(0);
			// setHeight
			videoSubscene.setHeight(mainStage.getHeight() - 30);
			videoSubscene.setWidth(mainStage.getWidth());

			mediaView.setFitHeight(mainStage.getHeight() - 80);
			mediaView.setFitWidth(mainStage.getWidth());

			fulsrcBtImg.setImage(new Image(getClass().getResourceAsStream("/graphics/back_from_fullscreen.png")));
		}
	}

	public void captionOn(ActionEvent event) throws IOException, InterruptedException {
		// TODO: turn on captions
		//need .srt file, .srt parser and implementation onto the video
	}

	// Mute and unmute the audio
	public void muteAudio(ActionEvent event) {
		// mute
		if (mediaView.getMediaPlayer().isMute() == false) {
			mediaView.getMediaPlayer().setMute(true);
			// changes the icon to muted
			muteBtImg.setImage(new Image(getClass().getResourceAsStream("/graphics/mute.png")));

		}
		// unmute
		else if (mediaView.getMediaPlayer().isMute() == true) {
			mediaView.getMediaPlayer().setMute(false);
			// changes the icon to sound
			muteBtImg.setImage(new Image(getClass().getResourceAsStream("/graphics/sound.png")));
		}
	}
}
