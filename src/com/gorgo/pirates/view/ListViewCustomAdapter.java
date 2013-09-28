package com.gorgo.pirates.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gorgo.pirates.R;

/**
 * @author Gorgo
 * 
 * Custom Adapter per visualizzare la ListView
 * 
 */

public class ListViewCustomAdapter extends BaseAdapter {
	public SparseArray<String> sp;

	public Context context;
	public LayoutInflater inflater;

	// Costruttore per SparseArray (Insulti/ControInsulti)
	public ListViewCustomAdapter(Context context, SparseArray<String> sp) {
		super();
		this.context = context;
		
		this.sp = sp;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// Costruttore per Array (Dialoghi)
	public ListViewCustomAdapter(Context context, String[] stringArray) {
		super();
		this.context = context;
		
		SparseArray<String> sp_temp = new SparseArray<String>();
		for (int i = 0; i < stringArray.length; i++)
			sp_temp.put(i, stringArray[i]);

		this.sp = sp_temp;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return sp.size();
	}

	@Override
	public Object getItem(int position) {
		return sp.valueAt(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public static class ViewHolder {
		TextView txtViewTitle;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Typeface chops = Typeface.createFromAsset(this.context.getAssets(),
				"Commodore.ttf"); // Font Commodore 64

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			
			// Inflate del Layout della riga
			convertView = inflater.inflate(R.layout.listitem_row, null);

			holder.txtViewTitle = (TextView) convertView
					.findViewById(R.id.txtViewTitle);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.txtViewTitle.setText(sp.valueAt(position));
		holder.txtViewTitle.setTypeface(chops);
		holder.txtViewTitle.setTextColor(Color.CYAN);
		holder.txtViewTitle.setShadowLayer(10, 5, 5, Color.BLACK);
		holder.txtViewTitle.measure(MeasureSpec.makeMeasureSpec(
				convertView.getWidth() - 5, MeasureSpec.EXACTLY), MeasureSpec
				.makeMeasureSpec(convertView.getHeight(), MeasureSpec.EXACTLY));
		holder.txtViewTitle.layout(0, 0,
				holder.txtViewTitle.getMeasuredWidth(),
				holder.txtViewTitle.getMeasuredHeight());
		holder.txtViewTitle.setTextSize(13);

		return convertView;
	}
}
