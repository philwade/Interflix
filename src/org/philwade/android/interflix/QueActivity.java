package org.philwade.android.interflix;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

public abstract class QueActivity extends ListActivity {
	protected static final int PROGRESS_DIALOG = 0;	
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
		}
		return null;
	}
	
	abstract void getQueContents();
	
	final OnClickListener moreListener = new OnClickListener()
	{
		public void onClick(View v) {
			getQueContents();
		}
	};
}
