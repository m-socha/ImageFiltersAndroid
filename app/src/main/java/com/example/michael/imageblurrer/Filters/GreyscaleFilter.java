package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.example.michael.imageblurrer.Core.ImageFilterApplication;
import com.example.michael.imageblurrer.ScriptC_greyscale;

public class GreyscaleFilter extends EffectFilter {

	private static GreyscaleFilter greyscaleFilterInstance;

	public static GreyscaleFilter getInstance() {
		if(greyscaleFilterInstance == null) {
			greyscaleFilterInstance = new GreyscaleFilter();
		}
		return greyscaleFilterInstance;
	}

	private GreyscaleFilter() {
		super();
	}

	@Override
	public Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight) {
		final Bitmap greyscaledBitmap = Bitmap.createBitmap(origBitmap);

		final RenderScript renderScript = RenderScript.create(ImageFilterApplication.getAppInstance().getAppContext());
		final ScriptC_greyscale greyscaleScript = new ScriptC_greyscale(renderScript);
		final Allocation inAlloc = Allocation.createFromBitmap(renderScript, origBitmap);
		final Allocation outAlloc = Allocation.createFromBitmap(renderScript, greyscaledBitmap);
		greyscaleScript.set_weight(weight);
		greyscaleScript.forEach_greyscale(inAlloc, outAlloc);

		outAlloc.copyTo(greyscaledBitmap);
		return greyscaledBitmap;
	}

}
