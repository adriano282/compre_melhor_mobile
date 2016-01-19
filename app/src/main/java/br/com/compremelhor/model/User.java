package br.com.compremelhor.model;

/**
 * Created by adriano on 29/09/15.
 */
public class User  extends DomainEntity{
    private String name;
    private String email;
    private String document;
    private TypeDocument typeDocument;
    private String password;
    private byte[] bytesPicture;

    public User() {}
    public User(String  name, String email) {
        this.name = name;
        this.email = email;
    }

    public void setPicture(byte[] bytes) {
        this.bytesPicture = bytes;
    }

    public byte[] getBytesPicture() {
        return bytesPicture;
    }

    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String type) {
        if (type == null)
            return;

        if (type.equals(TypeDocument.CNPJ.toString())) {
            this.typeDocument = TypeDocument.CNPJ;
        } else if (type.equals(TypeDocument.CPF.toString())) {
            this.typeDocument = TypeDocument.CPF;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
