package br.com.compremelhor;

public interface PaymentForm {
    String getCardNumber();
    String getCvc();
    Integer getExpMonth();
    Integer getExpYear();
    String getCurrency();
}
