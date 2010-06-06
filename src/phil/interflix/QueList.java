package phil.interflix;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class QueList extends TabActivity {
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.que);
    	
    	
    	TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab
 
        intent = new Intent().setClass(this, InstantQueActivity.class);
        spec = tabHost.newTabSpec("instant").setIndicator("Instant").setContent(intent);
        tabHost.addTab(spec);
        

        intent = new Intent().setClass(this, DiscQueActivity.class);
        spec = tabHost.newTabSpec("discs").setIndicator("Discs").setContent(intent);
        tabHost.addTab(spec);
        tabHost.setCurrentTab(0);
    }
    
    public static final String[] FAILURE = { "Authorization failure"};
    
    
}
