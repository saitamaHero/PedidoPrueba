package Models;

public interface Updatable<T> {
    /**
     * Actualiza un objeto con otro objeto del tipo deseado
     * @param item
     * @return verdadero si el elemento ha sido actualizado correctamente, falso en caso contrario.
     */
    public boolean update(T item);
}
