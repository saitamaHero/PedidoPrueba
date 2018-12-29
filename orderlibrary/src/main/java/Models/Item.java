package Models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import Models.ColumnsSqlite.ColumnsItem;

public class Item extends SimpleElement implements ITotal, Parcelable, ColumnsItem{
    private double stock;
    private double quantity;
    private double cost;
    private double price;
    private Category category;
    private Unit unit;
    private Uri photo;
    private double taxRate;

    public Item() {
        category = Category.UNKNOWN_CATEGORY;
        unit = Unit.UNKNOWN_UNIT;

    }

    public Item(String id, String name) {
        super(id, name);
        category = Category.UNKNOWN_CATEGORY;
        unit = Unit.UNKNOWN_UNIT;
    }

    protected Item(Parcel in) {
        super(in);
        stock = in.readDouble();
        quantity = in.readDouble();
        price = in.readDouble();
        category = in.readParcelable(Category.class.getClassLoader());
        unit = in.readParcelable(Unit.class.getClassLoader());
        cost = in.readDouble();
        photo = in.readParcelable(Uri.class.getClassLoader());
        taxRate = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(stock);
        dest.writeDouble(quantity);
        dest.writeDouble(price);
        dest.writeParcelable(category, flags);
        dest.writeParcelable(unit, flags);
        dest.writeDouble(cost);
        dest.writeParcelable(photo, flags);
        dest.writeDouble(taxRate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if(category == null) return;
        this.category = category;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        if(unit == null) return;
        this.unit = unit;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public double getTaxRate() {
        return taxRate > 1 ? taxRate / 100.00 : taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getTaxes(){
        return getTotal() * getTaxRate();
    }

    @Override
    public double getTotal() {
        return this.price * this.quantity;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = result * 31 + this.getId().hashCode();
        result = result * 31 + this.getName().hashCode();
        result = result * 31 + this.getCategory().hashCode();
        result = result * 31 + this.getUnit().hashCode();

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        Item item = ((Item)obj);

        if(item == null) return false;

        return getId().compareTo(item.getId()) == 0 && getName().compareTo(item.getName()) == 0;
    }

    @Override
    public String toString() {
        return "Item{"+ "id='" + getId() + '\'' + ", name='" + getName() + '\''  + ", stock=" + stock + ", quantity=" + quantity + ", cost=" + cost + ", price=" + price + ", category=" + category + ", unit=" + unit + '}';
    }
}
