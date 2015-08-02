package com.icarus;

import com.icarus.booking.Booking;
import com.icarus.booking.BookingSystem;
import com.icarus.flights.Offer;
import com.icarus.flights.Quote;
import com.icarus.flights.FlightDatabase;

import java.math.BigDecimal;
import java.util.*;

public class OnlineTicketingSystem implements TicketService {

    protected static final long MAX_QUOTE_AGE_MILLIS = 20 * 60 * 1000;

    public static final BigDecimal STANDARD_PROCESSING_CHARGE = new BigDecimal(10);


    protected Map<UUID, Quote> _quotes;
    protected List<Offer> _searchResults;


    public OnlineTicketingSystem(){
        _quotes = new HashMap<UUID, Quote>();
        _searchResults = new LinkedList<Offer>();
    }


    @Override
    public List<Offer> searchForTickets(String origin, String destination) {

        _searchResults = getFlightDatabase(origin, destination);
        for (Offer offer : _searchResults) {
            addOfferAsQuote(offer);
        }
        return _searchResults;
    }

    private List<Offer> getFlightDatabase(String origin, String destination) {
        return FlightDatabase.getInstance().searchFor(origin, destination);
    }

    @Override
    public void confirmBooking(UUID id, String userAuthToken) {

        validateOfferID(id);

        Quote quote = _quotes.get(id);

        long timeNow = System.currentTimeMillis();
        validateExpiration(quote.timestamp, timeNow, MAX_QUOTE_AGE_MILLIS);
        long quoteAge = timeNow - quote.timestamp;

        BigDecimal totalPrice = getPriceWithProcessingFee(quote.offer.price, quoteAge);

        Booking completeBooking = createBooking(userAuthToken, quote, timeNow, totalPrice);
        processBooking(completeBooking);
    }

    private Booking createBooking(String userAuthToken, Quote quote, long timeNow, BigDecimal processingFee) {
        return new Booking(processingFee, quote, timeNow, userAuthToken);
    }

    protected BigDecimal getPriceWithProcessingFee(BigDecimal quotePrice, long ageMillis) {

        long quoteAgeMinutes = ageMillis / 1000 / 60;
        BigDecimal processingCharge;

        processingCharge = getProcessingFee(quotePrice, quoteAgeMinutes);

        return quotePrice.add(processingCharge);
    }

    protected BigDecimal getProcessingFee(BigDecimal quotePrice, long quoteAgeMinutes) {
        BigDecimal processingCharge;
        if (quoteAgeMinutes <= 2) {
            processingCharge = new BigDecimal(0);
        }
        else if (quoteAgeMinutes <= 10) {
            processingCharge = getLesserOf5PercentOr10Pounds(quotePrice);
        }
        else if (quoteAgeMinutes <= 20){
            processingCharge = new BigDecimal(20);
        } else {
            throw new IllegalStateException("Unexpected quote age: " + quoteAgeMinutes);
        }
        return processingCharge;
    }

    protected BigDecimal getLesserOf5PercentOr10Pounds(BigDecimal quotePrice) {
        BigDecimal fivePercentOfPrice = quotePrice.multiply(new BigDecimal(0.05));
        BigDecimal tenPounds = new BigDecimal(10);

        return (fivePercentOfPrice.compareTo(tenPounds) == -1) ? fivePercentOfPrice : tenPounds;
    }

    protected void processBooking(Booking completeBooking) {
        BookingSystem.getInstance().process(completeBooking);
    }

    protected void validateExpiration(long quoteTimestamp, long timeNow, long maxQuoteAge) {
        if (timeNow - quoteTimestamp > maxQuoteAge) {
            throw new IllegalStateException("Quote expired, please get a new price");
        }
    }

    protected void validateOfferID(UUID id) {
        if (!_quotes.containsKey(id)) {
            throw new NoSuchElementException("Offer ID is invalid");
        }
    }

    protected void addOfferAsQuote(Offer offer){
        _quotes.put(offer.id, new Quote(offer, System.currentTimeMillis()));
    }

}