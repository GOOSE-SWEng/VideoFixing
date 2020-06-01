package media;

import java.io.IOException;
import java.util.ArrayList;


import javafx.geometry.Pos;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import main.InteractiveLearningApp;

public class VideoLayer {
	int height;
	int width;
	StackPane sp = new StackPane();
	public ArrayList<Video> videos;

	public VideoLayer(int width, int height, ArrayList<Video> videos) {
		this.height = height;
		this.width = width;
		this.videos = videos;
		sp.setPickOnBounds(false);
		sp.setAlignment(Pos.TOP_LEFT);
		//sp.setMinSize(width, height);
		//window = new SubScene(sp, width, height/2);
	}

	public void addVideo(String urlName, String subUrlName, int startTime, Boolean loop, int xStart, int yStart, int slideNumber) throws IOException {
		// creates the video object and its subscene
		Video video = new Video(urlName, subUrlName, startTime, loop, xStart, yStart, 0, 0);
		
		if(video.videoFail == false) {
			// adds the video object to the array list
			videos.add(video);
			InteractiveLearningApp.slides.get(slideNumber).getSlideVideos().add(video);
			// adds the SubScene(created with the constructor) to the video layer stack pane
			sp.getChildren().add(video.get());
			video.get().setTranslateX(xStart*InteractiveLearningApp.getStageWidth()/100);
			video.get().setTranslateY(yStart*InteractiveLearningApp.getStageHeight()/100);
		}
		
	}
		

	public void removeVideo(Video video) {
		sp.getChildren().remove(video.get());
	}

	//please comment on what this exactly is
	public StackPane get() {
		return sp;
	}
}
