package net.samverstraete.dranklijstje.objects;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DrinkArray extends ArrayList<DrinkItem> implements Parcelable{
	private static final long serialVersionUID = -4618778561416388826L;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
        int size = this.size();
        //We have to write the list size, we need him recreating the list
        dest.writeInt(size);

        for (int i = 0; i < size; i++) {
                DrinkItem c = this.get(i);
        		dest.writeString(c.name);
        		dest.writeInt(c.iconpos);
        		dest.writeFloat(c.price);
        		dest.writeInt(c.quant);
        }
	}
	
	private DrinkArray(Parcel in) {
        this.clear();       
        //First we have to read the list size
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
             DrinkItem c = new DrinkItem(in.readString(),in.readInt(),in.readFloat(),in.readInt());
                this.add(c);
        }
	}
	
	public DrinkArray() {
		
	}

	public static final Parcelable.Creator<DrinkArray> CREATOR = new Parcelable.Creator<DrinkArray>() {
		public DrinkArray createFromParcel(Parcel in) {
			return new DrinkArray(in);
		}

		public DrinkArray[] newArray(int size) {
			return new DrinkArray[size];
		}
	};

}
