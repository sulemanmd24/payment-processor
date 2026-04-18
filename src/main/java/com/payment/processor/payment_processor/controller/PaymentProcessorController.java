package com.payment.processor.payment_processor.controller;

import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentProcessorController {

    @GetMapping("/metrics/summary")
    public String getMetricsSummary() {
        return "Metrics Summary: Total Payments: 100, Total Amount: $5000, Average Amount: $50";
    }

    @GetMapping("/reports/summary")
    public String getDailyReport() {
        return "Testing";
    }

    @GetMapping("/reports/activity")
    public String getActivityReport() {
        return "Testing";
    }

    @GetMapping("/accounts/{accountId}/history")
    public String getTransactionsForAccount(@PathParam("accountId") String accountId) {
        return "Transactions for Account: 12345 - [Transaction1, Transaction2, Transaction3]";
    }
}
