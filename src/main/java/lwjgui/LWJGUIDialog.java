package lwjgui;

import java.io.File;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class LWJGUIDialog {
	
	public enum DialogType {
		OK("ok"),
		OK_CANCEL("okcancel"),
		YES_NO("yesno");
		
		String key;
		
		private DialogType(String key) {
			this.key = key;
		}
	}

	public enum DialogIcon {
		INFORMATION("info"),
		WARNING("warning"),
		ERROR("error"),
		QUESTION("question");
		
		String key;
		
		private DialogIcon(String key) {
			this.key = key;
		}
	};
	
	/**
	 * Shows a message dialog with the given window title and message.
	 * 
	 * @param title
	 * @param message 
	 */
	public static void showMessageDialog(String title, String message, DialogIcon icon) {
		TinyFileDialogs.tinyfd_messageBox(title, message, DialogType.OK.key, icon.key, true);
	}
	
	/**
	 * Show a confirm dialog with the given settings.
	 * 
	 * @param title - window title
	 * @param message - window message
	 * @param type - confirm type (buttons used)
	 * @param icon - window icon
	 * @param defaultButtonIsOK - if true, the default button highlighted will be the "yes" button.
	 * 
	 * @return true if "yes" or an equivalent is selected.
	 */
	public static boolean showConfirmDialog(String title, String message, DialogType type, DialogIcon icon, boolean defaultButtonIsOK) {
		return TinyFileDialogs.tinyfd_messageBox(title, message, type.key, icon.key, defaultButtonIsOK);
	}
	
	/**
	 * Shows a dialog for selecting a folder. 
	 * 
	 * @param title - window title
	 * @param defaultPath - default filepath to start from
	 * 
 	 * @return the selected folder path in a File object
	 */
	public static File showOpenFolderDialog(String title, File defaultPath){
        String result = TinyFileDialogs.tinyfd_selectFolderDialog(title, defaultPath.getAbsolutePath());
		return result != null ? new File(result) : null; 
	}
	
	/**
	 * Opens a file open dialog.
	 * 
	 * @param title window title
	 * @param defaultPath default file path
	 * @param filterDescription description of the accepted file extension(s)
	 * @param acceptedFileExtension the first accepted file extension (example: "txt", use * for all)
	 * @param additionalAcceptedFileExtensions any additional accepted file extensions
	 * 
	 * @return the selected file
	 */
	public static File showOpenFileDialog(String title, File defaultPath, String filterDescription, String acceptedFileExtension, String... additionalAcceptedFileExtensions){

		MemoryStack stack = MemoryStack.stackPush();

		PointerBuffer filters = stack.mallocPointer(1 + additionalAcceptedFileExtensions.length);

        filters.put(stack.UTF8("*." + acceptedFileExtension));
        for(int i = 0; i < additionalAcceptedFileExtensions.length; i++){
			filters.put(stack.UTF8("*." + additionalAcceptedFileExtensions[i]));
        }

        filters.flip();

        defaultPath = defaultPath.getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if(defaultPath.isDirectory() && !defaultString.endsWith(File.separator)){
        	defaultString += File.separator;
        }
        
        String result = TinyFileDialogs.tinyfd_openFileDialog(title, defaultString, filters, filterDescription, false);

		stack.pop();

		return result != null ? new File(result) : null; 
	}

	/**
	 * Opens a file save dialog.
	 * 
	 * @param title window title
	 * @param defaultPath default file path
	 * @param filterDescription description of the accepted file extension(s)
	 * @param fileExtension the file extension (example: "txt")
	 * @param forceExtension the user can select any file regardless of extension. If this is set to true, then the given extension will be automatically added if the extension is wrong.
	 * 
	 * @return the selected file
	 */
	public static File showSaveFileDialog(String title, File defaultPath, String filterDescription, String fileExtension, boolean forceExtension){

		MemoryStack stack = MemoryStack.stackPush();

		PointerBuffer filters = stack.mallocPointer(1);

        filters.put(stack.UTF8("*." + fileExtension)).flip();
        
        defaultPath = defaultPath.getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if(defaultPath.isDirectory() && !defaultString.endsWith(File.separator)){
        	defaultString += File.separator;
        }

        //System.out.println(defaultString + " : exists: " + new File(defaultString).exists());
        
        String result = TinyFileDialogs.tinyfd_saveFileDialog(title, defaultString, filters, filterDescription);

        stack.pop();

        if(result == null){
        	return null;
        }
        
        if(forceExtension && !result.endsWith("." + fileExtension)){
        	result += "." + fileExtension;
        }

        return new File(result);
	}
}