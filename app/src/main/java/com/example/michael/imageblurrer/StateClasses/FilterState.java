package com.example.michael.imageblurrer.StateClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.michael.imageblurrer.Filters.EffectFilter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by michael on 2/27/16.
 */
public class FilterState {

	private final static int MAX_IMAGE_DIM = 1024;

	private Bitmap originalBitmap;
	private EffectFilter selectedFilter;
	private int rotationAngle;
	private ArrayList<FilterApplication> filterApplications = new ArrayList<>();

	public static final int FILTER_NOT_FOUND = -1;

	public FilterState(Bitmap bitmap) {
		this.originalBitmap = bitmap;
		final float maxDimRatio = (float) Math.max(this.originalBitmap.getWidth(), this.originalBitmap.getHeight()) / MAX_IMAGE_DIM;
		if(maxDimRatio > 1) {
			final Bitmap unscaledBitmap = this.originalBitmap;
			this.originalBitmap = this.originalBitmap.createScaledBitmap(this.originalBitmap, Math.round(this.originalBitmap.getWidth() / maxDimRatio), Math.round(this.originalBitmap.getHeight() / maxDimRatio),  false);
			if(unscaledBitmap != originalBitmap) {
				unscaledBitmap.recycle();
			}
		}
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

	public float getAppliedWeight(EffectFilter filter) {
		final int filterIndex = this.indexOfApplication(filter);
		return (filterIndex != FilterState.FILTER_NOT_FOUND) ? this.filterApplications.get(filterIndex).weight : 0f;
	}

	public void generateAndShowBitmap(ImageView imageView) {
		try {
			new BitmapGenerationTask(imageView).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private class BitmapGenerationTask extends AsyncTask<Void, Void, Bitmap> {
		private WeakReference<ImageView> imageViewWeakReference;

		public BitmapGenerationTask(ImageView imageView) {
			this.imageViewWeakReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(Void... imageView) {
			Bitmap alteredBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
			for(final FilterApplication filterApplication : filterApplications) {
				if(filterApplication.weight > 0) {
					final Bitmap previousBitmap = alteredBitmap;
					alteredBitmap = filterApplication.filter.getBitmapFromFilter(previousBitmap, filterApplication.weight);
				}
			}

			if(rotationAngle != 0) {
				final Matrix matrix = new Matrix();
				matrix.setRotate(rotationAngle);
				final Bitmap previousBitmap = alteredBitmap;
				alteredBitmap = Bitmap.createBitmap(alteredBitmap, 0, 0, alteredBitmap.getWidth(), alteredBitmap.getHeight(), matrix, true);
				if(previousBitmap != alteredBitmap) {
					previousBitmap.recycle();
				}
			}
			return alteredBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			final ImageView imageView = this.imageViewWeakReference.get();
			if(imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}

	}

}
