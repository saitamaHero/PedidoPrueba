package Models;

import android.os.Parcel;
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
    public enum InvoicePayment{CREDIT, CASH}
    private Date date;
    private List<Item> items;
    private Client client;
    private double balance;
    private double discount;
    private InvoicePayment invoiceType;
    private String comment;

    public Invoice(Date date, List<Item> items) {
        this.date = date;
        this.items = items;
    }

    public Invoice(String id, String name, Date date) {
        super(id, name);
        this.date = date;
        this.items = new ArrayList<>();
    }

    protected Invoice(Parcel in) {
        super(in);
        items = in.createTypedArrayList(Item.CREATOR);
        client = in.readParcelable(Client.class.getClassLoader());
        balance = in.readDouble();
        discount = in.readDouble();
        invoiceType = InvoicePayment.values()[in.readInt()];
        comment = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(items);
        dest.writeParcelable(client, flags);
        dest.writeDouble(balance);
        dest.writeDouble(discount);
        dest.writeInt(invoiceType.ordinal());
        dest.writeString(comment);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Invoice> CREATOR = new Creator<Invoice>() {
        @Override
        public Invoice createFromParcel(Parcel in) {
            return new Invoice(in);
        }

        @Override
        public Invoice[] newArray(int size) {
            return new Invoice[size];
        }
    };

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
        return this.invoiceType == InvoicePayment.CREDIT;
    }

    public boolean isCash(){
        return this.invoiceType == InvoicePayment.CASH;
    }

    public Client getClient() {
        return client;
    }

    public double getBalance() {
        return balance;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public InvoicePayment getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoicePayment invoiceType) {
        this.invoiceType = invoiceType;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
