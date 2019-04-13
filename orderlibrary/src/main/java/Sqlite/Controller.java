package Sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador básico para acciones crud para {@link SQLiteDatabase}
 */

public abstract class Controller<T> {
    private SQLiteDatabase sqLiteDatabase;

    public Controller(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    public abstract List<T> getAll();

    public List<T> getAll(int count){
        return null;
    }

    public List<T> getAllLike(String likeStr){
        return null;
    }

    public List<T> getAllById(Object id){return null;}

    public List<T> getAllRange(Object id, String lower, String upper){
        return null;
    }

    public abstract T getById(Object id);

    public T getById(String field, Object id){
        return null;
    }

    public List<T> getAll(String selection, String[] selectionArgs){
        return null;
    }

    public Cursor query(String[] columns,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String orderBy, String limit){

        return null;
    }


    public boolean exists(String field, Object object){
        throw new UnsupportedOperationException("Not Implmented, yet");
    }

    /**
     * Obtains data from {@link Cursor}  for type T
     * @param cursor
     * @return a object filled from cursor
     */
    public T getDataFromCursor(Cursor cursor){
        return null;
    }

    /**
     * Override this method for create a contentValues
     * @param item for create the {@link ContentValues}
     * @return
     */
    public ContentValues getContentValues(T item){
        return new ContentValues();
    }

    /*Operations with single item*/
    public abstract boolean update(T item);
    public abstract boolean insert(T item);
    public abstract boolean delete(T item);

    /*Operations with multiple items*/
    public abstract boolean insertAll(List<T> item);
    public abstract boolean deleteAll(List<T> item);

    public void closeDatabase(){
        sqLiteDatabase.close();
    }



    public interface OnResultListener{
        int ACTION_INSERT_SINGLE = 1;
        int ACTION_INSERT_MULTIPLE = 2;
        int ACTION_UPDATE = 3;
        int ACTION_DELETE_SINGLE = 4;
        int ACTION_DELETE_MULTIPLE = 5;

        void onSuccessfull(int actionType, int rows);
        void onError(int actionType, int errorCode);
    }

    public static class ConditionCreator{
        private List<Condition> conditions;

        public ConditionCreator() {
            conditions = new ArrayList<>();
        }

        public ConditionCreator add(String field, String value)
        {
            this.conditions.add(new Condition(field,value));
            return this;
        }

        public ConditionCreator add(String field, String value, String operator)
        {
            this.conditions.add(new Condition(field,value, operator, ""));
            return this;
        }

        public ConditionCreator add(Condition condition)
        {
            this.conditions.add(condition);
            return this;
        }


        public String build(){
            String format = "";

            for(int i = 0; i < conditions.size(); i++)
            {
                Condition c = conditions.get(i);

                if(i + 1 < conditions.size())
                {
                    format = format.concat(
                            String.format(" %s %s %s",
                                    c.getField(), c.getOperator(), c.getConcatOperator()));
                }else{
                    format = format.concat(
                            String.format(" %s %s;",
                                    c.getField(), c.getOperator()));
                }

            }


            return format;
        }

    }


    public static class Condition{
        private String field;
        private String value;
        private String operator;
        private String concatOperator;

        public Condition(String field, String value) {
            this.field = field;
            this.value = value;
            this.operator = "=";
            this.concatOperator = "and";
        }

        public Condition(String field, String value, String operator, String concatOperator) {
            this.field = field;
            this.value = value;
            this.operator = operator;
            this.concatOperator = concatOperator;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getConcatOperator() {
            return concatOperator;
        }

        public void setConcatOperator(String concatOperator) {
            this.concatOperator = concatOperator;
        }
    }
}
