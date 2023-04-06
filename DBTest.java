import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void testSubCollectionCreate(){
        Document doc = db.getDocumentAtPath("users/jeffrey");
        doc.createSubCollection("posts");
        db.createDocument(
                DataStructure.Post.class,
                "users/jeffrey/posts/0",
                new DataStructure.Post("jeffrey", "hey guys", 0)
        );
        Document post = db.getDocumentAtPath("users/jeffrey/posts/0");
        assert post != null;
        System.out.println(((DataStructure.Post) post.data).content);
        post = db.getDocumentAtPath("users/jeffrey/posts");
        assert post == null;
    }
}
