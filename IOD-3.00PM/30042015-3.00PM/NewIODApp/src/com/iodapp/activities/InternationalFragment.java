package com.iodapp.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;





import com.iodapp.adapter.CustomListAdapter;

import com.iodapp.model.Movie;
import com.iodapp.util.URLSuppoter;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class InternationalFragment extends Fragment {

	// Log tag
	private static final String TAG = DoctorAvailableActivity.class.getSimpleName();

	// Movies json url
	private static final String url = "http://jsoncdn.webcodeplus.com/TokenData.svc/GetAvailableDoctorList/0/"+Sharedpref.getcustomerid();
	private final String ganrateTokenUrl = "http://jsoncdn.webcodeplus.com/TokenData.svc/AddTokenForMedia";
	private final String getApplist = "http://jsoncdn.webcodeplus.com/AppointmentData.svc/GetAppointmentsList/"+Sharedpref.getcustomerid();
	private String Credit_url  = "http://jsoncdn.webcodeplus.com/CkeckCreditData.svc/GetCreditAvailable/"+Sharedpref.getcustomerid()+"/0";
	
	private ProgressDialog pDialog;
	private List<Movie> movieList = new ArrayList<Movie>();
	private ListView listView;
	private CustomListAdapter adapter;
	private String img_path = "http://cms.ionlinedoctor.com/AdminData/";
	
	TextView UserName;
	private String msg,msgTag = "";
	private String dr_name,dr_id="";
	private String imgUrl="";
	private String dr_rate="0";
	private String Q_no="";
	protected int LocalAppcount,IntAppCount=0;
	private int l_cradit,i_cradit;
	public PersonDetail person;

	Timer timer = new Timer();

	private Typeface sysFont;

	private TextView name;

	private TextView avilibity;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_international, container, false);
		
//		UserName=(TextView)rootView.findViewById(R.id.txtfullname);
		listView = (ListView) rootView.findViewById(R.id.list);
		name = (TextView) rootView.findViewById(R.id.title);
		avilibity = (TextView) rootView.findViewById(R.id.rating);
		adapter = new CustomListAdapter(getActivity(), movieList);
		listView.setAdapter(adapter);
		
		pDialog = new ProgressDialog(getActivity());
		// Showing progress dialog before making http request
		pDialog.setMessage("Loading...");
		pDialog.show();

		
//		getActivity().getActionBar().setBackgroundDrawable(
//				new ColorDrawable(Color.parseColor("#1b1b1b")));
//		
		
		//UserName.setText(Sharedpref.getfirstname()+" "+Sharedpref.getlastname());
		// changing action bar color
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				Log.i("testing", "repeat testing");// display the data
//				i = i + 1;
				getDoctorsList();
				
			}
		}, 0, 10000); // Creating volley request obj
		// getDoctorsList();
		
listView.setOnItemClickListener(new OnItemClickListener() {

			

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				
				
				new getCredit().execute(Credit_url);
				getBookedSchedualList();
				
				
				Movie m1 = (Movie) movieList.get(position);
				dr_name = m1.getTitle();
				dr_id = m1.getDoctore_id();
				imgUrl = m1.getThumbnailUrl();
				dr_rate = m1.getRating();
				Q_no = m1.getQueue_no();
				
				
				
				if(i_cradit > 0)
				{
				
					if(i_cradit > IntAppCount)
					{
				
						if(m1.getAvailable().equalsIgnoreCase("Available"))
						{
							msg = "Video Call Doctor?";
							msgTag= "Avialable";
					
					
//					Toast.makeText(getActivity().getApplicationContext(), m1.getTitle()+" : "+m1.getAvailable(), Toast.LENGTH_SHORT).show();
							showDialog();
						}
						else
						{
							msg ="You are number "+Q_no+" in Queue.Do you wish to continue Waiting ?";
							msgTag = "Busy";
//					Toast.makeText(getActivity().getApplicationContext(), m1.getTitle()+" : Doactore Busy", Toast.LENGTH_SHORT).show();
							showDialog();
						}
					}
					else
					{
						msg = "Cancel appointment to video call.";
						msgTag = "CrossCheck";
						showDialog();
					}
				}
				else
				{
					msg = "Purchase more credit to call Doctor.";
					msgTag="Credit";
					showDialog();
				}
				
				
				
			}
		});

//		setSystemFont();
		return rootView;
	}
	
	
	private void setSystemFont()
	{
		sysFont = Typeface.createFromAsset(getActivity().getAssets(),"font/Leelawadee.ttf");
		name.setTypeface(sysFont);
		avilibity.setTypeface(sysFont);
		
		
		
	}
	private void showDialog()
	{
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
 
		
			// set title
//			alertDialogBuilder.setTitle("1. By Logging into and/or Registering New Account with I Online Doctor, I accept all I Online Terms and Conditions.");
			
			// set dialog message
			alertDialogBuilder
				.setMessage(msg).setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						
						
						if(msgTag.equalsIgnoreCase("CrossCheck"))
						{
							
							Intent intent = new Intent(getActivity().getApplicationContext(), DeleteAppointment.class);
					        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				       
				        
//					       
							Bundle bndlanimation = 
									ActivityOptions.makeCustomAnimation(getActivity(), R.anim.animation,R.anim.animation2).toBundle();
							getActivity().startActivity(intent, bndlanimation);
							timer.cancel();
							getActivity().finish();
							
							
						}
						
						if(msgTag.equalsIgnoreCase("Credit"))
						{
							Intent plan = new Intent(getActivity(), PricingPlan.class);
							plan.putExtra("Fragment_Code", R.id.list);
							
							startActivity(plan);
							timer.cancel();
							getActivity().finish();
							
							dialog.cancel();
						}
						
						if(msgTag.equalsIgnoreCase("Avialable"))
						{
						
							new HttpAsyncTaskpost()
							.execute(ganrateTokenUrl);
							
							
						}
						else if(msgTag.equalsIgnoreCase("Busy"))
						{
//							Toast.makeText(getActivity().getApplicationContext(), "Busy you Wait..", Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(getActivity().getApplicationContext(), CallWaiting.class);
					        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				        intent.putExtra("Dr.Name", dr_name);
				        intent.putExtra("drID",dr_id);
				        intent.putExtra("imagePath", imgUrl);
				        intent.putExtra("Dr_rating", dr_rate);
				        intent.putExtra("frag_code", 1);
				        
//					       
							Bundle bndlanimation = 
									ActivityOptions.makeCustomAnimation(getActivity(), R.anim.animation,R.anim.animation2).toBundle();
							getActivity().startActivity(intent, bndlanimation);
							timer.cancel();
							getActivity().finish();
							
						}
						
						
					
						
					}
				  }).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							
							
							dialog.cancel();
							
						}});
			
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
				alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#ffffff"));
				alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(Color.parseColor("#ffffff"));
				alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#00bfff"));
				alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00bfff"));
				alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextSize(19.0f);
				alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(19.0f);
	}
	
	public void getDoctorsList() {
		JsonArrayRequest movieReq = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						Log.d(TAG, response.toString());
						hidePDialog();
						String str = "";
						// Parsing json
						movieList.clear();
						for (int i = 0; i < response.length(); i++) {
							try {

								JSONObject obj = response.getJSONObject(i);
								Movie movie = new Movie();
								movie.setTitle(obj.getString("FirstName") + " "
										+ obj.getString("LastName"));
								movie.setThumbnailUrl(img_path+obj.getString("SmallImage"));
								
								if (obj.getString("Available")
										.equalsIgnoreCase("y")) {
									str = "Available";
								} else {
									str = "Busy";
								}
								movie.setAvailable(str);
								movie.setDoctore_id(obj.getString("DoctorID"));
								movie.setYear(obj.getString("DoctorID"));
								movie.setSpeciality(obj.getString("Specialization"));
								movie.setExp(obj.getString("Experience"));
								movie.setRating(obj.getString("AvgRate"));
								movie.setQueue_no(obj.getString("QueNumber"));
								//
								// // Genre is json array
								// JSONArray genreArry =
								// obj.getJSONArray("genre");
								// ArrayList<String> genre = new
								// ArrayList<String>();
								// for (int j = 0; j < genreArry.length(); j++)
								// {
								// genre.add((String) genreArry.get(j));
								// }
								// movie.setGenre(genre);

								// adding movie to movies array
								movieList.add(movie);

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						// notifying list adapter about data changes
						// so that it renders the list view with updated data
						adapter.notifyDataSetChanged();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						hidePDialog();

					}
				});

		// Adding request to request queue
		Sharedpref.getInstance().addToRequestQueue(movieReq);

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		hidePDialog();
	}

	private void hidePDialog() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}
	
private class HttpAsyncTaskpost extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... urls) {

			person = new PersonDetail();

			person.setCustomerID(Sharedpref.getcustomerid());
			person.setState(Sharedpref.getstatecode());
			person.setdoctorid(dr_id );

			return POST(urls[0], person);
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.d("Result data", result.toString());
			
			String tokenid = "";
			tokenid = result;
			//Sharedpref.setTokenId(result);
			
						
			if(!(tokenid==null))
			{
				
				 Intent intent = new Intent(getActivity().getApplicationContext(), ImageAndDescription.class);
			        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        intent.putExtra("Dr.Name", dr_name);
		        intent.putExtra("drID", dr_id);
		        intent.putExtra("TokenId", tokenid);
		       
					Bundle bndlanimation = 
							ActivityOptions.makeCustomAnimation(getActivity(), R.anim.animation,R.anim.animation2).toBundle();
					getActivity().startActivity(intent, bndlanimation);
					timer.cancel();
					getActivity().finish();
					
			}
			else
			{
				Toast.makeText(getActivity().getApplicationContext(),"Your Token Not ganarete." ,Toast.LENGTH_LONG).show();
			}
//			
						
			}
		}
	
	public static String POST(String url, PersonDetail person) {
		InputStream inputStream = null;
		String result = "";
		try {

			// 1. create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			// 2. make POST request to the given URL
			HttpPost httpPost = new HttpPost(url);
			// String json = "";

			// 3. build jsonObject
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("TokenID", "0");
			jsonObject.accumulate("CustomerID", person.getCustomerID());
			jsonObject.accumulate("DoctorID", person.getdoctorid());
			jsonObject.accumulate("StateID", person.getState());
			
			Log.d("Post JsonObject", jsonObject.toString());
			
			// 4. convert JSONObject to JSON to String
			// json = jsonObject.toString();

			// ** Alternative way to convert Person object to JSON string usin
			// Jackson Lib
			// ObjectMapper mapper = new ObjectMapper();
			// json = mapper.writeValueAsString(person);

			// 5. set json to StringEntity
			StringEntity se = new StringEntity(jsonObject.toString());

			// 6. set httpPost Entity
			httpPost.setEntity(se);

			// // 7. Set some headers to inform server about the type of the
			// content
			// httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader(HTTP.CONTENT_TYPE,
					"application/json; charset=utf-8");
			// se.setContentType("application/json;charset=UTF-8");
			// se.setContentEncoding(new
			// BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
			// 8. Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 9. receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// 10. convert inputstream to string
			if (inputStream != null)
			{
				result = convertInputStreamToString(inputStream);
			
			}
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		// 11. return result
		Log.d("result",""+result);
		return result;
	}
	
	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}
	
	public void getBookedSchedualList() {
		JsonArrayRequest movieReq = new JsonArrayRequest(getApplist,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						Log.d("BookNow", response.toString());
						hidePDialog();
						LocalAppcount=0;
						IntAppCount=0;
						for (int i = 0; i < response.length(); i++) {
							try {

								JSONObject obj = response.getJSONObject(i);
								String sid="";
								
								sid = obj.getString("StateID").toString();
								if(Integer.parseInt(sid) > 0)
								{
									LocalAppcount++;
								}
								else
								{
									IntAppCount++;
								}
								
								
								
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
					}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						VolleyLog.d("Cancel Appointment", "Error: " + error.getMessage());
						hidePDialog();

					}
				});

		// Adding request to request queue
	Sharedpref.getInstance().addToRequestQueue(movieReq);

	}
	
	private class getCredit extends AsyncTask<String, Void, String> {
		private URLSuppoter suppoter;
		


        protected ProgressDialog progressDialog;

		
		
		@Override
		protected String doInBackground(String... urls) {

			suppoter = new URLSuppoter();
			return suppoter.GET(urls[0]);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progressBar.setVisibility(View.VISIBLE);
			   progressDialog = ProgressDialog.show(getActivity(), "Loading", "Please Wait", true, false);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {

			// progressBar.setVisibility(View.INVISIBLE);
			try {
				// result = jsonstr;
				
//				JSONArray ja = new JSONArray(result);
				JSONObject ja = new JSONObject(result);
				
				l_cradit = Integer.parseInt(ja.getString("RemianLoacalCredit"));
				i_cradit = Integer.parseInt(ja.getString("RemainInternationalCredit"));
				
				
				hidePDialog();
				progressDialog.dismiss();
				
				
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				progressDialog.dismiss();
				
			}

			
			
			       
		}

		@Override
		protected void onCancelled() {

			hidePDialog();
			super.onCancelled();
//			Toast toast = Toast.makeText(DashboardActivity.this,
//					"Error is occured due to some probelm", Toast.LENGTH_LONG);
//			toast.show();

		}
	}

}
