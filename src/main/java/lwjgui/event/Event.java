package lwjgui.event;

public class Event {
	private boolean consumed;
	
	public void consume() {
		this.consumed = true;
	}
	
	public boolean isConsumed() {
		return this.consumed;
	}
	
	protected void setConsumed(boolean b) {
		this.consumed = b;
	}
}
