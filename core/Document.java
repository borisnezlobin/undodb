package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Document {
    public Class dataType;
    public final String documentName;
    public Object data;
    public ArrayList<Collection> childrenCollections;

    public Document(Class dataType, String name, Object data){
        this.dataType = dataType;
        this.documentName = name;
        this.data = data;
        this.childrenCollections = new ArrayList<>();
    }

    public Collection createSubCollection(String name){
        Collection newCollection = new Collection(new ArrayList<>(), name);
        childrenCollections.add(newCollection);
        return newCollection;
    }

    public Document getDocumentWithPath(String[] path){
        for(Collection collection : childrenCollections){
            if(Objects.equals(collection.collectionName, path[0])){
                return collection.getDocumentWithPath(Arrays.copyOfRange(path, 1, path.length));
            }
        }
        return null;
    }

    public Document createDocument(Class dataType, String[] path, Object data){
        for(Collection collection : childrenCollections){
            if(Objects.equals(collection.collectionName, path[0])){
                return collection.createDocument(
                        dataType,
                        Arrays.copyOfRange(path, 1, path.length),
                        data
                );
            }
        }
        if(path.length == 2){
            Document doc = new Document(dataType, path[1], data);
            childrenCollections.add(new Collection(
                    doc,
                    path[0]
            ));
            return doc;
        }
        return null;
    }
}
