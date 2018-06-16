package org.revault.moneytransfer.api;

import org.revault.moneytransfer.api.data.Fail;
import org.revault.moneytransfer.api.data.Success;
import org.revault.moneytransfer.api.data.Transfer;
import org.revault.moneytransfer.err.DaoException;
import org.revault.moneytransfer.err.ServiceExeption;
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

    @PUT
    @Path(value = "transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response makeTransfer(Transfer transfer)
    {
        try{
            transactionService.makeTransfer(
                    transfer.getDebitAcc(),
                    transfer.getCreditAcc(),
                    transfer.getAmount()
            );
        }
        catch(ServiceExeption ex){
            return Response.status(Response.Status.PRECONDITION_FAILED)
                    //.entity(ex.getError().toString())
                    .entity(new Fail(ex.getError().toString()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(new Success("Transfer has been successfully completed"))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}