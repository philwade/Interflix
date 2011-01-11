package org.philwade.android.interflix;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.content.Context;

public class ErrorReceiver extends Handler {
	public static final int BROKEN_NETWORK = -1;
	private Context mContext;
	
	public ErrorReceiver(Context c)
	{
		super();
		mContext = c;
		
	}
	@Override
	public void handleMessage(Message msg)
	{
		String userMessage;
		
		switch(msg.what)
		{
			case BROKEN_NETWORK:
				userMessage = mContext.getString(R.string.network_error);
			default:
				userMessage = mContext.getString(R.string.default_error);
		}
		
		Toast.makeText(mContext, userMessage, Toast.LENGTH_LONG).show();
	}

}
