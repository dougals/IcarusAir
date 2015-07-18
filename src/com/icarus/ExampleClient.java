package com.icarus;

import com.icarus.flights.Offer;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by daverand on 18/07/2015.
 */

public class ExampleClient {
    public static void main(String[] args) throws Exception {

        TicketService ticketingSystem = new OnlineTicketingSystem();
        String userAuthToken = "tom@example.com";

        List<Offer> searchResults = ticketingSystem.searchForTickets("London", "New York");

        if (searchResults.isEmpty()) {
            System.out.println("No search results found");
        } else {
            Offer offer = searchResults.get(0);

            // some time may pass...
            Thread.sleep(21 * 60 * 1000);

            if (priceAcceptable(offer.price)) {
                ticketingSystem.confirmBooking(offer.id, userAuthToken);
            }
        }
    }

    private static boolean priceAcceptable(BigDecimal price) {
        return true;
    }
}