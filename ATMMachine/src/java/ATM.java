package com.myatm.backend;

public class ATM {
    private BankAccount userAccount;
    public ATM(BankAccount account) {
        this.userAccount = account;
    }

    public double checkBalance() {
        return userAccount.getBalance();
    }

    public void deposit(double amount) {
        userAccount.deposit(amount);
    }

    public boolean withdraw(double amount) {
        return userAccount.withdraw(amount);
    }
}