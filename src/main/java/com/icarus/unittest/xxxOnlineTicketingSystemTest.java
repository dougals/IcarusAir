package com.icarus.unittest;

import com.icarus.BookingService;
import com.icarus.OnlineTicketingSystem;
import com.icarus.TicketService;
import com.icarus.flights.Offer;
import com.icarus.flights.Quote;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by daverand on 18/07/2015.
 */
public class xxxOnlineTicketingSystemTest {

    public JUnitRuleMockery context = new JUnitRuleMockery();

    protected OnlineTicketingSystem tickingSystem;
    protected Map<UUID, Quote> _quotes;
    protected UUID _id = new UUID(1, 1);

    BookingService bookingServiceMock = context.mock(BookingService.class);
    TicketService ticketServiceMock = context.mock(TicketService.class);

    public xxxOnlineTicketingSystemTest() {
        _quotes = new HashMap<UUID, Quote>();
    }

    @Before
    public void init() {
        tickingSystem = new OnlineTicketingSystem();
    }

    @Test
    public void searchForTicketsWithEmptyValuesReturnsOffers() throws Exception {
        Expectations results = new Expectations() {{
            exactly(1).of(ticketServiceMock).searchForTickets("", "");
        }};
        context.checking(results);

        List<Offer> offers = tickingSystem.searchForTickets("", "");

        assertNotNull(offers);
        assertNotEquals(0, offers.size());
    }

    @Test
    public void confirmBookingShouldAddToQuotes() throws Exception {
        Expectations results = new Expectations() {{
            exactly(0).of(ticketServiceMock).confirmBooking(_id, "");
        }};
        context.checking(results);

        List<Offer> offers = tickingSystem.searchForTickets("", "");
        tickingSystem.confirmBooking(offers.get(0).id, "");

    }



}