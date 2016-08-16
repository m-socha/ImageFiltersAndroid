package com.example.michael.imageblurrer.Filters;

import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
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
	protected void setupFilterScript(RenderScript renderScript, Allocation inAlloc, Allocation outAlloc, float weight) {
		final ScriptC_invert invertScript = new ScriptC_invert(renderScript);
		invertScript.set_weight(weight);
		invertScript.forEach_invert(inAlloc, outAlloc);
	}

}