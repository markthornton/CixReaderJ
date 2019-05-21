package uk.me.mthornton.cix.model;

public class ChangeEvent {
    public enum Type {add, remove, update};

    private final Object object;
    private final Type type;

    public static ChangeEvent add(Object object) {
        return new ChangeEvent(object, Type.add);
    }

    public static ChangeEvent remove(Object object) {
        return new ChangeEvent(object, Type.remove);
    }

    public ChangeEvent(Object object, Type type) {
        this.object = object;
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public Type getType() {
        return type;
    }
}
