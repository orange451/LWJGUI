package lwjgui.style;

import lwjgui.LWJGUIUtil;
import lwjgui.collections.ObservableList;
import lwjgui.geometry.Insets;
import lwjgui.scene.Context;

public interface BlockPaneRenderer extends StyleBorder,StyleBackground,StyleBoxShadow {
	
	public double getX();
	public double getY();
	public double getWidth();
	public double getHeight();
	
	public static void render(Context context, BlockPaneRenderer node) {
		
		// Draw drop shadows
		for (int i = 0; i < node.getBoxShadowList().size(); i++) {
			BoxShadow shadow = node.getBoxShadowList().get(i);
			if ( shadow.isInset() )
				continue;
			LWJGUIUtil.drawBoxShadow(context, shadow, node.getBorderRadii(), node.getBorderWidth(), (int) node.getX(), (int) node.getY(), (int)node.getWidth(), (int)node.getHeight());
		}
		
		// Draw border
		Insets border = node.getBorder();
		if ( node.getBorderStyle() != BorderStyle.NONE && (border.getWidth() > 0 || border.getHeight() > 0) && node.getBorderColor() != null ) {
			LWJGUIUtil.drawBorder(context, node.getX(), node.getY(), node.getWidth(), node.getHeight(), border, node.getBackground(), node.getBorderColor(), node.getBorderRadii() );
		}
		
		// Draw background
		ObservableList<Background> backgrounds = node.getBackgrounds();
		if ( backgrounds.size() == 0 && node.getBackground() != null ) { // Legacy SINGLE background rendering
			node.getBackground().render(context, node.getX()+border.getLeft(), node.getY()+border.getTop(), node.getWidth()-border.getWidth(), node.getHeight()-border.getHeight(), node.getBorderRadii());
		} else {
			// New multibackground rendering
			for (int i = 0; i < backgrounds.size(); i++) {
				backgrounds.get(i).render(context, node.getX()+border.getLeft(), node.getY()+border.getTop(), node.getWidth()-border.getWidth(), node.getHeight()-border.getHeight(), node.getBorderRadii());
			}
		}
		
		// Draw inset shadows
		for (int i = 0; i < node.getBoxShadowList().size(); i++) {
			BoxShadow shadow = node.getBoxShadowList().get(i);
			if ( !shadow.isInset() )
				continue;
			LWJGUIUtil.drawBoxShadow(context, shadow, node.getBorderRadii(), node.getBorderWidth(), (int) (node.getX()+border.getLeft()), (int) (node.getY()+border.getTop()), (int)(node.getWidth()-border.getWidth()), (int)(node.getHeight()-border.getHeight()));
		}
	}
}
