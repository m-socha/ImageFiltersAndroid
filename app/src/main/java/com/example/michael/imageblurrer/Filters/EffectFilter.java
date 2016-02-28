package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;

/**
 * Created by michael on 2/27/16.
 */
public abstract class EffectFilter {

	private String filterTag;

	public EffectFilter(String filterTag) {
		this.filterTag = filterTag;
	}

	public abstract Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight);

	public String getFilterTag() {
		return this.filterTag;
	}

	@Override
	public boolean equals(Object object) {
		if(object == null) {
			return false;
		}
		if(!(object instanceof EffectFilter)) {
			return false;
		}
		return ((EffectFilter) object).getFilterTag().equals(this.getFilterTag());
	}

}
