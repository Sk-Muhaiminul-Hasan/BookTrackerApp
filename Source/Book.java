public class Book {
    private String title;
    private String author;
    private int progress;
    private boolean isRead;

    public Book(String title, String author, int progress) {
        this.title = title;
        this.author = author;
        this.progress = progress;
        this.isRead = (progress >= 100);
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getProgress() { return progress; }
    public boolean isRead() { return isRead; }
}
