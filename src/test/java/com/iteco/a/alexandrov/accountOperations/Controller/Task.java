package com.iteco.a.alexandrov.accountOperations.Controller;

import com.iteco.a.alexandrov.accountOperations.AccountOperationsApplication;

import java.util.concurrent.Callable;




public class Task implements Runnable {
        ControllerIntegrationTest controllerIntegrationTest = new ControllerIntegrationTest();

    @Override
    public void run() {
        try {
            controllerIntegrationTest.testPostTransactions_whenPostTransactions_thenHttp201_andJsonResponseMessage();
        } catch (Exception e) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            e.printStackTrace();
        }
    }
}
