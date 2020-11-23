package lwjgui.scene;

import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeLimits;
import static org.lwjgl.system.MemoryUtil.memAlloc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import lwjgui.LWJGUIApplication;
import lwjgui.collections.ObservableList;
import lwjgui.event.Event;
import lwjgui.event.EventHelper;
import lwjgui.font.Font;
import lwjgui.glfw.input.MouseHandler;
import lwjgui.scene.control.PopupWindow;
import lwjgui.scene.image.Image;
import lwjgui.style.Stylesheet;
import lwjgui.util.Bounds;

public class Context {

	private Window window;

	private long nvgContext;

	private boolean modernOpenGL;
	private boolean isCore;

	private Node selected = null;
	private Node hovered = null;
	private Node lastPressed = null;

	private List<Node> hoveredNodes = new ArrayList<>();

	protected Bounds clipBounds;

	private List<Stylesheet> currentSheets = new ArrayList<>();

	private List<ByteBuffer> fontBuffers = new ArrayList<>();

	private Set<Font> loadedFonts = new HashSet<>();

	private List<Image> loadedImages = new ArrayList<>();

	public Context(Window window) {
		this.window = window;
	}

	public void init() {
		this.modernOpenGL = (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) > 3)
				|| (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) == 3 && GL11.glGetInteger(GL30.GL_MINOR_VERSION) >= 2);

		if (this.isModernOpenGL()) {
			int flags = NanoVGGL3.NVG_STENCIL_STROKES | NanoVGGL3.NVG_ANTIALIAS;
			nvgContext = NanoVGGL3.nvgCreate(flags);
		} else {
			int flags = NanoVGGL2.NVG_STENCIL_STROKES | NanoVGGL2.NVG_ANTIALIAS;
			nvgContext = NanoVGGL2.nvgCreate(flags);
		}

		loadFont(Font.SANS, true);
		loadFont(Font.COURIER, true);
		loadFont(Font.CONSOLAS, true);
		loadFont(Font.ARIAL, true);
		loadFont(Font.SEGOE, true);
		loadFont(Font.DINGBAT, true);

		isCore = LWJGUIApplication.ModernOpenGL;
	}

	public void dispose() {
		for (ByteBuffer buf : fontBuffers) {
			MemoryUtil.memFree(buf);
		}
		for (Image image : loadedImages) {
			image.dispose();
		}
		loadedFonts.clear();
		fontBuffers.clear();
		loadedImages.clear();
		currentSheets.clear();
		if (this.isModernOpenGL()) {
			NanoVGGL3.nvgDelete(nvgContext);
		} else {
			NanoVGGL2.nvgDelete(nvgContext);
		}
	}

	protected void setContextSize(int width, int height) {
		this.window.setSize(width, height);
	}

	/**
	 * Set the minWidth/minHeight of the Window.
	 * 
	 * @param minWidth
	 * @param minHeight
	 */
	public void setContextSizeLimits(int minWidth, int minHeight) {
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
	public void setContextSizeLimits(int minWidth, int minHeight, int maxWidth, int maxHeight) {
		WindowManager.runLater(() -> glfwSetWindowSizeLimits(window.getID(), minWidth, minHeight, maxWidth, maxHeight));
	}

	protected void updateContext() {
		mouseHover();
	}

	protected boolean hoveringOverPopup;

	private void mouseHover() {
		// Get scene
		Scene scene = window.getScene();

		// Calculate current hover
		hoveringOverPopup = false;
		hovered = calculateHoverRecursive(null, scene);
		Node last = hovered;
		hovered = calculateHoverPopups(scene);

		// Check if hovering over a popup
		if (last != null && !last.equals(hovered)) {
			hoveringOverPopup = true;
		}

		// Not hovering over popups
		if (last != null && last.equals(hovered)) {
			for (int i = 0; i < scene.getPopups().size(); i++) {
				PopupWindow popup = scene.getPopups().get(i);
				popup.weakClose();
			}
		}

		// Check all hovered nodes to see if no longer hovered
		for (int i = 0; i < hoveredNodes.size(); i++) {
			if ( i >= hoveredNodes.size() )
				continue;
			Node node = hoveredNodes.get(i);
			if ( node == null )
				continue;
			
			MouseHandler mh = window.getMouseHandler();
			float mouseX = mh.getX();
			float mouseY = mh.getY();
			Bounds rootBounds = node.getNodeBounds();
			
			if (mouseX <= rootBounds.getX() || mouseX > rootBounds.getX() + rootBounds.getWidth() || mouseY <= rootBounds.getY() || mouseY > rootBounds.getY() + rootBounds.getHeight()) {
				hoveredNodes.remove(node);
				node.onMouseExited();
			}
		}
	}

	private Node calculateHoverPopups(Scene scene) {
		MouseHandler mh = window.getMouseHandler();
		ObservableList<PopupWindow> popups = scene.getPopups();
		for (int i = 0; i < popups.size(); i++) {
			PopupWindow popup = popups.get(i);
			if (popup.contains(mh.getX(), mh.getY())) {
				return calculateHoverRecursive(null, popup);
			}
		}

		return hovered;
	}

	protected Node calculateHoverRecursive(Node parent, Node root) {
		// Use scene as an entry point into nodes
		if (parent == null && root instanceof Scene)
			root = ((Scene) root).getRoot();

		// If there's no root. then there's nothing to hover
		if (root == null)
			return null;

		// Ignore if unclickable
		if (root.isMouseTransparent())
			return parent;

		// Ignore if not visible
		if (!root.isVisible())
			return parent;

		Bounds rootBounds = root.getNodeBounds();

		MouseHandler mh = window.getMouseHandler();
		float mouseX = mh.getX();
		float mouseY = mh.getY();

		// If mouse is out of our bounds, ignore.
		if (mouseX <= rootBounds.getX() || mouseX > rootBounds.getX() + rootBounds.getWidth()
				|| mouseY <= rootBounds.getY() || mouseY > rootBounds.getY() + rootBounds.getHeight()) {
			return parent;
		}

		if (!hoveredNodes.contains(root)) {
			hoveredNodes.add(root);
			root.onMouseEntered();
		}

		// Check children
		ObservableList<Node> children = root.getChildren();
		for (int i = children.size() - 1; i >= 0; i--) {
			Node ret = calculateHoverRecursive(root, children.get(i));
			if (ret != null && !ret.equals(root)) {
				return ret;
			}
		}
		return root;
	}

	/**
	 * Returns the internal NanoVG pointer.
	 * 
	 * @return
	 */
	public long getNVG() {
		return nvgContext;
	}

	/**
	 * Tests if the given node is the current selected node.
	 * 
	 * @param node
	 * @return
	 */
	public boolean isSelected(Node node) {
		if (selected == null)
			return false;

		return selected.equals(node);
	}

	/**
	 * Tests if the given node is the current hovered node.
	 * 
	 * @param node
	 * @return
	 */
	public boolean isHovered(Node node) {
		if (hovered == null)
			return false;
		if (node == null)
			return false;
		return node.equals(getHovered());
	}

	/**
	 * Sets the current selected node for this context.
	 * 
	 * @param node
	 */
	public void setSelected(Node node) {
		if (node == this.selected)
			return;

		Node previouslySelected = this.getSelected();
		if (previouslySelected != null) {
			EventHelper.fireEvent(previouslySelected.getDeselectedEventInternal(), new Event());
			EventHelper.fireEvent(previouslySelected.getDeselectedEvent(), new Event());
		}

		this.selected = node;
		if (this.selected != null) {
			EventHelper.fireEvent(this.selected.getSelectedEventInternal(), new Event());
			EventHelper.fireEvent(this.selected.getSelectedEvent(), new Event());
		}
	}

	/**
	 * Returns the current hovered node.
	 * 
	 * @return
	 */
	public Node getHovered() {
		return hovered;
	}

	/**
	 * Returns a list of all current popups within the window.
	 * 
	 * @return
	 */
	protected ObservableList<PopupWindow> getPopups() {
		Scene scene = window.getScene();
		return scene.getPopups();
	}

	/**
	 * Close all open popups.
	 */
	protected void closePopups() {
		ObservableList<PopupWindow> popups = getPopups();
		while (popups.size() > 0) {
			popups.get(0).close();
		}
	}

	/**
	 * Force resets the OpenGL Viewport to fit the window.
	 */
	public void refresh() {
		this.window.resetViewport();
	}

	/**
	 * Returns whether the internal renderer is using modern opengl (OpenGL 3.2+)
	 * 
	 * @return
	 */
	public boolean isModernOpenGL() {
		return this.modernOpenGL;
	}

	/**
	 * Returns whether the window was created with a core OpenGL profile or not.
	 * 
	 * @return
	 */
	public boolean isCoreOpenGL() {
		return this.isCore;
	}

	/**
	 * Returns the current selected node.
	 * 
	 * @return
	 */
	public Node getSelected() {
		return this.selected;
	}

	/**
	 * Returns the current list of stylesheets used for rendering the node.
	 * 
	 * @return
	 */
	public List<Stylesheet> getCurrentStyling() {
		return currentSheets;
	}

	/**
	 * Returns whether the mouse intersects a node.
	 * 
	 * @param node
	 * @return
	 */
	public boolean isMouseInside(Node node) {
		MouseHandler mh = window.getMouseHandler();
		return node.getNodeBounds().isInside(mh.getX(), mh.getY());
	}

	/**
	 * Returns the last node clicked in this context.
	 * 
	 * @return
	 */
	public Node getLastPressed() {
		return lastPressed;
	}

	protected void setLastPressed(Node node) {
		this.lastPressed = node;
	}

	public Window getWindow() {
		return window;
	}

	public void loadFont(Font font) {
		this.loadFont(font, true);
	}

	public boolean isFontLoaded(Font font) {
		return loadedFonts.contains(font);
	}

	public void loadFont(Font font, boolean loadFallbacks) {
		if (isFontLoaded(font))
			return;

		loadFont(font.getFontPath(), font.getFontNameRegular(), loadFallbacks);
		loadFont(font.getFontPath(), font.getFontNameBold(), loadFallbacks);
		loadFont(font.getFontPath(), font.getFontNameLight(), loadFallbacks);
		loadFont(font.getFontPath(), font.getFontNameItalic(), loadFallbacks);

		// Fallback for fonts loaded via bytebuffer
		if (font.getFontPath() == null && font.getInternalByteBuffer() != null)
			loadFont(font.getFontNameRegular(), font.getInternalByteBuffer(), loadFallbacks);

		// Mark this font as loaded in this context.
		loadedFonts.add(font);
	}

	/**
	 * Loads a given Font
	 * 
	 * @param fontPath
	 * @param loadName
	 * @param suffix
	 * @param map
	 */
	private void loadFont(String fontPath, String loadName, boolean loadFallbacks) {
		if (loadName == null || fontPath == null)
			return;

		try {
			String path = fontPath + loadName;

			// Create normal font
			ByteBuffer buf = ioResourceToByteBuffer(path, 1024 * 1024);

			// Load the font
			loadFont(loadName, buf, loadFallbacks);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads a given Font.
	 * 
	 * @param fontName
	 * @param fontData
	 * @param loadFallbacks
	 */
	private void loadFont(String fontName, ByteBuffer fontData, boolean loadFallbacks) {
		int fontCallback;
		try {
			// Create normal font
			fontCallback = NanoVG.nvgCreateFontMem(nvgContext, fontName, fontData, 0);
			fontBuffers.add(fontData);

			// Fallback emoji fonts
			if (loadFallbacks) {
				addFallback(fontCallback, "sansemoji", Font.fallbackSansEmoji);
				addFallback(fontCallback, "regularemoji", Font.fallbackRegularEmoji);
				addFallback(fontCallback, "arial", Font.fallbackArial);
				addFallback(fontCallback, "entypo", Font.fallbackEntypo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addFallback(int fontCallback, String name, ByteBuffer fontData) {
		NanoVG.nvgAddFallbackFontId(nvgContext, fontCallback, NanoVG.nvgCreateFontMem(nvgContext, name, fontData, 0));
	}
	
	private static InputStream inputStream(String path) throws IOException {
		InputStream stream;
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			stream = new FileInputStream(file);
		} else {
			stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		}
		return stream;
	}
	
	private static byte[] toByteArray(InputStream stream, int bufferSize) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[bufferSize];

		try {
			while ((nRead = stream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toByteArray();
	}

	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer data = null;
		InputStream stream = inputStream(resource);
		if (stream == null) {
			throw new FileNotFoundException(resource);
		}
		byte[] bytes = toByteArray(stream, bufferSize);
		data = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder()).put(bytes);
		data.flip();
		return data;
	}

	public void loadImage(Image image) {
		loadedImages.add(image);
	}

	private Bounds scissor = new Bounds(0, 0, 0, 0);

	public void setScissor(double x, double y, double w, double h) {
		Context context = this.getWindow().getContext();
		scissor.set(x, y, x + w, y + h);
		NanoVG.nvgScissor(context.getNVG(), (int) x, (int) y, (int) w, (int) h);
	}

	public Bounds getScissor() {
		return this.scissor;
	}
}
