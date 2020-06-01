package media;

import java.io.IOException;
import java.util.ArrayList;


import javafx.geometry.Pos;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import main.InteractiveLearningApp;
/**
 * Class for the audio handler
 * @author - Rimas Radziunas and Cezara-Lidia Jalba
 * @version - 1.1
 * @date - 21/05/20
 */
public class VideoLayer {
	int height;
	int width;
	StackPane sp;
	public ArrayList<Video> videos;

	public VideoLayer(int width, int height, ArrayList<Video> videos, StackPane sp) {
		this.height = height;
		this.width = width;
		this.videos = videos;
		this.sp = sp;
	}

	// create and add a video to slide video array list
	public void addVideo(String urlName, String subUrlName, int startTime, Boolean loop, int xStart, int yStart, int slideNumber) throws IOException {
		// creates the video object and its subscene
		Video video = new Video(urlName, subUrlName, startTime, loop, slideNumber);
		
		if(video.videoFail == false) {
			// adds the video object to the array list
			videos.add(video);
			InteractiveLearningApp.slides.get(slideNumber).getSlideVideos().add(video);
			// set the position of the the subScene
			video.getSubScene().setTranslateX(xStart*InteractiveLearningApp.getStageWidth()/100);
			video.getSubScene().setTranslateY(yStart*InteractiveLearningApp.getStageHeight()/100);
		}
		
	}
		
	public void remove(int i) {
		if (sp.getChildren().contains(videos.get(i).getSubScene())) {
			videos.get(i).stop();
			sp.getChildren().remove(videos.get(i).getSubScene());

		}
	}
	public void add(int i) {
		if (sp.getChildren().contains(videos.get(i).getSubScene()) == false) {
			sp.getChildren().add(videos.get(i).getSubScene());
			videos.get(i).play();
		}
	}
	
	public void removeVideo(Video video) {
		sp.getChildren().remove(video.getSubScene());
	}

	//please comment on what this exactly is
	public StackPane get() {
		return sp;
	}
}
