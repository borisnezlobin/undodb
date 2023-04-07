package core;

import core.Change;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class Update {
    public String path;
    public Object newValue;

    /**
     * More user-friendly version of core.Change.java
     */
    public Update(String path, Object newValue){
        this.path = path;
        this.newValue = newValue;
    }

    public Change[] toChanges(Document changeTarget, String path) throws IllegalAccessException{
        ArrayList<Change> changes = new ArrayList<>();
        for(Field field : changeTarget.dataType.getFields()){
            field.setAccessible(true);
            Object oldFieldValue = field.get(changeTarget.dataType.cast(changeTarget.data));
            Object newFieldValue = field.get(changeTarget.dataType.cast(newValue));
            if(!Objects.deepEquals(oldFieldValue, newFieldValue)){
                Change change = new Change(
                        path,
                        field.getName(),
                        newFieldValue
                );
                change.prevValue = oldFieldValue;
                changes.add(change);
            }
        }

        return changes.toArray(Change[]::new);
    }
}
