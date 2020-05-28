package MyObjects;

import MyObjects.Food;

public class Order extends Food {
    private int quantity;
    private String date;

    public Order(){}

    public Order(int quantity, String date)
    {
        this.quantity = quantity;
        this.date = date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
