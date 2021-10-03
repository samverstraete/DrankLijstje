package net.samverstraete.dranklijstje;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import net.samverstraete.dranklijstje.objects.DrinkArray;
import net.samverstraete.dranklijstje.objects.DrinkItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.DecimalFormat;

public class DrinksSelector  extends AppCompatActivity {
	GridView gridview;
	DrinkArray drinks;

	static final String FILENAME = "DrinksList";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if(drinks==null){
			if(savedInstanceState!=null) {
				// Restore drinks from memory
				Log.d("DrankLijstje", "Restore drinks from memory...");
				drinks = savedInstanceState.getParcelable("drinks");
			} else {
				// Restore drinks from file
				Log.d("DrankLijstje", "Restore drinks from file...");
				try {
					FileInputStream fis = openFileInput(FILENAME);
					ObjectInputStream ois = new ObjectInputStream(fis);
					drinks = (DrinkArray) ois.readObject();
					ois.close();
					fis.close();
				} catch (FileNotFoundException e) {
					drinks = createNewDrinksList();	
				} catch (StreamCorruptedException e) {
					drinks = createNewDrinksList();
				} catch (IOException e) {
					drinks = createNewDrinksList();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} else {
			Log.d("DrankLijstje", "app still active, not loading drinks...");
		}

		// Start building the view
		gridview = findViewById(R.id.gridview);
		gridview.setAdapter(new DrinkSelAdapter(this,drinks));

		gridview.setOnItemClickListener((parent, v, position, id) -> {
			drinks.get(position).quant++;
			updateItemValue(position,v);
			Log.d("onClick", "position ["+position+"]");
		});
		registerForContextMenu(gridview);

		Button bt = findViewById(R.id.btnMakeList);
		bt.setOnClickListener(v -> gotoList());
	}

	private DrinkArray createNewDrinksList() {
		Resources res = this.getResources();
		String[] drinknames = res.getStringArray(R.array.KLJDrankNamen);
		TypedArray drinklogos = res.obtainTypedArray(R.array.KLJDrankLogos);
        TypedArray drinkprices = res.obtainTypedArray(R.array.KLJDrankPrijzen);

		DrinkArray drinklist = new DrinkArray();
		for(int i = 0; i<drinknames.length;i++){
			DrinkItem di = new DrinkItem(
					drinknames[i],
					res.getResourceName(drinklogos.getResourceId(i, R.drawable.klj)),
					drinkprices.getFloat(i,0.80f),0);
			drinklist.add(i,di);
		}

		drinklogos.recycle();
		drinkprices.recycle();
		return drinklist;
	}

	@Override
	protected void onStop(){
		super.onStop();

		// Commit drinks to file
		FileOutputStream fos;
		try {
			fos = openFileOutput(FILENAME,0);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(drinks);
			oos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		if (item.getItemId() == R.id.substract) {
			if (drinks.get(info.position).quant > 0) drinks.get(info.position).quant--;
			updateItemValue(info.position, info.targetView);
			Log.d("substract", "position [" + info.position + "]");
			return true;
		} else if (item.getItemId() == R.id.change) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.edit, null);
			EditText name = view.findViewById(R.id.editName);
			EditText price = view.findViewById(R.id.editPrice);
			builder.setView(view)
					.setTitle(R.string.verander)
					.setPositiveButton(R.string.opslaan, (dialog, id) -> {
						drinks.get(info.position).name = name.getText().toString();
						String localePrice = price.getText().toString().replace(",",".");
						try {
							drinks.get(info.position).price = Float.parseFloat(localePrice);
						} catch (NumberFormatException ignored) {}
						gridview.invalidateViews();
					})
					.setNegativeButton(R.string.annuleren, (dialog, id) -> {});
			AlertDialog dialog = builder.create();

			name.setText(drinks.get(info.position).name);
			DecimalFormat tdf = new DecimalFormat("0.##");
			price.setText(tdf.format(drinks.get(info.position).price));
			dialog.show();

			return true;
		} else if (item.getItemId() == R.id.remove) {
			Log.d("remove", "Removing pos " + info.position+ " drink: " + drinks.get(info.position).name);
			drinks.remove(info.position);
			gridview.invalidateViews();
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.selectorcontext, menu);
	}

	public void gotoList(){
		Intent myIntent = new Intent(this,DrinksList.class);
		Bundle b = new Bundle();
		b.putParcelable("drinks", drinks);
		myIntent.putExtras(b);
		drinksListResultLauncher.launch(myIntent);
	}

	ActivityResultLauncher<Intent> drinksListResultLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
				if (result.getResultCode() == Activity.RESULT_OK) {
					for (int i = 0; i < drinks.size(); i++) drinks.get(i).quant = 0;
					gridview.invalidateViews();
				}
			});

	protected void updateItemValue(int position, View v) {
		ViewGroup vg = (ViewGroup) v;
		//TODO: dit moet beter kunnen
		TextView q = vg.getChildAt(0).findViewById(R.id.icon_quant);
		if(drinks.get(position).quant==0)q.setText("");
		else q.setText(String.valueOf(drinks.get(position).quant));
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putParcelable("drinks", drinks);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menulist) {
			gotoList();
		} else if (item.getItemId() == R.id.menuclear) {
			for (int i = 0; i < drinks.size(); i++) drinks.get(i).quant = 0;
			gridview.invalidateViews();
		} else if (item.getItemId() == R.id.menutoevoegen){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = LayoutInflater.from(this.getBaseContext()).inflate(R.layout.edit, null);
			EditText name = view.findViewById(R.id.editName);
			EditText price = view.findViewById(R.id.editPrice);
			builder.setView(view)
					.setTitle(R.string.toevoegen)
					.setPositiveButton(R.string.opslaan, (dialog, id) -> {
						String localePrice = price.getText().toString().replace(",",".");
						float newprice = 0f;
						try {
							newprice = Float.parseFloat(localePrice);
						} catch (NumberFormatException ignored) {}
						drinks.add(new DrinkItem(
								name.getText().toString(),
								this.getResources().getResourceName(R.drawable.klj),
								newprice,
								0));
						gridview.invalidateViews();
					})
					.setNegativeButton(R.string.annuleren, (dialog, id) -> {});
			AlertDialog dialog = builder.create();
			dialog.show();
		} else if (item.getItemId() == R.id.menufullreset){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.bevestiglistreset)
					.setTitle(R.string.listreset)
					.setPositiveButton(R.string.ok, (dialog, id) -> {
						drinks = createNewDrinksList();
						gridview.setAdapter(new DrinkSelAdapter(this,drinks));
						gridview.invalidateViews();
					})
					.setNegativeButton(R.string.annuleren, (dialog, id) -> {});
			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
            Log.e("DrinksSelector menu", "Unknown menu selection!");
        }
		return true;
	}

}