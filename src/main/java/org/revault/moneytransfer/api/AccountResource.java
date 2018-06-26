package org.revault.moneytransfer.api;


import org.revault.moneytransfer.api.data.Account;
import org.revault.moneytransfer.api.data.Fail;
import org.revault.moneytransfer.api.data.Success;
import org.revault.moneytransfer.err.DaoException;
import org.revault.moneytransfer.service.AccountService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("accountresource")
public class AccountResource {
    private final AccountService accountService;

    @Inject
    public AccountResource (AccountService accountService){
        this.accountService = accountService;
    }

    @GET
    @Path(value = "retrieve/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("number") String number) {
        try {
            return accountService.retrieve(number);
        }
        catch(DaoException ex){
            return null;
        }
    }

    @POST
    @Path(value = "save")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveAccount(Account account) {
        try {
            accountService.save(account);
            return Response.status(Response.Status.OK)
                    .entity(new Success("Account has been successfully saved"))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        catch(DaoException ex){
            return Response.status(Response.Status.OK)
                    .entity(new Fail(ex.getError().toString()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}