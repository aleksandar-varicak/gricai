package com.cerSprikRu.BeerOBan.model.objects;

import com.cerSprikRu.BeerOBan.model.board.Tile;
import com.cerSprikRu.BeerOBan.view.graphicobjects.GraphicObject;

public abstract class GameObject {

	private Tile position;
	private GraphicObject graphics;
	private boolean animated;

	public Tile getPosition() {
		return position;
	}

	public void setPosition(Tile position) {
		this.position = position;
	}

	public GraphicObject getGraphics() {
		return graphics;
	}

	public void setGraphics(GraphicObject graphics) {
		this.graphics = graphics;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}

	public boolean isAnimated() {
		return animated;
	}
	
}
