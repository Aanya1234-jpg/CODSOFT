package com.myatm.backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AtmServlet", urlPatterns = {"/atm-service"})
public class ATMServlet extends HttpServlet {

    private static final DecimalFormat df = new DecimalFormat("#,##,##0.00");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        ATM atm;
        if (session.getAttribute("atm") == null) {
            BankAccount account = new BankAccount(40000.00); 
            atm = new ATM(account);
            session.setAttribute("atm", atm);
        } else {
            atm = (ATM) session.getAttribute("atm");
        }

        String action = request.getParameter("action");
        String message = "";
        boolean success = true;

        try {
            switch (action) {
                case "checkBalance":
                    message = "Your current balance is displayed.";
                    break;

                case "deposit":
                    double depositAmount = Double.parseDouble(request.getParameter("amount"));
                    if (depositAmount <= 0) {
                        message = "Deposit amount must be positive.";
                        success = false;
                    } else {
                        atm.deposit(depositAmount);
                        message = "Successfully deposited ₹ " + df.format(depositAmount) + ".";
                    }
                    break;

                case "withdraw":
                    double withdrawAmount = Double.parseDouble(request.getParameter("amount"));
                     if (withdrawAmount <= 0) {
                        message = "Withdrawal amount must be positive.";
                        success = false;
                    } else if (atm.withdraw(withdrawAmount)) {
                        message = "Successfully withdrew ₹ " + df.format(withdrawAmount) + ".";
                    } else {
                        message = "Withdrawal failed. Insufficient funds.";
                        success = false;
                    }
                    break;
                    
                default:
                     message = "Invalid action.";
                     success = false;
            }
        } catch (NumberFormatException e) {
            message = "Invalid amount entered. Please enter a valid number.";
            success = false;
        }

        String jsonResponse = String.format(
            "{\"success\": %b, \"message\": \"%s\", \"balance\": \"%s\"}",
            success, message, df.format(atm.checkBalance())
        );
        out.print(jsonResponse);
        out.flush();
    }
}