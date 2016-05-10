package com.example.michael.imageblurrer.StateClasses;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.example.michael.imageblurrer.Filters.EffectFilter;

import java.util.ArrayList;

/**
 * Created by michael on 2/27/16.
 */
public class FilterState {

	private Bitmap originalBitmap;
	private EffectFilter selectedFilter;
	private int rotationAngle;
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

		if(this.rotationAngle != 0) {
			final Matrix matrix = new Matrix();
			matrix.setRotate(this.rotationAngle);
			alteredBitmap = Bitmap.createBitmap(alteredBitmap, 0, 0, alteredBitmap.getWidth(), alteredBitmap.getHeight(), matrix, true);
		}
		return alteredBitmap;
	}

	public void setSelectedFilter(EffectFilter filter) {
		this.selectedFilter = filter;
	}

	public EffectFilter getSelectedFilter() {
		return this.selectedFilter;
	}

	public void updateRotationAngle(int angleToAdd) {
		this.rotationAngle = (this.rotationAngle + angleToAdd) % 360;
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
