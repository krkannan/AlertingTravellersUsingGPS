package com.example.googemap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity1 extends FragmentActivity {

	GoogleMap googleMap;
	LocationManager locationManager;
	PendingIntent pendingIntent;
	SharedPreferences sharedPreferences;

	Bundle extras;
	LatLng point;
	String add;
	
	

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);

		extras = getIntent().getExtras();

		if (extras == null) {

		} else {

			add = extras.getString("add");
			point = new LatLng(extras.getDouble("to_lat"),
					extras.getDouble("to_long"));

			// Removes the existing marker from the Google Map

		}

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else { // Google Play Services are available

			// Getting reference to the SupportMapFragment of activity_main.xml
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

			// Getting GoogleMap object from the fragment
			googleMap = fm.getMap();

			// Enabling MyLocation Layer of Google Map
			googleMap.setMyLocationEnabled(true);

			// Getting LocationManager object from System Service
			// LOCATION_SERVICE
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// Opening the sharedPreferences object
			sharedPreferences = getSharedPreferences("location", 0);

			googleMap.clear();

			// Drawing marker on the map
			drawMarker(point);

			// Drawing circle on the map
			drawCircle(point);

			// This intent will call the activity ProximityActivity
			Intent proximityIntent = new Intent(
					"in.wptrafficanalyzer.activity.proximity");

			// Creating a pending intent which will be invoked by
			// LocationManager when the specified region is
			// entered or exited
			pendingIntent = PendingIntent.getActivity(getBaseContext(), 0,
					proximityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

			// Setting proximity alert
			// The pending intent will be invoked when the device enters or
			// exits the region 20 meters
			// away from the marked point
			// The -1 indicates that, the monitor will not be expired
			locationManager.addProximityAlert(point.latitude, point.longitude,
					20, -1, pendingIntent);

			/** Opening the editor object to write data to sharedPreferences */
			SharedPreferences.Editor editor = sharedPreferences.edit();

			/**
			 * Storing the latitude of the current location to the shared
			 * preferences
			 */
			editor.putString("lat", Double.toString(point.latitude));

			/**
			 * Storing the longitude of the current location to the shared
			 * preferences
			 */
			editor.putString("lng", Double.toString(point.longitude));

			/** Storing the zoom level to the shared preferences */
			editor.putString("zoom",
					Float.toString(googleMap.getCameraPosition().zoom));

			/** Saving the values stored in the shared preferences */
			editor.commit();

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					MainActivity1.this);

			// set title
			alertDialogBuilder.setTitle("Location Alert!");

			// set dialog message
			alertDialogBuilder
					.setMessage(
							"Location alert for the address " + add
									+ " has been added")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									// Moving CameraPosition to previously
									// clicked position
									googleMap.moveCamera(CameraUpdateFactory
											.newLatLng(new LatLng(
													point.latitude,
													point.longitude)));

									// Setting the zoom level in the map
									googleMap.animateCamera(CameraUpdateFactory
											.zoomTo(18));
								}

							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

			

			// Getting stored latitude if exists else return 0
			String lat = sharedPreferences.getString("lat", "0");

			// Getting stored longitude if exists else return 0
			String lng = sharedPreferences.getString("lng", "0");

			// Getting stored zoom level if exists else return 0
			String zoom = sharedPreferences.getString("zoom", "0");

			// If coordinates are stored earlier
			if (!lat.equals("0")) {

				// Drawing circle on the map
				drawCircle(new LatLng(Double.parseDouble(lat),
						Double.parseDouble(lng)));

				// Drawing marker on the map
				drawMarker(new LatLng(Double.parseDouble(lat),
						Double.parseDouble(lng)));

				// Moving CameraPosition to previously clicked position
				googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
						Double.parseDouble(lat), Double.parseDouble(lng))));

				// Setting the zoom level in the map
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float
						.parseFloat(zoom)));

			}

			googleMap.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {

					// Removes the existing marker from the Google Map
					googleMap.clear();

					// Drawing marker on the map
					drawMarker(point);

					// Drawing circle on the map
					drawCircle(point);

					// This intent will call the activity ProximityActivity
					Intent proximityIntent = new Intent(
							"in.wptrafficanalyzer.activity.proximity");

					// Creating a pending intent which will be invoked by
					// LocationManager when the specified region is
					// entered or exited
					pendingIntent = PendingIntent.getActivity(getBaseContext(),
							0, proximityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

					// Setting proximity alert
					// The pending intent will be invoked when the device enters
					// or exits the region 20 meters
					// away from the marked point
					// The -1 indicates that, the monitor will not be expired
					locationManager.addProximityAlert(point.latitude,
							point.longitude, 20, -1, pendingIntent);

					/**
					 * Opening the editor object to write data to
					 * sharedPreferences
					 */
					SharedPreferences.Editor editor = sharedPreferences.edit();

					/**
					 * Storing the latitude of the current location to the
					 * shared preferences
					 */
					editor.putString("lat", Double.toString(point.latitude));

					/**
					 * Storing the longitude of the current location to the
					 * shared preferences
					 */
					editor.putString("lng", Double.toString(point.longitude));

					/** Storing the zoom level to the shared preferences */
					editor.putString("zoom",
							Float.toString(googleMap.getCameraPosition().zoom));

					/** Saving the values stored in the shared preferences */
					editor.commit();

					Toast.makeText(getBaseContext(),
							"Proximity Alert is added", Toast.LENGTH_SHORT)
							.show();

				}
			});

			googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {
				@Override
				public void onMapLongClick(LatLng point) {
					Intent proximityIntent = new Intent(
							"in.wptrafficanalyzer.activity.proximity");

					pendingIntent = PendingIntent.getActivity(getBaseContext(),
							0, proximityIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

					// Removing the proximity alert
					locationManager.removeProximityAlert(pendingIntent);

					// Removing the marker and circle from the Google Map
					googleMap.clear();

					// Opening the editor object to delete data from
					// sharedPreferences
					SharedPreferences.Editor editor = sharedPreferences.edit();

					// Clearing the editor
					editor.clear();

					// Committing the changes
					editor.commit();

					Toast.makeText(getBaseContext(),
							"Location Alert is removed", Toast.LENGTH_LONG)
							.show();
				}
			});
		}
	}

	private void drawMarker(LatLng point) {
		// Creating an instance of MarkerOptions
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting latitude and longitude for the marker
		markerOptions.position(point);

		// Adding marker on the Google Map
		googleMap.addMarker(markerOptions);

	}

	private void drawCircle(LatLng point) {

		// Instantiating CircleOptions to draw a circle around the marker
		CircleOptions circleOptions = new CircleOptions();

		// Specifying the center of the circle
		circleOptions.center(point);

		// Radius of the circle
		circleOptions.radius(20);

		// Border color of the circle
		circleOptions.strokeColor(Color.BLACK);

		// Fill color of the circle
		circleOptions.fillColor(0x30ff0000);

		// Border width of the circle
		circleOptions.strokeWidth(2);

		// Adding the circle to the GoogleMap
		googleMap.addCircle(circleOptions);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}