package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.example.michael.imageblurrer.Core.ImageFilterApplication;
import com.example.michael.imageblurrer.ScriptC_darken;

public class DarkenFilter extends EffectFilter {

		private static DarkenFilter darkenFilterInstance;

		public static DarkenFilter getInstance() {
			if(darkenFilterInstance == null) {
				darkenFilterInstance = new DarkenFilter();
			}
			return darkenFilterInstance;
		}

		private DarkenFilter() {
			super();
		}

		@Override
		public Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight) {
		final Bitmap darkenedBitmap = Bitmap.createBitmap(origBitmap);

		final RenderScript renderScript = RenderScript.create(ImageFilterApplication.getAppInstance().getAppContext());
		final ScriptC_darken darkenScript = new ScriptC_darken(renderScript);
		final Allocation inAlloc = Allocation.createFromBitmap(renderScript, origBitmap);
		final Allocation outAlloc = Allocation.createFromBitmap(renderScript, darkenedBitmap);
		darkenScript.set_weight(weight);
		darkenScript.forEach_darken(inAlloc, outAlloc);

		outAlloc.copyTo(darkenedBitmap);
		renderScript.destroy();
		return darkenedBitmap;
		}

}
