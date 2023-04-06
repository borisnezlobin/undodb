public class Change{
    public String pathOfModification;
    public String fieldName;
    public Object prevValue;
    public Object newValue;

    public Change(String documentPath, String fieldName, Object newValue){
        this.pathOfModification = documentPath;
        this.fieldName = fieldName;
        this.prevValue = null;
        this.newValue = newValue;
    }
}