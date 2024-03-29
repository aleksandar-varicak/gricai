package rs.novosti;

import java.util.List;

import rs.novosti.adapter.CategoryLayoutAdapter;
import rs.novosti.adapter.CategoryPreviewAdapter;
import rs.novosti.model.Article;
import rs.novosti.model.Category;
import rs.novosti.model.Main;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
/**
 * Activity class for displaying starting view
 * @author aleksandarvaricak
 *
 */
public class NovostiPortal extends Activity {

	Category naslovna;

	List<Article> sliderArticles;
	public CategoryPreviewAdapter categoryPreviewAdapter;
	public CategoryLayoutAdapter categoryLayoutAdapter;
	public CategoryLayoutAdapter activeCategory;
	ProgressDialog progressDialog;
	LinearLayout menuView;
	private String currentCategory;
	
	Handler mainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == 0){
				createMenu();
				createMainPage();
			}
			if(msg.arg1 == 1){
				if(progressDialog!=null)
					progressDialog.dismiss();
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		SharedPreferences sPrefs = this.getSharedPreferences("novostiPrefs", MODE_WORLD_READABLE);
		if(sPrefs.getInt("isFirstTime", 1) == 1){
			Intent myIntent = new Intent(this, Disclaimer.class);
			startActivityForResult(myIntent, 0);
		}
		checkForNetworkAndStart();
		
	}
	/**
	 * Method that checks if internet connection is available
	 */
	private void checkForNetworkAndStart(){
		if(!isNetworkAvailable()){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Ne postoji internet konekcija")
			       .setCancelable(false)
			       .setPositiveButton("Pokusaj ponovo", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               checkForNetworkAndStart(); 
			           }
			       })
			       .setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   NovostiPortal.this.finish();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			Thread thread = new Thread(new LoaderThread());
			thread.start();
		}
	}
	
	public void resetMenuView() {
		for (int i = 0; i < menuView.getChildCount(); i++) {
			TextView tv = (TextView) menuView.getChildAt(i);
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(0xFFe1302a);
			tv.setBackgroundColor(0xFFFFFFFF);
		}
	}

	public void colorSelected(String name) {
		resetMenuView();
		for (int i = 0; i < menuView.getChildCount(); i++) {
			if (((TextView) menuView.getChildAt(i)).getText().toString().trim()
					.equalsIgnoreCase(name)) {
				menuView.getChildAt(i).setBackgroundResource(
						R.color.menu_high_light);
				return;
			}
		}
	}
	/**
	 * Method for creating menu items
	 */
	private void createMenu() {
		setContentView(R.layout.main);
		Button home = (Button) findViewById(R.id.HomeButton);
		Button gallery = (Button) findViewById(R.id.ImageGalleryButton);
		gallery.setBackgroundResource(R.drawable.photo_gallery_no);
		HorizontalScrollView horScrView = (HorizontalScrollView) findViewById(R.id.menuScrollView);
		horScrView.setHorizontalScrollBarEnabled(false);
		menuView = (LinearLayout) findViewById(R.id.Menu);
		menuView.setHorizontalScrollBarEnabled(false);
		home.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				currentCategory = null;
				resetMenuView();
				createMainPage();
			}
		});

		gallery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), Galerija.class);
				startActivityForResult(myIntent, 0);
			}
		});

		String names[] = { "Top vesti", "Sport", "Politika", "Društvo", "Ekonomija",
				"Hronika", "Beograd", "Dosije", "Spektakl", "Život plus",
				"Tehnologije", "Auto" };

		for (String categoryName:names) {
			final String name = categoryName;
			final TextView tv = new TextView(this);
			tv.setHeight(30);
			tv.setTextSize(16);
			tv.setTextColor(0xFFe1302a);
			tv.setBackgroundColor(0xFFFFFFFF);
			tv.setPadding(5, 0, 5, 0);
			tv.setGravity(Gravity.CENTER);
			tv.setTypeface(Typeface.DEFAULT_BOLD);
			tv.setText(" " + name + " ");
			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (categoryLayoutAdapter != null) {
						categoryLayoutAdapter.clear();
					}
					new CategoryLoaderTask().execute(name);
					resetMenuView();
					tv.setGravity(Gravity.CENTER);
					tv.setTextColor(0xFFFFFFFF);
					tv.setBackgroundColor(0xFFe1302a);
					
				}
			});
			menuView.addView(tv);
		}
	}
	/**
	 * Method for creating the main view
	 */
	private void createMainPage() {
		Main main = Main.getInstance();
		sliderArticles = main.getSliderArticles();
		ListView view = (ListView) findViewById(R.id.Content);
		if (categoryPreviewAdapter == null)
			categoryPreviewAdapter = new CategoryPreviewAdapter(this, main
					.getNaslovna().getArticles(), sliderArticles);
		view.setAdapter(categoryPreviewAdapter);
		view.setItemsCanFocus(true);
		

		final TextView refTime = (TextView) findViewById(R.id.time_refreshed);
		refTime.setText(Main.getInstance().getTimeRefreshed());
		LinearLayout refresh = (LinearLayout) findViewById(R.id.refresh_button);
		refresh.setClickable(true);
		refresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(currentCategory!=null){
					activeCategory.refresh();
					refTime.setText(Main.getInstance().getTimeRefreshed());
				}
				
			}
		});
		final TextView technicomView = (TextView) findViewById(R.id.tehnicom_solutions);
		technicomView.setText(Html.fromHtml("</style><a href=\"http://www.tehnicomsolutions.com\">Tehnicom computers</a>"));
		technicomView.setMovementMethod(LinkMovementMethod.getInstance());
		technicomView.setLinkTextColor(Color.WHITE);
	}

	private class LoaderThread implements Runnable {

		@Override
		public void run() {
			Main.getInstance();
			Message msg = new Message();
			msg.arg1 = 0;
			mainHandler.sendMessage(msg);
		}

	}
	/**
	 * AsyncTask for loading category data
	 * @author aleksandarvaricak
	 *
	 */
	private class CategoryLoaderTask extends AsyncTask<String, Void, CategoryLayoutAdapter>{

		@Override
		protected CategoryLayoutAdapter doInBackground(String... params) {
			categoryLayoutAdapter = new CategoryLayoutAdapter(
					NovostiPortal.this, params[0]);
			currentCategory = params[0];
			return categoryLayoutAdapter;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(NovostiPortal.this, "", "Molimo sačekajte");
		}
		
		@Override
		protected void onPostExecute(CategoryLayoutAdapter result) {
			super.onPostExecute(result);
			if(categoryPreviewAdapter!=null)
				categoryPreviewAdapter.clear();
			activeCategory = result;
			categoryLayoutAdapter = null;
			ListView view = (ListView) findViewById(R.id.Content);
			view.setAdapter(result);
			view.setItemsCanFocus(true);
			view.setFocusable(false);
			progressDialog.dismiss();
			
		}
		
	}
	/**
	 * Method for handling results returned by other activities
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == 1440) {
				resetMenuView();
				createMainPage();
			}
			if (resultCode == 4373) {
				finish();
			}
			if (resultCode == 1410) {
				Intent myIntent = new Intent(this, Galerija.class);
				startActivityForResult(myIntent, 0);
			}
		}
	}

	private boolean isNetworkAvailable() {
		try{
		    ConnectivityManager connectivityManager 
		          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo.isConnected();
		}catch (Exception e) {
			return false;
		}
	}

}