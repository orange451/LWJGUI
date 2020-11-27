package lwjgui.scene;

import static lwjgui.event.listener.EventListener.EventListenerType.CURSOR_POS_LISTENER;
import static lwjgui.event.listener.EventListener.EventListenerType.KEY_LISTENER;
import static lwjgui.event.listener.EventListener.EventListenerType.MOUSE_BUTTON_LISTENER;
import static lwjgui.event.listener.EventListener.EventListenerType.MOUSE_WHEEL_LISTENER;
import static lwjgui.event.listener.EventListener.EventListenerType.WINDOW_CLOSE_LISTENER;
import static lwjgui.event.listener.EventListener.EventListenerType.WINDOW_SIZE_LISTENER;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwFocusWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwMaximizeWindow;
import static org.lwjgl.glfw.GLFW.glfwRestoreWindow;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCharModsCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIconifyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMaximizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowRefreshCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import lwjgui.LWJGUI;
import lwjgui.Task;
import lwjgui.collections.ObservableList;
import lwjgui.event.EventHelper;
import lwjgui.event.KeyEvent;
import lwjgui.event.MouseEvent;
import lwjgui.event.ScrollEvent;
import lwjgui.event.TypeEvent;
import lwjgui.event.listener.CursorPositionListener;
import lwjgui.event.listener.EventListener;
import lwjgui.event.listener.EventListener.EventListenerType;
import lwjgui.event.listener.KeyListener;
import lwjgui.event.listener.MouseButtonListener;
import lwjgui.event.listener.MouseWheelListener;
import lwjgui.event.listener.WindowCloseListener;
import lwjgui.event.listener.WindowFocusListener;
import lwjgui.event.listener.WindowSizeListener;
import lwjgui.gl.Renderer;
import lwjgui.glfw.Callbacks.CharCallback;
import lwjgui.glfw.Callbacks.CharModsCallback;
import lwjgui.glfw.Callbacks.CursorEnterCallback;
import lwjgui.glfw.Callbacks.CursorPosCallback;
import lwjgui.glfw.Callbacks.FramebufferSizeCallback;
import lwjgui.glfw.Callbacks.KeyCallback;
import lwjgui.glfw.Callbacks.MouseButtonCallback;
import lwjgui.glfw.Callbacks.ScrollCallback;
import lwjgui.glfw.Callbacks.WindowCloseCallback;
import lwjgui.glfw.Callbacks.WindowFocusCallback;
import lwjgui.glfw.Callbacks.WindowIconifyCallback;
import lwjgui.glfw.Callbacks.WindowMaximizeCallback;
import lwjgui.glfw.Callbacks.WindowPosCallback;
import lwjgui.glfw.Callbacks.WindowRefreshCallback;
import lwjgui.glfw.Callbacks.WindowSizeCallback;
import lwjgui.glfw.DisplayUtils;
import lwjgui.glfw.input.KeyboardHandler;
import lwjgui.glfw.input.MouseHandler;
import lwjgui.paint.Color;
import lwjgui.scene.control.PopupWindow;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;

public class Window {

	protected final long windowID;

	private List<Task<?>> tasks = new ArrayList<>();

	protected DisplayUtils displayUtils;

	protected GLCapabilities capabilities;

	private Context context;
	private Scene scene;

	protected boolean created = false;
	protected boolean dirty = false;
	protected boolean destroy = false;

	private boolean autoClear = true;
	private boolean autoClose = true;
	private Renderer renderCallback;

	protected int oldPosX = 0, oldPosY = 0, oldWidth = 0, oldHeight = 0;

	protected boolean resized = false;
	protected boolean iconified = false;
	protected boolean visible = true;
	protected boolean maximized = false;
	protected boolean fullscreen;
	protected boolean focused;
	protected int posX = 0;
	protected int posY = 0;
	protected int width = 0;
	protected int height = 0;
	protected int framebufferWidth = 0;
	protected int framebufferHeight = 0;
	protected float pixelRatio = 1;

	protected CharSequence title;

	protected double lastLoopTime;
	protected float timeCount;

	private WindowSizeCallback windowSizeCallback;
	private WindowCloseCallback windowCloseCallback;
	private WindowFocusCallback windowFocusCallback;
	private KeyCallback keyCallback;
	private CharCallback charCallback;
	private MouseButtonCallback mouseButtonCallback;
	private CursorPosCallback cursorPosCallback;
	private ScrollCallback scrollCallback;
	private WindowIconifyCallback windowIconifyCallback;
	private FramebufferSizeCallback framebufferSizeCallback;
	private CursorEnterCallback cursorEnterCallback;
	private CharModsCallback charModsCallback;
	private WindowPosCallback windowPosCallback;
	private WindowMaximizeCallback windowMaximizeCallback;
	private WindowRefreshCallback windowRefreshCallback;

	private MouseHandler mouseHandler;
	private KeyboardHandler keyboardHandler;

	private HashMap<EventListenerType, ArrayList<EventListener>> eventListeners = new HashMap<>();

	public Window(long windowID, int width, int height, String title) {
		this.windowID = windowID;
		this.displayUtils = new DisplayUtils();
		this.width = width;
		this.height = height;
		this.title = title;
		this.visible = getWindowAttribute(GLFW_VISIBLE);
		this.setCallbacks();
		WindowManager.setCursor(this, Cursor.NORMAL);
		
		newContext();
	}
	
	private void newContext() {
		if ( context != null )
			return;
		
		context = new Context(this);
	}

	protected void setCallbacks() {
		cursorPosCallback = new CursorPosCallback();
		cursorPosCallback.addCallback(glfwSetCursorPosCallback(windowID, cursorPosCallback));
		cursorPosCallback.addCallback(this::cursorPosCallback);

		charCallback = new CharCallback();
		charCallback.addCallback(glfwSetCharCallback(windowID, charCallback));
		charCallback.addCallback(this::charCallback);

		keyCallback = new KeyCallback();
		keyCallback.addCallback(glfwSetKeyCallback(windowID, keyCallback));
		keyCallback.addCallback(this::keyCallback);

		mouseButtonCallback = new MouseButtonCallback();
		mouseButtonCallback.addCallback(glfwSetMouseButtonCallback(windowID, mouseButtonCallback));
		mouseButtonCallback.addCallback(this::mouseButtonCallback);

		windowFocusCallback = new WindowFocusCallback();
		windowFocusCallback.addCallback(glfwSetWindowFocusCallback(windowID, windowFocusCallback));
		windowFocusCallback.addCallback(this::focusCallback);
		windowFocusCallback.addCallback((window, focused) -> {
			this.focused = focused;
		});

		windowCloseCallback = new WindowCloseCallback();
		windowCloseCallback.addCallback((window) -> {
			glfwSetWindowShouldClose(window, autoClose);
		});
		windowCloseCallback.addCallback(glfwSetWindowCloseCallback(windowID, windowCloseCallback));
		windowCloseCallback.addCallback(this::closeCallback);

		windowSizeCallback = new WindowSizeCallback();
		windowSizeCallback.addCallback(glfwSetWindowSizeCallback(windowID, windowSizeCallback));
		windowSizeCallback.addCallback(this::sizeCallback);
		windowSizeCallback.addCallback((window, width, height) -> {
			if (width == 0 || height == 0 || this.framebufferWidth == 0 || this.framebufferHeight == 0)
				return;
			pixelRatio = (this.framebufferWidth <= width) ? 1 : this.framebufferWidth / width;
			this.width = width;
			this.height = height;
			resized = true;
			/*
			 * submitTask(new Task<Void>() {
			 * 
			 * @Override protected Void call() { GL.setCapabilities(null);
			 * glfwMakeContextCurrent(NULL); return null; }
			 * 
			 * }).get(); glfwMakeContextCurrent(window); GL.setCapabilities(capabilities);
			 * Window prev = LWJGUI.getThreadWindow(); LWJGUI.setThreadWindow(this);
			 * renderInternal(); glfwSwapBuffers(window); GL.setCapabilities(null);
			 * glfwMakeContextCurrent(NULL); LWJGUI.setThreadWindow(prev); submitTask(new
			 * Task<Void>() {
			 * 
			 * @Override protected Void call() { glfwMakeContextCurrent(window);
			 * GL.setCapabilities(capabilities); return null; }
			 * 
			 * }).get();
			 */
		});

		scrollCallback = new ScrollCallback();
		scrollCallback.addCallback(glfwSetScrollCallback(windowID, scrollCallback));
		scrollCallback.addCallback(this::scrollCallback);

		windowIconifyCallback = new WindowIconifyCallback();
		windowIconifyCallback.addCallback(glfwSetWindowIconifyCallback(windowID, windowIconifyCallback));
		windowIconifyCallback.addCallback((window, iconify) -> {
			iconified = iconify;
		});

		framebufferSizeCallback = new FramebufferSizeCallback();
		framebufferSizeCallback.addCallback(glfwSetFramebufferSizeCallback(windowID, framebufferSizeCallback));
		framebufferSizeCallback.addCallback((window, width, height) -> {
			if (width == 0 || height == 0 || this.width == 0 || this.height == 0)
				return;
			pixelRatio = (width <= this.width) ? 1 : width / this.width;
			framebufferWidth = width;
			framebufferHeight = height;
			System.out.println("Framebuffer call back fired: " + width + " / " + height);
		});

		cursorEnterCallback = new CursorEnterCallback();
		cursorEnterCallback.addCallback(glfwSetCursorEnterCallback(windowID, cursorEnterCallback));

		charModsCallback = new CharModsCallback();
		charModsCallback.addCallback(glfwSetCharModsCallback(windowID, charModsCallback));

		windowPosCallback = new WindowPosCallback();
		windowPosCallback.addCallback(glfwSetWindowPosCallback(windowID, windowPosCallback));
		windowPosCallback.addCallback((window, xpos, ypos) -> {
			posX = xpos;
			posY = ypos;
		});

		windowMaximizeCallback = new WindowMaximizeCallback();
		windowMaximizeCallback.addCallback(glfwSetWindowMaximizeCallback(windowID, windowMaximizeCallback));
		windowMaximizeCallback.addCallback((window, maximized) -> {
			this.maximized = maximized;
		});

		windowRefreshCallback = new WindowRefreshCallback();
		windowRefreshCallback.addCallback(glfwSetWindowRefreshCallback(windowID, windowRefreshCallback));

		mouseHandler = new MouseHandler(this);
		keyboardHandler = new KeyboardHandler(this);
	}

	public void init() {
		newContext();
		
		context.init();
		scene = new Scene(new StackPane());
	}

	public void render() {
		
		// Render Window
		if (!iconified/* && !resized */)
			renderInternal();

		// Perform deferred tasks
		//while (!tasks.isEmpty())
			//tasks.poll().callI();
		
		synchronized(tasks) {
			for (int i = 0; i < tasks.size(); i++) {
				if ( i >= tasks.size() )
					continue;
				Task<?> task = tasks.get(i);
				if ( task == null )
					continue;
				
				task.callI();
			}
			tasks.clear();
		}
	}

	private void renderInternal() {
		// Set correct sizes
		scene.setMinSize(width, height);
		scene.setPrefSize(width, height);
		scene.setMaxSize(width, height);

		// Update context
		context.updateContext();

		// Begin rendering prepass
		context.refresh();

		// Clear screen
		if (isWindowAutoClear()) {
			Color c = Theme.current().getPane();
			glClearColor(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 1);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		}
		if (this.renderCallback != null) {
			this.renderCallback.render(context, width, height);
		}

		// Do NVG frame
		context.refresh();
		nvgBeginFrame(context.getNVG(), width, height, pixelRatio);
		context.setScissor(scene.getX(), scene.getY(), scene.getWidth(), scene.getHeight());
		scene.render(context);

		nvgRestore(context.getNVG());
		nvgEndFrame(context.getNVG());
	}

	/**
	 * Calls {@link GLFW#glfwSwapBuffers(long)} and then optional throttles the
	 * thread
	 * 
	 * @param fps Target FPS, 0 disables it
	 */
	public void updateDisplay(int fps) {
		// if (!resized)
		glfwSwapBuffers(this.windowID);
		this.displayUtils.sync(fps);
	}

	public void setVisible(boolean flag) {
		WindowManager.runLater(() -> {
			if (flag)
				glfwShowWindow(this.windowID);
			else
				glfwHideWindow(this.windowID);
		});
		visible = flag;
	}

	public void setPosition(int x, int y) {
		WindowManager.runLater(() -> glfwSetWindowPos(this.windowID, x, y));
	}

	public void setAlwaysOnTop(boolean ontop) {
		WindowManager.runLater(() -> GLFW.glfwSetWindowAttrib(this.windowID, GLFW.GLFW_FLOATING, ontop?GLFW.GLFW_TRUE:GLFW.GLFW_FALSE));
	}
	
	public boolean isAlwaysOnTop() {
		return GLFW.glfwGetWindowAttrib(this.windowID, GLFW.GLFW_FLOATING)==GLFW_TRUE;
	}

	public void setSize(int width, int height) {
		WindowManager.runLater(() -> glfwSetWindowSize(this.windowID, width, height));
	}

	public void resetViewport() {
		glViewport(0, 0, (int) (width * pixelRatio), (int) (height * pixelRatio));
	}

	public void setViewport(int x, int y, int width, int height) {
		glViewport(x, y, width, height);
	}

	public void enableVSync(boolean vsync) {
		WindowManager.runLater(() -> glfwSwapInterval(vsync ? 1 : 0));
	}

	public void maximize() {
		WindowManager.runLater(() -> glfwMaximizeWindow(windowID));
	}

	public void restore() {
		WindowManager.runLater(() -> glfwRestoreWindow(windowID));
	}

	public void enterFullScreen() {
		if (fullscreen)
			return;
		oldPosX = posX;
		oldPosY = posY;
		oldWidth = width;
		oldHeight = height;
		WindowManager.runLater(() -> {
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowMonitor(windowID, glfwGetPrimaryMonitor(), 0, 0, vidmode.width(), vidmode.height(),
					vidmode.refreshRate());
		});
		fullscreen = true;
	}

	public void exitFullScreen() {
		if (!fullscreen)
			return;
		WindowManager.runLater(
				() -> glfwSetWindowMonitor(windowID, NULL, oldPosX, oldPosY, oldWidth, oldHeight, GLFW_DONT_CARE));
		fullscreen = false;
	}

	public float getDelta() {
		double time = WindowManager.getTime();
		float delta = (float) (time - this.lastLoopTime);
		this.lastLoopTime = time;
		this.timeCount += delta;
		return delta;
	}

	public boolean isWindowCreated() {
		return this.created;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	/**
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 */
	public boolean isResizable() {
		return this.getWindowAttribute(GLFW_RESIZABLE);
	}

	public boolean isIconified() {
		return iconified;
	}

	public boolean isVisible() {
		return visible;
	}

	public int getWindowX() {
		return this.posX;
	}

	public int getWindowY() {
		return this.posY;
	}

	public boolean wasResized() {
		return this.resized;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getFrameBufferWidth() {
		return this.framebufferWidth;
	}

	public int getFrameBufferHeight() {
		return this.framebufferHeight;
	}

	public float getPixelRatio() {
		return this.pixelRatio;
	}

	public long getID() {
		return this.windowID;
	}

	public boolean isCloseRequested() {
		return glfwWindowShouldClose(this.windowID);
	}

	public boolean isMaximized() {
		return maximized;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public boolean isFocused() {
		return focused;
	}

	public boolean toDestroy() {
		return destroy;
	}

	/**
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 */
	private boolean getWindowAttribute(int attribute) {
		return (glfwGetWindowAttrib(this.windowID, attribute) == GLFW_TRUE ? true : false);
	}

	public GLCapabilities getCapabilities() {
		return this.capabilities;
	}

	/**
	 * Freeds callbacks and destroys the window.
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 */
	public void closeDisplay() {
		if (!this.created)
			return;
		Callbacks.glfwFreeCallbacks(this.windowID);
		glfwDestroyWindow(this.windowID);
		this.created = false;
	}

	/**
	 * Unloads any native resource, destroys the OpenGL context and it's data.
	 * 
	 * <p>
	 * This function must only be called from the window thread.
	 * </p>
	 */
	public void dispose() {
		scene.dispose();
		context.dispose();
		glfwMakeContextCurrent(NULL);
		GL.setCapabilities(null);
		this.destroy = true;
	}

	public void setWindowTitle(CharSequence title) {
		this.title = title;
		WindowManager.runLater(() -> glfwSetWindowTitle(this.windowID, title));
	}

	public CharSequence getWindowTitle() {
		return title;
	}

	/**
	 * Return the current context object for this window. The context object stores
	 * more complex information about the window and how its drawn.
	 * 
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Signal to the OS that the window should be focused.
	 */
	public void focus() {
		WindowManager.runLater(() -> glfwFocusWindow(windowID));
	}

	/**
	 * Orange451's hack for forcing the window to focus in scenarios where GLFW's
	 * built-in function doesn't work.
	 */
	public void focusHack() {
		// Hack to get window to focus on windows. Often using Windows 10 a GLFW window
		// will not have focus when it's first on the screen...
		WindowManager.runLater(() -> {
			glfwHideWindow(windowID);
			glfwShowWindow(windowID);
		});
	}

	/**
	 * Default: true. When set to true the window will automatically call OpenGL's
	 * SwapBuffers method after drawing.
	 * 
	 * @param autoDraw
	 * @deprecated Does nothing, for buffer swap and thread throttling use
	 *             {@link #updateDisplay(int)}
	 */
	@Deprecated
	public void setWindowAutoDraw(boolean autoDraw) {
	}

	/**
	 * Default: true. When set to true, the window will automatically clear OpenGL's
	 * depth and color at the start of drawing.
	 * 
	 * @param autoClear
	 */
	public void setWindowAutoClear(boolean autoClear) {
		this.autoClear = autoClear;
	}

	public boolean isWindowAutoClear() {
		return this.autoClear;
	}

	/**
	 * Returns whether the window is managed by LWJGUI. A Managed LWJGUI window will
	 * automatically handle resource deallocation and window closing. Managed
	 * windows are created by using {@link LWJGUI#initialize()} and
	 * {@link LWJGUI#initialize(boolean)}.<br>
	 * Using {@link LWJGUI#initialize(long)} will create an unmanaged window.
	 * 
	 * @return
	 * @deprecated Replaced by {@link WindowThread} object, default return value is
	 *             true
	 */
	@Deprecated
	public boolean isManaged() {
		return true;
	}

	/**
	 * Closes the window
	 */
	public void close() {
		glfwSetWindowShouldClose(windowID, true);
	}

	/**
	 * Iconify (minimise) the window.
	 */
	public void iconify() {
		WindowManager.runLater(() -> glfwIconifyWindow(windowID));
	}

	/**
	 * Attempts to show this Window by setting visibility to true
	 */
	public void show() {
		setVisible(true);
		focus();
		// focusHack();
	}

	public void setScene(Scene scene) {
		this.scene = scene;
		this.scene.setWindow(this);

		try {
			final int s = 10000;
			// Set scene size
			scene.setMinSize(0, 0);
			scene.setMaxSize(s, s);
			scene.size.set(s, s);

			// Buffer it a few times
			for (int i = 0; i < 8; i++) {
				scene.size.set(s, s);
				scene.position(null);
			}

			double maxWid = scene.getMaxElementWidth();
			double maxHei = scene.getMaxElementHeight();

			// Uh oh, we're too big! AHHHHH
			if (maxWid >= s || maxHei >= s) {
				maxWid = Math.max(100, scene.getPrefWidth());
				maxHei = Math.max(100, scene.getPrefHeight());
			}

			// Size window
			// Set window to match scene's size
			double sw = maxWid;
			double sh = maxHei;
			if (scene.getPrefHeight() > 1 && scene.getPrefWidth() > 1) {
				sw = scene.getPrefWidth();
				sh = scene.getPrefHeight();
			}
			double[] values = new double[2];
			values[0] = sw;
			values[1] = sh;
			WindowManager.runLater(() -> {
				setSize((int) Math.ceil(values[0]), (int) Math.ceil(values[1]));
			});
			context.updateContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the current scene object used by this window.
	 * 
	 * @return
	 */
	public Scene getScene() {
		return this.scene;
	}

	/**
	 * Sets the title of the window.
	 * 
	 * @param title
	 */
	public void setTitle(CharSequence title) {
		this.setWindowTitle(title);
	}

	/**
	 * Sets an internal flag which controls whether or not a window can be closed by
	 * the user.
	 * 
	 * @param close
	 */
	public void setCanUserClose(boolean close) {
		this.autoClose = close;
	}

	/**
	 * Sets the rendering callback for this window. By default there is no rendering
	 * callback.<br>
	 * A rendering callback runs directly before the window renders its UI.
	 * 
	 * @param callback
	 */
	public void setRenderingCallback(Renderer callback) {
		this.renderCallback = callback;
	}

	/**
	 * Add the given EventListener to the Window.
	 * 
	 * @param listener
	 */
	public void addEventListener(EventListener listener) {
		EventListenerType key = listener.getEventListenerType();

		if (eventListeners.containsKey(key)) {
			eventListeners.get(key).add(listener);
		} else {
			eventListeners.put(key, new ArrayList<EventListener>());
			addEventListener(listener);
		}
	}

	/**
	 * Remove the given EventListener.
	 * 
	 * @param listener
	 * @return
	 */
	public boolean removeEventListener(EventListener listener) {
		EventListenerType key = listener.getEventListenerType();

		if (eventListeners.containsKey(key)) {
			return eventListeners.get(key).remove(listener);
		} else {
			return false;
		}
	}

	/**
	 * Remove all EventListeners of the given type.
	 * 
	 * @param type
	 */
	public void removeAllEventListeners(EventListenerType type) {
		eventListeners.put(type, new ArrayList<EventListener>());
	}

	/**
	 * Get all EventListeners of the given type.
	 * 
	 * @param type
	 * @return
	 */
	public ArrayList<EventListener> getEventListenersForType(EventListenerType type) {
		if (eventListeners.containsKey(type)) {
			return eventListeners.get(type);
		} else {
			eventListeners.put(type, new ArrayList<EventListener>());
			return getEventListenersForType(type);
		}
	}

	public void setResizible(boolean resizable) {
		glfwSetWindowAttrib(windowID, GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
	}

	public WindowSizeCallback getWindowSizeCallback() {
		return windowSizeCallback;
	}

	public WindowCloseCallback getWindowCloseCallback() {
		return windowCloseCallback;
	}

	public WindowFocusCallback getWindowFocusCallback() {
		return windowFocusCallback;
	}

	public WindowIconifyCallback getWindowIconifyCallback() {
		return windowIconifyCallback;
	}

	public KeyCallback getKeyCallback() {
		return keyCallback;
	}

	public CharCallback getCharCallback() {
		return charCallback;
	}

	public MouseButtonCallback getMouseButtonCallback() {
		return mouseButtonCallback;
	}

	public CursorPosCallback getCursorPosCallback() {
		return cursorPosCallback;
	}

	public ScrollCallback getScrollCallback() {
		return scrollCallback;
	}

	public CursorEnterCallback getCursorEnterCallback() {
		return cursorEnterCallback;
	}

	public CharModsCallback getCharModsCallback() {
		return charModsCallback;
	}

	public FramebufferSizeCallback getFramebufferSizeCallback() {
		return framebufferSizeCallback;
	}

	public WindowMaximizeCallback getWindowMaximizeCallback() {
		return windowMaximizeCallback;
	}

	public WindowPosCallback getWindowPosCallback() {
		return windowPosCallback;
	}

	public MouseHandler getMouseHandler() {
		return mouseHandler;
	}

	public KeyboardHandler getKeyboardHandler() {
		return keyboardHandler;
	}

	public WindowRefreshCallback getWindowRefreshCallback() {
		return windowRefreshCallback;
	}

	public void runLater(Runnable runnable) {
		submitTask(new Task<Void>() {
			@Override
			protected Void call() {
				runnable.run();
				return null;
			}
		});
	}

	public <T> Task<T> submitTask(Task<T> t) {
		if (t == null)
			return null;
		
		synchronized(tasks) {
			tasks.add(t);
		}
		
		return t;
	}

	private void closeCallback(long window) {
		/*
		 * Call window event listeners
		 */
		ArrayList<EventListener> listeners = getEventListenersForType(WINDOW_CLOSE_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((WindowCloseListener) listeners.get(i)).invoke(window);
		}
	}

	private void sizeCallback(long window, int width, int height) {

		/*
		 * Call window event listeners
		 */
		ArrayList<EventListener> listeners = getEventListenersForType(WINDOW_SIZE_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((WindowSizeListener) listeners.get(i)).invoke(window, width, height);
		}

		/*
		 * Update context
		 */
	}

	private void focusCallback(long window, boolean focus) {
		/*
		 * Call window event listeners
		 */
		ArrayList<EventListener> listeners = getEventListenersForType(MOUSE_WHEEL_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((WindowFocusListener) listeners.get(i)).invoke(window, focus);
		}

		/*
		 * Update context
		 */

	}

	private void keyCallback(long handle, int key, int scancode, int action, int mods) {
		boolean isCtrlDown = (mods & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL || (mods & GLFW_MOD_SUPER) == GLFW_MOD_SUPER;
		boolean isAltDown = (mods & GLFW_MOD_ALT) == GLFW_MOD_ALT;
		boolean isShiftDown = (mods & GLFW_MOD_SHIFT) == GLFW_MOD_SHIFT;

		/*
		 * Call window event listeners
		 */
		ArrayList<EventListener> listeners = getEventListenersForType(KEY_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((KeyListener) listeners.get(i)).invoke(handle, key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
		}

		/*
		 * Call scene node listeners (in lwjgui thread)
		 */
		runLater(() -> {
			KeyEvent event = new KeyEvent(key, scancode, action, mods, isCtrlDown, isAltDown, isShiftDown);
			
			notifyKeyInput(scene, event);

			ObservableList<PopupWindow> popups = scene.getPopups();
			for (int i = 0; i < popups.size(); i++) {
				Node root = popups.get(i);
				notifyKeyInput(root, event);
			}
		});
	}

	private void notifyKeyInput(Node root, KeyEvent event) {
		Node selected = Window.this.getContext().getSelected();
		if (root == null || selected == null)
			return;

		ObservableList<Node> children = root.getChildren();
		for (int i = 0; i < children.size(); i++) {
			notifyKeyInput(children.get(i), event);
		}
		
		// Key presses are only sent to ancestors of selected node, and node itself.
		if ( !root.equals(selected) && !selected.isDescendentOf(root) )
			return;

		/*
		 * Key pressed
		 */
		if (event.action == GLFW_PRESS) {
			if (root.keyPressedEventInternal != null && EventHelper.fireEvent(root.keyPressedEventInternal, event)) {
				return;
			}

			if (root.keyPressedEvent != null && EventHelper.fireEvent(root.keyPressedEvent, event)) {
				return;
			}
		}
		/*
		 * Key repeat (e.g. holding backspace to "spam" it)
		 */
		if (event.action == GLFW_REPEAT) {
			if (root.keyRepeatEventInternal != null && EventHelper.fireEvent(root.keyRepeatEventInternal, event)) {
				return;
			}

			if (root.keyRepeatEvent != null && EventHelper.fireEvent(root.keyRepeatEvent, event)) {
				return;
			}
		}
		/*
		 * Key released
		 */
		if (event.action == GLFW_RELEASE) {
			if (root.keyReleasedEventInternal != null && EventHelper.fireEvent(root.keyReleasedEventInternal, event)) {
				return;
			}

			if (root.keyReleasedEvent != null && EventHelper.fireEvent(root.keyReleasedEvent, event)) {
				return;
			}
		}
	}

	private void charCallback(long window, int codepoint) {
		runLater(() -> {
			notifyTextInput(scene, new TypeEvent(codepoint));

			ObservableList<PopupWindow> popups = scene.getPopups();
			for (int i = 0; i < popups.size(); i++) {
				Node root = popups.get(i);
				notifyTextInput(root, new TypeEvent(codepoint));
			}
		});
	}

	private void notifyTextInput(Node root, TypeEvent event) {
		boolean consumed = false;

		Node selected = Window.this.getContext().getSelected();
		if (root == null || selected == null)
			return;

		if (root.textInputEventInternal != null && EventHelper.fireEvent(root.textInputEventInternal, event)) {
			consumed = true;
		}

		if (root.textInputEvent != null && EventHelper.fireEvent(root.textInputEvent, event)) {
			consumed = true;
		}

		if (consumed)
			return;
		

		// Key presses are only sent to ancestors of selected node, and node itself.
		if ( !root.equals(selected) && !selected.isDescendentOf(root) )
			return;

		ObservableList<Node> children = root.getChildren();
		for (int i = 0; i < children.size(); i++) {
			notifyTextInput(children.get(i), event);
		}
	}

	private void mouseButtonCallback(long window, int button, int downup, int modifier) {

		/*
		 * Call window event listeners
		 */
		ArrayList<EventListener> listeners = getEventListenersForType(MOUSE_BUTTON_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((MouseButtonListener) listeners.get(i)).invoke(window, button, downup, modifier);
		}
		runLater(() -> {
			/*
			 * Call scene node/etc listeners
			 */
			
			float mouseX = mouseHandler.getX();
			float mouseY = mouseHandler.getY();
			MouseEvent event = new MouseEvent(mouseX, mouseY, button);

			if (downup == 1) { // Press
				if (!context.hoveringOverPopup && context.getPopups().size() > 0) {
					context.closePopups();
					return;
				}

				Node hovered = context.getHovered();
				if (hovered != null) {
					hovered.onMousePressed(event);
				}
			} else { // Release
				Node lastPressed = context.getLastPressed();

				Node hovered = context.getHovered();
				if (hovered != null && hovered.mousePressed) {
					boolean consumed = hovered.onMouseReleased(event);

					// If not consumed, set selected
					if (button == GLFW_MOUSE_BUTTON_LEFT && !consumed) {
						context.setSelected(hovered);
					}

					if (hovered.mouseDragged)
						EventHelper.fireEvent(hovered.getMouseDraggedEndEvent(),
								new MouseEvent(mouseX, mouseY, GLFW_MOUSE_BUTTON_LEFT));
					if (hovered.mouseDragged)
						EventHelper.fireEvent(hovered.getMouseDraggedEndEventInternal(),
								new MouseEvent(mouseX, mouseY, GLFW_MOUSE_BUTTON_LEFT));
					hovered.mouseDragged = false;
				}

				// If we released on a different node than the one we clicked on...
				if (lastPressed != null && lastPressed != hovered) {
					lastPressed.mousePressed = false;

					if (lastPressed.mouseDragged) {
						EventHelper.fireEvent(lastPressed.getMouseDraggedEndEvent(),
								new MouseEvent(mouseX, mouseY, GLFW_MOUSE_BUTTON_LEFT));
					}

					if (lastPressed.mouseDragged) {
						EventHelper.fireEvent(lastPressed.getMouseDraggedEndEventInternal(),
								new MouseEvent(mouseX, mouseY, GLFW_MOUSE_BUTTON_LEFT));
					}

					lastPressed.mouseDragged = false;
				}
			}
		});
	}

	public void cursorPosCallback(long window, double x, double y) {

		/*
		 * Call window event listeners
		 */
		ArrayList<EventListener> listeners = getEventListenersForType(CURSOR_POS_LISTENER);

		for (int i = 0; i < listeners.size(); i++) {
			((CursorPositionListener) listeners.get(i)).invoke(window, x, y);
		}

		runLater(() -> {
			/*
			 * Call scene node listeners
			 */
			Node selected = context.getSelected();
			if (selected == null)
				return;

			if (selected.mousePressed) {
				selected.mouseDragged = true;
				if (selected.mouseDraggedEvent != null) {
					EventHelper.fireEvent(selected.mouseDraggedEvent, new MouseEvent(x, y, GLFW_MOUSE_BUTTON_LEFT));
				}

				if (selected.mouseDraggedEventInternal != null) {
					EventHelper.fireEvent(selected.mouseDraggedEventInternal,
							new MouseEvent(x, y, GLFW_MOUSE_BUTTON_LEFT));
				}
			}
		});
	}

	public void scrollCallback(long window, double dx, double dy) {
		/*
		 * Call window event listeners
		 */
		ArrayList<EventListener> listeners = getEventListenersForType(MOUSE_WHEEL_LISTENER);

		// System.out.println(dy);

		// Scale scrolling down
		if (dx != 1 && dx != -1 && dy != -1 && dy != 1) {
			if (Math.abs(dx) < 1 || Math.abs(dy) < 1) {
				dx = Math.signum(dx) * (dx * dx);
				dy = Math.signum(dy) * (dy * dy);
			} else {
				dx = (dx - 1) * 0.5 + 1;
				dy = (dy - 1) * 0.5 + 1;
			}
		}

		for (int i = 0; i < listeners.size(); i++) {
			((MouseWheelListener) listeners.get(i)).invoke(window, dx, dy);
		}

		/*
		 * Call scene node listeners
		 */

		double[] values = new double[2];
		values[0] = dx;
		values[1] = dy;
		runLater(() -> {
			notifyScroll(scene, values[0], values[1]);
		});
	}

	private void notifyScroll(Node t, double x, double y) {
		if (t == null)
			return;

		ObservableList<Node> children = t.getChildren();
		for (int i = 0; i < children.size(); i++) {
			notifyScroll(children.get(i), x, y);
		}
		if (t.mouseScrollEventInternal != null) {
			EventHelper.fireEvent(t.mouseScrollEventInternal, new ScrollEvent(x, y));
		}
		if (t.mouseScrollEvent != null) {
			EventHelper.fireEvent(t.mouseScrollEvent, new ScrollEvent(x, y));
		}
	}

}
