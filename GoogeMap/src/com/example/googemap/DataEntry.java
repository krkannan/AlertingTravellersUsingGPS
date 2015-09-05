package com.example.googemap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.googemap.MyLocation.LocationResult;
import com.google.android.maps.GeoPoint;

public class DataEntry extends Activity {

	CustomAutoCompleteTextView from, to;
	PlacesTask placesTask;
	ParserTask parserTask;
	String address_;
	
	public static String addressss;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_entry);

		from = (CustomAutoCompleteTextView) findViewById(R.id.from);
		from.setThreshold(1);

		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(Location location) {
				// Got the location!
				getAddress(location.getLatitude(), location.getLongitude());

			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(this, locationResult);

		from.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				placesTask = new PlacesTask("from");
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

		to = (CustomAutoCompleteTextView) findViewById(R.id.to);
		to.setThreshold(1);

		to.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				placesTask = new PlacesTask("to");
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}

	public void find(View v) {

		GeoPoint from_geo = getLocationFromAddress(from.getText().toString());
		GeoPoint to_geo = getLocationFromAddress(to.getText().toString());

		System.out.println("lat::::::" + from_geo.getLatitudeE6() / 1E6);
		System.out.println("lat::::::" + to_geo.getLatitudeE6() / 1E6);

		Intent i = new Intent();

		i.putExtra("from_lat", from_geo.getLatitudeE6() / 1E6);
		i.putExtra("from_long", from_geo.getLongitudeE6() / 1E6);
		i.putExtra("to_lat", to_geo.getLatitudeE6() / 1E6);
		i.putExtra("to_long", to_geo.getLongitudeE6() / 1E6);
		i.putExtra("add", to.getText().toString());

		i.setClass(DataEntry.this, MainActivity1.class);
		startActivity(i);

	}

	public class PlacesTask extends AsyncTask<String, Void, String> {

		String value;

		public PlacesTask(String val) {
			value = val;
		}

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=AIzaSyAJ5xrKQ6U_eHDsYw4ZcrZZEb2zJaZIJjQ";

			String input = "";

			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			// place type to be searched
			String types = "types=geocode";

			// Sensor enabled
			String sensor = "sensor=false";

			// Building the parameters to the web service
			String parameters = input + "&" + types + "&" + sensor + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
					+ output + "?" + parameters;

			try {
				// Fetching the data from web service in background
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Creating ParserTask
			parserTask = new ParserTask(value);

			// Starting Parsing the JSON string returned by Web Service
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;
		String value;

		public ParserTask(String val) {
			// TODO Auto-generated constructor stub
			value = val;
		}

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;

			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			String[] from_ = new String[] { "description" };
			int[] to_ = new int[] { android.R.id.text1 };

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(DataEntry.this, result,
					android.R.layout.simple_list_item_1, from_, to_);

			// Setting the adapter
			if (value.equalsIgnoreCase("from")) {
				from.setAdapter(adapter);
			} else if (value.equalsIgnoreCase("to")) {
				to.setAdapter(adapter);
			}

		}
	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	public GeoPoint getLocationFromAddress(String strAddress) {

		Geocoder coder = new Geocoder(this);
		List<Address> address;
		GeoPoint p1 = null;

		try {
			address = coder.getFromLocationName(strAddress, 5);
			if (address == null) {
				return null;
			}
			Address location = address.get(0);
			location.getLatitude();
			location.getLongitude();

			p1 = new GeoPoint((int) (location.getLatitude() * 1E6),
					(int) (location.getLongitude() * 1E6));

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		}
		return p1;
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
				addressss=address;
				from.setText(address_);
				return addresses;
			} else {
				Toast.makeText(DataEntry.this,
						"latitude and longitude are null", Toast.LENGTH_LONG)
						.show();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
