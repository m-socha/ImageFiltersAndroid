package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;

public abstract class EffectFilter {

	public EffectFilter() {}

	public abstract Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight);

	@Override
	public boolean equals(Object object) {
		if(!(object instanceof EffectFilter)) {
			return false;
		}
		return ((EffectFilter) object).getClass().equals(this.getClass());
	}

}
