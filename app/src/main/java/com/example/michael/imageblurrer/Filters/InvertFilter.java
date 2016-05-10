package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;
import android.support.v8.renderscript.*;

import com.example.michael.imageblurrer.Core.ImageFilterApplication;
import com.example.michael.imageblurrer.ScriptC_invert;

/**
 * Created by michael on 2/28/16.
 */
public class InvertFilter extends EffectFilter{

	private static InvertFilter invertFilterInstance;

	public static InvertFilter getInstance() {
		if(invertFilterInstance == null) {
			invertFilterInstance = new InvertFilter();
		}
		return invertFilterInstance;
	}

	private InvertFilter() {
		super();
	}

	@Override
	public Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight) {
		final Bitmap invertedBitmap = Bitmap.createBitmap(origBitmap);

		final RenderScript renderScript = RenderScript.create(ImageFilterApplication.getAppInstance().getAppContext());
		final ScriptC_invert invertScript = new ScriptC_invert(renderScript);
		final Allocation inAlloc = Allocation.createFromBitmap(renderScript, origBitmap);
		final Allocation outAlloc = Allocation.createFromBitmap(renderScript, invertedBitmap);
		invertScript.set_weight(weight);
		invertScript.forEach_invert(inAlloc, outAlloc);

		outAlloc.copyTo(invertedBitmap);
		renderScript.destroy();
		return invertedBitmap;
	}
}