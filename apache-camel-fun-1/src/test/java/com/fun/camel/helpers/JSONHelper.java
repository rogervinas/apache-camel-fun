package com.fun.camel.helpers;

public abstract class JSONHelper {

    public static String jsonUserQuote(int id, String user, String quote) {
        return String.format(
                "{ " +
                "\"id\" : %d, " +
                "\"user\" : \"%s\", " +
                "\"quote\" : \"%s\"" +
                " }",
                id, user, quote
        );
    }
}
