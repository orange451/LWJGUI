package lwjgui.style;

import java.util.ArrayList;
import java.util.List;

import lwjgui.transition.Transition;

public class StyleTransition {
	private String property;
	private long timeMillis;
	private StyleTransitionType type;
	private List<Transition> currentTransitions = new ArrayList<>();
	
	public StyleTransition(String cssProperty, long timeMillis, StyleTransitionType transitionType) {
		this.property = cssProperty;
		this.timeMillis = timeMillis;
		this.type = transitionType;
	}
	
	public String getProperty() {
		return this.property;
	}
	
	public long getDurationMillis() {
		return this.timeMillis;
	}
	
	public StyleTransitionType getType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return "Transition(" + this.property + ", " + timeMillis + "ms, " + this.type + ")";
	}
	
	public List<Transition> getTransitions() {
		
		for (int i = 0; i < this.currentTransitions.size(); i++) {
			Transition t = this.currentTransitions.get(i);
			if ( t.isFinished() ) {
				this.currentTransitions.remove(i--);
			}
		}
		
		return this.currentTransitions;
	}

	public void setDuration(long duration) {
		this.timeMillis = duration;
	}
	
	public void stopTransitions() {
		while (this.currentTransitions.size() > 0) {
			this.currentTransitions.get(0).stop();
			this.currentTransitions.remove(0);
		}
	}
}

enum StyleTransitionType {
	LINEAR,
	EASE;
}