package lwjgui.style;

import lwjgui.LWJGUIUtil;
import lwjgui.scene.Context;

public interface BlockPaneRenderer extends StyleBorder,StyleBackground,StyleBoxShadow,CSSStyleable {
	
	public double getX();
	public double getY();
	public double getWidth();
	public double getHeight();
	
	public static void render(Context context, BlockPaneRenderer node) {
		node.stylePush();
		
		// Draw drop shadows
		for (int i = 0; i < node.getBoxShadowList().size(); i++) {
			BoxShadow shadow = node.getBoxShadowList().get(i);
			if ( shadow.isInset() )
				continue;
			LWJGUIUtil.drawBoxShadow(context, shadow, node.getBorderRadii(), (int) node.getX(), (int) node.getY(), (int)node.getWidth(), (int)node.getHeight());
		}
		
		// Draw border
		if ( node.getBorderStyle() != BorderStyle.NONE && node.getBorderWidth() > 0 && node.getBorderColor() != null ) {
			LWJGUIUtil.drawBorder(context, node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getBorderWidth(), node.getBackground(), node.getBorderColor(), node.getBorderRadii() );
		}
		
		// Draw background
		if ( node.getBackground() != null ) {
			node.getBackground().render(context, node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getBorderRadii());
		}
		
		// Draw inset shadows
		for (int i = 0; i < node.getBoxShadowList().size(); i++) {
			BoxShadow shadow = node.getBoxShadowList().get(i);
			if ( !shadow.isInset() )
				continue;
			LWJGUIUtil.drawBoxShadow(context, shadow, node.getBorderRadii(), (int) node.getX(), (int) node.getY(), (int)node.getWidth(), (int)node.getHeight());
		}
		
		node.stylePop();
	}
}
