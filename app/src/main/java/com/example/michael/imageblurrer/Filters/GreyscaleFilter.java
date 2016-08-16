package com.example.michael.imageblurrer.Filters;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
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
	protected void setupFilterScript(RenderScript renderScript, Allocation inAlloc, Allocation outAlloc, float weight) {
		final ScriptC_greyscale greyscaleScript = new ScriptC_greyscale(renderScript);
		greyscaleScript.set_weight(weight);
		greyscaleScript.forEach_greyscale(inAlloc, outAlloc);
	}

}
