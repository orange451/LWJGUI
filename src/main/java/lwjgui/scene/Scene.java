package lwjgui.scene;

import org.lwjgl.glfw.GLFW;

import lwjgui.collections.ObservableList;
import lwjgui.scene.control.PopupWindow;
import lwjgui.transition.TransitionManager;

/**
 * Every window has a Scene that contains various nodes that add functionality to the program. 
 *
 */
public class Scene extends Node {
	private Node root;
	
	private ObservableList<PopupWindow> popups = new ObservableList<PopupWindow>();

	public Scene(Node root) {
		this(root, 1, 1);
	}
	
	public Scene(Node root, double prefWidth, double prefHeight) {
		setRoot(root);
		this.setPrefSize(prefWidth, prefHeight);
	}

	@Override
	public double getX() {
		return 0;
	}
	
	@Override
	public double getY() {
		return 0;
	}
	
	/**
	 * Sets the base node of the scene, essentially becoming the "container" for everything else.
	 * 
	 * @param node
	 */
	public void setRoot(Node node) {
		this.children.clear();
		this.children.add(node);
		this.root = node;
	}

	public Node getRoot() {
		return this.root;
	}
	
	@Override
	public boolean isResizeable() {
		return false;
	}
	
	private boolean first = true;

	@Override
	public void render(Context context) {
		this.flag_clip = true;
		
		if ( root == null )
			return;
		
		// Tick Transitions first so that any changes are reflected in the rendering
		TransitionManager.tick();

		// Stretch to match screen
		root.setPrefSize(getWidth(), getHeight());
		root.setMinSize(getWidth(), getHeight());
		root.setMaxSize(getWidth(), getHeight());
		
		//Root fills the entire screen if it's a FillableRegion
		if ( root instanceof FillableRegion ) {
			((FillableRegion) root).setFillToParentHeight(true);
			((FillableRegion) root).setFillToParentWidth(true);
		}
		
		// Position elements
		for (int i = 0; i < (first?20:2); i++) {
			position(null);
		}
		root.updateChildrenLocalRecursive();
		
		// Render normal
		first = false;
		root.render(context);
		
		// Render popups
		clip(context);
		for (int i = 0; i < popups.size(); i++) {
			PopupWindow p = popups.get(i);
			p.render(context);
		}
	}

	public void showPopup(PopupWindow popup) {
		popups.add(popup);
	}
	
	public void closePopup(PopupWindow popup) {
		popups.remove(popup);
	}

	public ObservableList<PopupWindow> getPopups() {
		return this.popups;
	}

	private Cursor lastCursor = Cursor.NORMAL;
	public void setCursor(Cursor cursor) {
		GLFW.glfwSetCursor(cached_context.getWindowHandle(), cursor.getCursor(cached_context.getWindowHandle()));
		lastCursor = cursor;
	}

	public Cursor getCursor() {
		return lastCursor;
	}
}
