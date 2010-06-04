package phil.interflix;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.SharedPreferences;

public class NetflixQueRetriever extends NetflixDataRetriever {
	
	private HttpGet request = null;
	private DefaultHttpClient client = null;
	
	public NetflixQueRetriever(SharedPreferences prefs)
	{
		super(prefs);
		client = new DefaultHttpClient();
	}
	
	public String[] getQue()
	{
		String[] temp = {"test1", "test2"};
		return temp;
	}
}
