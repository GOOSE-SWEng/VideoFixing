package media;
/**
 * Class for the audio handler
 * @author - Rimas Radziunas and Cezara-Lidia Jalba
 * @version - 1.1
 * @date - 21/05/20
 */

// 1 block of subtitle
public class SubtitleBlock {
	private double startTime;
	private double endTime;
	private String text;

	public SubtitleBlock() {
	}

	public double getStartTime() {
		return startTime;
	}
	
	public void setTimeFrame(String timeFrame) {
		String startTimeString =  timeFrame.substring(0, 10);
		String endTimeString = timeFrame.substring(17, 28);
		this.startTime = stringToMilis(startTimeString);
		this.endTime = stringToMilis(endTimeString);
		
	}
	
	public double getEndTime() {
		return endTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public double stringToMilis(String timeString) {
		int hour = Integer.parseInt(timeString.substring(0, 2));
		int minutes = Integer.parseInt(timeString.substring(3, 5));
		int seconds = Integer.parseInt(timeString.substring(6, 8));
		int miliseconds = Integer.parseInt(timeString.substring(9, 10));
		double timeMilis = hour * 60 * 60 * 1000 + minutes * 60 * 1000 + seconds * 1000 + miliseconds;
		return timeMilis;
	}
}
