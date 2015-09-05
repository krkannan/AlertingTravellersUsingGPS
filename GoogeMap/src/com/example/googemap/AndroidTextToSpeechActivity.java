package com.example.googemap;

import java.util.List;
import java.util.Locale;

import com.example.googemap.MyLocation.LocationResult;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AndroidTextToSpeechActivity extends Activity implements
		TextToSpeech.OnInitListener {
	/** Called when the activity is first created. */

	private TextToSpeech tts;
	private Button btnSpeak;
	private EditText txtText;
	String address_;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(Location location) {
				// Got the location!
				getAddress(location.getLatitude(), location.getLongitude());

			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);

		tts = new TextToSpeech(this, this);

		

		txtText = (EditText) findViewById(R.id.txtText);
		//txtText.setText("have purchased two of this kind with size L and this one did not fit to me properly whereas the other one is perfect fit. Not sure if that one is better in size or need to loose some weight.");

		
	}
	
	public List<Address> getAddress(double latitude, double longitude) {
		try {
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(getApplicationContext());
			if (latitude != 0 || longitude != 0) {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
				String address = addresses.get(0).getAddressLine(0);
				String city = addresses.get(0).getAddressLine(1);
				String country = addresses.get(0).getAddressLine(2);
				Log.d("TAG", "address = " + address + ", city =" + city
						+ ", country = " + country);

				address_ = address + "," + city + "," + country;
				
				txtText.setText(address_);
				speakOut();
				return addresses;
			} else {
				Toast.makeText(AndroidTextToSpeechActivity.this,
						"latitude and longitude are null", Toast.LENGTH_LONG)
						.show();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			// tts.setPitch(5); // set pitch level

			// tts.setSpeechRate(2); // set speech speed rate

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "Language is not supported");
			} else {
				//btnSpeak.setEnabled(true);
				speakOut();
			}

		} else {
			Log.e("TTS", "Initilization Failed");
		}

	}

	private void speakOut() {

		String text = txtText.getText().toString();

		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
}