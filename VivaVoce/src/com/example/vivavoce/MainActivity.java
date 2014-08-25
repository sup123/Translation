package com.example.vivavoce;

import java.util.ArrayList;
import java.util.Locale;
/*
import org.ispeech.SpeechSynthesis;
import org.ispeech.error.InvalidApiKeyException;

import com.memetix.mst.language.Language;


import android.support.v7.app.ActionBarActivity;*/
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;


/* To Figure out 
 * 		What is toast
 * 		How to use it
 * 		
 * Need to make 2 drop down menus
 * 		Language In
 * 		Language Out
 * 		Do drop down menus work on cell phones, or is there a more user friendly way
 * 			Maybe a wheel thing where you select your ins and outs,
 * 				Is this is practical for the number of languages available
 * */

public class MainActivity extends Activity implements OnClickListener, OnInitListener{
	String[] languages = {
			"Arabic",
			"English",
			"French",
			"German",
			"Hindi",
			"Italian",
			"Thai",
	};
	private String inputLanguage, outputLanguage; //for help with translation code 
	private ImageView imageYes;
	private Button buttonYes;
	private TextToSpeech tts;
	private EditText editText1;
	private Spinner inputSpinner, outputSpinner;
	protected static final int REQUEST_OK = 1;

	/* ADDED START */
	public static SpeechSynthesis phrase_tts;
	/* ADDED END */


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		/* ADDED START */
		try {
			phrase_tts = SpeechSynthesis.getInstance(this);
		} catch (InvalidApiKeyException e) {
			Log.i("Error", "Key was invalid");
			Log.i("Key was invalid","Error");
			e.printStackTrace();
		}
		/* ADDED END */


		setContentView(R.layout.activity_main);
		tts = new TextToSpeech(this, this);
		buttonYes = (Button)findViewById(R.id.button1);
		imageYes = (ImageView)findViewById(R.id.image1);
		editText1 = (EditText)findViewById( R.id.editText1);
		// user clicked on drop down menus for languages
		inputSpinner = (Spinner)findViewById(R.id.spinner1);
		outputSpinner = (Spinner)findViewById(R.id.spinner2);
		imageYes.setOnClickListener(this); // microphone icon
		buttonYes.setOnClickListener(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, languages);

		inputSpinner.setAdapter(adapter);
		outputSpinner.setAdapter(adapter);
		// display what language user chose as input
		inputSpinner.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						//inputLanguage now has user's input
						inputLanguage = String.valueOf(inputSpinner.getSelectedItem());

					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				}
				);

		// Display what language user chose as output

		outputSpinner.setOnItemSelectedListener(
				new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						
						//outputLanguage now has user's chosen output
						outputLanguage = String.valueOf(inputSpinner.getSelectedItem());
						
					}
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				}
				);
	}

	@Override
	public void onInit(int code) {
		if (code==TextToSpeech.SUCCESS) {
			/*This Will Need To be inputed by the user */
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


					/*
					if (!tts.isSpeaking()) {
						tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
					}
					 */

					this.doTranslation(text);
				}

			} 
		}

		/* User clicked on microphone icon*/
		/* How do we get the stuff entered from mic */
		if(v== imageYes){

			Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

			/*"en-US" will have to be entered by the user; language input from mic */
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


	/*What Does This Do? */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
			ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			((TextView)findViewById(R.id.textView1)).setText(thingsYouSaid.get(0));
		}
	}

	public class PhraseExtraThread extends AsyncTask<Phrase,Void,String> {

		@Override
		protected String doInBackground(Phrase... phrases) {

			Phrase test = phrases[0];

			test.doTranslationAndAudio();
			return test.toString();

		}

		@Override
		protected void onPostExecute(String result){
			editText1.setText(result);
		}

	}

	private void doTranslation(String toTranslate){
		Phrase phraseToTranslate = new Phrase(toTranslate, Language.ENGLISH, 
				Language.SPANISH);

		PhraseExtraThread task = new PhraseExtraThread();
		task.execute(new Phrase[]{phraseToTranslate});

	}


}



/* BASIC HOW TO USE PHRASE

	public void OnButtonClickChangeTextOfTextView1( View view){
    	editText = (EditText) findViewById(R.id.editText1);
    	String toTranslate = editText.getText().toString();

    	Phrase phraseToTranslate = new Phrase(toTranslate, Language.ENGLISH, 
    			Language.SPANISH);

    	PhraseExtraThread task = new PhraseExtraThread();
    	task.execute(new Phrase[]{phraseToTranslate});
    }

    public class PhraseExtraThread extends AsyncTask<Phrase,Void,String> {

    	@Override
    	protected String doInBackground(Phrase... phrases) {

    	    	Phrase test = phrases[0];

    	    	test.doTranslationAndAudio();
    	    	return test.toString();

    	}

    	@Override
    	protected void onPostExecute(String result){
    		editText.setText(result);
    	}

    }


 */






