package phil.interflix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class InterFlix extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}