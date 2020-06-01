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

public class Subtitles {
	BufferedReader reader;
	ArrayList<SubtitleBlock> subtitleList = new ArrayList<SubtitleBlock>();
	private int currentSubBlock = 0; 
	
	String displayString = "";//Display string
	Boolean advance = false; //indicate weather to advance to the next subtitle block
	Boolean changeSubtitle = true; //weather to change the subtitle label
	
	//Gap between the current the next subtitle block
	double gapStart;
	double gapEnd;
	
	//
	public Subtitles(File subFile) throws IOException {
		
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(subFile), "UTF-8"));
		//(reader);
		String line = "";
		int index = 0;
		while(line != null) {
			index++;
			SubtitleBlock subBlock = new SubtitleBlock();
			if(reader.readLine() == null) {
				break;
			}
			subBlock.setIndex(index);
			line = reader.readLine();
			////("line2: " + line);
			subBlock.setTimeFrame(line);
			String displayString = "";
			while(!((line = reader.readLine()).isEmpty())) {
				displayString = displayString + line + "\n";
			}
			subBlock.setText(displayString);
			subtitleList.add(subBlock);
		}
	}
	
	//
	public double getStartTimeOfText() {
		return subtitleList.get(currentSubBlock).getStartTime();
		
	}
	
	//
	public double getEndTimeOfText() {
		return subtitleList.get(currentSubBlock).getEndTime();
		
	}
	//
	public void seekPosition(Double currentTime) {
		if(currentTime < getStartTimeOfText()) {
			while(currentTime < getStartTimeOfText()) {
				setGapToNextSubtitle();
				
				if(currentTime > gapStart && currentTime < gapEnd || currentSubBlock == 0) {
					
					break;
				}
				reverseSubTrack();
			}
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
	
	public void setSubtitleText(Label label, MediaPlayer mp) {
		double currentTime = mp.getCurrentTime().toMillis();
		if(currentTime > getStartTimeOfText() && currentTime < getEndTimeOfText()) {
			
			if(changeSubtitle) {
				//Opacity approach to subtitles
				label.setOpacity(1);
				label.setText(getCurrentText());
				changeSubtitle = false;
			}
			advance = true;
		}
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
	
//	public void displaySubtitles() {
//		for(int i = 0; i < subtitleList.size(); i++) {
//			SubtitleBlock newBlock = subtitleList.get(i);
//			
//		}
//	}
	
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
