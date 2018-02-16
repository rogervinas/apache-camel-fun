package com.fun.camel.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class UserQuoteTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void should_serialize_to_json() throws JsonProcessingException, JSONException {
        int id = 14;
        String user = "Vincent Vega";
        String quote = "No man, they got the metric system. They wouldn't know what the fuck a Quarter Pounder is";
        UserQuote userQuote = new UserQuote(id, user, quote);
        String json = objectMapper.writeValueAsString(userQuote);
        assertEquals(
                String.format("{ \"id\": %d, \"user\" : \"%s\", \"quote\" : \"%s\" }", id, user, quote),
                json,
                true
        );
    }

    @Test
    public void equals_should_return_true_for_same_UserQuotes() throws JsonProcessingException, JSONException {
        int id = 15;
        String user = "Vincent Vega";
        String quote = "Well, a Big Mac's a Big Mac, but they call it le Big-Mac";
        UserQuote userQuote1 = new UserQuote(id, user, quote);
        UserQuote userQuote2 = new UserQuote(id, user, quote);
        assertThat(userQuote1.equals(userQuote2)).isTrue();
    }

    @Test
    public void equals_should_return_false_for_UserQuotes_with_different_Id() throws JsonProcessingException, JSONException {
        int id1 = 16;
        int id2 = 17;
        String user = "Jules Winnfield";
        String quote = "Le Big-Mac. Ha ha ha ha. What do they call a Whopper?";
        UserQuote userQuote1 = new UserQuote(id1, user, quote);
        UserQuote userQuote2 = new UserQuote(id2, user, quote);
        assertThat(userQuote1.equals(userQuote2)).isFalse();
    }

    @Test
    public void equals_should_return_false_for_UserQuotes_with_different_User() throws JsonProcessingException, JSONException {
        int id = 18;
        String user1 = "Vincent Vega";
        String user2 = "Jules Winnfield";
        String quote = "I dunno, I didn't go into Burger King";
        UserQuote userQuote1 = new UserQuote(id, user1, quote);
        UserQuote userQuote2 = new UserQuote(id, user2, quote);
        assertThat(userQuote1.equals(userQuote2)).isFalse();
    }

    @Test
    public void equals_should_return_false_for_UserQuotes_with_different_Quote() throws JsonProcessingException, JSONException {
        int id = 19;
        String user = "Jules Winnfield";
        String quote1 = "I'm not giving you that money. I'm buying something from you. Wanna know what I'm buyin' Ringo?";
        String quote2 = "Your life. I'm givin' you that money so I don't have to kill your ass. You read the Bible?";
        UserQuote userQuote1 = new UserQuote(id, user, quote1);
        UserQuote userQuote2 = new UserQuote(id, user, quote2);
        assertThat(userQuote1.equals(userQuote2)).isFalse();
    }
}
