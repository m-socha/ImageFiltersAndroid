package com.example.michael.imageblurrer.Filters;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
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
	protected void setupFilterScript(RenderScript renderScript, Allocation inAlloc, Allocation outAlloc, float weight) {
		final ScriptC_darken darkenScript = new ScriptC_darken(renderScript);
		darkenScript.set_weight(weight);
		darkenScript.forEach_darken(inAlloc, outAlloc);
	}

}
