package media;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
/**
 * Class for the audio handler
 * @author - Rimas Radziunas and Cezara-Lidia Jalba
 * @version - 1.1
 * @date - 21/05/20
 */

public class Subtitles {
	BufferedReader reader;
	// store the subtitle increments to be displayed 
	ArrayList<SubtitleBlock> subtitleList = new ArrayList<SubtitleBlock>();
	// track subtitles
	private int currentSubBlock = 0; 
	
	String displayString = "";
	// indicate whether to advance to the next subtitle block
	Boolean advance = false; 
	// indicate whether to change the subtitle label
	Boolean changeSubtitle = true; 
	
	// gap between the current the next subtitle block
	double gapStart;
	double gapEnd;
	
	// read srt file, create subtitle block, store in the list
	public Subtitles(File subFile) throws IOException {
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(subFile), "UTF-8"));
		String line = "";
		//read until the end of the file
		while(line != null) {
			// first read of the number
			if(reader.readLine() == null) {
				break;
			}
			SubtitleBlock subBlock = new SubtitleBlock();
			// second is the timing information
			line = reader.readLine(); 
			subBlock.setTimeFrame(line);
			String displayString = "";
			//read subsequent lines which is the text
			while(!((line = reader.readLine()).isEmpty())) {
				displayString = displayString + line + "\n";
			}
			subBlock.setText(displayString);
			subtitleList.add(subBlock);
		}
	}
	
	public double getStartTimeOfText() {
		return subtitleList.get(currentSubBlock).getStartTime();
		
	}

	public double getEndTimeOfText() {
		return subtitleList.get(currentSubBlock).getEndTime();
		
	}
	
	// seeks the correct subtitle time frame with for the current time of the video
	public void seekPosition(Double currentTime) {
		// if the subtitle is ahead of the current time, cycle back till correct one is reached
		if(currentTime < getStartTimeOfText()) {
			while(currentTime < getStartTimeOfText()) {
				setGapToNextSubtitle();
				if(currentTime > gapStart && currentTime < gapEnd || currentSubBlock == 0) {
					break;
				}
				reverseSubTrack();
			}
		// if the subtitle is behind the current time, cycle forwards till correct one is reached
		} else if(currentTime > getEndTimeOfText()) {
			while(currentTime > getStartTimeOfText()) {
				fowardSubTrack();
				setGapToNextSubtitle();
				if(currentTime > gapStart && currentTime < gapEnd) {
					break;
				}
				fowardSubTrack();
			}
		} 	
	}
	
	public String getCurrentText() {
		return subtitleList.get(currentSubBlock).getText();
	}
	
	public void fowardSubTrack() {
		currentSubBlock++;
		if(currentSubBlock > subtitleList.size()-1) {
			currentSubBlock--;
		}
	}
	public void reverseSubTrack() {
		currentSubBlock--;
		if(currentSubBlock < 0) {
			currentSubBlock++;
		}
	}
	
	// set the subtitle text
	public void setSubtitleText(Label label, MediaPlayer mp) {
		double currentTime = mp.getCurrentTime().toMillis();
		// when the current time falls into current subtitle time frame, set opacity to 1 display it
		// and set the text, stop the change of subtitle text and opacity, enable advancing to the next subTitle
		if(currentTime > getStartTimeOfText() && currentTime < getEndTimeOfText()) {
			if(changeSubtitle) {
				label.setOpacity(1);
				label.setText(getCurrentText());
				changeSubtitle = false;
			}
			advance = true;
		}
		// when the current time leave the time frame, set label opacity to 0, if advance is true
		// jump to the next subtitle, stop advancement and enable the change of the label
		else if(currentTime > getEndTimeOfText() || currentTime < getStartTimeOfText()) {
			label.setOpacity(0);
			if(advance) {
				fowardSubTrack();
				advance = false;
				changeSubtitle = true;
			}		
		}
	}
	
	public void setGapToNextSubtitle() {
		gapStart = (int) subtitleList.get(currentSubBlock).getEndTime();
		gapEnd = (int) subtitleList.get(currentSubBlock + 1).getStartTime();
	}
	
	public String formatTime(double time) {
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
}
