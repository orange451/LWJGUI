package lwjgui.scene.control;

public class IndexRange {
	private int start;
	private int end;
	
	public IndexRange(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public IndexRange normalize() {
		if ( end < start ) {
			int temp = start;
			start = end;
			end = temp;
		}
		return this;
	}
	
	public int getLength() {
		return Math.abs(start-end);
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public boolean equals(Object object) {
		if ( object == null )
			return false;
		
		if ( !object.getClass().equals(this.getClass()) )
			return false;
		
		IndexRange r = (IndexRange)object;
		if ( r.getStart() != this.getStart() || r.getEnd() != this.getEnd() )
			return false;
		
		return true;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
}
