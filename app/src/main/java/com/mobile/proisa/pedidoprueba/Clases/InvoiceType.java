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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        InvoiceType other = (InvoiceType) obj;

        return this.getInvoicePayment().equals(other.getInvoicePayment());
    }
}
