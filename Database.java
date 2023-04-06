import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Database {
    public ArrayList<Collection> collections;
    public ArrayList<Transaction> transactions;

    public static Database createFromTransactions(ArrayList<Transaction> transactions){
        Database db = new Database();
        for(Transaction transaction : transactions){
            db.executeTransaction(transaction);
        }
        return db;
    }

    public Database(){
        this.collections = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public Document createDocument(Class dataClass, String path, Object data){
        String[] paths = path.split("/");
        if(paths.length % 2 == 1){
            throw new IllegalArgumentException("Invalid path '" + path + "'. Path must lead to a document, not a collection.");
        }
        for(Collection collection : collections){
            if(Objects.equals(collection.collectionName, paths[0])){
                return collection.createDocument(
                        dataClass,
                        Arrays.copyOfRange(paths, 1, paths.length),
                        data
                );
            }
        }
        Collection collection = createCollection(paths[0]);
        Document doc = collection.createDocument(
                dataClass,
                Arrays.copyOfRange(paths, 1, paths.length),
                data
        );
        if(doc == null){
            throw new IllegalArgumentException(
                    "Creating document at '" + path + "' failed: Collection '" + collection.collectionName + "' did not have document '" + paths[1] + "'"
            );
        }
        return doc;
    }

    public Collection createCollection(String name){
        Collection newCollection = new Collection(new ArrayList<>(), name);
        collections.add(newCollection);
        return newCollection;
    }

    public void executeTransaction(Transaction t){
        for(Change change : t.changes){
            executeChange(change);
        }
        transactions.add(t);
    }

    private void executeChange(Change change){
        Document changeTarget = getDocumentAtPath(change.pathOfModification);
        if(changeTarget == null){
            throw new NullPointerException("Document at " + change.pathOfModification + " does not exist!");
        }
        try{
            Field changeField = changeTarget.data.getClass().getField(change.fieldName);
            change.prevValue = changeField.get(changeTarget.data);
            changeField.set(changeTarget.data, change.newValue);
        }catch(Exception e){
            throw new RuntimeException("Error while executing change: " + e);
        }
    }

    public Document getDocumentAtPath(String path){
        String[] parts = path.split("/");
        if(parts.length % 2 == 1){
            throw new IllegalArgumentException("Invalid path '" + path + "'. Path must lead to a document, not a collection.");
        }
        for(Collection collection : collections){
            if(Objects.equals(collection.collectionName, parts[0])){
                return collection.getDocumentWithPath(Arrays.copyOfRange(parts, 1, parts.length));
            }
        }
        return null;
    }
}
