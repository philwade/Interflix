package phil.interflix;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
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
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, 0);
        
    	Uri uri = this.getIntent().getData();
		if(uri != null && uri.toString().startsWith(NetflixDataRetriever.APP_URI)) {
			handleAuthReturn(uri, prefs);
			authDone = true;
		}
        
        String user_id = prefs.getString("user_id", null);
        if(user_id != null)
        {
			authDone = true;
        }
        
        if(!authDone)
        {
        	try {
				String requestAuthUrl = NetflixDataRetriever.requestAuthUrl(prefs);
				SharedPreferences.Editor editor = prefs.edit();
				Uri authUri = Uri.parse(requestAuthUrl);
				//save some stuff here cause we need it later to get our access tokens...
				//OAuth is insane, btw
				String requestToken = authUri.getQueryParameter("oauth_token");
				editor.putString("request_key", requestToken);
				editor.commit();
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
    }
    
    public OnClickListener queListener()
    {
    	return new OnClickListener()
    	{
    		public void onClick(View v)
    		{
    			Intent queIntent = new Intent();
    			queIntent.setClassName("phil.interflix", "phil.interflix.QueList");
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
    			queIntent.setClassName("phil.interflix", "phil.interflix.MovieSearch");
    			startActivity(queIntent);
    		}
    	};
    }
    
    public void handleAuthReturn(Uri uri, SharedPreferences prefs)
    {
        	try {
			  	String access_token = uri.getQueryParameter("oauth_token");
			  	NetflixDataRetriever.setupAccessTokens(access_token, prefs);
			  	Toast.makeText(getApplicationContext(), "Thanks for registering", 200);
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
		
