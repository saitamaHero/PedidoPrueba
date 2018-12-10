package com.mobile.proisa.pedidoprueba.Clases;

import Models.Invoice;

public class InvoiceType {
    private Invoice.InvoicePayment invoicePayment;

    public InvoiceType(Invoice.InvoicePayment invoicePayment) {
        this.invoicePayment = invoicePayment;
    }

    public Invoice.InvoicePayment getInvoicePayment() {
        return invoicePayment;
    }


}
