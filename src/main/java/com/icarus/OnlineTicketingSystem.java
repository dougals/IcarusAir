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

        Booking completeBooking = new Booking(quote.offer.price.add(STANDARD_PROCESSING_CHARGE), quote, timeNow, userAuthToken);

        processBooking(completeBooking);
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