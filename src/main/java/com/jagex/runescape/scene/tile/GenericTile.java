package com.jagex.runescape.scene.tile;

public class GenericTile {

	public GenericTile(int colourA, int colourB, int colourC, int colourD, int texture, int rgbColor, boolean flat) {
		this.colourA = colourA;
		this.colourB = colourB;
		this.colourC = colourC;
		this.colourD = colourD;
		this.texture = texture;
		this.rgbColor = rgbColor;
		this.flat = flat;
	}

	public int colourA;
	public int colourB;
	public int colourD;
	public int colourC;
	public int texture;
	public boolean flat;
	public int rgbColor;
}
