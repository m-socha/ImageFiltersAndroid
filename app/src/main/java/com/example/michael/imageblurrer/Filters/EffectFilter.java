package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.example.michael.imageblurrer.Core.ImageFilterApplication;

public abstract class EffectFilter {

	public EffectFilter() {}

	public Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight) {
		final Bitmap outputBitmap = Bitmap.createBitmap(origBitmap);
		final RenderScript renderScript = RenderScript.create(ImageFilterApplication.getAppInstance().getAppContext());
		final Allocation inAlloc = Allocation.createFromBitmap(renderScript, origBitmap);
		final Allocation outAlloc = Allocation.createFromBitmap(renderScript, outputBitmap);

		this.setupFilterScript(renderScript, inAlloc, outAlloc, weight);

		outAlloc.copyTo(outputBitmap);
		renderScript.destroy();
		return outputBitmap;
	}

	@Override
	public boolean equals(Object object) {
		if(!(object instanceof EffectFilter)) {
			return false;
		}
		return ((EffectFilter) object).getClass().equals(this.getClass());
	}

	protected abstract void setupFilterScript(RenderScript renderScript, Allocation inAlloc, Allocation outAlloc, float weight);

}
