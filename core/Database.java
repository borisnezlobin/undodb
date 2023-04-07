package core;

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
        if(paths.length != 2){
            throw new IllegalArgumentException(
                    "Creating document at '" + path + "' failed: invalid path. Make sure that:\n"
                    + "1. Your path points to a document\n"
                    + "2. Your path points to a first-level document (there should only be one '/' in your path)"
            );
        }
        Collection collection = createCollection(paths[0]);
        return collection.createDocument(
                dataClass,
                Arrays.copyOfRange(paths, 1, paths.length),
                data
        );
    }

    public Collection createCollection(String name){
        Collection newCollection = new Collection(new ArrayList<>(), name);
        collections.add(newCollection);
        return newCollection;
    }

    public void updateDoc(Update update){
        Document doc = getDocumentAtPath(update.path);
        if(doc == null){
            createDocument(
                    update.newValue.getClass(),
                    update.path,
                    update.newValue
            );
            return;
        }
        try{
            Change[] changes = update.toChanges(doc, update.path);
            Transaction t = new Transaction(changes);
            executeTransaction(t);
        }catch(IllegalAccessException e){
            throw new IllegalArgumentException("Unable to access a field in document at '" + update.path + "': " + e.getMessage());
        }
    }

    public void executeTransaction(Transaction t){
        for(Change change : t.changes){
            executeChange(change);
        }
        try{
            transactions.get(transactions.size() - 1).nextHash = t.hash;
        }catch(IndexOutOfBoundsException ignored){}
        transactions.add(t);
    }

    private void executeChange(Change change){
        Document changeTarget = getDocumentAtPath(change.pathOfModification);
        if(changeTarget == null){
            throw new NullPointerException("core.Document at " + change.pathOfModification + " does not exist!");
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
