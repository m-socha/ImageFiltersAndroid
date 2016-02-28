package com.example.michael.imageblurrer.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.example.michael.imageblurrer.Filters.BlurFilter;
import com.example.michael.imageblurrer.Filters.EffectFilter;
import com.example.michael.imageblurrer.Filters.InvertFilter;
import com.example.michael.imageblurrer.R;
import com.example.michael.imageblurrer.StateClasses.FilterState;

import java.util.HashMap;
import java.util.Stack;
public class MediaEffectActivity extends Activity implements View.OnTouchListener {

	private final static int LOAD_IMAGE_RESULT = 1;

	private LinearLayout openImageContainer;
	private LinearLayout saveImageContainer;
	private LinearLayout closeImageContainer;
	private LinearLayout exitProgramContainer;

	private ImageView mainImageView;

	private ImageView rotateCwImage;
	private ImageView rotateCcwImage;
	private SeekBar filterWeightSeekBar;

	private HashMap<String, EffectFilter> labelToFilter;
	private Stack<FilterState> stateStack;

	private int rotationAngle = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_effect);

		this.openImageContainer = (LinearLayout) findViewById(R.id.open_image_container);
		this.openImageContainer.setOnTouchListener(this);
		this.saveImageContainer = (LinearLayout) findViewById(R.id.save_image_container);
		this.saveImageContainer.setOnTouchListener(this);
		this.closeImageContainer = (LinearLayout) findViewById(R.id.close_image_container);
		this.closeImageContainer.setOnTouchListener(this);
		this.exitProgramContainer = (LinearLayout) findViewById(R.id.exit_program_container);
		this.exitProgramContainer.setOnTouchListener(this);

		this.mainImageView = (ImageView) findViewById(R.id.main_image_view);

		this.rotateCwImage = (ImageView) findViewById(R.id.rotate_cw_image);
		this.rotateCwImage.setOnTouchListener(this);
		this.rotateCcwImage = (ImageView) findViewById(R.id.rotate_ccw_image);
		this.rotateCcwImage.setOnTouchListener(this);
		this.filterWeightSeekBar = (SeekBar) findViewById(R.id.filter_weight_bar);
		this.filterWeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Log.v("SeekbarProgress", seekBar.getProgress() + "");
			}
		});

		this.stateStack = new Stack<>();

	}

	private void setupLabelToFilterMap() {
		this.labelToFilter = new HashMap<>();
		this.labelToFilter.put(getString(R.string.blur_tab), new BlurFilter());
		this.labelToFilter.put(getString(R.string.invert_tab), new InvertFilter());
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			switch(view.getId()) {
				case R.id.open_image_container:
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, LOAD_IMAGE_RESULT);
					break;

				case R.id.exit_program_container:
					this.exitProgramDialog();
					break;

				case R.id.rotate_cw_image:
					this.updateRotationAngle(90);
					this.updateBitmap();
					break;

				case R.id.rotate_ccw_image:
					this.updateRotationAngle(-90);
					this.updateBitmap();
					break;
			}
			return true;
		} else {
			return false;
		}
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

			Bitmap originalBitmap = BitmapFactory.decodeFile(picturePath);
			this.stateStack.push(new FilterState(originalBitmap));
			this.updateRotationAngle(90);
			this.updateBitmap();
		}
	}

	private void updateBitmap() {
		Bitmap filtersApplied = this.stateStack.peek().generateBitmap();
		Bitmap rotatedBitmap = this.rotateBitmap(filtersApplied);
		filtersApplied.recycle();
		this.mainImageView.setImageBitmap(rotatedBitmap);
	}

	private Bitmap rotateBitmap(Bitmap origBitmap) {
		Matrix matrix = new Matrix();
		matrix.setRotate(this.rotationAngle);
		Bitmap rotatedBitmap = Bitmap.createBitmap(origBitmap, 0, 0, origBitmap.getWidth(), origBitmap.getHeight(), matrix, true);
		return rotatedBitmap;
	}

	private void updateRotationAngle(int angleToAdd) {
		this.rotationAngle = (this.rotationAngle + angleToAdd) % 360;
	}

	private void exitProgramDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.exit_program)
				.setMessage(R.string.exit_program_confirmation)
				.setPositiveButton(R.string.exit_program, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setIcon(android.R.drawable.ic_dialog_alert)
				.show();
	}

}
