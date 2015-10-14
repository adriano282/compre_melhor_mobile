package br.com.compremelhor.model;

/**
 * Created by adriano on 29/09/15.
 */
public enum  TypeDocument {
    CPF("cpf"),
    CNPJ("cnpj");

    private String type;
    TypeDocument(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
