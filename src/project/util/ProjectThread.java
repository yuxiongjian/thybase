/**
 * 
 */
package project.util;

/**
 * @author thomasy
 * 
 */
public class ProjectThread extends Thread {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#setUncaughtExceptionHandler(java.lang.Thread.
	 * UncaughtExceptionHandler)
	 */
	@Override
	public void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
		// TODO Auto-generated method stub
		// Thread.setDefaultUncaughtExceptionHandler(new
		// GlobalExceptionHandler());
		super.setUncaughtExceptionHandler(new GlobalExceptionHandler(null));
	}

}
