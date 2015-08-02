package com.icarus.acceptancetest;

import com.icarus.OnlineTicketingSystem;
import com.icarus.TicketService;
import com.icarus.flights.Offer;
import fit.ColumnFixture;

import java.util.List;

public class ConfirmBookingFixture extends ColumnFixture {
    private String origin;
    private String destination;
    private String userAuthToken = "tom@example.com";

    public void setOrigin(String orig){
        this.origin = orig;
    }
    public void setDestination(String dest){
        this.destination = dest;
    }

    public boolean confirmBooking() {
        TicketService ticketingSystem = new OnlineTicketingSystem();
        List<Offer> searchResults = ticketingSystem.searchForTickets(origin, destination);

        if (searchResults.size() > 0) {
            ticketingSystem.confirmBooking(searchResults.get(0).id, userAuthToken);
            return true;
        }
        else {
            return false;
        }
    }
}
