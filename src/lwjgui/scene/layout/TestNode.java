package lwjgui.scene.layout;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.geometry.Node;

public class TestNode extends Node {
	
	public TestNode() {
		this.setPrefSize(64, 64);
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		clip(context);
		
		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRoundedRect(context.getNVG(), (float)getAbsoluteX(), (float)getAbsoluteY(), (float)getWidth(), (float)getHeight(), 4);
		NanoVG.nvgFillColor(context.getNVG(), Color.AQUA.getNVG());
		NanoVG.nvgFill(context.getNVG());
		
		
		for (int i = 0; i < children.size(); i++) {
			// Draw child
			Node child = children.get(i);
			child.render(context);
		}
	}

}
