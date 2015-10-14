package support;

import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.View;


/**Class for extra helper functions
 * @author bhargav
 *
 */
public class Utils {

	/**To generate random unique integers
	 * 
	 */
	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

	/**Log the message along with exception printstack
	 * @param String classname
	 * @param String s
	 * @param Exception e
	 */
	public static void logv(String classname, String s, Exception e){
		if(e!=null) Log.v("Clicker", classname+": "+s, e);
		else{
			Log.v("Clicker", classname+": "+s);
		}
	}

	/**Log the message
	 * @param String classname
	 * @param String msg
	 */
	public static void logv(String classname, String msg){
		Log.v("Clicker",classname+" : "+msg);
	}

	/**Get the ip-address of the device
	 * @param context
	 * @return
	 */
	public static String getIpAddress(Context context){
		WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		int ipAddress = wifiInf.getIpAddress();
		String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
		return ip;
	}

	
	/**For lower api generate unique view id 
	 * @return int ID
	 */
	public static int lowerapiGenerateViewId() {
		for (;;) {
			final int result = sNextGeneratedId.get();
			// aapt-generated IDs have the high byte nonzero; clamp to the range under that.
			int newValue = result + 1;
			if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
			if (sNextGeneratedId.compareAndSet(result, newValue)) {
				return result;
			}
		}
	}

	/**For higher api generate unique view id
	 * @return int ID
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static int generateViewId() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {

			return Utils.generateViewId();

		} else {

			return View.generateViewId();

		}
	}
}