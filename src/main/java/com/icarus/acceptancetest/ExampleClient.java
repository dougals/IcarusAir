package com.icarus.acceptancetest;

import com.icarus.OnlineTicketingSystem;
import com.icarus.TicketService;
import com.icarus.flights.Offer;
import sun.security.ssl.Debug;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by daverand on 18/07/2015.
 */

public class ExampleClient {
    public static void main(String[] args) throws Exception {

        TicketService ticketingSystem = new OnlineTicketingSystem();
        String userAuthToken = "tom@example.com";

        List<Offer> searchResults = ticketingSystem.searchForTickets("", "");

        if (searchResults.isEmpty()) {
            System.out.println("No search results found");
        } else {
            Offer offer = searchResults.get(0);

            for (Offer o: searchResults){
                Debug.println("Result", o.toString());
            }

            // some time may pass...
            Thread.sleep(2 * 1000);

            if (priceAcceptable(offer.price)) {
                ticketingSystem.confirmBooking(offer.id, userAuthToken);
            }
        }
    }

    private static boolean priceAcceptable(BigDecimal price) {
        return true;
    }
}