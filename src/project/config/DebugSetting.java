package project.config;



/**
 * Created by thomasy on 14-1-16.
 */
public class DebugSetting {
	
	

	public static enum DEBUG {
		RELEASE, INNERDEBUG, OUTERDEBUG, LOCALDEBUG, REALDEBUG
	};

	public static DEBUG debug= DEBUG.RELEASE;


	public static boolean _bCompleteStep;

	public static boolean isRelease() {
		return debug == DEBUG.RELEASE;
	}

	public static boolean isDebug() {
		return debug != DEBUG.RELEASE;
	}

	public static boolean isInnerDebug() {
		return debug == DEBUG.INNERDEBUG;
	}

	public static boolean isOuterDebug() {
		return debug == DEBUG.OUTERDEBUG;
	}

	public static boolean isLocalDebug() {
		return debug == DEBUG.LOCALDEBUG;
	}

	
}
