package com.mybank.tui;

import com.mybank.domain.Account;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu  

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu 

        setFocusFollowsMouse(true);
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {
        TWindow custWin = addWindow("Customer Window", 2, 1, 40, 10, TWindow.NOZOOMBOX);
        custWin.newStatusBar("Enter valid customer number and press Show...");

        custWin.addLabel("Enter customer number: ", 2, 2);
        TField custNo = custWin.addField(24, 2, 3, false);
        TText details = custWin.addText("Owner Name: \nAccount Type: \nAccount Balance: ", 2, 4, 38, 8);
        custWin.addButton("&Show", 28, 2, new TAction() {
            @Override
            public void DO() {
                try {
                    int custNum = Integer.parseInt(custNo.getText());
                    //details about customer with index==custNum
                    
                    File file = new File("data/test.dat");
                    BufferedReader in = new BufferedReader(new FileReader(file)); 
                    String line = in.readLine();
                    String[] customerData;
                    int counter = 0;
                    
                    while ( line != null) {
                        String[] splitOut = line.split("\t");
                        
                        if(line.equals(""))  {
                            counter +=1;
                            line = in.readLine();
                        } else if (counter == custNum) {
                            if(!splitOut[0].equals("S") && !splitOut[0].equals("C")) {
                                Customer customer = new Customer(splitOut[0], splitOut[1]);
                               
                                StringBuilder rd = new StringBuilder();
                                rd.append("Owner Name: ").append(customer.getFirstName()).append(" ").append(customer.getLastName()).append(" [id=").append(custNum).append("]\n");
                                
                                line = in.readLine();
                                while( !"".equals(line) && line != null){
                                    customerData = line.split("\t");
                                    
                                    if(customerData[0].equals("S")){
                                        customer.addAccount(new SavingsAccount(
                                            Double.parseDouble(customerData[1]),
                                            Double.parseDouble(customerData[2])    
                                        ));   
                                    }
                                    else if(customerData[0].equals("C")){
                                        customer.addAccount(new CheckingAccount(
                                            Double.parseDouble(customerData[1]),
                                            Double.parseDouble(customerData[2])    
                                        ));   
                                    }
                                    line = in.readLine();
                                }
                               
                                for( int i = 0; i < customer.getNumberOfAccounts(); i++) {
                                    Account account = customer.getAccount(i);
                                    
                                    if(account instanceof SavingsAccount) {
                                        rd.append("\nAccount Type: Savings -- ").append(account.getBalance());
                                    } else if(account instanceof CheckingAccount) {
                                        rd.append("\nAccount Type: Checking -- ").append(account.getBalance());
                                    }
                                }
                                details.setText(rd.toString());
                                line = null;
                            } else {
                                line = in.readLine();
                            }
                        }
                        else{
                            line = in.readLine();
                        }
                    }
                    in.close();
                }
                catch (Exception e) {
                    messageBox("Error", "You must provide a valid customer number!").show();
                }
            }
        });
    }
}
