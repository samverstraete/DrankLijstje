package net.samverstraete.dranklijstje;

import java.util.ArrayList;

import net.samverstraete.dranklijstje.objects.DrinkItem;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrinkSelAdapter extends BaseAdapter{
	Context mContext;
	ArrayList<DrinkItem> drinks;

	public DrinkSelAdapter(Context c, ArrayList<DrinkItem> drinks){
		mContext = c;
		this.drinks = drinks;
	}
	@Override
	public int getCount() {
		return drinks.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v;
		if(convertView==null){
			LayoutInflater li = ((Activity)mContext).getLayoutInflater();
			v = li.inflate(R.layout.item, parent,false);
		} else {
			v = convertView;
		}
		
		TextView tv = v.findViewById(R.id.icon_text);
		tv.setText(drinks.get(position).name);
		ImageView iv = v.findViewById(R.id.icon_image);

		try {
			iv.setImageResource(mContext.getResources().getIdentifier(
					drinks.get(position).icon,
					"drawable",
					null));
		} catch(NullPointerException | Resources.NotFoundException nfe) {
			iv.setImageResource(R.drawable.klj);
		}
		TextView q = v.findViewById(R.id.icon_quant);
		if(drinks.get(position).quant>0)
			q.setText(String.valueOf(drinks.get(position).quant));
		else 
			q.setText("");
		return v;
	}
	@Override
	public Object getItem(int position) {
		return drinks.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	
}
