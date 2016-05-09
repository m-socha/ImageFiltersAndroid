package com.example.michael.imageblurrer.StateClasses;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.michael.imageblurrer.Filters.EffectFilter;

import java.util.ArrayList;

/**
 * Created by michael on 2/27/16.
 */
public class FilterState {

	private Bitmap originalBitmap;
	private EffectFilter selectedFilter;
	private ArrayList<FilterApplication> filterApplications = new ArrayList<>();

	public static final int FILTER_NOT_FOUND = -1;

	public FilterState(Bitmap bitmap) {
		this.originalBitmap = bitmap;
	}

	private int indexOfApplication(EffectFilter filter) {
		for(int i = 0; i < this.filterApplications.size(); i++) {
			if(this.filterApplications.get(i).filter.equals(filter)) {
				return i;
			}
		}
		return FilterState.FILTER_NOT_FOUND;
	}

	public void updateFilter(float weight) {
		final int filterIndex = this.indexOfApplication(this.selectedFilter);
		if(filterIndex == FilterState.FILTER_NOT_FOUND) {
			this.filterApplications.add(new FilterApplication(this.selectedFilter, weight));
		} else {
			this.filterApplications.get(filterIndex).weight = weight;
		}
	}

	public void removeFilter() {
		final int filterIndex = this.indexOfApplication(this.selectedFilter);
		if(filterIndex != FilterState.FILTER_NOT_FOUND) {
			this.filterApplications.remove(filterIndex);
		}
	}

	public float getAppliedWeight(EffectFilter filter) {
		final int filterIndex = this.indexOfApplication(filter);
		return (filterIndex != FilterState.FILTER_NOT_FOUND) ? this.filterApplications.get(filterIndex).weight : 0f;
	}

	public Bitmap generateBitmap() {
		Bitmap alteredBitmap = Bitmap.createBitmap(this.originalBitmap);
		for(FilterApplication filterApplication : this.filterApplications) {
			if(filterApplication.weight > 0) {
				alteredBitmap = filterApplication.filter.getBitmapFromFilter(alteredBitmap, filterApplication.weight);
			}
		}
		return alteredBitmap;
	}

	public void setSelectedFilter(EffectFilter filter) {
		this.selectedFilter = filter;
	}

	public EffectFilter getSelectedFilter() {
		return this.selectedFilter;
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
