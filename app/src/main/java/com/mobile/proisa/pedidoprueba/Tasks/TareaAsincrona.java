package com.mobile.proisa.pedidoprueba.Tasks;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.Stack;

public abstract class TareaAsincrona<Params,Progress,Result> extends AsyncTask<Params, Progress, Result> {
    private int id;
    private Activity context;
    private Bundle data;
    private OnFinishedProcess listener;
    private Stack<Exception> exceptions;


    public TareaAsincrona(int id, Activity context, OnFinishedProcess listener) {
        this.id = id;
        this.context = context;
        this.listener = listener;
        this.exceptions = new Stack<>();
        this.data = new Bundle();
    }

    /**
     * Metodo que devuelve el id del proceso
     * @return Devuelve el identificador del proceso que esta en esta instancia
     * */
    public int getId() {
        return id;
    }

    /**
     * Devuelve el contexto en el que se ejecuta el proceso
     * @return la actividad que invocó a este proceso
     */
    public Activity getContext() {
        return context;
    }

    public void putData(Bundle data){ this.data = data;}

    public Bundle getData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public boolean hasErrors(){return (this.exceptions.size()>0)?(true):(false);}

    @Override
    protected void onPostExecute(Result result){
        if(listener != null){
            listener.onFinishedProcess(this);

            if(exceptions.size() > 0){
                listener.onErrorOccurred(getId(),exceptions);
            }
        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();

        if(exceptions.size() > 0){
            listener.onErrorOccurred(getId(),exceptions);
        }
    }

    /**
     *Añade una excepcion a la pila de excepciones
     */
    protected void publishError(Exception e){
        exceptions.add(e);
    }


    /**
     * Interfaz que debe ser implementada en las actividades don corre uno o varios procesos
     */
    public interface OnFinishedProcess{
        /**
        * Metodo que es llamado luego de que una tarea finalice independiente de si ocurrio un
        * error o no*/
        void onFinishedProcess(TareaAsincrona task);
        /**
         * Este metodo es llamado cuando termina el proceso y hay excepciones por leer en la pila de excepciones
         */
        void onErrorOccurred(int id, Stack<Exception> exceptions);
    }


}