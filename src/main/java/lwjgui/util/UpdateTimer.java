package lwjgui.util;

/**
 * Allows you to create update() functions that abide by a given UPS in while loops.
 */
public abstract class UpdateTimer {
	private long lastTime = -1;

	/**
	 * Call this in the main while loop of the program to run the UpdateTimer. update() will be called at the given UPS (how many times per second update() is to be called).
	 */
	public void run(double updatesPerSecond) {
		double distanceBetweenUpdates = (1000.0 / updatesPerSecond);
		long currentTime = System.currentTimeMillis();
		
		if (lastTime == -1 || currentTime - lastTime >= distanceBetweenUpdates) {
			update();
			lastTime = currentTime;
		}
	}
	
	protected abstract void update();
}
