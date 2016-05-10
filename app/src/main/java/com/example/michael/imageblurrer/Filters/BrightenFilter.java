package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.example.michael.imageblurrer.Core.ImageFilterApplication;
import com.example.michael.imageblurrer.ScriptC_brighten;

/**
 * Created by michael on 5/9/16.
 */
public class BrightenFilter extends EffectFilter {

	private static BrightenFilter brightenFilterInstance;

	public static BrightenFilter getInstance() {
		if(brightenFilterInstance == null) {
			brightenFilterInstance = new BrightenFilter();
		}
		return brightenFilterInstance;
	}

	private BrightenFilter() {
		super();
	}

	@Override
	public Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight) {
		final Bitmap brightenedBitmap = Bitmap.createBitmap(origBitmap);

		final RenderScript renderScript = RenderScript.create(ImageFilterApplication.getAppInstance().getAppContext());
		final ScriptC_brighten brightenScript = new ScriptC_brighten(renderScript);
		final Allocation inAlloc = Allocation.createFromBitmap(renderScript, origBitmap);
		final Allocation outAlloc = Allocation.createFromBitmap(renderScript, brightenedBitmap);
		brightenScript.set_weight(weight);
		brightenScript.forEach_brighten(inAlloc, outAlloc);

		outAlloc.copyTo(brightenedBitmap);
		renderScript.destroy();
		return brightenedBitmap;
	}

}
