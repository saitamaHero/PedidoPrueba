package Models;

/**
 * Clase que representa la categoría de un {@link Item}
 * para más detalle vea {@link SimpleElement}
 */
public class Category extends SimpleElement {
    public static final Category UNKNOWN_CATEGORY = new Category("","Desconocido");

    public Category() {
    }

    public Category(String id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return String.format("Category{id='%s' name='%s' }",getId(), getName());
    }



}
