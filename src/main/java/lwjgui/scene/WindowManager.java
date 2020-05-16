package lwjgui.scene;

import static org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_HRESIZE_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_IBEAM_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_VRESIZE_CURSOR;
import static org.lwjgl.glfw.GLFW.glfwCreateCursor;
import static org.lwjgl.glfw.GLFW.glfwCreateStandardCursor;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyCursor;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import lwjgui.LWJGUI;
import lwjgui.Task;
import lwjgui.font.Font;
import lwjgui.glfw.CustomCursor;
import lwjgui.glfw.DecodeTextureException;
import lwjgui.glfw.GLFWException;

public final class WindowManager {

	private static List<Window> windows = new ArrayList<>();
	private static List<Window> toRemove = new ArrayList<>();
	private static Queue<Task<?>> tasks = new ConcurrentLinkedQueue<>();
	private static Map<Cursor, Long> cursors = new HashMap<>();

	private static long mainThread = -1;

	private WindowManager() {
	}

	/**
	 * Generates a {@link WindowHandle}.
	 * 
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 * 
	 * @param width
	 * @param height
	 * @param title
	 * @return
	 */
	public static WindowHandle generateHandle(int width, int height, String title) {
		return new WindowHandle(width, height, title, false);
	}

	public static WindowHandle generateHandle(int width, int height, String title, boolean legacyGL) {
		return new WindowHandle(width, height, title, legacyGL);
	}

	/**
	 * Generates a {@link Window} given a {@link WindowHandle}.
	 * 
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 * 
	 * @param handle
	 * @return
	 */
	public static Window generateWindow(WindowHandle handle) {
		return generateWindow(handle, NULL);
	}

	/**
	 * Generates a {@link Window} given a {@link WindowHandle} and a parent window.
	 * 
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 * 
	 * @param handle
	 * @param parentID
	 * @return
	 */
	public static Window generateWindow(WindowHandle handle, long parentID) {
		System.out.println("Creating new Window '" + handle.title + "'");
		handle.applyHints();
		long windowID = glfwCreateWindow(handle.width, handle.height, (handle.title == null ? "" : handle.title), NULL,
				parentID);
		if (windowID == NULL)
			throw new GLFWException("Failed to create GLFW Window '" + handle.title + "'");

		Window window = new Window(windowID, handle.width, handle.height, handle.title);
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(windowID, (vidmode.width() - window.width) / 2, (vidmode.height() - window.height) / 2);

		try (MemoryStack stack = stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer comp = stack.mallocInt(1);

			if (handle.icons.size() != 0) {
				Buffer iconsbuff = GLFWImage.mallocStack(handle.icons.size(), stack);
				int i = 0;
				for (Icon icon : handle.icons) {

					ByteBuffer imageBuffer;
					try {
						imageBuffer = Context.ioResourceToByteBuffer(icon.getPath(), 16 * 1024);
					} catch (IOException e) {
						throw new GLFWException(e);
					}

					if (!stbi_info_from_memory(imageBuffer, w, h, comp))
						throw new DecodeTextureException("Failed to read image information: " + stbi_failure_reason());

					icon.image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
					if (icon.image == null)
						throw new DecodeTextureException("Failed to load image: " + stbi_failure_reason());

					iconsbuff.position(i).width(w.get(0)).height(h.get(0)).pixels(icon.image);
					i++;
					memFree(imageBuffer);
				}
				iconsbuff.position(0);
				glfwSetWindowIcon(windowID, iconsbuff);
				for (Icon icon : handle.icons)
					stbi_image_free(icon.image);
			}
		}

		int[] h = new int[1];
		int[] w = new int[1];

		glfwGetFramebufferSize(windowID, w, h);
		window.framebufferHeight = h[0];
		window.framebufferWidth = w[0];
		glfwGetWindowSize(windowID, w, h);
		window.height = h[0];
		window.width = w[0];
		window.pixelRatio = (float) window.framebufferWidth / (float) window.width;
		windows.add(window);
		return window;
	}

	/**
	 * Method provided for migration of project into the new multi-thread system.
	 * Generates a {@link Window} from a native glfwWindow
	 * 
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 * 
	 * @param windowID
	 * @return
	 */
	public static Window generateWindow(long windowID) {

		// Initialize window manager
		if ( mainThread == -1 )
			WindowManager.init();
		
		Window window = new Window(windowID, 0, 0, "");
		LWJGUI.setThreadWindow(window);
		window.capabilities = GL.getCapabilities();
		window.init();

		int[] h = new int[1];
		int[] w = new int[1];

		glfwGetFramebufferSize(windowID, w, h);
		window.framebufferHeight = h[0];
		window.framebufferWidth = w[0];
		glfwGetWindowSize(windowID, w, h);
		window.height = h[0];
		window.width = w[0];
		window.pixelRatio = (float) window.framebufferWidth / (float) window.width;
		window.lastLoopTime = getTime();
		window.created = true;
		return window;
	}

	/**
	 * Creates OpenGL and NanoVG contexts in the caller's thread.
	 * 
	 * @param handle
	 * @param window
	 * @param vsync
	 */
	public static void createWindow(WindowHandle handle, Window window, boolean vsync) {
		long windowID = window.getID();

		glfwMakeContextCurrent(windowID);
		glfwSwapInterval(vsync ? 1 : 0);

		window.capabilities = GL.createCapabilities(!handle.legacyGL);
		window.init();

		window.lastLoopTime = getTime();
		window.resetViewport();
		window.created = true;
	}

	public static Window getWindow(long windowID) {
		for (Window window : windows) {
			if (window.windowID == windowID) {
				int index = windows.indexOf(window);
				if (index != 0)
					Collections.swap(windows, 0, index);// Swap the window to the front of
														// the array to speed up future
														// recurring searches
				return window;
			}
		}
		return null;
	}

	private static void addCursor(Cursor cursor, int shape) {
		cursors.put(cursor, glfwCreateStandardCursor(shape));
	}

	/**
	 * Adds or replaces a cursor.
	 * 
	 * @param cursor       Cursor to be replaced
	 * @param customCursor Cursor image
	 */
	public static void addCursor(Cursor cursor, CustomCursor customCursor) {
		runLater(() -> {
			try (MemoryStack stack = stackPush()) {
				IntBuffer w = stack.mallocInt(1);
				IntBuffer h = stack.mallocInt(1);
				IntBuffer comp = stack.mallocInt(1);

				ByteBuffer imageBuffer;
				try {
					imageBuffer = Context.ioResourceToByteBuffer(customCursor.getPath(), 1 * 1024);
				} catch (IOException e) {
					throw new GLFWException(e);
				}

				if (!stbi_info_from_memory(imageBuffer, w, h, comp))
					throw new DecodeTextureException("Failed to read image information: " + stbi_failure_reason());

				ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
				if (image == null)
					throw new DecodeTextureException("Failed to load image: " + stbi_failure_reason());

				GLFWImage img = GLFWImage.mallocStack(stack).set(w.get(0), h.get(0), image);
				long custom = glfwCreateCursor(img, customCursor.getHotX(), customCursor.getHotY());

				memFree(imageBuffer);
				stbi_image_free(image);

				Long prev = cursors.put(cursor, custom);
				if (prev != null)
					glfwDestroyCursor(prev);
			}
		});
	}

	/**
	 * Sets the current active cursor for a {@link Window}
	 * 
	 * @param window Target Window
	 * @param cursor Cursor to use
	 */
	public static void setCursor(Window window, Cursor cursor) {
		runLater(() -> {
			Long handle = cursors.get(cursor);
			if (handle != null)
				glfwSetCursor(window.getID(), handle);
		});
	}

	/**
	 * Executes any scheduled {@link Task}, removes closed {@link Window}s and calls
	 * into {@link GLFW#glfwPollEvents()}.
	 * 
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 */
	public static void update() {
		while (!tasks.isEmpty())
			tasks.poll().callI();
		toRemove.clear();
		for (Window window : windows) {
			window.dirty = false;
			window.resized = false;
			window.getMouseHandler().update();
			if (window.toDestroy()) {
				window.closeDisplay();
				toRemove.add(window);
			}
		}
		glfwPollEvents();
		for (Window window : windows)
			window.getMouseHandler().dropInput();
		for (Window window : toRemove) 
			windows.remove(window);
	}

	/**
	 * Initializes Window Manager objects.
	 * 
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 */
	public static void init() {
		if ( mainThread > -1 )
			return;
		
		mainThread = Thread.currentThread().getId();
		addCursor(Cursor.NORMAL, GLFW_ARROW_CURSOR);
		addCursor(Cursor.VRESIZE, GLFW_VRESIZE_CURSOR);
		addCursor(Cursor.HRESIZE, GLFW_HRESIZE_CURSOR);
		addCursor(Cursor.IBEAM, GLFW_IBEAM_CURSOR);
	}

	/**
	 * Disposes Window Manager objects.
	 * 
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 */
	public static void dispose() {
		for (long cursor : cursors.values())
			glfwDestroyCursor(cursor);
		Font.dispose();
	}

	public static double getTime() {
		return glfwGetTime();
	}

	public static long getNanoTime() {
		return (long) (getTime() * (1000L * 1000L * 1000L));
	}

	public static boolean isEmpty() {
		return windows.isEmpty();
	}

	/**
	 * Executes code at the beginning of the next frame or if the caller's thread is
	 * the main thread it is executed immediately.
	 * 
	 * @param runnable
	 */
	public static void runLater(Runnable runnable) {
		submitTask(new Task<Void>() {
			@Override
			protected Void call() {
				runnable.run();
				return null;
			}
		});
	}

	/**
	 * Submits a {@link Task} to be execute at the beginning of the next frame if
	 * the caller's thread is the main thread it is executed immediately.
	 * 
	 * @param <T> Return value
	 * @param t   Task
	 * @return A {@link Task} with the specificed return value
	 */
	public static <T> Task<T> submitTask(Task<T> t) {
		if (t == null)
			return null;
		if (Thread.currentThread().getId() == mainThread)
			t.callI();
		else
			tasks.add(t);
		return t;
	}

}
