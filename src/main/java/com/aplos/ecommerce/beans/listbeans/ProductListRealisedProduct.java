package com.aplos.ecommerce.beans.listbeans;

public class ProductListRealisedProduct {
	private double price;
	private int quantity;

	public ProductListRealisedProduct( int quantity, double price ) {
		this.quantity = quantity;
		this.price = price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	public double getPrice() {
		return price;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getQuantity() {
		return quantity;
	}
}
