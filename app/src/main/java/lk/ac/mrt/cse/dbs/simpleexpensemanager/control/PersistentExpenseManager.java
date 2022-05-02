package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 * This is the class Persistent Expense Manager which extends Expense Manger.
 * This will setup the persistent Account DAO and the Persistent Transaction Manager.
 * **/

public class PersistentExpenseManager extends ExpenseManager {

    private final Context context;

    public PersistentExpenseManager(Context context) throws ExpenseManagerException {
        this.context = context;
        setup();
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void setup() throws ExpenseManagerException {
        /*** Persistent implementation ***/

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(this.getContext());
        setAccountsDAO(persistentAccountDAO);

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(this.context);
        setTransactionsDAO(persistentTransactionDAO);


    }
}
