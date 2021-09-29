package net.samverstraete.dranklijstje;

import java.text.DecimalFormat;
import java.util.ArrayList;

import net.samverstraete.dranklijstje.objects.DrinkArray;
import net.samverstraete.dranklijstje.objects.DrinkItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DrinkListAdapter extends BaseAdapter{
	Context mContext;
	ArrayList<DrinkItem> drinks;

	public DrinkListAdapter(Context c, DrinkArray drinkarray){
		mContext = c;
		this.drinks = new ArrayList<>();
		for(DrinkItem j : drinkarray) {
			if(j.quant>0)this.drinks.add(j);
		}
	}

	@Override
	public int getCount() {
		return drinks.size();
	}

	@Override
	public Object getItem(int position) {
		return drinks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if(convertView==null){
			LayoutInflater li = ((Activity)mContext).getLayoutInflater();
			v = li.inflate(R.layout.listitem, parent,false);
		}
		else
		{
			v = convertView;
		}
		TextView tv1 = v.findViewById(R.id.listitemname);
		tv1.setText(drinks.get(position).name);
		TextView tv2 = v.findViewById(R.id.listitemquant);
		tv2.setText(String.valueOf(drinks.get(position).quant));
		DecimalFormat tdf = new DecimalFormat("0.##");
		TextView tv3 = v.findViewById(R.id.listitemprice);
		tv3.setText(tdf.format(drinks.get(position).price)+" EUR");
		return v;
	}

}
