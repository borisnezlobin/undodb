public class DataStructure {
    public int uid;
    public String username;
    public Post[] posts;

    public DataStructure(int id, String username, Post[] posts){
        this.uid = id;
        this.username = username;
        this.posts = posts;
    }

    public static class Post{
        public String author;
        public String content;
        public int uid;
        public int likes;

        public Post(String writer, String body, int id){
            author = writer;
            content = body;
            uid = id;
            likes = 0;
        }
    }
}
