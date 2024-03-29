package org.philwade.android.interflix;

import java.util.ArrayList;


import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public abstract class QueActivity extends ListActivity {
	protected static final int PROGRESS_DIALOG = 0;	
	protected static final int NUMBER_PICK_DIALOG = 2;	
	protected static final int LOAD_MORE_ID = 1;	
	protected int offsetIncrement;
	protected int QUE_OFFSET = 0;
	protected int queLength = 0;
	protected int displayCount = 0; //number of titles we're displaying
	protected boolean firstLoad = true;
	protected boolean appendNew = true;
	public Dialog pickerDialog;
	public Button moreButton;
	public NetflixTitle[] queItems;
	public NetflixTitle currentClickedTitle;
	public ErrorReceiver mErrorReceiver;
	public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
		   
		   offsetIncrement = Integer.parseInt(getSharedPreferences(InterFlix.PREFS_FILE, 0).getString("que step", "25"));
		   moreButton = new Button(this);
		   moreButton.setText(R.string.load_more_text);
		   moreButton.setId(LOAD_MORE_ID);
		   moreButton.setOnClickListener(moreListener);
		   registerForContextMenu(getListView());
	}
	
	final Handler queHandler = new Handler();
	
	final Runnable updateQue = new Runnable()
	{
		public void run() 
		{
			removeDialog(PROGRESS_DIALOG);
			if(queItems != null)
			{
				TitleAdapter la = (TitleAdapter) getListAdapter();
				if(firstLoad == true || appendNew == false)
				{
					la.clear();
					firstLoad = false;
				}
				
				
				for(NetflixTitle item : queItems)
				{
					displayCount++;
					if(displayCount <= queLength)
					{
						la.add(item);
					}
					
					if(displayCount == queLength)
					{
						moreButton.setEnabled(false);
					}
				}
				la.notifyDataSetChanged();
			}
		}
	};
	
	final OnItemClickListener clickListener = new OnItemClickListener()
	{
		@SuppressWarnings("unchecked")
		public void onItemClick(AdapterView<?> adapterView, View view, int position,
				long id) {
			ArrayAdapter<NetflixTitle> adapter = (ArrayAdapter<NetflixTitle>) getListAdapter();
			NetflixTitle clickedTitle = adapter.getItem(position);
			Intent viewIntent = new Intent();
			viewIntent.setClassName("org.philwade.android.interflix", "org.philwade.android.interflix.TitleActivity");
			viewIntent.putExtra("idUrl", clickedTitle.idUrl);
			startActivity(viewIntent);
		}
		
	};
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch(id)
		{
			case PROGRESS_DIALOG:
				ProgressDialog dialog = new ProgressDialog(this);
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                dialog.setMessage("Loading...");
                return dialog;
			case NUMBER_PICK_DIALOG:
				pickerDialog = new Dialog(this);
				pickerDialog.setTitle("Choose New Position");
				pickerDialog.setContentView(R.layout.number_picker_pref);
				Button okButton = (Button) pickerDialog.findViewById(R.id.num_pick_ok);
				okButton.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						NumberPicker picker = (NumberPicker) pickerDialog.findViewById(R.id.pref_num_picker);
						changeQuePosition(currentClickedTitle, picker.getCurrent());
						pickerDialog.dismiss();
						resetList();
						getQueContents();
					}
				});
				Button cancelButton = (Button) pickerDialog.findViewById(R.id.num_pick_cancel);
				cancelButton.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						removeDialog(NUMBER_PICK_DIALOG);
					}
				});
				
				return pickerDialog;
		}
		return null;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.title_context, menu);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  ArrayAdapter<NetflixTitle> adapter = (ArrayAdapter<NetflixTitle>) getListAdapter();
	  NetflixTitle clickedTitle = adapter.getItem(info.position);
	  currentClickedTitle = clickedTitle;
	  NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
	  switch (item.getItemId()) {
	  case R.id.item_remove:
		  removeFromQue(clickedTitle, queRetriever);
		  resetList();
		  getQueContents();
	    return true;
	  case R.id.item_move:
		  showDialog(NUMBER_PICK_DIALOG);
	    return true;
	  default:
	    return super.onContextItemSelected(item);
	  }
	}
	
	abstract void getQueContents();
	abstract void changeQuePosition(NetflixTitle title, int position);

	public void removeFromQue(NetflixTitle title, NetflixQueRetriever retriever)
	{
		title.removeFromQue(retriever, mErrorReceiver);
	}
	
	public void resetList()
	{
		//set these values so list gets cleared when we reload
		firstLoad = true; 
		QUE_OFFSET = 0;
		moreButton.setEnabled(true);
		queLength = 0;
	}
	
	final OnClickListener moreListener = new OnClickListener()
	{
		public void onClick(View v) {
			getQueContents();
		}
	};
	
	   protected class TitleAdapter extends ArrayAdapter<NetflixTitle> {
	    	@SuppressWarnings("unused") //this gets used in child classes
			private ArrayList<NetflixTitle> items = null;
	    	public TitleAdapter(Context context, int textViewResourceId)
	    	{
	    		super(context, textViewResourceId);	
	    	}
	    	
	    	@Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	    		View v = convertView;
	    		if(v == null)
	    		{
	    			LayoutInflater inflator = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			v = inflator.inflate(R.layout.title_row, null);
	    		}
	    		
	    		NetflixTitle title = getItem(position);
	    		if(title != null)
	    		{
	    			NetflixDataRetriever retriever = new NetflixDataRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
	    			TextView tv = (TextView) v.findViewById(R.id.list_title_title);
	    			tv.setText(title.title);
	    			TextView ytv = (TextView) v.findViewById(R.id.list_title_year);
	    			ytv.setText(title.year);
	    			ImageView iv = (ImageView) v.findViewById(R.id.list_title_cover);
	    			iv.setImageResource(R.drawable.loading);
	    			retriever.fetchImageOnThread(title.coverArt, iv);
	    			RatingBar rating = (RatingBar) v.findViewById(R.id.queListRatingbar);
	    			rating.setRating(title.rating);
	    			Log.d("QueActivity", "" + title.rating);
	    			Log.d("QueActivity", "" + rating.getRating());
	    		}
	    		return v;
	    	}
	    };
	    
}
