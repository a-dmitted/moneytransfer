package org.revault.moneytransfer.api;

import org.revault.moneytransfer.service.TransactionService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(value = "moneytransfer")
public class MoneyTransferResource {

    private final TransactionService transactionService;

    @Inject
    public MoneyTransferResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GET
    @Path(value = "transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response makeTransfer(
        @QueryParam("debitAcc") String debitAcc,
        @QueryParam("creditAcc") String creditAcc,
        @QueryParam("amount") Long amount
    )
    {
        transactionService.makeTransfer(debitAcc, creditAcc, amount);

        return Response.status(Response.Status.OK).entity("transaction has been successfully completed").type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path(value = "dummy")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response dummyResponse() {
        return Response.status(Response.Status.OK).entity("transaction has been successfully completed").type(MediaType.APPLICATION_JSON).build();
    }
}