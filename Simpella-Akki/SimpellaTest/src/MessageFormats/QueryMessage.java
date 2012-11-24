package MessageFormats;

public class QueryMessage {

	private byte minSpeed;
	private String searchString = "";
	
	public byte getMinSpeed() {
		return minSpeed;
	}
	public void setMinSpeed(byte minSpeed) {
		this.minSpeed = minSpeed;
	}
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
}
