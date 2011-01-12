package org.philwade.android.interflix;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.content.Context;

public class ErrorReceiver extends Handler {
	public static final int BROKEN_NETWORK = -1;
	public static final int AUTH_FAIL = -2;
	public static final int PARSE_FAIL = -3;
	public static final int DEFAULT = -999;
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
				break;
			case AUTH_FAIL:	
				userMessage = mContext.getString(R.string.auth_error);
				break;
			case PARSE_FAIL:
				userMessage = mContext.getString(R.string.parse_error);
				break;
			case DEFAULT:
			default:
				userMessage = mContext.getString(R.string.default_error);
		}
		
		Toast.makeText(mContext, userMessage, Toast.LENGTH_LONG).show();
	}

}
