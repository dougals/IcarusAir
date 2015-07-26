package com.icarus.unittest;

import com.icarus.OnlineTicketingSystem;
import com.icarus.booking.Booking;
import com.icarus.flights.Offer;
import org.junit.Before;
import org.junit.Test;
import sun.security.ssl.Debug;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
        assertNotEquals(0, offers.size());
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
    public void timeGreaterThanExpirationShouldThrowException(){
        long testQuoteTime= System.currentTimeMillis();
        long testMaxAge = TestableTicketingSystem.MAX_QUOTE_AGE_MILLIS;
        // Elapse the clock past expiration.
        long testCurrentTime = testQuoteTime + testMaxAge + 1000;

        testableTicketingSystem.validateExpiration(testQuoteTime, testCurrentTime, testMaxAge);
    }

    @Test
    public void timeLessThan20MinutesShouldValidate(){
        long testQuoteTime= System.currentTimeMillis();
        long testMaxAge = (1000 * 60 * 20) - 1;
        // Elapse the clock but not past expiration.
        long testCurrentTime = testQuoteTime + testMaxAge - 1000;

        testableTicketingSystem.validateExpiration(testQuoteTime, testCurrentTime, testMaxAge);
    }

    @Test(expected = IllegalStateException.class)
    public void timeGreaterThan20MinutesShouldThrowException(){
        long testQuoteTime= System.currentTimeMillis();
        long testMaxAge = (1000 * 60 * 20) + 1;
        // Elapse the clock past expiration.
        long testCurrentTime = testQuoteTime + testMaxAge + 1000;

        testableTicketingSystem.validateExpiration(testQuoteTime, testCurrentTime, testMaxAge);
    }

    @Test
    public void noProcessingChargeIfQuoteIsTwoMinutesOldOrLess(){
        BigDecimal quotePrice = new BigDecimal(100);
        long quoteAgeMillis = (1000 * 60 * 2);

        BigDecimal totalPrice = testableTicketingSystem.getPriceWithProcessingFee(quotePrice, quoteAgeMillis);
        BigDecimal processingCharge = totalPrice.subtract(quotePrice).setScale(2, RoundingMode.HALF_UP);

        BigDecimal expectedCharge = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedCharge, processingCharge);
    }

    @Test
    public void quotesBetween2And10MinutesLesserOf5PercentOr10PoundFee_cheapQuote(){
        BigDecimal quotePrice = new BigDecimal(100);
        long quoteAgeMillis = (1000 * 60 * 5);

        BigDecimal totalPrice = testableTicketingSystem.getPriceWithProcessingFee(quotePrice, quoteAgeMillis);
        BigDecimal processingCharge = totalPrice.subtract(quotePrice).setScale(2, RoundingMode.HALF_UP);

        BigDecimal expectedCharge = quotePrice.multiply(new BigDecimal(0.05)).setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedCharge, processingCharge);
    }

    @Test
    public void quotesBetween2And10MinutesLesserOf5PercentOr10PoundFee_expensiveQuote(){
        BigDecimal quotePrice = new BigDecimal(1000);
        long quoteAgeMillis = (1000 * 60 * 5);

        BigDecimal totalPrice = testableTicketingSystem.getPriceWithProcessingFee(quotePrice, quoteAgeMillis);
        BigDecimal processingCharge = totalPrice.subtract(quotePrice).setScale(2, RoundingMode.HALF_UP);

        BigDecimal expectedCharge = new BigDecimal(10).setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedCharge, processingCharge);
    }

    @Test
    public void quotesBetween10And20Minutes20Pounds20PoundFee(){
        BigDecimal quotePrice = new BigDecimal(1000);
        long quoteAgeMillis = (1000 * 60 * 15);

        BigDecimal totalPrice = testableTicketingSystem.getPriceWithProcessingFee(quotePrice, quoteAgeMillis);
        BigDecimal processingCharge = totalPrice.subtract(quotePrice).setScale(2, RoundingMode.HALF_UP);

        BigDecimal expectedCharge = new BigDecimal(20).setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedCharge, processingCharge);
    }



    private class TestableTicketingSystem extends OnlineTicketingSystem {

        protected static final long MAX_QUOTE_AGE_MILLIS = 20 * 60 * 1000;

        @Override
        public void confirmBooking(UUID id, String userAuthToken) {
            validateOfferID(id);

            long timeNow = System.currentTimeMillis();
            long quoteExpiration = timeNow + 5000;
            long quoteAge = 15000;

            BigDecimal quotePrice = BigDecimal.valueOf(100.00);
            BigDecimal totalPrice = getPriceWithProcessingFee(quotePrice, quoteAge);

            validateExpiration(quoteExpiration, timeNow, MAX_QUOTE_AGE_MILLIS);

            Debug.println("Test", "Processed test booking: " + totalPrice.toString());
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
        protected BigDecimal getPriceWithProcessingFee(BigDecimal quotePrice, long ageMillis){
            return super.getPriceWithProcessingFee(quotePrice, ageMillis);
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
