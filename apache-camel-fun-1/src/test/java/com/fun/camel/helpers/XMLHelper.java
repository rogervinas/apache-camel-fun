package com.fun.camel.helpers;

public abstract class XMLHelper {

    public static String xmlUserQuotes(String... xmlUserQuotes) {
        String xml = "<UserQuotes>";
        for(String xmlUserQuote : xmlUserQuotes) {
            xml += xmlUserQuote;
        }
        xml += "</UserQuotes>";
        return xml;
    }

    public static String xmlUserQuote(int id, String name, String surname, String quote) {
        return String.format(
                "<UserQuote Id=\"%s\" Name=\"%s\" Surname=\"%s\">" +
                    "<Quote>%s</Quote>" +
                "</UserQuote>",
                id, name, surname, quote
        );
    }
}
