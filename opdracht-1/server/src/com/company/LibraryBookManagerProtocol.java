package com.company;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sven Westerlaken on 22-5-2017.
 */
public class LibraryBookManagerProtocol {
    private static final int WAITING = 0;
    private static final int RESPONSED = 1;

    private static int status = WAITING;

    //ISBN-10 regex
    private static String isbnReg = "^[0-9]{9}([0-9]|X)$";
    //ISBN-13 regex
    private static String isbnRegTT = "^97[8-9][0-9]{9}([0-9]|X)$";
    //Base URL for Google's book API
    private static String baseURL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";


    //First method called to 'startup' the server when user connects.
    public String startup() {
        status = WAITING;
        return  "Welcome, please type the ISBN of the book to get information";
    }

    //Everytime the users send a 'request' this method will be called
    public String processInput(String input) throws MalformedURLException {
        String output = null;

        //If the user needs to typ in an ISBN
        if(status == WAITING) {
            if (!input.matches(isbnReg)) {
                //Doesn't match a ISBN-10
                if (input.matches(isbnRegTT)) {
                    //ISBN-13 is used
                    output = "That's an ISBN-13, please use the 10 digit version.";
                } else {
                    //Doesn't match ISBN-10 nor ISBN-13
                    output = "That's not a valid ISBN. Please try again";
                }
            } else {
                URL book = new URL(baseURL + input);
                try (InputStream stream = book.openStream()) {

                    //Get stream and convert the JSON into Java objects just GSON
                    Reader reader = new InputStreamReader(stream, "UTF-8");
                    JSONResult result = new Gson().fromJson(reader, JSONResult.class);

                    //Returns null if there was no valid book found
                    if (result.getBook() != null) {
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < result.getBookDetails().getAuthors().size(); i++) {
                            if(i != 0) {
                                sb.append(", ");
                            }

                            sb.append(result.getBookDetails().getAuthors().get(i));
                        }

                        //Book is found and ask for a new try to find a book
                        output =    "Title: " + result.getBookDetails().getTitle() +
                                    " | Subtitle: " + result.getBookDetails().getTitle() +
                                    " | Authors: " + sb +
                                    " | Would you like to search for another book? (Y/N)";

                        status = RESPONSED;

                    } else {
                        //The book could not be found with the given ISBN
                        output = "Your book information could not be found. Please check for any mistakes in your ISBN";
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    output = "Something went wrong while searching for your book.";
                }

            }
        //Previous time the book was found, server asked the user if it wants to search for another book
        } else if (status == RESPONSED) {
            //User responded with Y(es)
            if(input.equalsIgnoreCase("Y")) {
                //Wait for the user to typ a new ISBN
                output = "Please type the ISBN of the book to get information.";
                status = WAITING;
            //User responded with N(o)
            } else if (input.equalsIgnoreCase("N")) {
                //System will send this message and shut down after
                output = "Have a nice day!";
            } else {
                //User did not use the valid format and will be asked again.
                output = "Please type Y = yes or N = no";
            }

        }

        return output;
    }
}
