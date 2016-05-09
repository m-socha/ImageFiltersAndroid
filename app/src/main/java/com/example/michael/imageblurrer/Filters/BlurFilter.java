package com.example.michael.imageblurrer.Filters;

import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.example.michael.imageblurrer.Core.ImageFilterApplication;

/**
 * Created by michael on 2/27/16.
 */
public class BlurFilter extends EffectFilter {

	public BlurFilter() {};

	public Bitmap getBitmapFromFilter(Bitmap origBitmap, float weight) {
		final float radius = weight * 25f;
		final float scaleFactor = 1f / (1f + 10*weight);
		final int newHeight = Math.round(origBitmap.getHeight() * scaleFactor);
		final int newWidth = Math.round(origBitmap.getWidth() * scaleFactor);
		final Bitmap blurringSource = Bitmap.createScaledBitmap(origBitmap, newWidth, newHeight, false);
		final Bitmap blurredBitmap = Bitmap.createBitmap(blurringSource);

		final RenderScript renderScript = RenderScript.create(ImageFilterApplication.getAppInstance().getAppContext());
		final ScriptIntrinsicBlur blurEffect = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
		final Allocation inAlloc = Allocation.createFromBitmap(renderScript, blurringSource);
		final Allocation outAlloc = Allocation.createFromBitmap(renderScript, blurredBitmap);

		blurEffect.setRadius(radius);
		blurEffect.setInput(inAlloc);
		blurEffect.forEach(outAlloc);
		outAlloc.copyTo(blurredBitmap);
		return blurredBitmap;
	}

}