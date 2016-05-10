package com.example.michael.imageblurrer.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.michael.imageblurrer.Adapters.NavDrawerListAdapter;
import com.example.michael.imageblurrer.Filters.BlurFilter;
import com.example.michael.imageblurrer.Filters.BrightenFilter;
import com.example.michael.imageblurrer.Filters.DarkenFilter;
import com.example.michael.imageblurrer.Filters.EffectFilter;
import com.example.michael.imageblurrer.Filters.GreyscaleFilter;
import com.example.michael.imageblurrer.Filters.InvertFilter;
import com.example.michael.imageblurrer.Models.NavDrawerItem;
import com.example.michael.imageblurrer.R;
import com.example.michael.imageblurrer.StateClasses.FilterState;
import com.example.michael.imageblurrer.Utility.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MediaEffectActivity extends Activity implements View.OnTouchListener {

	private final static int LOAD_IMAGE_RESULT = 1;

	private DrawerLayout navDrawerLayout;

	private View topBarlayout;
	private View bottomBarLayout;
	private View noImagesSelectedPrompt;

	private LinearLayout tabHolderLayout;

	private ImageView mainImageView;

	private ImageView rotateCwImage;
	private ImageView rotateCcwImage;
	private SeekBar filterWeightSeekBar;

	private final HashMap<Integer, EffectFilter> labelToFilter = new HashMap<>();
	private final Stack<FilterState> stateStack = new Stack<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_effect);

		setupNavDrawer();

		this.topBarlayout = findViewById(R.id.top_bar);
		this.bottomBarLayout = findViewById(R.id.bottom_bar);
		this.noImagesSelectedPrompt = findViewById(R.id.no_images_selected_prompt);
		this.updateVisibility(false);

		this.mainImageView = (ImageView) findViewById(R.id.main_image_view);

		this.rotateCwImage = (ImageView) findViewById(R.id.rotate_cw_image);
		this.rotateCwImage.setOnTouchListener(this);
		this.rotateCcwImage = (ImageView) findViewById(R.id.rotate_ccw_image);
		this.rotateCcwImage.setOnTouchListener(this);
		this.filterWeightSeekBar = (SeekBar) findViewById(R.id.filter_weight_bar);
		this.filterWeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				MediaEffectActivity.this.getCurrentFilterState().updateFilter((float) seekBar.getProgress() / 100);
				MediaEffectActivity.this.updateBitmap();
			}
		});

		this.setupLabelToFilterMap();
		this.setupFilterTabViews();
	}

	private void setupNavDrawer() {
		this.navDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
		final ListView navDrawerListView = (ListView) findViewById(R.id.nav_drawer_list);
		final ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>(Arrays.asList(
				new NavDrawerItem(R.string.open_image, R.drawable.open_icon),
				new NavDrawerItem(R.string.save_image, R.drawable.save_icon),
				new NavDrawerItem(R.string.close_image, R.drawable.close_icon)
		));
		final NavDrawerListAdapter navDrawerListAdapter = new NavDrawerListAdapter(this, navDrawerItems);
		navDrawerListView.setAdapter(navDrawerListAdapter);
		navDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View navDrawerItemView, int position, long id) {
				final NavDrawerItem navDrawerItem = navDrawerItems.get(position);
				switch (navDrawerItem.headingId) {
					case R.string.open_image:
						final Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(intent, LOAD_IMAGE_RESULT);
						break;

					case R.string.save_image:
						if (mainImageView.getDrawable() != null && !stateStack.empty()) {
							final Bitmap displayedBitmap = ((BitmapDrawable) mainImageView.getDrawable()).getBitmap();
							String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
							final boolean saveSuccess = Utility.saveImage(getContentResolver(), displayedBitmap, "ImageFilterer" + timeStamp + ".jpg",
									String.format(getString(R.string.saved_image_desc), timeStamp.toString()));
							new AlertDialog.Builder(MediaEffectActivity.this)
									.setTitle(R.string.saving_image)
									.setMessage(saveSuccess ? R.string.save_success : R.string.save_failed)
									.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel();
										}
									})
									.setIcon(android.R.drawable.ic_dialog_alert)
									.show();
						}
						break;

					case R.string.close_image:
						if (!stateStack.empty()) {
							stateStack.pop();
							if (!stateStack.empty()) {
								final FilterState filterState = getCurrentFilterState();
								updateForFilter(filterState, filterState.getSelectedFilter());
								updateBitmap();
							} else {
								updateVisibility(false);
								mainImageView.setImageBitmap(null);
								openNavDrawer();
							}
						}
						break;
				}
			}
		});
		this.openNavDrawer();
	}

	private void setupLabelToFilterMap() {
		this.labelToFilter.put(R.id.blur_filter_tab, BlurFilter.getInstance());
		this.labelToFilter.put(R.id.greyscale_filter_tab, GreyscaleFilter.getInstance());
		this.labelToFilter.put(R.id.brighten_filter_tab, BrightenFilter.getInstance());
		this.labelToFilter.put(R.id.darken_filter_tab, DarkenFilter.getInstance());
		this.labelToFilter.put(R.id.invert_filter_tab, InvertFilter.getInstance());
	}

	private void setupFilterTabViews() {
		this.tabHolderLayout = (LinearLayout) findViewById(R.id.tab_holder_layout);
		for(int i = 0; i < this.tabHolderLayout.getChildCount(); i++) {
			this.tabHolderLayout.getChildAt(i).setOnTouchListener(this);
		}
	}

	private void updateForFilter(int filterId) {
		final FilterState currentState = this.getCurrentFilterState();
		final EffectFilter newFilter = this.labelToFilter.get(filterId);
		this.updateForFilter(currentState, newFilter);
	}

	private void updateForFilter(FilterState currentState, EffectFilter newFilter) {
		currentState.setSelectedFilter(newFilter);

		for(int i = 0; i < this.tabHolderLayout.getChildCount(); i++) {
			final TextView tab = (TextView) this.tabHolderLayout.getChildAt(i);
			tab.setBackgroundColor((tab.getId() == this.getIdByFilter(newFilter) ? ContextCompat.getColor(this, R.color.filter_tab_selected_background)
					: ContextCompat.getColor(this, R.color.filter_tab_background)));
		}

		this.filterWeightSeekBar.setProgress(Math.round(currentState.getAppliedWeight(newFilter) * 100));
	}

	private int getIdByFilter(EffectFilter filter) {
		for(Map.Entry<Integer, EffectFilter> entry : this.labelToFilter.entrySet()) {
			if(entry.getValue().equals(filter)) {
				return entry.getKey();
			}
		}
		return FilterState.FILTER_NOT_FOUND;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			switch(view.getId()) {
				case R.id.rotate_cw_image:
					this.getCurrentFilterState().updateRotationAngle(90);
					this.updateBitmap();
					break;

				case R.id.rotate_ccw_image:
					this.getCurrentFilterState().updateRotationAngle(-90);
					this.updateBitmap();
					break;

				case R.id.blur_filter_tab:
				case R.id.brighten_filter_tab:
				case R.id.darken_filter_tab:
				case R.id.greyscale_filter_tab:
				case R.id.invert_filter_tab:
					this.updateForFilter(view.getId());
					break;
			}
			return true;
		} else {
			return false;
		}
	}

	private FilterState getCurrentFilterState() {
		return this.stateStack.peek();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == LOAD_IMAGE_RESULT && resultCode == RESULT_OK && data != null) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			this.closeNavDrawer();

			Bitmap originalBitmap = BitmapFactory.decodeFile(picturePath);
			final FilterState filterState = new FilterState(originalBitmap);
			this.stateStack.push(filterState);
			this.updateBitmap();
			this.updateVisibility(true);
			this.updateForFilter(this.getCurrentFilterState(), BlurFilter.getInstance());
		}
	}

	private void updateBitmap() {
		this.getCurrentFilterState().generateAndShowBitmap(this.mainImageView);
	}

	private void updateVisibility(boolean imageSelected) {
		this.topBarlayout.setVisibility(imageSelected ? View.VISIBLE : View.INVISIBLE);
		this.bottomBarLayout.setVisibility(imageSelected ? View.VISIBLE : View.INVISIBLE);
		this.noImagesSelectedPrompt.setVisibility(imageSelected ? View.INVISIBLE : View.VISIBLE);
	}

	private void openNavDrawer() {
		this.navDrawerLayout.openDrawer(Gravity.LEFT);
	}

	private void closeNavDrawer() {
		this.navDrawerLayout.closeDrawer(Gravity.LEFT);
	}

}
