package lwjgui.collections;

import java.util.ArrayList;

public class StateStack<E> {
	private final int maxStates;
	private ArrayList<E> states = new ArrayList<E>();
	private int current;
	
	public StateStack(int maxStates) {
		this.maxStates = maxStates;
	}
	
	public StateStack() {
		this(512);
	}
	
	/**
	 * Push a state onto the stack. If the current state is not fully forwarded, all future states are lost.
	 * @param state
	 */
	public void Push(E state) {
		// Remove any states that exist after the current state. (Delete future)
		while ( states.size() > current ) {
			states.remove(states.size()-1);
		}
		
		// Delete history if we've exceeded maxStates
		while ( states.size() >= maxStates ) {
			states.remove(0);
		}
		
		// Add to states
		states.add(state);
		
		// Set current state
		current = states.size();
	}
	
	/**
	 * Returns if the current state is the most recent state.
	 * @return
	 */
	public boolean isCurrent() {
		return current == states.size();
	}

	/**
	 * Rewind one state on the stack.
	 * @return
	 */
	public E Rewind() {
		current--;
		if ( current < 0 ) {
			current = 0;
		}
		if ( states.size() == 0 )
			return null;
		
		return states.get(current);
	}
	
	/**
	 * Move forward one state on the stack
	 * @return
	 */
	public E Forward() {
		current++;
		if ( current >= states.size() ) {
			current = states.size()-1;
			//return states.get(current);
		}
		return states.get(current);
	}
}