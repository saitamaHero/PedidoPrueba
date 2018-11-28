package Models;

public interface ColumnsSqlite {
    String DBNAME = "appcontaproruteros.db";

    public interface ColumnModification{
        String _LASTMOD = "_last_mod";
    }

    public interface ColumnPhoto{
        String _PHOTO = "_photo";
    }

    /**
     * Columnas de la tabla de articulos
     */
    public interface ColumnsItem extends ColumnModification, ColumnPhoto{
        String TABLE_NAME = "products_table";
        String _ID = "_id";
        String _NAME = "_name";
        String _QTY = "_quantity";
        String _COST = "_cost";
        String _PRICE = "_price";
        String _CAT = "_category";
        String _UNIT = "_unit";
        String _STOCK = "_stock";
        //String _BCODE = "_barcode";
    }

    /**
     * Columnas de la tabla de categorías o Departamentos
     */
    public interface ColumnsCategory extends ColumnModification{
        String TABLE_NAME = "products_category";
        String _ID = "_id";
        String _NAME = "_name";
    }

    /**
     * Columnas de la tabla de unidades de un producto
     */
    public interface ColumnsUnit extends ColumnModification{
        String TABLE_NAME = "products_unit";
        String _ID = "_id";
        String _NAME = "_name";
    }


    /**
     * Columnas base para tabla con campos de persona por ejemplo
     * {@link ColumnsClient}
     */
    public interface ColumnsPerson extends ColumnModification{
        String _ID = "_id";
        String _NAME = "_name";
        String _IDCARD = "_id_card"; /*Cedula o RNC*/
        String _BIRTH = "_birth_date";
        String _ENTERED = "_entered_date";
        String _EMAIL = "_email";
        String _ADDRESS = "_address";
        String _LAT = "_latitude";
        String _LNG = "_longitude";
    }

    /**
     * Columnas de la tabla de clientes, extiende de {@link ColumnsPerson}
     */
    public interface ColumnsClient extends ColumnsPerson
    {
        String TABLE_NAME = "clients_table";
        String _CR_LIMIT = "_credit_limit";
    }

    /**
     * Columnas de la tabla de las visitas del {@link Vendor}
     */
    public interface ColumnsDiary extends  ColumnModification{
        String TABLE_NAME = "vendor_diary";
        String _ID = "_id";
        String _EVENT = "_date_event";
        String _COMMENT = "_comment";
    }



}
