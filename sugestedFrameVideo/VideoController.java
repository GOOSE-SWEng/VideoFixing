package media;

import java.io.IOException;

import javafx.beans.property.DoubleProperty;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
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
	
	//Play button
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
	}

	// Full screen button control
	public void setFullScreen(ActionEvent event) {
		// Retrieve the correct containers
		Scene pane = vidBPane.getScene();
		Stage mainStage = (Stage) pane.getWindow();
		Scene parent = vidBPane.getScene();
		BorderPane bordPane = (BorderPane) parent.getRoot();
		System.out.println("scene: " + bordPane);
		Pane centerPane = (Pane) bordPane.getCenter();
		StackPane stackpain = (StackPane) centerPane.getChildren().get(2);
		SubScene videoSubscene = (SubScene) stackpain.getChildren().get(0);
		
		
		if (mainStage.isFullScreen()) {
			//Set main stage to fullscreen
			mainStage.setFullScreen(false);
			//Set video subscene position and size back to original
			videoSubscene.setTranslateX(vidSubBounds.getMinX());
			videoSubscene.setTranslateY(vidSubBounds.getMinY());
			videoSubscene.setHeight(vidSubBounds.getHeight());
			videoSubscene.setWidth(vidSubBounds.getWidth());
			//Set subtitle label size
			subtitleLB.setPrefWidth(vidSubBounds.getWidth());
			subtitleLB.setPrefHeight(subtitleLB.getHeight()-20);
			//decrease subtitle the font size
			subtitleLB.setFont(new Font("Arial",24));
			//move subtitles to correct position
			subtitleLB.layoutYProperty().bind(videoSubscene.layoutYProperty().add(videoSubscene.getHeight()/10*7));
			//Resize media view to fill the subscene
			mediaView.setFitHeight(vidSubBounds.getHeight() - 50);
			mediaView.setFitWidth(vidSubBounds.getWidth());
			//Change the button icon
			fulsrcBtImg.setImage(new Image(getClass().getResourceAsStream("/graphics/fullscreen.png")));

		} else {
			//If the screen is not full screen mode save the bounds
			if (!mainStage.isFullScreen()) {
				vidSubBounds = videoSubscene.getBoundsInParent();
			}
			// Set the main stage to fullscreen
			mainStage.setFullScreen(true);
			// Put the video to the top left corner
			videoSubscene.setTranslateX(0);
			videoSubscene.setTranslateY(0);
			//Set the height and the width of the video subscene and media view
			videoSubscene.setHeight(mainStage.getHeight() - 30);
			videoSubscene.setWidth(mainStage.getWidth());
			mediaView.setFitHeight(mainStage.getHeight() - 80);
			mediaView.setFitWidth(mainStage.getWidth());
			//Change the width and position of the subtitles to fit with the fullscreen
			subtitleLB.setPrefWidth(mainStage.getWidth());
			subtitleLB.setPrefHeight(subtitleLB.getHeight()+20);
			//Set Y position of the subtitles
			subtitleLB.layoutYProperty().bind(videoSubscene.layoutYProperty().add(videoSubscene.getHeight()/10*8.4));
			//Increase the size of the subtitles
			subtitleLB.setFont(new Font("Arial", 32));
			//Change the button icon
			fulsrcBtImg.setImage(new Image(getClass().getResourceAsStream("/graphics/back_from_fullscreen.png")));
		}
	}
	
	//Caption button control
	public void captionOn(ActionEvent event) throws IOException, InterruptedException {
		if(!subtitleLB.isVisible()) {
			subtitleLB.setVisible(true);
		}
		else {
			subtitleLB.setVisible(false);
		}
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
