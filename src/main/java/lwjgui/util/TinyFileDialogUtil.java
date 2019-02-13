package lwjgui.util;

import java.io.File;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class TinyFileDialogUtil {
	

	/**
	 * Shows a message dialog.
	 * @param title
	 * @param message 
	 */
	public static void showMessage(String title, String message) {
		TinyFileDialogs.tinyfd_messageBox(title, message, "", "", true);
	}
	
	/**
	 * Opens a file open dialog.
	 * 
	 * @param title window title
	 * @param defaultPath default file path
	 * @param filterDescription description of the accepted file extension(s)
	 * @param acceptedFileExtension the first accepted file extension (use * for all)
	 * @param additionalAcceptedFileExtensions any additional accepted file extensions
	 * 
	 * @return the selected file
	 */
	public static File openFileDialog(String title, File defaultPath, String filterDescription, String acceptedFileExtension, String... additionalAcceptedFileExtensions){
		
		PointerBuffer filters = MemoryStack.stackMallocPointer(1 + additionalAcceptedFileExtensions.length);

        filters.put(MemoryStack.stackUTF8("*." + acceptedFileExtension));
        for(int i = 0; i < additionalAcceptedFileExtensions.length; i++){
        	filters.put(MemoryStack.stackUTF8("*." + additionalAcceptedFileExtensions[i]));
        }

        filters.flip();

        defaultPath = defaultPath.getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if(defaultPath.isDirectory() && !defaultString.endsWith(File.separator)){
        	defaultString += File.separator;
        }
        
        //System.out.println(defaultString + " : exists: " + new File(defaultString).exists());
        
        String result = TinyFileDialogs.tinyfd_openFileDialog(title, defaultString, filters, filterDescription, false);
		
		return result != null ? new File(result) : null; 
	}

	/**
	 * Opens a file save dialog.
	 * 
	 * @param title window title
	 * @param defaultPath default file path
	 * @param filterDescription description of the accepted file extension(s)
	 * @param fileExtension the file extension
	 * @param forceExtension the user can select any file regardless of extension. If this is set to true, then the given extension will be automatically added if the extension is wrong.
	 * 
	 * @return the selected file
	 */
	public static File saveFileDialog(String title, File defaultPath, String filterDescription, String fileExtension, boolean forceExtension){
		
		PointerBuffer filters = MemoryStack.stackMallocPointer(1);

        filters.put(MemoryStack.stackUTF8("*." + fileExtension)).flip();
        
        defaultPath = defaultPath.getAbsoluteFile();
        String defaultString = defaultPath.getAbsolutePath();
        if(defaultPath.isDirectory() && !defaultString.endsWith(File.separator)){
        	defaultString += File.separator;
        }

        //System.out.println(defaultString + " : exists: " + new File(defaultString).exists());
        
        String result = TinyFileDialogs.tinyfd_saveFileDialog(title, defaultString, filters, filterDescription);
        
        if(result == null){
        	return null;
        }
        
        if(forceExtension && !result.endsWith("." + fileExtension)){
        	result += "." + fileExtension;
        }
        return new File(result);
	}
}