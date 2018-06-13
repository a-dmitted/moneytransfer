package org.revault.moneytransfer.api;


import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.service.TransactionService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("accountresource")
public class AccountResource {
    private final TransactionService transactionService;

    @Inject
    public AccountResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GET
    @Path(value = "retrieve/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("number") String number) {
        return transactionService.getAccountService().retreive(number);
    }
}