package com.example.michael.imageblurrer.Utility;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class Utility {

	public static boolean saveImage(ContentResolver cr, Bitmap source, String title, String description) {
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, title);
		values.put(MediaStore.Images.Media.DESCRIPTION, description);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

		Uri url = null;

		try {
			url = cr.insert(MediaStore.Images.Media.getContentUri("external") , values);

			if (source != null) {
				OutputStream imageOut = cr.openOutputStream(url);
				try {
					source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
				} finally {
					imageOut.close();
				}

				long id = ContentUris.parseId(url);
				// Wait until MINI_KIND thumbnail is generated.
				Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
						MediaStore.Images.Thumbnails.MINI_KIND, null);
				// This is for backward compatibility.
				Bitmap microThumb = StoreThumbnail(cr, miniThumb, id, 50F, 50F,
						MediaStore.Images.Thumbnails.MICRO_KIND);
			} else {
				Log.e("Save Image", "Failed to create thumbnail, removing original");
				cr.delete(url, null, null);
				url = null;
			}
		} catch (Exception e) {
			Log.e("Save Image", "Failed to insert image", e);
			if (url != null) {
				cr.delete(url, null, null);
				url = null;
			}
		}

		return url != null;
	}

	private static final Bitmap StoreThumbnail(
			ContentResolver cr,
			Bitmap source,
			long id,
			float width, float height,
			int kind) {
		// create the matrix to scale it
		Matrix matrix = new Matrix();

		float scaleX = width / source.getWidth();
		float scaleY = height / source.getHeight();

		matrix.setScale(scaleX, scaleY);

		Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
				source.getWidth(),
				source.getHeight(), matrix,
				true);

		ContentValues values = new ContentValues(4);
		values.put(MediaStore.Images.Thumbnails.KIND,     kind);
		values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int)id);
		values.put(MediaStore.Images.Thumbnails.HEIGHT,   thumb.getHeight());
		values.put(MediaStore.Images.Thumbnails.WIDTH,    thumb.getWidth());

		Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

		try {
			OutputStream thumbOut = cr.openOutputStream(url);

			thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
			thumbOut.close();
			return thumb;
		}
		catch (FileNotFoundException ex) {
			return null;
		}
		catch (IOException ex) {
			return null;
		}
	}

}
