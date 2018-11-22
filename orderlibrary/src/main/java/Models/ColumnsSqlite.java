package Models;

public interface ColumnsSqlite {

    public interface ColumnModification{
        String _LASTMOD = "_last_mod";
    }

    public interface ColumnsItem extends ColumnModification{
        String _ID = "_id";
        String _NAME = "_name";
        String _QTY = "_quantity";
        String _COST = "_cost";
        String _PRICE = "_price";
        String _CAT = "_category";
        String _UNIT = "_unit";
        String _STOCK = "_stock";
        String _BCODE = "_barcode";
    }


    public interface ColumnsPerson extends ColumnModification{
        String _ID = "_id";
        String _NAME = "_name";
        String _IDCARD = "_id_card";
        String _BIRTH = "_birth";
        String _ENTERED = "_entered_date";
        String _EMAIL = "_email";
        String _ADDRESS = "_address";
        String _LAT = "_latitude";
        String _LNG = "_longitude";


    }



}
