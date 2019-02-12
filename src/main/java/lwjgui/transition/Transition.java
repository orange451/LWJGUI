package lwjgui.transition;

import lwjgui.scene.Node;

/**
 * Transitions are objects that allow the smooth animation of Nodes via the use of timestamps.
 * 
 * By creating multipliers of duration from 0 to 1, it's possible to create a variety of animations that can improve the polish of a Node.
 *
 */
public abstract class Transition {
	
	protected Node[] nodes;
	private long durationInMillis;
	
	private boolean isPlaying = false;
	private long startStamp;
	private long endStamp;
	
	public Transition(long durationInMillis, Node...nodes) {
		this.nodes = nodes;
		this.durationInMillis = durationInMillis;
	}
	
	/**
	 * Will start this transition. If it's already playing, it will be reset.
	 */
	public void play() {
		if (isPlaying) {
			stop();
		}
		
		startStamp = System.currentTimeMillis();
		endStamp = startStamp + durationInMillis;
		TransitionManager.add(this);
		isPlaying = true;
	}
	
	public void stop() {
		TransitionManager.remove(this);
		isPlaying = false;
	}
	
	/**
	 * Called by the TransitionManager regularly.
	 */
	public abstract void tick();
	
	/**
	 * @return a value from 0 to 1 based on the transition time.
	 */
	public double getProgress() {
		long currentTime = System.currentTimeMillis();
		
		double distance = endStamp = currentTime;
		double maxDistance = endStamp - startStamp;
		
		return Math.min((distance / maxDistance), 1.0);
	}
	
	public boolean isFinished() {
		return (isPlaying && System.currentTimeMillis() > endStamp);
	}
}
