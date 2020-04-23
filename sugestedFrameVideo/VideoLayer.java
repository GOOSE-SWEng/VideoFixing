package sugestedFrameVideo;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;

public class VideoLayer {
	int height;
	int width;
	StackPane sp = new StackPane();
	ArrayList<Video> videos = new ArrayList<Video>();
	SubScene window = new SubScene(sp, width, height);

	public VideoLayer(int width, int height) {
		this.height = height;
		this.width = width;
		sp.setMinSize(width, height);
	}

	public void addVideo(String urlName, int startTime, Boolean loop, int xStart, int yStart) throws IOException {
		// creates the video object and its subscene
		Video video = new Video(urlName, startTime, loop, xStart, yStart, width, height);
		// adds the video object to the array list
		videos.add(video);
		// adds the SubScene(created with the constructor) to the video layer stack pane
		sp.getChildren().add(video.get());
	}

	public void removeVideo(Video video) {
		sp.getChildren().remove(video.get());
	}

	//please comment on what this exactly is
	public SubScene get() {
		return (window);
	}
}
