package com.cerSprikRu.CocktailMixer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.cerSprikRu.CocktailMixer.favorites.FavoritesManager;
import com.cerSprikRu.CocktailMixer.model.drink.Category;
import com.cerSprikRu.CocktailMixer.model.drink.CocktailCreator;
import com.cerSprikRu.CocktailMixer.model.drink.Drink;
import com.cerSprikRu.CocktailMixer.threads.CocktailsCreatorThread;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class Chooser extends Activity {

	private static final String TYPE_TAG="type";
	private static final String DRINK_TAG="drink";
	private static final String SUBTYPE_TAG="subtype";
	
	public static Chooser instance;
	private boolean fullRandom = false;
	private List<Category> categories;
	private int currentCategory = 0;
	private int currentDrink = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		LinearLayout layout = (LinearLayout) findViewById(R.id.drink_list);
		initDrinks();
		for (Category category:categories) {
			TextView categoryView = new TextView(this);
			categoryView.setTextSize(27);
			categoryView.setText(category.getName().toUpperCase());
			categoryView.setTextColor(Color.WHITE);
			categoryView.setGravity(Gravity.CENTER);
			categoryView.setTypeface(null, Typeface.BOLD);
			layout.addView(categoryView);
			TableLayout categoryDrinksLayout = new TableLayout(this);
			categoryDrinksLayout.setStretchAllColumns(true);
			TableRow row = new TableRow(this);
			int indicator = 1;
			currentDrink = 0;
			for (Drink drink:category.getDrinks()) {
				StateListDrawable stateList = new StateListDrawable();
				int statePressed = android.R.attr.state_pressed;
				int stateChecked = android.R.attr.state_checked;
				stateList.addState(new int[] {-stateChecked}, new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.check_3)));
				stateList.addState(new int[] {stateChecked}, new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.check_1)));
				stateList.addState(new int[] {statePressed}, new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.check_2)));
				final CheckBox box = new CheckBox(this);
				box.setLayoutParams(new TableRow.LayoutParams(0,LayoutParams.WRAP_CONTENT,1f));
				String name = drink.getName();
				if(name.contains(" "))
					name = name.substring(0,name.indexOf(' '));
				box.setText(name);
				box.setTextColor(Color.WHITE);
				box.setButtonDrawable(stateList);
				CocktailTrace trace = new CocktailTrace();
				trace.currentCategory = currentCategory;
				trace.currentDrink = currentDrink;
				box.setTag(trace);
				box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						CocktailTrace trace = (CocktailTrace)box.getTag();
						final Drink drink = categories.get(trace.currentCategory).getDrinks().get(trace.currentDrink);
						boolean newValue = !drink.isPresent();
						drink.setPresent(newValue);
						if(newValue && drink.getSubDrinks()!=null && drink.getSubDrinks().size() > 0){
							AlertDialog.Builder builder = new AlertDialog.Builder(Chooser.this);
							CharSequence names[] = new CharSequence[drink.getSubDrinks().size()];
							int i=0;
							for(Drink subDrink:drink.getSubDrinks()){
								names[i++]=subDrink.getName();
							}
							builder.setTitle("Choose specific types");
							builder.setMultiChoiceItems(names,null, new DialogInterface.OnMultiChoiceClickListener() {
	
								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
									boolean present = drink.getSubDrinks().get(which).isPresent();
									drink.getSubDrinks().get(which).setPresent(!present);
								}
								
							});
							builder.setPositiveButton("done", new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int id) {
					                dialog.dismiss();
					           }
							});
							builder.create().show();
						}else {
							if(drink.getSubDrinks()!=null)
								for(Drink subDrink:drink.getSubDrinks()){
									subDrink.setPresent(false);
								}
						}
					}
				});
				row.addView(box);
				if (indicator % 2 == 0) {
					if (row != null) {
						categoryDrinksLayout.addView(row,
								new TableLayout.LayoutParams(
										LayoutParams.FILL_PARENT,
										LayoutParams.WRAP_CONTENT));
					}
					row = new TableRow(this);
				}
				currentDrink++;
				indicator++;
			}
			if(indicator %2 == 0){
				categoryDrinksLayout.addView(row,
						new TableLayout.LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
			}
			layout.addView(categoryDrinksLayout);
			currentCategory++;
		}
		
		Button favorites = (Button) findViewById(R.id.favorites);
		favorites.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent favoritesIntent = new Intent(v.getContext(),
						DisplayFavorites.class);
				startActivity(favoritesIntent);
			}
		});
		Button mix = (Button) findViewById(R.id.mix);
		mix.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CocktailCreator.getInstance().reset();
				for(Category category:categories){
					for(Drink drink:category.getDrinks()){
						if(drink.isPresent()){
							CocktailCreator.getInstance().addDrink(drink);
						}
					}
				}
				if (CocktailCreator.getInstance().size() < 5) {
					showError("If You have less than 5 drinks, You dont need cocktail mixer for that, just experiment. ;)");
				} else if (CocktailCreator.getInstance().canMix()) {
					CocktailsCreatorThread cct = new CocktailsCreatorThread(
							"cct", fullRandom);
					try {
						cct.t.join();
					} catch (InterruptedException e) {
					}
					Intent myIntent = new Intent(v.getContext(),
							DisplayCocktails.class);
					startActivityForResult(myIntent, 0);
				} else {
					showError("You cannot make cocktails with only strong drinks!");
				}
			}
		});
		
		AdView adView1 = (AdView) findViewById(R.id.adView1);
		adView1.loadAd(new AdRequest());
		AdView adView2 = (AdView) findViewById(R.id.adView2);
		adView2.loadAd(new AdRequest());
		new FavoritesManager(this);
		instance = this;
	}
	
	private void initDrinks() {
		categories = new ArrayList<Category>();
		try {
			InputStream stream = getAssets().open("drink_list.xml");
			XmlPullParser parser = XmlPullParserFactory.newInstance()
					.newPullParser();
			parser.setInput(stream, "UTF-8");
			int parserEvent = parser.getEventType();
			String tag = "";
			Category category=null;
			String categoryName = null;
			Drink currentDrink = null;;
			Drink subDrink=null;
			while (parserEvent != XmlPullParser.END_DOCUMENT) {
				switch (parserEvent) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if(tag.equalsIgnoreCase(TYPE_TAG)){
						category = new Category();
						categoryName = parser.getAttributeValue(null, "name");
						category.setName(categoryName);
					}else if(tag.equalsIgnoreCase(DRINK_TAG)){
						currentDrink = new Drink();
						currentDrink.setName(parser.getAttributeValue(null,"name"));
						currentDrink.setType(categoryName);
					}else if(tag.equalsIgnoreCase(SUBTYPE_TAG)){
						subDrink = new Drink();
					}
					break;
				case XmlPullParser.TEXT:
					String text = parser.getText().trim();
					if(subDrink!=null && text.length()>0){
						subDrink.setName(text);
						if(currentDrink!=null)
							currentDrink.addSubDrink(subDrink);
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if(tag.equalsIgnoreCase(DRINK_TAG)){
						if(category!=null)
							category.addDrink(currentDrink);
					}else if(tag.equalsIgnoreCase(TYPE_TAG)){
						if(category!=null && categoryName!=null){
							categories.add(category);
						}
					}
					break;
				default:
					break;
				}
				parserEvent = parser.next();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		System.out.println(categories);
	}

	private void showError(String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(text);
		builder.setTitle("cannot create");
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private class CocktailTrace implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		int currentCategory;
		int currentDrink;
	}
}