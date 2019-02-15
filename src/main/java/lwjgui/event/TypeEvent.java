package lwjgui.event;

public class TypeEvent extends Event {
	public final char character;
	
	public TypeEvent(int codepoint) {
		character = (char) codepoint;
	}
	
	public String getCharacterString() {
		return Character.toString(character);
	}
}
