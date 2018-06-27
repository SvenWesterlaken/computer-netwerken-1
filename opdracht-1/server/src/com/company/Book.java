package com.company;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sven Westerlaken on 22-5-2017.
 */
public class Book {
    @SerializedName("id")
    private String id;
    @SerializedName("volumeInfo")
    private BookDetails bookDetails;

    public BookDetails getDetails() {
        return bookDetails;
    }
}
