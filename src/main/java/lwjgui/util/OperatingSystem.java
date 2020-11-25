package lwjgui.util;

public enum OperatingSystem {
	WINDOWS,
	MAC,
	LINUX,
	ANDROID,
	IOS,
	OTHER;
	
	/**
	 * This function uses Java to detect the OS that this program is running on and will return the corresponding enum from this class.
	 */
	public static OperatingSystem detect() {
		String os = System.getProperty("os.name").toLowerCase();
		
		if ( os.indexOf("ios") >= 0 )
			return IOS;
		
		if ( os.indexOf("android") >= 0 )
			return ANDROID;
		
		if (os.indexOf("win") >= 0) {
			return WINDOWS;
		}
		
		if (os.indexOf("mac") >= 0) {
			return MAC;
		}
		
		if (os.indexOf("nix") >=0 || os.indexOf("nux") >=0){
			return LINUX;
		}
		
		return OTHER;
	}
	
	public static boolean isMobile() {
		return detect() == IOS || detect() == ANDROID;
	}
	
	public static boolean isDesktop() {
		return detect() == WINDOWS || detect() == MAC || detect() == LINUX;
	}
}
