package Models;

/**
 * Clase que representa la unidad de un {@link Item}
 * para más detalle vea {@link SimpleElement}
 */
public class Unit extends SimpleElement {
    public static final Unit UNKNOWN_UNIT = new Unit("","Desconocida");


    public Unit() {
    }

    public Unit(String id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return String.format("Unit{id='%s' name='%s' }", getId(), getName());
    }
}
