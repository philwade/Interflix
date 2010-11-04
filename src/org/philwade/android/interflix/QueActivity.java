package org.philwade.android.interflix;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public abstract class QueActivity extends ListActivity {
	protected static final int PROGRESS_DIALOG = 0;	
	protected static final int NUMBER_PICK_DIALOG = 2;	
	protected static final int LOAD_MORE_ID = 1;	
	protected int QUE_OFFSET = 0;
	protected boolean firstLoad = true;
	public Button moreButton;
	public NetflixTitle[] queItems;
	public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
		   moreButton = new Button(this);
		   moreButton.setText(R.string.load_more_text);
		   moreButton.setId(LOAD_MORE_ID);
		   moreButton.setOnClickListener(moreListener);
		   registerForContextMenu(getListView());
	}
	
	final Handler queHandler = new Handler();
	
	final Runnable updateQue = new Runnable()
	{
		@SuppressWarnings("unchecked")
		public void run() 
		{
			removeDialog(PROGRESS_DIALOG);
			if(queItems != null)
			{
				if(queItems.length < NetflixDataRetriever.OFFSET_INCREMENT)
				{
					//when we get less than we asked for, turn off button
					moreButton.setEnabled(false);
					//TODO: find a better way to know when we hit bottom
				}
				ArrayAdapter<NetflixTitle> la = (ArrayAdapter<NetflixTitle>) getListAdapter();
				if(firstLoad == true)
				{
					la.clear();
					firstLoad = false;
				}
				
				for(NetflixTitle item : queItems)
				{
					la.add(item);
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
                return dialog;
			case NUMBER_PICK_DIALOG:
				Dialog pickerDialog = new Dialog(this);
				pickerDialog.setTitle("Choose New Position");
				pickerDialog.setContentView(R.layout.number_picker_pref);
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
	  NetflixQueRetriever queRetriever = new NetflixQueRetriever(getSharedPreferences(InterFlix.PREFS_FILE, 0));
	  switch (item.getItemId()) {
	  case R.id.item_remove:
		  removeFromQue(clickedTitle, queRetriever);
		  resetList();
		  getQueContents();
	    return true;
	  case R.id.item_move:
		  //TODO: write up the position change code
		  showDialog(NUMBER_PICK_DIALOG);
	    return true;
	  default:
	    return super.onContextItemSelected(item);
	  }
	}
	
	abstract void getQueContents();
	public void removeFromQue(NetflixTitle title, NetflixQueRetriever retriever)
	{
		title.removeFromQue(retriever);
	}
	
	public void resetList()
	{
		//set these values so list gets cleared when we reload
		firstLoad = true; 
		QUE_OFFSET = 0;
		moreButton.setEnabled(true);
	}
	
	final OnClickListener moreListener = new OnClickListener()
	{
		public void onClick(View v) {
			getQueContents();
		}
	};
}
