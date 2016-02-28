package com.example.michael.imageblurrer.StateClasses;

import android.graphics.Bitmap;

import com.example.michael.imageblurrer.Filters.EffectFilter;

import java.util.ArrayList;

/**
 * Created by michael on 2/27/16.
 */
public class FilterState {

	private Bitmap originalBitmap;

	ArrayList<FilterApplication> filterApplications;

	public FilterState(Bitmap bitmap) {
		this.originalBitmap = bitmap;
		this.filterApplications = new ArrayList<>();
	}

	private void updateFilterHistory(EffectFilter filter, Float weight) {
		boolean filterApplied = false;
		for(FilterApplication filterApplication : this.filterApplications) {
			if(filterApplication.filter.equals(filter)) {
				if(weight > 0) {
					filterApplication.weight = weight;
				} else {
					this.filterApplications.remove(filterApplication);
				}
				filterApplied = true;
				break;
			}
		}

		if(!filterApplied && weight > 0) {
			this.filterApplications.add(new FilterApplication(filter, weight));
		}
	}

	public Bitmap generateBitmap() {
		Bitmap alteredBitmap = Bitmap.createBitmap(this.originalBitmap);
		for(FilterApplication filterApplication : this.filterApplications) {
			Bitmap oldBitmap = alteredBitmap;
			alteredBitmap = filterApplication.filter.getBitmapFromFilter(alteredBitmap, filterApplication.weight);
			oldBitmap.recycle();
		}
		return alteredBitmap;
	}

	private class FilterApplication {

		public FilterApplication(EffectFilter filter, float weight) {
			this.filter = filter;
			this.weight = weight;
		}

		private EffectFilter filter;
		private float weight;
	}

}
