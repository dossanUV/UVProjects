package com.uncannyvision.apps.imagerecognitiondemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.uncannyvision.apps.demo_vodafone_similarity_and_recognition.R;

public class DialogActvity extends SherlockActivity {
	static public final String KEY_TITLE = "TITLE"; 
	static public final String KEY_CONTENT = "CONTENT";
	private String title;
	private String content; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        
        title = getIntent().getExtras().getString(KEY_TITLE);
        content = getIntent().getExtras().getString(KEY_CONTENT);
        setTitle(title != null ? title: "");
		((TextView) findViewById(R.id.dialogText)).setText(content != null ? content:"");
    }    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }    
    
    public void onBtnClick(View view) {
    	switch(view.getId()) {
    	case R.id.dialogOKBtn:
    		finish();
    	}
    }
    
    static public Intent constructIntent(Context context, String title, String content) {
		Intent intent = new Intent(context,	DialogActvity.class);
		intent.putExtra(DialogActvity.KEY_TITLE, title);
		intent.putExtra(DialogActvity.KEY_CONTENT, content);
		
		return intent;
    }
}