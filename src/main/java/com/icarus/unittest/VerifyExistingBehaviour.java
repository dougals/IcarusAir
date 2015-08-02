package com.icarus.unittest;

import com.icarus.OnlineTicketingSystem;
import com.icarus.TicketService;
import com.icarus.flights.Offer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Created by Dave on 19/07/2015.
 */
public class VerifyExistingBehaviour {

    TicketService ticketingSystem;
    String userAuthToken;
    List<Offer> searchResults;
    List<Offer> emptySearchResults;
    Offer offer;

    @Before
    public void init() {
        ticketingSystem = new OnlineTicketingSystem();
        userAuthToken = "tom@example.com";
        searchResults = ticketingSystem.searchForTickets("London", "New York");
        emptySearchResults = ticketingSystem.searchForTickets("London", "Minneapolis");
    }

    @Test
    public void testThatSearchingForAValidFlightReturnsOffers() {
        assertNotNull(searchResults);
        assertTrue(searchResults.size() > 0);
    }

    @Test
    public void testThatSearchingForABadRouteReturnsNoOffers() {
        assertNotNull(emptySearchResults);
        assertTrue(emptySearchResults.size() == 0);
    }

    @Test
    public void testThatConfirmBookingCompletes() {
        offer = searchResults.get(0);

        ticketingSystem.confirmBooking(offer.id, userAuthToken);
    }

    @Test(expected=IllegalStateException.class)
    public void testThatConfirmBookingThrowsIllegalStateExceptionAfterMaxQuoteAge() throws InterruptedException {
        offer = searchResults.get(0);

        // Wait 21 minutes
        Thread.sleep(21 * 60 * 1000);

        // This should throw an exception:
        ticketingSystem.confirmBooking(offer.id, userAuthToken);
    }
}
