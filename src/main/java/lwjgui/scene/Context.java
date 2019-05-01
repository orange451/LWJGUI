package lwjgui.scene;

import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeLimits;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIApplication;
import lwjgui.collections.ObservableList;
import lwjgui.scene.control.PopupWindow;
import lwjgui.util.Bounds;

public class Context {
	private long windowHandle = -1;
	private long nvgContext;

	protected int windowWidth = 1;
	protected int windowHeight = 1;
	private int screenPixelRatio = 1;
	
	private boolean modernOpenGL;
	private boolean isCore;

	private Node selected = null;
	private Node hovered = null;
	private Node lastPressed = null;

	private double mouseX;
	private double mouseY;
	protected boolean focused;

	public Context( long window ) {
		windowHandle = window;

		this.modernOpenGL = (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) > 3) || (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) == 3 && GL11.glGetInteger(GL30.GL_MINOR_VERSION) >= 2);
		
		if ( this.isModernOpenGL() ) {
			int flags = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS;
			nvgContext = NanoVGGL3.nvgCreate(flags);
		} else {
			int flags = NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS;
			nvgContext = NanoVGGL2.nvgCreate(flags);
		}
		
		isCore = LWJGUIApplication.ModernOpenGL;
	}

	/**
	 * Returns if this context is the current focused context.
	 * @return
	 */
	public boolean isFocused() {
		return focused;
	}
	
	protected void setContextSize( int width, int height ) {
		this.windowWidth = width;
		this.windowHeight = height;
	}

	/**
	 * Set the minWidth/minHeight of the Window.
	 * 
	 * @param minWidth
	 * @param minHeight
	 */
	public void setContextSizeLimits(int minWidth, int minHeight){
		setContextSizeLimits(minWidth, minHeight, GLFW_DONT_CARE, GLFW_DONT_CARE);
	}
	
	/**
	 * Set the minWidth/minHeight/maxWidth/maxHeight of the Window.
	 * 
	 * @param minWidth
	 * @param minHeight
	 * @param maxWidth
	 * @param maxHeight
	 */
	public void setContextSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight){
		glfwSetWindowSizeLimits(windowHandle, minWidth, minHeight, maxWidth, maxHeight);
	}
	
	protected void updateContext() {
		if ( windowHandle == -1 )
			return;
		
		int[] windowWidthArr = {0}, windowHeightArr = {0};
		int[] frameBufferWidthArr = {0}, frameBufferHeightArr = {0};
		int[] xposArr = {0}, yposArr = {0};
		glfwGetWindowSize(windowHandle, windowWidthArr, windowHeightArr);
		glfwGetFramebufferSize(windowHandle, frameBufferWidthArr, frameBufferHeightArr);
		glfwGetWindowPos(windowHandle, xposArr, yposArr);

		if ( windowWidthArr[0] == 0 ) {
			return;
		}

		windowWidth = windowWidthArr[0];
		windowHeight = windowHeightArr[0];
		screenPixelRatio = frameBufferWidthArr[0]/windowWidthArr[0];

		double[] mousePosX = {0},mousePosY = {0};
		GLFW.glfwGetCursorPos(windowHandle, mousePosX, mousePosY);
		mouseX = mousePosX[0];
		mouseY = mousePosY[0];

		mouseHover();
	}

	private Node lastHovered = null;
	protected boolean hoveringOverPopup;
	private void mouseHover() {
		// Get scene
		Window window = LWJGUI.getWindowFromContext(windowHandle);
		Scene scene = window.getScene();

		// Calculate current hover
		hoveringOverPopup = false;
		hovered = calculateHoverRecursive(null, scene);
		Node last = hovered;
		hovered = calculateHoverPopups(scene);

		// Check if hovering over a popup
		if ( last != null && !last.equals(hovered) ) {
			hoveringOverPopup = true;
		}

		// Not hovering over popups
		if ( last != null && last.equals(hovered) ) {
			for (int i = 0; i < scene.getPopups().size(); i++) {
				PopupWindow popup = scene.getPopups().get(i);
				popup.weakClose();
			}
		}
		
		// Mouse hovered event
		if ( hovered != null && (lastHovered == null || !lastHovered.equals(hovered)) ) {
			hovered.onMouseEntered();
		}
		if (lastHovered != null && (hovered == null || !lastHovered.equals(hovered)) ) {
			lastHovered.onMouseExited();
		}
		lastHovered = hovered;
	}

	private Node calculateHoverPopups(Scene scene) {
		ObservableList<PopupWindow> popups = scene.getPopups();
		for (int i = 0; i < popups.size(); i++) {
			PopupWindow popup = popups.get(i);
			if ( popup.contains(mouseX, mouseY) ) {
				return calculateHoverRecursive(null, popup);
			}
		}

		return hovered;
	}

	protected Node calculateHoverRecursive(Node parent, Node root) {
		// Use scene as an entry point into nodes
		if ( parent == null && root instanceof Scene ) 
			root = ((Scene)root).getRoot();

		// If there's no root. then there's nothing to hover
		if ( root == null )
			return null;

		// Ignore if unclickable
		if ( root.isMouseTransparent() )
			return parent;

		Bounds rootBounds = root.getNodeBounds();
		
		// If mouse is out of our bounds, we're not clickable
		if (mouseX <= rootBounds.getX() || mouseX > rootBounds.getX() + rootBounds.getWidth()
				|| mouseY <= rootBounds.getY() || mouseY > rootBounds.getY() + rootBounds.getHeight()) {

			/*System.err.println(parent + " " + root + " -> " 
					+ "\n" + rootBounds.getX() + " " + rootBounds.getY() + " " + rootBounds.getWidth() + " " + rootBounds.getHeight() 
					+ "\n" + root.getX() + " " + root.getY() + " " + root.getWidth() + " " + root.getHeight());*/
			return parent;
		}

		// Check children
		ObservableList<Node> children = root.getChildren();
		for (int i = children.size()-1; i >= 0; i--) {
			Node ret = calculateHoverRecursive( root, children.get(i));
			if ( ret != null && !ret.equals(root)) {
				return ret;
			}
		}
		return root;
	}
	
	/**
	 * Returns the current height of the context window.
	 * @return
	 */
	public int getWidth() {
		return windowWidth;
	}

	/**
	 * Returns the current width of the context window.
	 * @return
	 */
	public int getHeight() {
		return windowHeight;
	}

	/**
	 * Returns the current pixel ratio for this context.<br>
	 * Retina displays will commonly return 2. Screens with a larger pixel ratio pack more detail in a smaller space.
	 * @return
	 */
	public int getPixelRatio() {
		return screenPixelRatio;
	}

	/**
	 * Returns the internal NanoVG pointer.
	 * @return
	 */
	public long getNVG() {
		return nvgContext;
	}

	/**
	 * Returns the OpenGL window handle.
	 * @return
	 */
	public long getWindowHandle() {
		return windowHandle;
	}

	/**
	 * Tests if the given node is the current selected node.
	 * @param node
	 * @return
	 */
	public boolean isSelected(Node node) {
		if ( selected == null )
			return false;
		
		return selected.equals(node);
	}
	/**
	 * Tests if the given node is the current hovered node.
	 * @param node
	 * @return
	 */
	public boolean isHovered(Node node) {
		if ( hovered == null )
			return false;
		if ( node == null )
			return false;
		return node.equals(getHovered());
	}

	/**
	 * Sets the current selected node for this context.
	 * @param node
	 */
	public void setSelected(Node node) {
		this.selected = node;
	}
	
	/**
	 * Returns the mouses X position relative to this context.
	 * @return
	 */
	public double getMouseX() {
		return mouseX;
	}

	/**
	 * Returns the mouses Y position relative to this context.
	 * @return
	 */
	public double getMouseY() {
		return mouseY;
	}

	/**
	 * Returns the current hovered node.
	 * @return
	 */
	public Node getHovered() {
		return hovered;
	}

	/**
	 * Returns a list of all current popups within the window.
	 * @return
	 */
	protected ObservableList<PopupWindow> getPopups() {
		Window window = LWJGUI.getWindowFromContext(windowHandle);
		Scene scene = window.getScene();
		return scene.getPopups();
	}

	/**
	 * Close all open popups.
	 */
	protected void closePopups() {
		ObservableList<PopupWindow> popups = getPopups();
		while (popups.size() > 0 ) {
			popups.get(0).close();
		}
	}

	/**
	 * Force resets the OpenGL Viewport to fit the window.
	 */
	public void refresh() {
		GL11.glViewport(0, 0, (int)(getWidth()*getPixelRatio()), (int)(getHeight()*getPixelRatio()));
	}

	/**
	 * Returns whether the internal renderer is using modern opengl (OpenGL 3.2+)
	 * @return
	 */
	public boolean isModernOpenGL() {
		return this.modernOpenGL;
	}
	
	/**
	 * Returns whether the window was created with a core OpenGL profile or not.
	 * @return
	 */
	public boolean isCoreOpenGL() {
		return this.isCore;
	}

	/**
	 * Returns the current selected node.
	 * @return
	 */
	public Node getSelected() {
		return this.selected;
	}

	/**
	 * Returns whether the mouse intersects a node.
	 * @param node
	 * @return
	 */
	public boolean isMouseInside(Node node) {
		return node.getNodeBounds().isInside(mouseX, mouseY);
	}

	/**
	 * Returns the last node clicked in this context.
	 * @return
	 */
	public Node getLastPressed() {
		return lastPressed;
	}

	protected void setLastPressed(Node node) {
		this.lastPressed = node;
	}

	/**
	 * Returns the viewport width for the context. Normally returns width, unless the pixel ratio is non 1.
	 * @return
	 */
	public int getViewportWidth() {
		return getWidth()*getPixelRatio();
	}
	
	/**
	 * Returns the viewport height for the context. Normally returns height, unless the pixel ratio is non 1.
	 * @return
	 */
	public int getViewportHeight() {
		return getHeight()*getPixelRatio();
	}
}
