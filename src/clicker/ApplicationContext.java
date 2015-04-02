package clicker;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import support.Question;
import support.UserSession;
import support.Utils;
import android.app.Application;

public class ApplicationContext extends Application{
	public static String classname = "ApplicationContext";
	
	private static int NetworkConnectionTimeout_ms = 5000;
	private static DefaultHttpClient httpClient;
	private static UserSession usersession;
	private static Question question;	
	
	// link: http://foo.jasonhudgins.com/2009/08/http-connection-reuse-in-android.html
	public synchronized static DefaultHttpClient getThreadSafeClient() {
		if(httpClient!=null) return httpClient;
		Utils.logv(classname, "New httpClient is created");
		
		// set params for connection...
		HttpParams params = new BasicHttpParams();
	    HttpConnectionParams.setStaleCheckingEnabled(params, false);
	    HttpConnectionParams.setConnectionTimeout(params, NetworkConnectionTimeout_ms);
	    HttpConnectionParams.setSoTimeout(params, NetworkConnectionTimeout_ms);
	    //ConnManagerParams.setMaxTotalConnections(params, 5);
	    
	    httpClient = new DefaultHttpClient(params);
	    ClientConnectionManager mgr = httpClient.getConnectionManager();
	    httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
	    		mgr.getSchemeRegistry()), params);
        return httpClient;
    }
	
	public synchronized static UserSession getThreadSafeUserSession(){
		if(usersession==null) usersession = new UserSession();
		return usersession;
	}
	
	public synchronized static Question getThreadSafeQuestion(){
		if(question==null) question = new Question();
		return question;
	}
	
	public synchronized static void invalidateSession(){
		if(usersession!=null) {
			usersession.clear();
			Utils.logv(classname, "usersession wiped");
		}
		if(question!=null) {
			question.clear();
			Utils.logv(classname, "question wiped");
		}
	}
}