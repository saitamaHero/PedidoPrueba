package Models;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dionicio on 25/11/18.
 */

/**
 * Clase para facturas y demás.
 *
 * Para más información vea {@link SimpleElement}
 */
public class Invoice extends SimpleElement implements ITotal, Parcelable{
    public static final int CREDIT = 0000000;
    public static final int CASH = 1111;
    private Date date;
    private List<Item> items;
    private Client client;
    private int balance;
    private double discount;
    private int invoiceType;

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

    public boolean isPaid(){
        return this.balance == 0.0;
    }

    public boolean isCredit(){
        return this.invoiceType == CREDIT;
    }

    public boolean isCash(){
        return this.invoiceType == CASH;
    }


    @Override
    public double getTotal() {
        double total = 0.0;

        for (Item i : items) {
            total += i.getTotal();
        }
        return total;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();

        result = result + client.hashCode();
        result = result + date.hashCode();

        return result;
    }
}
