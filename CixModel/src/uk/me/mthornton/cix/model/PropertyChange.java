package uk.me.mthornton.cix.model;

public class PropertyChange {
    private String name;
    private Object oldValue;
    private Object newValue;

    public PropertyChange(String name, Object oldValue, Object newValue) {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getName() {
        return name;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
