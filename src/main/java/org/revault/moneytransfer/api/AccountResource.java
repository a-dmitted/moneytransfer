package org.revault.moneytransfer.api;


import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.service.TransactionService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @POST
    @Path(value = "save")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveAccount(Account account) {
        transactionService.getAccountService().save(account);
        return Response.status(Response.Status.OK).entity("Account has been successfully saved").type(MediaType.APPLICATION_JSON).build();
    }
}