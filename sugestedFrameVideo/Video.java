package media;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import main.InteractiveLearningApp;

/**
 * Class for the video player
 * @author - Rimas Radziunas and Cezara-Lidia Jalba
 * @version - 1.2
 * @date - 20/04/20
 */
public class Video {
	//global variables
	private int startTime;
	private int slideNumber;
	private String urlName;
	private String subUrlName;
	private SubScene subScene;
	boolean videoFail = false;

	private MediaPlayer mediaPlayer;
	private Subtitles subtitleTrack = null;
	private Duration totTime;
	private Pane toolbarNode;
	private Slider volumeSlider;
	private Label currentTimeLabel;
	private Label totTimeLabel;

	/**
	 * Method to create the video object
	 * @param urlName - URL of video file to be played
	 * @param subUrlName 
	 * @param startTime - how long after the slide is opened should the video start
	 * @param loop - should the video loop
	 * @param canvasWidth - width of canvas
	 * @param canvasHeight - height of canvas
	 * @throws IOException - if video cannot be found
	 */
	public Video(String urlName, String subUrlName, int startTime, Boolean loop, int slideNumber)
			throws IOException {
		// loads the media player layout from a FXML file
		BorderPane root = FXMLLoader.load(getClass().getClassLoader().getResource("media/videoPlayer.fxml"));
		this.urlName = urlName;
		this.startTime = startTime;
		this.slideNumber = slideNumber;
		
		// creates a subScene
		subScene = new SubScene(root, 65*InteractiveLearningApp.getStageWidth()/100, 75*InteractiveLearningApp.getStageHeight()/100);	

		// video control bar, retrieved from the root
		toolbarNode = (Pane) root.getBottom();
		// current time and total time of the video labels
		currentTimeLabel = (Label) toolbarNode.getChildren().get(7);
		totTimeLabel = (Label) toolbarNode.getChildren().get(9);
		
		
		// video media file
		Media media = null;
		// opening URL video link
		if(urlName.startsWith("https://")) {
			try {
				media = new Media(urlName);
			} catch (Exception e) {
				videoFail = true;
				return;
			}	
		}
		// opening local video link
		else if(urlName.startsWith("resources/")) {
			try {
				File vidFile = new File(urlName);
				media = new Media(vidFile.toURI().toString());	
			} catch (Exception e) {
				videoFail = true;
				System.out.println("failed");
				return;
			}
		}
		//Open local video subtitles file
		try {
			File track = new File(subUrlName);
			subtitleTrack = new Subtitles(track);
		} catch (Exception e) {
			System.out.println(" subfailed");
		}
		
		// create media player with the video media file
		mediaPlayer = new MediaPlayer(media);
		// set video to loop
		if (loop) {
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		}
		
		// get video pane background
		Pane p = (Pane) root.getCenter();
		// subtitle label
		Label subtitleLabel = (Label) p.getChildren().get(1);
		// set the label width according to the size of the subScene and 70% down from the top of the subScene
		subtitleLabel.layoutYProperty().bind(subScene.layoutYProperty().add(subScene.getHeight()/10*7));
		subtitleLabel.setPrefWidth(subScene.getWidth());
		
		// volume slider, set initial value
		volumeSlider = (Slider) toolbarNode.getChildren().get(6);
		volumeSlider.setValue(mediaPlayer.getVolume() * 100);
		// volume slider change listener to adjust video sound level
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				mediaPlayer.setVolume(volumeSlider.getValue() / 100);
			}
		});
		
		// playBack slider
		Slider playbackSlider = (Slider) toolbarNode.getChildren().get(5);
		// set the current play time to the time specified by the play back slider
		playbackSlider.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				if (playbackSlider.isValueChanging() || playbackSlider.isPressed()) {
					if (mediaPlayer.getStatus() == Status.PLAYING || mediaPlayer.getStatus() == Status.PAUSED) {
						// seek the video to the time specified by the slider
						mediaPlayer.seek(totTime.multiply(playbackSlider.getValue() / 100));
						setTimeLabel(mediaPlayer.getCurrentTime(), currentTimeLabel);
						// seek for the correct subtitle position and set the text
						seekSubtitles(subtitleLabel);

					}
					// When the video is stopped and the slider is moved, set media player to
					// paused since seek function cannot be used while the status is STOPPED
					else if (mediaPlayer.getStatus() == Status.STOPPED) {
						mediaPlayer.pause();
						mediaPlayer.seek(totTime.multiply(playbackSlider.getValue() / 100));
						// seek for the correct subtitle position and set the text
						seekSubtitles(subtitleLabel);
					}
				}
			}
		});

		// when the video is paused, change the icon of the play button to play icon
		mediaPlayer.setOnPaused(new Runnable() {
			@Override
			public void run() {
				setUpBTOnPaused();
			}
		});

		// set on halted: when critical error occurs
		mediaPlayer.setOnHalted(new Runnable() {
			@Override
			public void run() {
			}
		});

		// when video is stopped, set current time to zero, 
		mediaPlayer.setOnStopped(new Runnable() {
			@Override
			public void run() {
				playbackSlider.setValue(0);
				setUpBTOnPaused();
			}
		});

		// when the video is ready (loaded), set the total time of the video label
		mediaPlayer.setOnReady(new Runnable() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						totTime = mediaPlayer.getMedia().getDuration();
						setTimeLabel(totTime, totTimeLabel);
					}
				});
			}
		});

		// set the slider to the beginning after video ends and stop the player
		mediaPlayer.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				if (!loop) {
					mediaPlayer.stop();
					mediaPlayer.seek(totTime.multiply(playbackSlider.getValue() / 100));
				}
			}
		});

		// set the play button to display pause icon when the video is playing
		mediaPlayer.setOnPlaying(new Runnable() {
			@Override
			public void run() {
				setUpBTOnPlay();
			}
		});

		// set the current time label and play back slider to follow the current time of
		// the video being played
		mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				Duration currentTime = mediaPlayer.getCurrentTime();
				playbackSlider.setValue(currentTime.toMillis() / totTime.toMillis() * 100);
				setTimeLabel(currentTime, currentTimeLabel);
			}
		});
		
		// if there is a subtitle track, listen for player time change to update the subtitles
		if(subtitleTrack != null) {
			mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable arg0) {
					subtitleTrack.setSubtitleText(subtitleLabel, mediaPlayer);
				}
			});
		}
		// add media player to media view
		MediaView mv = (MediaView) p.getChildren().get(0);
		mv.setMediaPlayer(mediaPlayer);
		// preserve ratio off so that the video would fill the player screen 
		mv.setPreserveRatio(false);
		// set mediaView to fit the subScene, -50 to leave space for control bar
		mv.setFitHeight(subScene.getHeight()-50);
		mv.setFitWidth(subScene.getWidth());
	}

	public SubScene getSubScene() {
		return subScene;
	}

	// removes video from the subScene
	public void remove() {
		BorderPane root = (BorderPane) subScene.getRoot();
		root.getChildren().clear();
	}

	// set the text of the label to display the time
	private void setTimeLabel(Duration time, Label label) {
		int min = (int) time.toMinutes();
		int sec = (int) (time.toSeconds() - 60 * min);
		if (sec < 10) {
			label.setText(min + ":0" + sec);
		} else {
			label.setText(min + ":" + sec);
		}
	}
		
	public void play() {
		mediaPlayer.play();
	}
	
	public void stop() {
		mediaPlayer.stop();
	}
	
	public MediaPlayer getPlayer() {
		return mediaPlayer;
	}
	
	public int getSlideNumber() {
		return(slideNumber);
	}
	
	public int getStartTime() {
		return(startTime);
	}
	

	public String formatTime(Duration time) {
		String string;
		//Duration timeD = new Duration(duration);
		int min = (int) time.toMinutes();
		int sec = (int) (time.toSeconds() - 60 * min);
		int mili = (int) (time.toMillis() - 1000 * sec);
		if (sec < 10) {
			string = min + ":0" + sec + ":" + mili;
		} else {
			string = min + ":" + sec + ":" + mili;
		}
		return string;
	}
	
	public String formatTime(Double time) {
		String string;
		Duration timeD = new Duration(time);
		int min = (int) timeD.toMinutes();
		int sec = (int) (timeD.toSeconds() - 60 * min);
		int mili = (int) (timeD.toMillis() - 1000 * sec);
		if (sec < 10) {
			string = min + ":0" + sec + ":" + mili;
		} else {
			string = min + ":" + sec + ":" + mili;
		}
		return string;
	}
	
	// set play/pause button to display play icon when paused, and the current time label to display current time
	private void setUpBTOnPaused() {
		Button play = (Button) toolbarNode.getChildren().get(0);
		ImageView playImg = (ImageView) play.getGraphic();
		playImg.setImage(new Image(getClass().getResourceAsStream("/graphics/play.png")));
		setTimeLabel(mediaPlayer.getCurrentTime(), currentTimeLabel);
	}
	// set play/pause button to display	pause icon when
	private void setUpBTOnPlay() {
		Button play = (Button) toolbarNode.getChildren().get(0);
		ImageView playImg = (ImageView) play.getGraphic();
		playImg.setImage(new Image(getClass().getResourceAsStream("/graphics/pause.png")));
	}
	// seek subtitles and set the label, when the playBack slider is adjusted
	private void seekSubtitles(Label subtitleLabel ) {
		if(subtitleTrack != null) {
			subtitleTrack.seekPosition(mediaPlayer.getCurrentTime().toMillis());
			subtitleTrack.setSubtitleText(subtitleLabel, mediaPlayer);
		}
	}
}
