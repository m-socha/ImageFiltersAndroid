package com.example.michael.imageblurrer.Filters;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
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
	protected void setupFilterScript(RenderScript renderScript, Allocation inAlloc, Allocation outAlloc, float weight) {
		final ScriptC_brighten brightenScript = new ScriptC_brighten(renderScript);
		brightenScript.set_weight(weight);
		brightenScript.forEach_brighten(inAlloc, outAlloc);
	}

}
