package com.icarus;

import com.icarus.flights.Offer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by daverand on 18/07/2015.
 */
public class OnlineTicketingSystemTest {

    private OnlineTicketingSystem tickingSystem;

    @Before
    public void init() {
        tickingSystem = new OnlineTicketingSystem();
    }

    @Test
    public void testThatSearchForTicketsWithEmptyValuesReturnsOfers() throws Exception {

        List<Offer> offers = tickingSystem.searchForTickets("", "");

        assertNotNull(offers);
        assertNotEquals(0, offers.size());
    }
}