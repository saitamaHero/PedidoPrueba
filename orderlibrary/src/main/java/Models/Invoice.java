package Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dionicio on 25/11/18.
 */

/**
 * Clase para facturas y demás
 */
public class Invoice extends SimpleElement implements ITotal {
    private Date date;
    private List<Item> items;

    public Invoice(Date date, List<Item> items) {
        this.date = date;
        this.items = items;
    }

    public Invoice(String id, String name, Date date) {
        super(id, name);
        this.date = date;
        this.items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean setItems(List<Item> items) {
        return this.items.addAll(items);
    }

    public boolean containsItem(Item i){
        return this.items.contains(i);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public double getTotal() {
        double total = 0.0;

        for (Item i : items) {
            total += i.getTotal();
        }
        return total;
    }
}
