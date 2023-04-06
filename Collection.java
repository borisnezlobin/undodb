import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Collection {
    public ArrayList<Document> docs;
    public String collectionName;

    public Collection(ArrayList<Document> docs, String name){
        this.docs = docs;
        this.collectionName = name;
    }

    public Document createDocumentInThisCollection(Class dataType, String name, Object data){
        Document newDocument = new Document(dataType, name, data);
        docs.add(newDocument);
        return newDocument;
    }

    public Document createDocument(Class dataType, String[] path, Object data){
        if(path.length == 1){
            return createDocumentInThisCollection(dataType, path[0], data);
        }
        for(Document doc : docs){
            if(Objects.equals(doc.documentName, path[0])){
                return doc.createDocument(
                        dataType,
                        Arrays.copyOfRange(path, 1, path.length),
                        data
                );
            }
        }
        return null;
    }

    public Document getDocumentWithPath(String[] path){
        for(Document doc : docs){
            if(Objects.equals(doc.documentName, path[0])){
                if(path.length == 1) return doc;
                return doc.getDocumentWithPath(Arrays.copyOfRange(path, 1, path.length));
            }
        }
        return null;
    }
}
