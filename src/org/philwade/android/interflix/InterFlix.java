package org.philwade.android.interflix;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import org.philwade.android.interflix.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class InterFlix extends Activity {
	public static final String PREFS_FILE = "InterflixPrefs";
	public boolean authDone = false;
	public NetflixDataRetriever dataRetriever = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, 0);
		dataRetriever = new NetflixDataRetriever(prefs);
        
    	Uri uri = this.getIntent().getData();
		if(uri != null && uri.toString().startsWith(NetflixDataRetriever.APP_URI)) {
			handleAuthReturn(uri, dataRetriever);
			authDone = true;
		  	Toast.makeText(getApplicationContext(), "Thanks for registering", 1000).show();
		}
        
        String user_id = prefs.getString("user_id", null);
        if(user_id != null)
        {
			authDone = true;
        }
        
        if(!authDone)
        {
		  	Toast.makeText(getApplicationContext(), "Welcome to InterFlix. Since you're new, we just need to set you up.", 5000).show();
        	try {
				Uri authUri = dataRetriever.requestAuthUri();
				Intent intent = new Intent(Intent.ACTION_VIEW, authUri);
				startActivityForResult(intent, 0);
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        setContentView(R.layout.main);
    	TextView que = (TextView) findViewById(R.id.que_link_view);
    	que.setOnClickListener(queListener());
    	TextView search = (TextView) findViewById(R.id.search_link_view);
    	search.setOnClickListener(searchListener());
    	TextView clear = (TextView) findViewById(R.id.clear_link_view);
    	clear.setOnClickListener(clearListener());
    	TextView title = (TextView) findViewById(R.id.title_link_view);
    	title.setOnClickListener(new OnClickListener(){
    		public void onClick(View v)
    		{
    			Intent titleIntent = new Intent();
    			titleIntent.setClassName("org.philwade.android.interflix", "org.philwade.android.interflix.TitleActivity");
    			startActivity(titleIntent);
    		}
    	});
    }
    
    public OnClickListener queListener()
    {
    	return new OnClickListener()
    	{
    		public void onClick(View v)
    		{
    			Intent queIntent = new Intent();
    			queIntent.setClassName("org.philwade.android.interflix", "org.philwade.android.interflix.QueList");
    			startActivity(queIntent);
    		}
    	};
    }
    
    public OnClickListener searchListener()
    {
    	return new OnClickListener()
    	{
    		public void onClick(View v)
    		{
    			Intent queIntent = new Intent();
    			queIntent.setClassName("org.philwade.android.interflix", "org.philwade.android.interflix.MovieSearch");
    			startActivity(queIntent);
    		}
    	};
    }
    
    //for debuggin
    public OnClickListener clearListener()
    {
    	return new OnClickListener()
    	{
    		public void onClick(View v)
    		{
    			dataRetriever.cleanPreferences();
    			Toast.makeText(getApplicationContext(), "Preferences cleared", 1000).show();
    		}
    	};
    }
    
    public void handleAuthReturn(Uri uri, NetflixDataRetriever dataRetriever)
    {
        	try {
			  	String access_token = uri.getQueryParameter("oauth_token");
			  	dataRetriever.setupAccessTokens(access_token);
			} catch (OAuthMessageSignerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
		
