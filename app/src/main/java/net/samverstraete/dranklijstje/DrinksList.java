package net.samverstraete.dranklijstje;

import java.text.DecimalFormat;

import net.samverstraete.dranklijstje.objects.DrinkArray;
import net.samverstraete.dranklijstje.objects.DrinkItem;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class DrinksList extends AppCompatActivity {
	ListView listview;
	TextView totaal;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.list);
    	DrinkArray drinkarray = getIntent().getParcelableExtra("drinks");
        listview = findViewById(R.id.thelist);
        listview.setAdapter(new DrinkListAdapter(this, drinkarray));
        totaal = findViewById(R.id.footer);
        double total=0;
        int q=0;
        for(DrinkItem i:drinkarray){
        	total += i.quant * i.price;
        	q += i.quant;
        }
        DecimalFormat tdf = new DecimalFormat("0.##");
        totaal.setText("Totaal: "+ q +" consumpties, "+tdf.format(total)+" EUR");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.listmenu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(item.getItemId() == R.id.menuclear) {
			setResult(RESULT_OK);
			finish();
		} else if (item.getItemId() == android.R.id.home) {
			setResult(RESULT_CANCELED);
			finish();
		} else {
	        Log.e("DrinksList menu", "Unknown menu selection!");
	    }
	    return true;
	}
}
