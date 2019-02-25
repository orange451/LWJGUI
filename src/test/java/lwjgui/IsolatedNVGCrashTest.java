package lwjgui;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.geometry.Pos;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.layout.StackPane;

public class IsolatedNVGCrashTest extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {
		try {
			loadFont(window, "lwjgui/scene/layout/Arial-Unicode.ttf", "test1");
			loadFont(window, "lwjgui/scene/layout/entypo.ttf", "test2");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadFont(Window window, String file1, String name) throws IOException{
		try {
			long vg = window.getContext().getNVG();
			ByteBuffer buf = resourceToByteBuffer(file1);
			int fontCallback = nvgCreateFontMem(vg, name, buf, 0);
	        NanoVG.nvgAddFallbackFontId(vg, fontCallback, nvgCreateFontMem(vg, name, resourceToByteBuffer("lwjgui/scene/layout/entypo.ttf"), 0));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ByteBuffer resourceToByteBuffer(String path) throws IOException {
		ByteBuffer data = null;
		InputStream stream = inputStream(path);
		if (stream == null) {
			throw new FileNotFoundException(path);
		}
		byte[] bytes = toByteArray(stream);
		data = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder()).put(bytes);
		data.flip();
		return data;
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
	
	private static byte[] toByteArray(InputStream stream) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

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

	@Override
	public void run() {}

	@Override
	public String getProgramName() {
		return "Open URL Example";
	}

	@Override
	public int getDefaultWindowWidth() {
		return WIDTH;
	}

	@Override
	public int getDefaultWindowHeight() {
		return HEIGHT;
	}
}