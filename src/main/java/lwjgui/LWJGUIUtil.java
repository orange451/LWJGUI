package lwjgui;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL11C.GL_TRUE;
import static org.lwjgl.opengl.GL11C.GL_VERSION;
import static org.lwjgl.opengl.GL11C.glGetString;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;

import lwjgui.font.Font;
import lwjgui.font.FontStyle;
import lwjgui.geometry.HPos;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.geometry.VPos;
import lwjgui.gl.BoxShadowShader;
import lwjgui.gl.TexturedQuad;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.style.Background;
import lwjgui.style.BoxShadow;
import lwjgui.util.Bounds;
import lwjgui.util.OperatingSystem;

public class LWJGUIUtil {
	private static BoxShadowShader boxShadowShader;
	private static TexturedQuad unitQuad;
	
	private static void hints(boolean modernOpenGL) {
		if ( modernOpenGL ) {
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		} else {
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_ANY_PROFILE);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		}
	}
	
	private static long createOpenGLWindow(String name, int width, int height, boolean resizable, boolean ontop, boolean modernOpenGL, boolean vsync) {
		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

		// Hints
		hints(modernOpenGL);
		glfwWindowHint(GLFW.GLFW_FLOATING, ontop?GL_TRUE:GL_FALSE);
		glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable?GL_TRUE:GL_FALSE);

		// Create the window
		long window = glfwCreateWindow(width, height, name, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Finalize window
		glfwMakeContextCurrent(window);
		glfwSwapInterval(vsync ? 1 : 0);

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

		// Center the window
		GLFW.glfwSetWindowPos(
				window,
				(vidmode.width() - width) / 2,
				(vidmode.height() - height) / 2
				);

		// Create context
		GL.createCapabilities();
		
		System.out.println("Creating opengl window: " + glGetString(GL_VERSION));

		return window;
	}
	
	/**
	 * Creates a standard OpenGL 3.2 window with a Core Profile.
	 * On Windows OS there is not a difference between old and new OpenGL contexts, graphics drivers 
	 * allow for the mixing of these two.<br><br>
	 * On Mac OS there is a difference between old and new OpenGL contexts. If you wish to use modern 
	 * OpenGL commands, they must be done on a OpenGL 3.2 Core context. If you wish to use deprecated 
	 * OpenGL commands, they must be done on a OpenGL 2.0 Non Core context.
	 * @param name
	 * @param width
	 * @param height
	 * @param resizable
	 * @param ontop
	 * @return GLFW Window Handle
	 */
	public static long createOpenGLCoreWindow(String name, int width, int height, boolean resizable, boolean ontop, boolean vsync) {
		return createOpenGLWindow( name, width, height, resizable, ontop, true, vsync );
	}
	
	/**
	 * Creates a standard OpenGL 2.0 window with NON Core profile.<br>
	 * On Windows OS there is not a difference between old and new OpenGL contexts, graphics drivers 
	 * allow for the mixing of these two.<br><br>
	 * On Mac OS there is a difference between old and new OpenGL contexts. If you wish to use modern 
	 * OpenGL commands, they must be done on a OpenGL 3.2 Core context. If you wish to use deprecated 
	 * OpenGL commands, they must be done on a OpenGL 2.0 Non Core context.
	 * @param name
	 * @param width
	 * @param height
	 * @param resizable
	 * @param ontop
	 * @return GLFW Window Handle
	 */
	public static long createOpenGLDeprecatedWindow(String name, int width, int height, boolean resizable, boolean ontop, boolean vsync) {
		return createOpenGLWindow( name, width, height, resizable, ontop, false, vsync );
	}

	
	/**
	 * Creates a standard OpenGL 3.2 window with a Core Profile.
	 * On Windows OS there is not a difference between old and new OpenGL contexts, graphics drivers 
	 * allow for the mixing of these two.<br><br>
	 * On Mac OS there is a difference between old and new OpenGL contexts. If you wish to use modern 
	 * OpenGL commands, they must be done on a OpenGL 3.2 Core context. If you wish to use deprecated 
	 * OpenGL commands, they must be done on a OpenGL 2.0 Non Core context.
	 * @param name
	 * @param width
	 * @param height
	 * @param resizable
	 * @param ontop
	 * @return GLFW Window Handle
	 */
	public static long createOpenGLCoreWindow(String name, int width, int height, boolean resizable, boolean ontop) {
		return createOpenGLWindow( name, width, height, resizable, ontop, true , true );
	}
	
	/**
	 * Creates a standard OpenGL 2.0 window with NON Core profile.<br>
	 * On Windows OS there is not a difference between old and new OpenGL contexts, graphics drivers 
	 * allow for the mixing of these two.<br><br>
	 * On Mac OS there is a difference between old and new OpenGL contexts. If you wish to use modern 
	 * OpenGL commands, they must be done on a OpenGL 3.2 Core context. If you wish to use deprecated 
	 * OpenGL commands, they must be done on a OpenGL 2.0 Non Core context.
	 * @param name
	 * @param width
	 * @param height
	 * @param resizable
	 * @param ontop
	 * @return GLFW Window Handle
	 */
	public static long createOpenGLDepricatedWindow(String name, int width, int height, boolean resizable, boolean ontop) {
		return createOpenGLWindow( name, width, height, resizable, ontop, false , true );
	}
	
	/**
	 * Fills a NanoVG rectangle at a given x/y with a given width/height and a specified color.
	 * @param context
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void fillRect(Context context, double x, double y, double width, double height, Color color) {
		if ( context == null )
			return;
		
		if ( color == null )
			return;

		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRect(context.getNVG(), (int)x, (int)y, (int)width, (int)height);
		NanoVG.nvgFillColor(context.getNVG(), color.getNVG());
		NanoVG.nvgFill(context.getNVG());
		NanoVG.nvgClosePath(context.getNVG());
	}

	/**
	 * Fills a rounded NanoVG rectangle at a given x/y with a given width/height and a specified color.
	 * @param context
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void fillRoundRect(Context context, double x, double y, double width, double height, double radius, Color color) {
		if ( context == null )
			return;
		
		if ( color == null )
			return;

		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRoundedRect(context.getNVG(), (int)x, (int)y, (int)width, (int)height, (float)radius);
		NanoVG.nvgFillColor(context.getNVG(), color.getNVG());
		NanoVG.nvgFill(context.getNVG());
		NanoVG.nvgClosePath(context.getNVG());
	}

	/**
	 * Fills a rounded NanoVG rectangle at a given x/y with a given width/height and a specified color.
	 * @param context
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void fillRoundRect(Context context, double x, double y, double width, double height, double radiusTopLeft, double radiusTopRight, double radiusBottomRight, double radiusBottomLeft, Color color) {
		if ( context == null )
			return;

		NanoVG.nvgBeginPath(context.getNVG());
		NanoVG.nvgRoundedRectVarying(context.getNVG(), (int)x, (int)y, (int)width, (int)height, (float)radiusTopLeft, (float)radiusTopRight, (float)radiusBottomRight, (float)radiusBottomLeft);
		if ( color != null )
			NanoVG.nvgFillColor(context.getNVG(), color.getNVG());
		NanoVG.nvgFill(context.getNVG());
		NanoVG.nvgClosePath(context.getNVG());
	}

	/**
	 * Outlines a NanoVG rectangle at a given x/y with a given width/height and a specified color.
	 * @param context
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void outlineRect(Context context, double x, double y, double w, double h, Color color) {
		if ( context == null )
			return;
		
		if ( color == null )
			return;

		x = (int)x;
		y = (int)y;
		w = (int)w;
		h = (int)h;
		fillRect( context, x, y, w, 1, color );
		fillRect( context, x, y+h, w+1, 1, color );
		fillRect( context, x, y, 1, h, color );
		fillRect( context, x+w, y, 1, h, color );
	}

	/**
	 * Restarts the Java virtual machine and forces it to run on the first thread IF and only IF it is not currently on the first thread. This allows your LWJGL3 program to run on Mac properly.
	 * <br>
	 * Your java program must return after calling this method if it returns true as to prevent your application from running twice. 
	 * <br><br>
	 * To implement this method, simply put it on the first line of your main(args) function. 
	 * <br><br>
	 * Credit goes to Spasi on JGO for making this utility.
	 * <br><br>
	 * Example of usage (first line in main() method):<br>
	 * if (LWJGUIUtil.restartJVMOnFirstThread(true, args)) {<br>
	 *		return;<br>
	 * }<br>
	 * 
	 * @param needsOutput - Whether or not the JVM should print to System.out.println
	 * @param args - the usual String[] args used in the main method
	 * @return true if the JVM was successfully restarted.
	 */
	public static boolean restartJVMOnFirstThread( boolean needsOutput, String... args ) {
		// Figure out the right class to call
		StackTraceElement[] cause = Thread.currentThread().getStackTrace();

		boolean foundThisMethod = false;
		String callingClassName = null;
		for (StackTraceElement se : cause) {
			// Skip entries until we get to the entry for this class
			String className = se.getClassName();
			String methodName = se.getMethodName();
			if (foundThisMethod) {
				callingClassName = className;
				break;
			} else if (LWJGUIUtil.class.getName().equals(className) && "restartJVMOnFirstThread".equals(methodName)) {
				foundThisMethod = true;
			}
		}

		if (callingClassName == null) {
			throw new RuntimeException("Error: unable to determine main class");
		}

		try {
			Class<?> theClass = Class.forName(callingClassName, true, Thread.currentThread().getContextClassLoader());
			
			return restartJVMOnFirstThread( needsOutput, theClass, args );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * Restarts the Java virtual machine and forces it to run on the first thread IF and only IF it is not currently on the first thread. This allows your LWJGL3 program to run on Mac properly. 
	 * <br><br>
	 * To implement this method, simply put it on the first line of your main(args) function. 
	 * <br><br>
	 * Credit goes to Spasi on JGO for making this utility.
	 * <br><br>
	 * Example of usage (first line in main() method):<br>
	 * if (LWJGUIUtil.restartJVMOnFirstThread(true, class, args)) {<br>
	 *		return;<br>
	 * }<br>
	 * 
	 * @param needsOutput - Whether or not the JVM should print to System.out.println
	 * @param customClass - Class where the main method is stored
	 * @param args - the usual String[] args used in the main method
	 * @return true if the JVM was successfully restarted.
	 */
	public static boolean restartJVMOnFirstThread(boolean needsOutput, Class<?> customClass, String... args) {

		// If we're already on the first thread, return
		String startOnFirstThread = System.getProperty("XstartOnFirstThread");
		if ( startOnFirstThread != null && startOnFirstThread.equals("true") )
			return false;

		// if not a mac then we're already on first thread, return.
		String osName = System.getProperty("os.name");
		if (!osName.startsWith("Mac") && !osName.startsWith("Darwin")) {
			return false;
		}

		// get current jvm process pid
		String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		// get environment variable on whether XstartOnFirstThread is enabled
		String env = System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid);

		// if environment variable is "1" then XstartOnFirstThread is enabled
		if (env != null && env.equals("1")) {
			return false;
		}

		// restart jvm with -XstartOnFirstThread
		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String mainClass = System.getenv("JAVA_MAIN_CLASS_" + pid);
		String jvmPath = System.getProperty("java.home") + separator + "bin" + separator + "java";

		List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();

		ArrayList<String> jvmArgs = new ArrayList<String>();


		jvmArgs.add(jvmPath);
		jvmArgs.add("-XstartOnFirstThread");
		jvmArgs.addAll(inputArguments);
		jvmArgs.add("-cp");
		jvmArgs.add(classpath);

		if ( customClass == null ) {
			jvmArgs.add(mainClass);
		} else {
			jvmArgs.add(customClass.getName());
		}
		for (int i = 0; i < args.length; i++) {
			jvmArgs.add(args[i]);
		}

		// if you don't need console output, just enable these two lines
		// and delete bits after it. This JVM will then terminate.
		if ( !needsOutput ) {
			try {
				ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
				processBuilder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				ProcessBuilder processBuilder = new ProcessBuilder(jvmArgs);
				processBuilder.redirectErrorStream(true);
				Process process = processBuilder.start();

				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;

				while ((line = br.readLine()) != null) {
					System.out.println(line);
				}
				System.exit(0);
				process.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * Draws a NanoVG string at a given location.
	 */
	private static float[] garbage = new float[4];
	public static void drawText(Context vg, String text, Font font, FontStyle style, double size, Color color, double x, double y, Pos alignment) {
		
		float xMult = 0;
		float yMult = 0;
		if ( alignment.getHpos().equals(HPos.CENTER))
			xMult = 0.5f;
		if ( alignment.getHpos().equals(HPos.RIGHT))
			xMult = 1;
		if ( alignment.getVpos().equals(VPos.CENTER))
			yMult = 0.5f;
		if ( alignment.getVpos().equals(VPos.BOTTOM))
			yMult = 1;
		
		float[] bounds = null;
		if ( xMult != 0 || yMult != 0 )
			bounds = font.getTextBounds(vg, text, style, size, garbage);
		
		float width = 0;
		float height = 0;
		if ( xMult != 0 )
			width = bounds[2]-bounds[0];
		if ( yMult != 0 )
			height = bounds[3]-bounds[1];
		
		double xx = x - width*xMult;
		double yy = y - height*yMult;
		
		// Setup font
		NanoVG.nvgFontSize(vg.getNVG(), (float)size);
		NanoVG.nvgFontFace(vg.getNVG(), font.getFont(style));
		NanoVG.nvgTextAlign(vg.getNVG(),NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);

		// Draw
		NanoVG.nvgBeginPath(vg.getNVG());
		NanoVG.nvgFontBlur(vg.getNVG(),0);
		NanoVG.nvgFillColor(vg.getNVG(), color.getNVG());
		NanoVG.nvgText(vg.getNVG(), (float)xx, (float)yy, text);
	}
	
	/**
	 * Opens the users web-browser to a given URL.
	 * @param url
	 * @throws IOException
	 */
	public static void openURLInBrowser(String url) throws IOException {
		Runtime rt = Runtime.getRuntime();
		
		switch (OperatingSystem.detect()) {
		case LINUX:
			String[] browsers = { "epiphany", "firefox", "mozilla", "konqueror", "netscape", "opera", "links", "lynx" };

			StringBuffer cmd = new StringBuffer();
			for (int i = 0; i < browsers.length; i++) {
				// If the first didn't work, try the next browser and so on
				if (i != 0) {
					cmd.append(" || ");
				}
				
				cmd.append(String.format("%s \"%s\"", browsers[i], url));
			}
			
			rt.exec(new String[] { "sh", "-c", cmd.toString() });
			break;
		case MAC:
			rt.exec("open " + url);
			break;
		case WINDOWS:
			rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
			break;
		case OTHER:
		default:
			System.err.println("This function will not work on this operating system.");
			break;
		}
	}

	public static void drawBoxShadow(Context context, BoxShadow boxShadow, float[] cornerRadii, float borderWidth, double x, double y, double width, double height) {
		if ( context == null )
			return;
		
		float xx = (float)x - boxShadow.getSpread() + boxShadow.getXOffset();
		float yy = (float)y - boxShadow.getSpread() + boxShadow.getYOffset();
		float ww = (float)width + boxShadow.getSpread()*2;
		float hh = (float)height + boxShadow.getSpread()*2;
		
		if ( boxShadow.isInset() ) {
			xx += boxShadow.getSpread() * 2;
			yy += boxShadow.getSpread() * 2;
			ww -= boxShadow.getSpread() * 4;
			hh -= boxShadow.getSpread() * 4;
		}
		
		// Compute the average corner radius
		float averageCorner = 0;
		if ( cornerRadii != null ) {
			for (int i = 0; i < cornerRadii.length; i++)
				averageCorner += cornerRadii[i];
			averageCorner /= (float)cornerRadii.length;
		}
		
		// Compute feather (f) and radius (r)
		float f = boxShadow.getBlurRadius();
		float r = averageCorner + boxShadow.getSpread() + (borderWidth*0.5f);
		if ( boxShadow.isInset() )
			r = averageCorner - boxShadow.getSpread();
		
		if ( boxShadow.isInset() ) {
			try(MemoryStack stack = MemoryStack.stackPush()) {
				NVGPaint paint = NanoVG.nvgBoxGradient(context.getNVG(), xx, yy, ww, hh, r, f, boxShadow.getToColor().getNVG(), boxShadow.getFromColor().getNVG(), NVGPaint.mallocStack(stack));
				NanoVG.nvgBeginPath(context.getNVG());
				NanoVG.nvgRoundedRectVarying(context.getNVG(), (float)x, (float)y, (float)width, (float)height, cornerRadii[0], cornerRadii[1], cornerRadii[2], cornerRadii[3]);
				NanoVG.nvgFillPaint(context.getNVG(), paint);
				NanoVG.nvgFill(context.getNVG());
				NanoVG.nvgClosePath(context.getNVG());
			}
		} else {
			if ( context.isCoreOpenGL() ) {
				// Save NANOVG
				NanoVG.nvgSave(context.getNVG());
				NanoVG.nvgEndFrame(context.getNVG());
		
				// Draw shadow to current FBO
				{
					if ( boxShadowShader == null )
						boxShadowShader = new BoxShadowShader();
					
					if ( unitQuad == null )
						unitQuad = new TexturedQuad(0, 0, 1, 1, -1);
					
					boxShadowShader.bind();
					
					// Flip the y :shrug:
					yy = LWJGUI.getThreadWindow().getHeight() - yy - hh;
					
					// Enable blending
					GL32.glBlendFunc(GL32.GL_SRC_ALPHA, GL32.GL_ONE_MINUS_SRC_ALPHA);
		            GL32.glEnable(GL32.GL_BLEND);
		            
		            if ( f < 0.5 )
		            	f = 0.5f;
					
		            // Apply uniforms
		            Bounds scissor = context.getScissor();
		            float scissorFlippedY = (float) (LWJGUI.getThreadWindow().getHeight() - scissor.getY());
					GL20.glUniform4f(GL20.glGetUniformLocation(boxShadowShader.getProgram(), "box"), xx, yy, xx+ww, yy+hh );
					GL20.glUniform4f(GL20.glGetUniformLocation(boxShadowShader.getProgram(), "scissor"),
							(float)scissor.getX(),
							(float)scissorFlippedY-(float)scissor.getHeight(),
							(float)scissor.getX()+(float)scissor.getWidth(),
							(float)scissorFlippedY);
					GL20.glUniform2f(GL20.glGetUniformLocation(boxShadowShader.getProgram(), "window"), LWJGUI.getThreadWindow().getWidth(), LWJGUI.getThreadWindow().getHeight());
					GL20.glUniform1f(GL20.glGetUniformLocation(boxShadowShader.getProgram(), "sigma"), f/2f);
					GL20.glUniform1f(GL20.glGetUniformLocation(boxShadowShader.getProgram(), "corner"), Math.max(f/2, r));
					GL20.glUniform4f(GL20.glGetUniformLocation(boxShadowShader.getProgram(), "boxColor"),
							boxShadow.getFromColor().getRedF(),
							boxShadow.getFromColor().getGreenF(),
							boxShadow.getFromColor().getBlueF(),
							boxShadow.getFromColor().getAlphaF());
					
					// Draw fullscreen quad
					unitQuad.render();
				}
				
				// Restore NANOVG
				NanoVG.nvgRestore(context.getNVG());
				context.refresh();
			} else {
				try(MemoryStack stack = MemoryStack.stackPush()) {
					NVGPaint paint = NanoVG.nvgBoxGradient(context.getNVG(), xx, yy, ww, hh, r, f, boxShadow.getFromColor().getNVG(), boxShadow.getToColor().getNVG(), NVGPaint.mallocStack(stack));
					NanoVG.nvgBeginPath(context.getNVG());
					NanoVG.nnvgRect(context.getNVG(), xx - boxShadow.getBlurRadius(), yy - boxShadow.getBlurRadius(), ww + boxShadow.getBlurRadius()*2, hh + boxShadow.getBlurRadius()*2);
					NanoVG.nvgFillPaint(context.getNVG(), paint);
					NanoVG.nvgFill(context.getNVG());
					NanoVG.nvgClosePath(context.getNVG());
				}
			}
		}
	}	

	public static void drawBorder(Context context, double x, double y, double width, double height, Insets border, Background background, Color borderColor, float[] borderRadii) {
		if ( context == null )
			return;
		
		float xx = (int) x;
		float yy = (int) y;
		float ww = (int) width;
		float hh = (int) height;
		long nvg = context.getNVG();
		if ( nvg <= 0 )
			return;

		float boundsTopLeft = (float) (border.getTop()+border.getLeft())/2f;
		float boundsTopRight = (float) (border.getTop()+border.getRight())/2f;
		float boundsBottomRight = (float) (border.getBottom()+border.getRight())/2f;
		float boundsBottomLeft = (float) (border.getBottom()+border.getLeft())/2f;
		if ( borderRadii[0] <= 0 )
			boundsTopLeft = 0;
		if ( borderRadii[1] <= 0 )
			boundsTopRight = 0;
		if ( borderRadii[2] <= 0 )
			boundsBottomRight = 0;
		if ( borderRadii[3] <= 0 )
			boundsBottomLeft = 0;
		
		// Force scissor
		//Bounds bounds = context.getClipBounds();
		//NanoVG.nvgScissor(nvg, (int)x, (int)y, (int)width, (int)height);
		
		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, borderColor.getNVG());

		float b1 = Math.max((borderRadii[0]) + boundsTopLeft, 0);
		float b2 = Math.max((borderRadii[1]) + boundsTopRight, 0);
		float b3 = Math.max((borderRadii[2]) + boundsBottomRight, 0);
		float b4 = Math.max((borderRadii[3]) + boundsBottomLeft, 0);
		NanoVG.nvgRoundedRectVarying(nvg, xx, yy, ww, hh, b1, b2, b3, b4);
		
		if ( background == null ) {
			xx += border.getLeft();
			yy += border.getTop();
			ww -= border.getWidth();
			hh -= border.getHeight();
			
			NanoVG.nvgPathWinding(nvg, NanoVG.NVG_CW);
			NanoVG.nvgRoundedRectVarying(nvg, xx, yy, ww, hh, borderRadii[0], borderRadii[1], borderRadii[2], borderRadii[3]);
			NanoVG.nvgPathWinding(nvg, NanoVG.NVG_CCW);
		}
		
		NanoVG.nvgFill(nvg);
		NanoVG.nnvgClosePath(nvg);

		// Reset scissor
		//NanoVG.nvgScissor(nvg, (float)bounds.getX(), (float)bounds.getY(), (float)bounds.getWidth(), (float)bounds.getHeight());
	}
}
