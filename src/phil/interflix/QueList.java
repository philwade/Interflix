package phil.interflix;

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
        NetflixQueRetriever queRetriever = new NetflixQueRetriever();
        try {
			names = queRetriever.getQue();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthException e) {
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
