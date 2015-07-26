package com.icarus.unittest;

import com.icarus.OnlineTicketingSystem;
import com.icarus.booking.Booking;
import com.icarus.flights.Offer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sun.security.ssl.Debug;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dave on 25/07/2015.
 */
public class TicketingSystemTest {

    TestableTicketingSystem testableTicketingSystem;
    String userToken;

    @Before
    public void init(){
        testableTicketingSystem = new TestableTicketingSystem();
        userToken = "Dave";
    }

    @Test
    public void searchShouldReturnOffers(){
        List<Offer> offers = testableTicketingSystem.searchForTickets("", "");
        Assert.assertNotEquals(0, offers.size());
    }

    @Test
    public void confirmOfferShouldNotThrowException() throws Exception{
        List<Offer> offers = testableTicketingSystem.searchForTickets("", "");
        testableTicketingSystem.confirmBooking(offers.get(0).id, userToken);
    }

    @Test
    public void timeLessThanExpirationShouldValidate(){
        long testQuoteTime= System.currentTimeMillis();
        long testMaxAge = TestableTicketingSystem.MAX_QUOTE_AGE_MILLIS;
        // Elapse the clock but not past expiration.
        long testCurrentTime = testQuoteTime + testMaxAge - 1000;

        testableTicketingSystem.validateExpiration(testQuoteTime, testCurrentTime, testMaxAge);
    }

    @Test(expected = IllegalStateException.class)
    public void timeGreaterThanExpirationShouldFailToValidate(){
        long testQuoteTime= System.currentTimeMillis();
        long testMaxAge = TestableTicketingSystem.MAX_QUOTE_AGE_MILLIS;
        // Elapse the clock past expiration.
        long testCurrentTime = testQuoteTime + testMaxAge + 1000;

        testableTicketingSystem.validateExpiration(testQuoteTime, testCurrentTime, testMaxAge);
    }



    private class TestableTicketingSystem extends OnlineTicketingSystem {

        protected static final long MAX_QUOTE_AGE_MILLIS = 20 * 60 * 1000;

        @Override
        public void confirmBooking(UUID id, String userAuthToken) {
            validateOfferID(id);

            long timeNow = System.currentTimeMillis();
            long quoteExpiration = timeNow + 5000;

            BigDecimal quotePrice = BigDecimal.valueOf(100.00);
            BigDecimal adjustedPrice = quotePrice.add(STANDARD_PROCESSING_CHARGE);

            validateExpiration(quoteExpiration, timeNow, MAX_QUOTE_AGE_MILLIS);

            Debug.println("Test", "Processed test booking: " + adjustedPrice.toString());
        }

        @Override
        public List<Offer> searchForTickets(String origin, String destination) {
            return super.searchForTickets(origin, destination);
        }

        @Override
        protected void processBooking(Booking completeBooking) {
            super.processBooking(completeBooking);
        }

        @Override
        protected void validateExpiration(long quoteTimestamp, long timeNow, long maxQuoteAge) {
            super.validateExpiration(quoteTimestamp, timeNow, maxQuoteAge);
        }

        @Override
        protected void validateOfferID(UUID id) {
            super.validateOfferID(id);
        }

        @Override
        protected void addOfferAsQuote(Offer offer){
            super.addOfferAsQuote(offer);
        }
    }
}
