package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;

/**
 * Created by michael on 2/28/16.
 */
public class InvertFilter extends EffectFilter{

	public InvertFilter() {
		super("Invert");
	}

	@Override
	public Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight) {
		return origBitmap;
	}
}
