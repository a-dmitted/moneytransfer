package org.revault.moneytransfer.api;

import org.revault.moneytransfer.service.TransactionService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(value = "moneytransfer")
public class MoneyTransferResource {

    private final TransactionService transactionService;

    @Inject
    public MoneyTransferResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @POST
    @Path(value = "save{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response makeTransfer(String debitAcc, String creditAcc, Long amount) {
        transactionService.makeTransfer(debitAcc, creditAcc, amount);

        return Response.status(Response.Status.OK).entity("transaction has been successfully completed").type(MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path(value = "dummy")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response dummyResponse() {


        return Response.status(Response.Status.OK).entity("transaction has been successfully completed").type(MediaType.APPLICATION_JSON).build();
    }
}