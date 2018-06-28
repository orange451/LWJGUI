package lwjgui.scene;

import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import lwjgui.LWJGUI;
import lwjgui.collections.ObservableList;
import lwjgui.scene.control.PopupWindow;

public class Context {
	private long windowHandle;
	private long nvgContext;

	private int windowWidth = 1;
	private int windowHeight = 1;
	private int screenPixelRatio = 1;
	
	private boolean modernOpenGL;

	private Node selected = null;
	private Node hovered = null;

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
	}

	public boolean isFocused() {
		return focused;
	}

	protected void updateContext() {
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

		//System.out.println(root.getClass().getSimpleName() + " / " + root.getWidth());
		
		// If mouse is out of our bounds, we're not clickable
		if ( mouseX <= root.getAbsoluteX() || mouseX > root.getAbsoluteX() + root.getWidth() )
			return parent;
		if ( mouseY <= root.getAbsoluteY() || mouseY > root.getAbsoluteY() + root.getHeight() )
			return parent;

		// Check children
		if ( root instanceof Parent ) {
			ObservableList<Node> children = ((Parent)root).getChildren();
			for (int i = 0; i < children.size(); i++) {
				Node ret = calculateHoverRecursive( root, children.get(i));
				if ( ret != null && !ret.equals(root)) {
					return ret;
				}
			}
		}
		return root;
	}

	public int getWidth() {
		return windowWidth;
	}

	public int getHeight() {
		return windowHeight;
	}

	public int getPixelRatio() {
		return screenPixelRatio;
	}

	public long getNVG() {
		return nvgContext;
	}

	public long getWindowHandle() {
		return windowHandle;
	}

	public boolean isSelected(Node node) {
		if ( selected == null ) {
			return false;
		}
		return node.equals(selected);
	}

	public boolean isHovered(Node node) {
		if ( hovered == null )
			return false;
		return node.equals(hovered);
	}

	public void setSelected(Node node) {
		this.selected = node;
	}

	public double getMouseX() {
		return mouseX;
	}

	public double getMouseY() {
		return mouseY;
	}

	public Node getHovered() {
		return hovered;
	}

	protected ObservableList<PopupWindow> getPopups() {
		Window window = LWJGUI.getWindowFromContext(windowHandle);
		Scene scene = window.getScene();
		return scene.getPopups();
	}

	protected void closePopups() {
		ObservableList<PopupWindow> popups = getPopups();
		while (popups.size() > 0 ) {
			popups.get(0).close();
		}
	}

	public void refresh() {
		GL11.glViewport(0, 0, (int)(getWidth()*getPixelRatio()), (int)(getHeight()*getPixelRatio()));
	}

	public boolean isModernOpenGL() {
		return this.modernOpenGL;
	}

	public Node getSelected() {
		return this.selected;
	}
}
