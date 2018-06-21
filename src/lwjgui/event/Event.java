package lwjgui.event;

public abstract class Event {
	private boolean consumed;
	public abstract void onEvent();
	
	public void consume() {
		this.consumed = true;
	}
	
	public boolean isConsumed() {
		return this.consumed;
	}
	
	public void setConsumed(boolean b) {
		this.consumed = b;
	}
}
