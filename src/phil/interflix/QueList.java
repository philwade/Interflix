package phil.interflix;

import java.io.IOException;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import android.app.ListActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

public class QueList extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] names = null;
        try {
        	NetflixSearchRetriever queRetriever = new NetflixSearchRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
			names = queRetriever.searchPeople("neil");
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(names != null)
		{
			setListAdapter(new ArrayAdapter<String>(this, R.layout.que, names));
		}
		else
		{
			setListAdapter(new ArrayAdapter<String>(this, R.layout.que, FAILURE));
		}
       
    }
    
private static final String[] FAILURE = { "Authorization failure"};
}
