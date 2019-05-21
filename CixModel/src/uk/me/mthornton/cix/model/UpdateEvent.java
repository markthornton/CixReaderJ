package uk.me.mthornton.cix.model;

import java.util.ArrayList;
import java.util.List;

public class UpdateEvent extends ChangeEvent {
    private List<PropertyChange> changes = new ArrayList<>();

    public UpdateEvent(Object object) {
        super(object, Type.update);
    }

    public List<PropertyChange> getChanges() {
        return changes;
    }

    public boolean isEmpty() {
        return changes.isEmpty();
    }

    <T> T compare(String property, T oldValue, T newValue) {
        if (newValue != null && !newValue.equals(oldValue)) {
            changes.add(new PropertyChange(property, oldValue, newValue));
            return newValue;
        } else {
            return oldValue;
        }
    }

    boolean compare(String property, boolean oldValue, boolean newValue) {
        if (newValue != oldValue) {
            changes.add(new PropertyChange(property, oldValue, newValue));
        }
        return newValue;
    }

}
