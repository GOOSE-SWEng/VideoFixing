package handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Subtitles {
	BufferedReader reader;
	ArrayList<SubtitleBlock> subtitleList = new ArrayList();
	private int currentSubBlock = 0; 
	
	String displayString = "";//Display string
	Boolean advance = false; //indicate weather to advance to the next subtitle block
	Boolean changeSubtitle = true; //weather to change the subtitle label
	
	//Gap between the current the next subtitle block
	double gapStart;
	double gapEnd;
	
	public Subtitles(File subFile) throws IOException {
		
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(subFile), "UTF-8"));
		System.out.println(reader);
		String line = "";
		int index = 0;
		while(line != null) {
			index++;
			SubtitleBlock subBlock = new SubtitleBlock();
			
			if(reader.readLine() == null) {
				System.out.println("BREAK!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				break;
			}
			subBlock.setIndex(index);
			line = reader.readLine();
			//System.out.println("line2: " + line);
			subBlock.setTimeFrame(line);
			String displayString = "";
			while(!((line = reader.readLine()).isEmpty())) {
				//System.out.println("line3: " + line);
				displayString = displayString + line + "\n";
				
				//System.out.println("line3: " + line);
				//System.out.println("line3: " + line);
			}
			//System.out.println("line4: " + displayString);
			subBlock.setText(displayString);
			subtitleList.add(subBlock);
		}
		System.out.println("DONE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
	
	
	//need to read the correct line for timing
	//need to read correct line for the text
	public double getStartTimeOfText() {
		return subtitleList.get(currentSubBlock).getStartTime();
		
	}
	
	public double getEndTimeOfText() {
		return subtitleList.get(currentSubBlock).getEndTime();
		
	}
	
	//need to find the correct place in the subtitles when the current video time is changed to a random point with a slider
	public void seekPosition(Double currentTime) {
		if(currentTime < getStartTimeOfText()) {
			System.out.println("start==========================================");
			while(currentTime < getStartTimeOfText()) {
				
				
				setGapToNextSubtitle();
				System.out.println("GAP bounds: start: " + formatTime(gapStart) + " end: " + formatTime(gapEnd));
				
				if(currentTime > gapStart && currentTime < gapEnd || currentSubBlock == 0) {
					System.out.println("within the gap, break");
					break;
				}
				reverseSubTrack();
				System.out.println("sub start: " + formatTime(this.getStartTimeOfText()) + ", end: " + formatTime(this.getEndTimeOfText()));
				System.out.println("SUbtitle is a head");
			}
			System.out.println("end==========================================");
			
		} else if(currentTime > getEndTimeOfText()) {
			System.out.println("start==========================================");
			while(currentTime > getStartTimeOfText()) {
				fowardSubTrack();
				setGapToNextSubtitle();
				System.out.println("GAP bounds: start: " + formatTime(gapStart) + " end: " + formatTime(gapEnd));
				
				if(currentTime > gapStart && currentTime < gapEnd) {
					
					System.out.println("within the gap, break");
					break;
				}
				fowardSubTrack();
				System.out.println("SUbtitle is a late");
			}
			System.out.println("sub start: " + formatTime(this.getStartTimeOfText()) + ", end: " + formatTime(this.getEndTimeOfText()));
			System.out.println("end==========================================");
			
		} 
			
			
	}
	
	public String getCurrentText() {
		return subtitleList.get(currentSubBlock).getText();
	}
	
	public void fowardSubTrack() {
		//System.out.println(currentSubBlock);
		currentSubBlock++;
		if(currentSubBlock > subtitleList.size()-1) {
			currentSubBlock--;
			//System.out.println("tooBig");
		}
	}
	public void reverseSubTrack() {
		//System.out.println(currentSubBlock);
		currentSubBlock--;
		if(currentSubBlock < 0) {
			currentSubBlock++;
		}
	}
	
	public void setSubtitleText(Label label, MediaPlayer mp) {
		double currentTime = mp.getCurrentTime().toMillis();
		if(currentTime > getStartTimeOfText() && currentTime < getEndTimeOfText()) {
			
			if(changeSubtitle) {
				
				label.setText(getCurrentText());
				System.out.println("seting label text:"+  getCurrentText());
				changeSubtitle = false;
			}
			advance = true;
		}
		else if(currentTime > getEndTimeOfText() || currentTime < getStartTimeOfText()) {
			//subtitleTrack.fowardSubTrack();
			label.setText("");
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
	
	public void displaySubtitles() {
		for(int i = 0; i < subtitleList.size(); i++) {
			SubtitleBlock newBlock = subtitleList.get(i);
			System.out.println("id: " + newBlock.getIndex() + "\n" +
							   "start time: " + newBlock.getStartTime() + "\n"+
							   "end time: " + newBlock.getEndTime() + "\n" +
							   "text: " + newBlock.getText());
		}
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
