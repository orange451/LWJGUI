package lwjgui;

import static org.lwjgl.opengl.GL11.*;

import lwjgui.gl.Renderer;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.Menu;
import lwjgui.scene.control.MenuBar;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.layout.BorderPane;

public class GearsDemo extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {

		ModernOpenGL = false;
		/* Flag to make the internal window to use deprecated openGL */
		/* We're using deprecated openGL in this example to keep it short. */
		/* This is needed for Mac users. Not needed for windows/Linux users. */
		/* Mac doesn't let you mix old/new openGL code together */

		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create a simple pane
		BorderPane root = new BorderPane();
		root.setBackgroundLegacy(null);
		
		// Top part of borderpane
		{
			// Create Menu Bar
			MenuBar bar = new MenuBar();
			root.setTop(bar);
			
			// Create File Menu
			Menu file = new Menu("File");
			file.getItems().add(new MenuItem("New"));
			file.getItems().add(new MenuItem("Open"));
			file.getItems().add(new MenuItem("Save"));
			bar.getItems().add(file);
			
			// Create Edit Menu
			Menu edit = new Menu("Edit");
			edit.getItems().add(new MenuItem("Undo"));
			edit.getItems().add(new MenuItem("Redo"));
			bar.getItems().add(edit);
		}

		// Add some text
		Label label = new Label("Hello World!");
		label.setTextFill(Color.WHITE);
		root.setCenter(label);

		// Set the scene
		window.setScene(new Scene(root, WIDTH, HEIGHT));
		window.show();

		// Start the gears application
		window.setRenderingCallback(new GearsApplication(null));
	}
	
	static class GearsApplication implements Renderer {
		static float[] pos = {5.0f, 5.0f, 10.0f, 0.0f};
		static float[] red = {0.8f, 0.1f, 0.0f, 1.0f};
		static float[] green = {0.0f, 0.8f, 0.2f, 1.0f};
		static float[] blue = {0.2f, 0.2f, 1.0f, 1.0f};
		static float view_rotx = 20.0f, view_roty = 30.0f, view_rotz = 0.0f;
		static int gear1, gear2, gear3;
		static float angle = 0.0f;
		
		private Node calledFrom;

		public GearsApplication(Node calledFrom) {
			this.calledFrom = calledFrom;
			
			glLightfv(GL_LIGHT0, GL_POSITION, pos);
			glEnable(GL_LIGHTING);
			glEnable(GL_LIGHT0);

			/* make the gears */
			gear1 = glGenLists(1);
			glNewList(gear1, GL_COMPILE);
			glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, red);
			gear(1.0f, 4.0f, 1.0f, 20, 0.7f);
			glEndList();

			gear2 = glGenLists(1);
			glNewList(gear2, GL_COMPILE);
			glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, green);
			gear(0.5f, 2.0f, 2.0f, 10, 0.7f);
			glEndList();

			gear3 = glGenLists(1);
			glNewList(gear3, GL_COMPILE);
			glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, blue);
			gear(1.3f, 2.0f, 0.5f, 10, 0.7f);
			glEndList();

			glEnable(GL_NORMALIZE);
		}

		static void gear(float inner_radius, float outer_radius, float width, int teeth, float tooth_depth) {
			int i;
			float r0, r1, r2;
			float angle, da;
			float u, v, len;

			r0 = inner_radius;
			r1 = outer_radius - tooth_depth / 2.0f;
			r2 = outer_radius + tooth_depth / 2.0f;

			da = (float) (2.0f * Math.PI / teeth / 4.0f);

			glShadeModel(GL_FLAT);

			glNormal3f(0.0f, 0.0f, 1.0f);

			/* draw front face */
			glBegin(GL_QUAD_STRIP);
			for (i = 0; i <= teeth; i++) {
				angle = (float) (i * 2.0 * Math.PI / teeth);
				glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), width * 0.5f);
				glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), width * 0.5f);
				glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), width * 0.5f);
				glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), width * 0.5f);
			}
			glEnd();

			/* draw front sides of teeth */
			glBegin(GL_QUADS);
			da = (float) (2.0 * Math.PI / teeth / 4.0);
			for (i = 0; i < teeth; i++) {
				angle = (float) (i * 2.0 * Math.PI / teeth);

				glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), width * 0.5f);
				glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), width * 0.5f);
				glVertex3f(r2 * (float)Math.cos(angle + 2 * da), r2 * (float)Math.sin(angle + 2 * da), width * 0.5f);
				glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), width * 0.5f);
			}
			glEnd();

			glNormal3f(0.0f, 0.0f, -1.0f);

			/* draw back face */
			glBegin(GL_QUAD_STRIP);
			for (i = 0; i <= teeth; i++) {
				angle = (float) (i * 2.0 * Math.PI / teeth);
				glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), -width * 0.5f);
				glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), -width * 0.5f);
				glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), -width * 0.5f);
				glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), -width * 0.5f);
			}
			glEnd();

			/* draw back sides of teeth */
			glBegin(GL_QUADS);
			da = (float) (2.0 * Math.PI / teeth / 4.0);
			for (i = 0; i < teeth; i++) {
				angle = (float) (i * 2.0 * Math.PI / teeth);

				glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), -width * 0.5f);
				glVertex3f(r2 * (float)Math.cos(angle + 2 * da), r2 * (float)Math.sin(angle + 2 * da), -width * 0.5f);
				glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), -width * 0.5f);
				glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), -width * 0.5f);
			}
			glEnd();

			/* draw outward faces of teeth */
			glBegin(GL_QUAD_STRIP);
			for (i = 0; i < teeth; i++) {
				angle = (float) (i * 2.0 * Math.PI / teeth);

				glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), width * 0.5f);
				glVertex3f(r1 * (float)Math.cos(angle), r1 * (float)Math.sin(angle), -width * 0.5f);
				u = r2 * (float)Math.cos(angle + da) - r1 * (float)Math.cos(angle);
				v = r2 * (float)Math.sin(angle + da) - r1 * (float)Math.sin(angle);
				len = (float) Math.sqrt(u * u + v * v);
				u /= len;
				v /= len;
				glNormal3f(v, -u, 0.0f);
				glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), width * 0.5f);
				glVertex3f(r2 * (float)Math.cos(angle + da), r2 * (float)Math.sin(angle + da), -width * 0.5f);
				glNormal3f((float)Math.cos(angle), (float)Math.sin(angle), 0.0f);
				glVertex3f(r2 * (float)Math.cos(angle + 2 * da), r2 * (float)Math.sin(angle + 2 * da), width * 0.5f);
				glVertex3f(r2 * (float)Math.cos(angle + 2 * da), r2 * (float)Math.sin(angle + 2 * da), -width * 0.5f);
				u = r1 * (float)Math.cos(angle + 3 * da) - r2 * (float)Math.cos(angle + 2 * da);
				v = r1 * (float)Math.sin(angle + 3 * da) - r2 * (float)Math.sin(angle + 2 * da);
				glNormal3f(v, -u, 0.0f);
				glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), width * 0.5f);
				glVertex3f(r1 * (float)Math.cos(angle + 3 * da), r1 * (float)Math.sin(angle + 3 * da), -width * 0.5f);
				glNormal3f((float)Math.cos(angle), (float)Math.sin(angle), 0.0f);
			}

			glVertex3f(r1 * (float)Math.cos(0), r1 * (float)Math.sin(0), width * 0.5f);
			glVertex3f(r1 * (float)Math.cos(0), r1 * (float)Math.sin(0), -width * 0.5f);

			glEnd();

			glShadeModel(GL_SMOOTH);

			/* draw inside radius cylinder */
			glBegin(GL_QUAD_STRIP);
			for (i = 0; i <= teeth; i++) {
				angle = (float) (i * 2.0 * Math.PI / teeth);

				glNormal3f((float)-Math.cos(angle), (float)-Math.sin(angle), 0.0f);
				glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), -width * 0.5f);
				glVertex3f(r0 * (float)Math.cos(angle), r0 * (float)Math.sin(angle), width * 0.5f);
			}
			glEnd();
		}

		@Override
		public void render(Context context) {
			angle += 1.0e-1f;
			
			int w = (int)context.getWidth();
			int h = (int)context.getHeight();
			if ( calledFrom != null ) {
				w = (int) calledFrom.getWidth();
				h = (int) calledFrom.getHeight();
			}
			float aspect = (float)h/(float)w;

			// Reset opengl flags
			glCullFace(GL_BACK);
			glEnable(GL_CULL_FACE);
			glEnable(GL_DEPTH_TEST);

			// Setup camera
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glFrustum(-1.0, 1.0, -aspect, aspect, 5.0, 60.0);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			glTranslatef(0.0f, 0.0f, -40.0f);

			glPushMatrix();
			glRotatef(view_rotx, 1.0f, 0.0f, 0.0f);
			glRotatef(view_roty, 0.0f, 1.0f, 0.0f);
			glRotatef(view_rotz, 0.0f, 0.0f, 1.0f);

			glPushMatrix();
			glTranslatef(-3.0f, -2.0f, 0.0f);
			glRotatef(angle, 0.0f, 0.0f, 1.0f);
			glCallList(gear1);
			glPopMatrix();

			glPushMatrix();
			glTranslatef(3.1f, -2.0f, 0.0f);
			glRotatef(-2.0f * angle - 9.0f, 0.0f, 0.0f, 1.0f);
			glCallList(gear2);
			glPopMatrix();

			glPushMatrix();
			glTranslatef(-3.1f, 4.2f, 0.0f);
			glRotatef(-2.0f * angle - 25.0f, 0.0f, 0.0f, 1.0f);
			glCallList(gear3);
			glPopMatrix();

			glPopMatrix();
		}
	}
}