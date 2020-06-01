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
/**
 * Class for the audio handler
 * @author - Rimas Radziunas and Cezara-Lidia Jalba
 * @version - 1.1
 * @date - 21/05/20
 */
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

	private Bounds vidSubBounds; // store the bounds of the video player for returning from full screen
	
	// play button
	@FXML
	public void play(ActionEvent event) {
		if (mediaView.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
			mediaView.getMediaPlayer().pause();

		} else if (mediaView.getMediaPlayer().getStatus() != MediaPlayer.Status.PLAYING) {
			mediaView.getMediaPlayer().play();
		}
	}

	// stop button control
	public void stop(ActionEvent event) {
		mediaView.getMediaPlayer().stop();
	}

	// Full screen button control
	public void setFullScreen(ActionEvent event) {
		// retrieve the correct containers
		Scene pane = vidBPane.getScene();
		Stage mainStage = (Stage) pane.getWindow();
		Scene parent = vidBPane.getScene();
		BorderPane bordPane = (BorderPane) parent.getRoot();//Main structure
		Pane centerPane = (Pane) bordPane.getCenter();//Centre pane/stackpane where the slides go
		StackPane stackpain = (StackPane) centerPane.getChildren().get(1);//Another stackpane		
		SubScene videoSubscene = (SubScene) stackpain.getChildren().get(stackpain.getChildren().size()-1);//<last postion in the stack pane. ALWAYS ADD THE VIDEO LAST to the stackpane 
		// return from full screen
		if (mainStage.isFullScreen()) {
			// set main stage to full screen
			mainStage.setFullScreen(false);
			// set video subScene position and size back to original
			videoSubscene.setTranslateX(vidSubBounds.getMinX());
			videoSubscene.setTranslateY(vidSubBounds.getMinY());
			videoSubscene.setHeight(vidSubBounds.getHeight());
			videoSubscene.setWidth(vidSubBounds.getWidth());
			// set subtitle label size
			subtitleLB.setPrefWidth(vidSubBounds.getWidth());
			subtitleLB.setPrefHeight(subtitleLB.getHeight()-20);
			// decrease subtitle the font size
			subtitleLB.setFont(new Font("Arial",24));
			// move subtitles to correct position
			subtitleLB.layoutYProperty().bind(videoSubscene.layoutYProperty().add(videoSubscene.getHeight()/10*7));
			// resize media view to fill the subscene
			mediaView.setFitHeight(vidSubBounds.getHeight() - 50);
			mediaView.setFitWidth(vidSubBounds.getWidth());
			// change the button icon
			fulsrcBtImg.setImage(new Image(getClass().getResourceAsStream("/graphics/fullscreen.png")));
		// set media player to full screen
		} else {
			// if the screen is not full screen mode save the bounds
			if (!mainStage.isFullScreen()) {
				vidSubBounds = videoSubscene.getBoundsInParent();
			}
			// set the main stage to full screen
			mainStage.setFullScreen(true);
			// put the video to the top left corner
			videoSubscene.setTranslateX(0);
			videoSubscene.setTranslateY(0);
			// set the height and the width of the video subscene and media view
			videoSubscene.setHeight(mainStage.getHeight() - 30);
			videoSubscene.setWidth(mainStage.getWidth());
			mediaView.setFitHeight(mainStage.getHeight() - 80);
			mediaView.setFitWidth(mainStage.getWidth());
			// change the width and position of the subtitles to fit with the fullscreen
			subtitleLB.setPrefWidth(mainStage.getWidth());
			subtitleLB.setPrefHeight(subtitleLB.getHeight()+20);
			// set Y position of the subtitles
			subtitleLB.layoutYProperty().bind(videoSubscene.layoutYProperty().add(videoSubscene.getHeight()/10*8.4));
			// increase the size of the subtitles
			subtitleLB.setFont(new Font("Arial", 32));
			// change the button icon
			fulsrcBtImg.setImage(new Image(getClass().getResourceAsStream("/graphics/back_from_fullscreen.png")));
		}
	}
	
	// caption button control
	public void captionOn(ActionEvent event) throws IOException, InterruptedException {
		if(!subtitleLB.isVisible()) {
			subtitleLB.setVisible(true);
		}
		else {
			subtitleLB.setVisible(false);
		}
	}

	// mute and unmute the audio
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
