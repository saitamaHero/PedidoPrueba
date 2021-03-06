package Models;

public interface ColumnsSqlite {

    public interface ColumnModification{
        String _LASTMOD = "_last_mod";
    }

    public interface ColumnPhoto{
        String _PHOTO = "_photo";
    }

    /**
     * Columna a implementar en las tablas que lleven comentarios como {@link ColumnsInvoice Invoice}
     */
    public interface ColumnComment{
        String _COMMENT = "_comment";
    }

    /**
     * Representa el estado de un registro en una base de datos local.
     *
     */
    public interface ColumnStatus{
        int STATUS_COMPLETE = -1;
        int STATUS_PENDING = 0;
        String _STATUS = "_status";

        public void setStatus(int status);
        public int getStatus();
        public boolean isPending();
    }

    /**
     * Representa tanto la columnnas o columna remota de la base de datos
     */
    public interface ColumnsRemote extends ColumnStatus{
        String _ID_REMOTE = "_id_remote";
        public void setRemoteId(Object remote);
        public Object getRemoteId();
    }

    /**
     * Columnas de la tabla de articulos
     */
    public interface ColumnsItem extends ColumnModification, ColumnPhoto{
        String TABLE_NAME = "products_table";
        String _ID = "_id";
        String _NAME = "_name";
        String _PRICE = "_price";
        String _CAT = "_category";
        String _UNIT = "_unit";
        String _STOCK = "_stock";
        String _TAX_RATE = "_tax_rate";
        String _COST = "_cost";
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
     * Columnas de la tabla de zonas de clientes
     */
    public interface ColumnsZone extends ColumnModification{
        String TABLE_NAME = "clients_zone";
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
     * Columnas de la tabla de numeros de comprobante fiscal
     */
    public interface ColumnsNcf extends ColumnModification{
        String TABLE_NAME = "clients_ncf";
        String _ID = "_id";
        String _NAME = "_name";
        String _TYPE = "_type";
    }

    /**
     * Columnas base para tabla con campos de persona por ejemplo
     * {@link ColumnsClient Client}
     */
    public interface ColumnsPerson extends ColumnModification, ColumnPhoto{
        String _ID      = "_id";
        String _NAME    = "_name";
        String _IDCARD  = "_id_card"; /*Cedula o RNC*/
        String _BIRTH   = "_birth_date";
        String _ENTERED = "_entered_date";
        String _EMAIL   = "_email";
        String _ADDRESS = "_address";
        String _LAT     = "_latitude";
        String _LNG     = "_longitude";
        String _PHONE   = "_phone";
    }

    /**
     * Columnas de la tabla de clientes, extiende de {@link ColumnsPerson Person}
     */
    public interface ColumnsClient extends ColumnsPerson, ColumnsRemote
    {
        String TABLE_NAME = "clients_table";
        String _CR_LIMIT  = "_credit_limit";
        String _CR_STATUS = "_credit_status";
        String _ZONE_ID   = "_zone_id";
        String _NCF_ID    = "_ncf_id";
    }

    /**
     * Columnas de la tabla de las visitas del {@link Vendor}
     */
    public interface ColumnsDiary extends  ColumnModification, ColumnComment, ColumnsRemote{
        String TABLE_NAME  = "vendor_diary";
        String _ID         = "_id";
        String _EVENT      = "_date_event";
        String _CLIENT_ID  = "_client_id";
        String _DURATION   = "_duration";
        String _START_TIME = "_start_time";
        String _END_TIME   = "_end_time";


        String TABLE_DIARY_INV = "invoices_per_diary";
    }


    /**
     * Columnas de la tabla de articulos
     */
    public interface ColumnsInvoice extends ColumnModification, ColumnComment, ColumnsRemote{
        String TABLE_NAME = "invoice_table";
        String _DATE      = "_creation_date";
        String _ID        = "_id_invoice";
        String _CLIENT    = "_client";
        String _INV_TYPE  = "_invoice_type";
        String _DISCOUNT  = "_discount";
        String _NCF_SEQ   = "_ncf_sequence";
        String _MONEY     = "_money_received";

        //Datos referentes al detalle del articulo
        String TABLE_NAME_DETAILS = "invoice_details";
        String _ITEM_ID           = "_item_id";
        String _ITEM_NAME         = "_item_name";
        String _QTY               = "_quantity";
        String _PRICE             = "_price";
        String _TAX_RATE          = "_tax_rate";

    }


    public interface ColumnsCompany{
        String TABLE_NAME       = "company_info_table";
        String _COMPANY_NAME    = "_company_name";
        String _COMPANY_ADDRESS = "_company_address";
        String _COMPANY_INFO    = "_company_info";
    }



}
