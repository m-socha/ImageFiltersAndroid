package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by michael on 2/28/16.
 */
public class InvertFilter extends EffectFilter{

	public InvertFilter() {
		super();
	}

	@Override
	public Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight) {
		final Bitmap filteredBitmap = Bitmap.createBitmap(origBitmap.getWidth(), origBitmap.getHeight(), origBitmap.getConfig());
		for(int x = 0; x < filteredBitmap.getWidth(); x++) {
			for(int y = 0; y < filteredBitmap.getHeight(); y++) {
				final int origPixel = origBitmap.getPixel(x, y);
				filteredBitmap.setPixel(x, y, Color.argb(
						Color.alpha(origPixel),
						this.getWeightedInverse(Color.red(origPixel), weight),
						this.getWeightedInverse(Color.green(origPixel), weight),
						this.getWeightedInverse(Color.blue(origPixel), weight)
				));
			}
		}
		return filteredBitmap;
	}

	private int getWeightedInverse(int colorVal, float weight) {
		return Math.round(colorVal*(1 - weight) + weight*(255 - colorVal));
	}

}
