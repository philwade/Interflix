package org.philwade.android.interflix;


import org.philwade.android.interflix.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class QueList extends TabActivity {
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.que);
    	
    	
    	Resources res = getResources();
    	TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab
 
        intent = new Intent().setClass(this, InstantQueActivity.class);
        spec = tabHost.newTabSpec("instant").setIndicator("Instant", res.getDrawable(R.drawable.monitor)).setContent(intent);
        tabHost.addTab(spec);
        

        intent = new Intent().setClass(this, DiscQueActivity.class);
        spec = tabHost.newTabSpec("discs").setIndicator("Discs", res.getDrawable(R.drawable.disc)).setContent(intent);
        tabHost.addTab(spec);
        tabHost.setCurrentTab(0);
    }
    
    public static final String[] FAILURE = { "Authorization failure"};
    
    
}
