package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;

/**
 * Created by michael on 2/27/16.
 */
public class BlurFilter extends EffectFilter {

	public BlurFilter() {
		super("Blur");
	};

	public Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight) {
		return origBitmap;
	}

}
