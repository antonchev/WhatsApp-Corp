package info.androidhive.gcm.model;

import android.widget.ImageView;

import com.ilocator.models.User;

import java.io.Serializable;

/**
 * Created by Lincoln on 07/01/16.
 */
public class Message implements Serializable {
    String id, message, createdAt,author,imageURL;

    User user;
    int from_me;

    public Message() {
    }

    public Message(String id, String message, String createdAt, User user, int from_me, String author, String imageURL) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.user = user;
        this.from_me = from_me;
        this.author = author;
        this.imageURL = imageURL;
    }
    public String getImage() {
        return imageURL;
    }

    public void setImage(String image) { this.imageURL = image; }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getFrom_me() {
        return from_me;
    }

    public void setFrom_me(int from_me) {
        this.from_me = from_me;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
