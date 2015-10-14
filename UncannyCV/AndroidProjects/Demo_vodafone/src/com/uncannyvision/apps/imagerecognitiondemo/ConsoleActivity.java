package com.uncannyvision.apps.imagerecognitiondemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.uncannyvision.apps.demo_vodafone_similarity_and_recognition.R;
import com.uncannyvision.uncannycv.IUncannyCVCallbackReceiver;
import com.uncannyvision.uncannycv.UncannyCVCaller;

public class ConsoleActivity extends SherlockActivity implements IUncannyCVCallbackReceiver {
    private TextView tv = null;
	private ScrollView scroller = null;
	private UncannyCVCaller uncannyCVCaller = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console_layout);
        scroller = (ScrollView)findViewById(R.id.logScroller);
        tv = (TextView) findViewById(R.id.logView);
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }    

	@Override
	protected void onResume() {
		super.onResume();
		
		uncannyCVCaller  = UncannyCVCaller.getInstance(this, MainActivity.LOG_TAG, this);
		if(null != uncannyCVCaller) {
			uncannyCVCaller.startRunAllAlgosAsynch();
		}
		
	}
    
    @Override
	protected void onPause() {
		super.onPause();

		if(null != uncannyCVCaller) {
			uncannyCVCaller.cancelAsynchIfAny();
			uncannyCVCaller.deinitialize();
		}
		
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	startActivityClearTop(MainActivity.class);
                return true;
            case R.id.aboutUs:
        		startActivity(
        				DialogActvity.constructIntent(getApplicationContext(), 
                		getString(R.string.aboutUsLabel),
                		getString(R.string.aboutUsText)));
                return true;
            /*
            case R.id.settingsAction:
            	startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            */
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	private void startActivityClearTop(Class<?> cls) {
		Intent intent = new Intent(getApplicationContext(),	cls);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onLog(String msg) {
		//Log.i(LOG_TAG, msg);
		tv.append(//String.format("%d:", counter++) + 
				msg);
    	scroller.post(new Runnable() { 
    	    public void run() { 
    	        scroller.fullScroll(ScrollView.FOCUS_DOWN); 
    	    } 
    	});
	}

	@Override
	public void onTimeGap(long timeGapInMs) {
		// TODO Auto-generated method stub
		
	}
}
