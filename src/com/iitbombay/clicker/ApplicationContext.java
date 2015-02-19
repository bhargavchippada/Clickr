package com.iitbombay.clicker;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import support.Utils;
import android.app.Application;

public class ApplicationContext extends Application{
	public static String classname = "ApplicationContext";
	
	private int NetworkConnectionTimeout_ms = 5000;
	private DefaultHttpClient httpClient;
	private CookieStore cookieStore;
	
	@Override
	public void onCreate() {
	}
	
	
	// link: http://foo.jasonhudgins.com/2009/08/http-connection-reuse-in-android.html
	public synchronized DefaultHttpClient getThreadSafeClient() {
		if(httpClient!=null) return httpClient;
		Utils.logv(classname, "New httpClient is created");
		
		// set params for connection...
		HttpParams params = new BasicHttpParams();
	    HttpConnectionParams.setStaleCheckingEnabled(params, false);
	    HttpConnectionParams.setConnectionTimeout(params, NetworkConnectionTimeout_ms);
	    HttpConnectionParams.setSoTimeout(params, NetworkConnectionTimeout_ms);
	    
	    //creating cookie
	    if(cookieStore == null)  cookieStore = new BasicCookieStore();
	    
	    httpClient = new DefaultHttpClient(params);
	    ClientConnectionManager mgr = httpClient.getConnectionManager();
	    httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params,
	    		mgr.getSchemeRegistry()), params);
	    httpClient.setCookieStore(cookieStore);
        return httpClient;
    }
	
}