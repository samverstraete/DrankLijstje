package net.samverstraete.dranklijstje.objects;

import java.io.Serializable;

public class DrinkItem implements Serializable{
	private static final long serialVersionUID = 6695634023460962L;
	public String name;
	public float price;
	public transient int quant;
	public int iconpos; 
	public DrinkItem(String name, int icon, float price, int quant) {
		this.name = name;
		this.price = price;
		this.quant = quant;
		this.iconpos = icon;
	}
}
