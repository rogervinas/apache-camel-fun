package com.fun.camel.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class UserQuote {

    private final int id;
    private final String user;
    private final String quote;

    @JsonCreator
    public UserQuote(
            @JsonProperty("id") int id,
            @JsonProperty("user") String user,
            @JsonProperty("quote") String quote
    ) {
        this.id = id;
        this.user = user;
        this.quote = quote;
    }

    public int getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getQuote() {
        return quote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserQuote userQuote = (UserQuote) o;
        return id == userQuote.id &&
                Objects.equals(user, userQuote.user) &&
                Objects.equals(quote, userQuote.quote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, quote);
    }

    @Override
    public String toString() {
        return "UserQuote{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", quote='" + quote + '\'' +
                '}';
    }
}
