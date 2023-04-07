import core.Change;
import core.Database;
import core.Document;
import core.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class DBTest {
    Database db;
    @BeforeEach
    public void fillDatabase(){
        db = new Database();
        db.createCollection("users");
        db.createDocument(
                DataStructure.class,
                "users/jeffrey",
                new DataStructure(0, "jeffrey", new DataStructure.Post[0])
        );
    }

    @Test
    public void testDocFind(){
        Document doc = db.getDocumentAtPath("users/jeffrey");
        System.out.println(db);
        assert doc != null;
        System.out.println("doc has data.username = " + ((DataStructure) doc.data).username);
        doc = db.getDocumentAtPath("users/bezos");
        assert doc == null;
    }

    @Test
    public void getAndReadTest(){
        db.createDocument(
                DataStructure.Post.class,
                "users/jeffrey/posts/0",
                new DataStructure.Post("jeffrey", "hey guys", 0)
        );
        Document post = db.getDocumentAtPath("users/jeffrey/posts/0");
        assert post != null;
        assert Objects.equals(((DataStructure.Post) post.data).content, "hey guys");
    }

    @Test
    public void updateToChangeArrayTest() throws IllegalAccessException{
        Document doc = db.getDocumentAtPath("users/jeffrey");
        Update update = new Update(
                "users/jeffrey",
                new DataStructure(1, "jeff", new DataStructure.Post[0])
        );
        Change[] changes = update.toChanges(doc, "users/jeffrey");
        assert changes.length == 2;
    }

    @Test
    public void updateDocument(){
        db.updateDoc(new Update(
                "users/jeffrey",
                new DataStructure(1, "jeff", new DataStructure.Post[0])
        ));
        Document doc = db.getDocumentAtPath("users/jeffrey");
        assert Objects.equals(((DataStructure) doc.data).username, "jeff");
        assert ((DataStructure) doc.data).uid == 1;
        assert db.transactions.size() == 1;
    }

    @Test
    public void testTransactionHash(){
        db.updateDoc(new Update(
                "users/jeffrey",
                new DataStructure(1, "jeff", new DataStructure.Post[0])
        ));
        db.updateDoc(new Update(
                "users/jeffrey",
                new DataStructure(2, "jeff2", new DataStructure.Post[0])
        ));
        assert Objects.equals(db.transactions.get(0).nextHash, db.transactions.get(1).hash);
        assert !Objects.equals(db.transactions.get(0).hash, db.transactions.get(1).hash);
        System.out.println(db.transactions.get(0).hash); // for fun
    }
}