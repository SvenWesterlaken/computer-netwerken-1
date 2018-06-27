package com.company;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sven Westerlaken on 22-5-2017.
 */
public class JSONResult {

    @SerializedName("Kind")
    private String kind;
    @SerializedName("totalItems")
    private int totalItems;
    @SerializedName("items")
    private List<Book> books;

    public Book getBook() {
        if(books != null) {
            return books.get(0);
        } else {
            return null;
        }
    }

    public int getSize() {
        return books.size();
    }

    public BookDetails getBookDetails() {
        return getBook().getDetails();
    }
}
