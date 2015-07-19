package com.icarus.unittest;

import com.icarus.OnlineTicketingSystem;
import com.icarus.flights.Offer;
import org.junit.Before;
import org.junit.Test;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by daverand on 18/07/2015.
 */
public class OnlineTicketingSystemTest {

    private OnlineTicketingSystem tickingSystem;
    public JUnitRuleMockery context = new JUnitRuleMockery();

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