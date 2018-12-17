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
public class Invoice extends SimpleElement implements ITotal, Parcelable, ColumnsSqlite.ColumnsInvoice{
    public enum InvoicePayment {CREDIT, CASH}
    private Date date;
    private List<Item> items;
    private Client client;
    private double balance;
    private double discount;
    private InvoicePayment invoiceType;
    private String comment;
    private int status;
    private String remoteId;

    public Invoice() {
        super();
        this.items = new ArrayList<>();
        this.invoiceType = InvoicePayment.CREDIT;

    }

    public Invoice(Date date, List<Item> items) {
        this.date = date;
        this.items = items;
        this.invoiceType = InvoicePayment.CREDIT;
    }

    public Invoice(String id, String name, Date date) {
        super(id, name);
        this.date = date;
        this.items = new ArrayList<>();
        this.invoiceType = InvoicePayment.CREDIT;
    }

    protected Invoice(Parcel in) {
        super(in);
        items = in.readArrayList(Item.class.getClassLoader());
        client = in.readParcelable(Client.class.getClassLoader());
        balance = in.readDouble();
        discount = in.readDouble();
        invoiceType = InvoicePayment.valueOf(in.readString());
        comment = in.readString();
        remoteId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(items);
        dest.writeParcelable(client, flags);
        dest.writeDouble(balance);
        dest.writeDouble(discount);
        dest.writeString(invoiceType.name());
        dest.writeString(comment);
        dest.writeString(remoteId);
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
        return discount > 1 ? discount / 100.00 : discount;
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

    @Override
    public String toString() {
        return "Invoice{"+ "id=" + getId() + "date=" + date + ", client=" + client + ", discount=" + discount + ", itemsCount=" + this.items.size() +
                ", invoicePayment=" +invoiceType.name() +'}';
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public boolean isPending() {
        return getStatus() == STATUS_PENDING;
    }

    @Override
    public void setRemoteId(Object remote) {
        if(remote != null){
            this.remoteId = remote.toString();
        }
    }

    @Override
    public Object getRemoteId() {
        return this.remoteId;
    }
}
