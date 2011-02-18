package com.cerSprikRu.BeerOBan.graphics;

import com.cerSprikRu.BeerOBan.R;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

public class PlayerGraphicObject extends GraphicObject{
	public PlayerGraphicObject(Resources resources){
		super(resources, BitmapFactory.decodeResource(resources, R.drawable.player));
	}
	public PlayerGraphicObject(Resources resources, Coordinates coordinates){
		super(resources, BitmapFactory.decodeResource(resources, R.drawable.player), coordinates);
	}
}
