package com.example.vivavoce;

import java.util.ArrayList;
import java.util.Locale;

import org.ispeech.SpeechSynthesis;
import org.ispeech.error.InvalidApiKeyException;

import com.memetix.mst.language.Language;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;


public class MainActivity extends Activity implements OnClickListener, OnInitListener{
	private ImageView imageYes;
	private Button buttonYes;
	private TextToSpeech tts;
	protected static final int REQUEST_OK = 1;
	
	//ADDED THIS
	public static SpeechSynthesis phrase_tts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//ADDED THIS
        try {
        	phrase_tts = SpeechSynthesis.getInstance(this);
		} catch (InvalidApiKeyException e) {
			Log.i("Error", "Key was invalid");
			Log.i("Key was invalid","Error");
			e.printStackTrace();
		}
		
		
		setContentView(R.layout.activity_main);
		tts = new TextToSpeech(this, this);
		buttonYes = (Button)findViewById(R.id.button1);
		imageYes = (ImageView)findViewById(R.id.image1);
		imageYes.setOnClickListener(this); 
		buttonYes.setOnClickListener(this);
		
		
		
	    
	    
		
		
		
	}

	@Override
	public void onInit(int code) {
		if (code==TextToSpeech.SUCCESS) {
			tts.setLanguage(Locale.getDefault());

		} else {
			tts = null;
			Toast.makeText(this, "Failed to initialize TTS engine.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {

		/* User clicked on Speak */
		if(v==buttonYes){
			if (tts!=null) {
				String text =
						((EditText)findViewById(R.id.editText1)).getText().toString();
				if (text!=null) {
					if (!tts.isSpeaking()) {
						tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
					}
				}
			} 
		}

		/* User clicked on microphone icon*/
		if(v== imageYes){

			Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
			try {
				startActivityForResult(i, REQUEST_OK);
			} catch (Exception e) {
				Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (tts!=null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
			ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			((TextView)findViewById(R.id.textView1)).setText(thingsYouSaid.get(0));
		}
	}
}

/*	Example Usage of Phrase
    	Phrase phraseToTranslate = new Phrase(toTranslate, Language.ENGLISH, 
    			Language.SPANISH);
    	
    	PhraseExtraThread task = new PhraseExtraThread();
    	task.execute(new Phrase[]{phraseToTranslate});
 */




