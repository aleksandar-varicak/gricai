package com.funforall.bakiapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

public class DBManager {
	
	DBAdapter adapter;

	public DBManager(Context context) {
		adapter = new DBAdapter(context, Constants.DB_NAME, Constants.PROJECT_PATH);
	}

	public List<Text> read(boolean rated) {
		adapter.openDataBase();
		List<Text> result = new ArrayList<Text>();
		String sql = "select * from all_text";
		if(!rated)
			sql+= " limit "+Constants.LIMIT;

		Cursor c = adapter.executeSql(sql, null);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String text = c.getString(c.getColumnIndex("text"));
					int id = c.getInt(c.getColumnIndex("_id"));
					boolean fav = false;
					try {
						fav = c.getInt(c.getColumnIndex("fav")) == 1 ? true
								: false;
					} catch (Exception e) {
						fav = false;
					}
					Text textObj = new Text();
					textObj.text = text;
					textObj.isFavorite = fav;
					textObj.id = id;
					result.add(textObj);
				} while (c.moveToNext());
			}
		}
		c.close();
		adapter.close();
		return result;
	}

	public void updateText(Text textObj) {
		adapter.openDataBase();
		String sql = "update all_text set fav = "
				+ (textObj.isFavorite ? 1 : 0) + " where _id = " + textObj.id;
		adapter.update(sql);
		adapter.close();
	}
}