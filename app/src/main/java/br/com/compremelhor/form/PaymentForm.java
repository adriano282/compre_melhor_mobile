package br.com.compremelhor.form;

public interface PaymentForm {
    String getCardNumber();
    String getCvc();
    Integer getExpMonth();
    Integer getExpYear();
    String getCurrency();
}
