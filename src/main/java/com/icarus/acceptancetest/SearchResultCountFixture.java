package com.icarus.acceptancetest;

import com.icarus.OnlineTicketingSystem;
import com.icarus.TicketService;
import com.icarus.flights.Offer;
import fit.ColumnFixture;

import java.util.List;

/**
 * Created by Dave on 19/07/2015.
 */
public class SearchResultCountFixture extends ColumnFixture {

    private String origin;
    private String destination;

    public void setOrigin(String orig){
        this.origin = orig;
    }
    public void setDestination(String dest){
        this.destination = dest;
    }

    public int searchForTickets() {
        TicketService ticketingSystem = new OnlineTicketingSystem();
        List<Offer> searchResults = ticketingSystem.searchForTickets(origin, destination);
        return searchResults.size();
    }

}
