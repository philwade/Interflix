package org.philwade.android.interflix;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import org.philwade.android.interflix.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class InterFlix extends Activity {
	public static final String PREFS_FILE = "InterflixPrefs";
	protected static final int SETUP_DIALOG = 0;	
	public boolean authDone = false;
	private int viewH;
	private int viewW;
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
		  	Toast.makeText(getApplicationContext(), "Thanks for authorizing", 2000).show();
		}
        
        String user_id = prefs.getString("user_id", null);
        if(user_id != null)
        {
			authDone = true;
        }
        
        if(!authDone)
        {
        	showDialog(SETUP_DIALOG);
        	
        }
        
        setContentView(R.layout.main);
        
        
    	ImageButton que = (ImageButton) findViewById(R.id.que_link_view);
    	que.setOnClickListener(queListener());
    	ImageButton search = (ImageButton) findViewById(R.id.search_link_view);
    	search.setOnClickListener(searchListener());
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
    	if(hasFocus)
    	{
        	LinearLayout mLayout = (LinearLayout) findViewById(R.id.home_root);
        	viewH = mLayout.getHeight();
        	viewW = mLayout.getWidth();
        	int wPadding = (int) ((int) viewW * 0.30);
        	int hPadding = (int) ((int) viewH * 0.25);
        	mLayout.setPadding(wPadding, hPadding, 0, 0);
    	}
    }
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	switch(id)
    	{
    		case SETUP_DIALOG:
    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setMessage("Welcome to Interflicks. Since this is your first time using the program, we just need to setup your account.");
    			builder.setCancelable(false);
    			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
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
				});
    			builder.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						InterFlix.this.finish();
					}
				});
    			return builder.create();
    	}
    	return null;
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
    
    //TODO: move this to data retriever probably
    public void handleAuthReturn(Uri uri, NetflixDataRetriever dataRetriever)
    {
        	try {
			  	String access_token = uri.getQueryParameter("oauth_verifier");
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


		
