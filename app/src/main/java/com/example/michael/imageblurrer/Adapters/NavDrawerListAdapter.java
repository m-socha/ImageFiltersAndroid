package com.example.michael.imageblurrer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.michael.imageblurrer.Core.ImageFilterApplication;
import com.example.michael.imageblurrer.Models.NavDrawerItem;
import com.example.michael.imageblurrer.R;

import java.util.ArrayList;

public class NavDrawerListAdapter extends ArrayAdapter<NavDrawerItem> {

	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
		super(context, 0, navDrawerItems);
	}

	@Override
	public View getView(int position, View navItemView, ViewGroup parent) {
		final NavDrawerItem navDrawerItem = getItem(position);
		if (navItemView == null) {
			navItemView = LayoutInflater.from(getContext()).inflate(R.layout.nav_drawer_item, parent, false);
		}
		final ImageView navImage = (ImageView) navItemView.findViewById(R.id.nav_image);
		final TextView navHeading = (TextView) navItemView.findViewById(R.id.nav_heading);
//		navImage.setImageResource(ImageFilterApplication.getAppInstance().getAppContext().getDrawable(1));
		navHeading.setText(navDrawerItem.headingId);
		return navItemView;
	}

}
