package br.com.compremelhor.model;

/**
 * Created by adriano on 29/09/15.
 */
public class User {
    private Long id;
    private String name;
    private String email;
    private String document;
    private TypeDocument typeDocument;
    private String password;

    public TypeDocument getTypeDocument() {
        return typeDocument;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setTypeDocument(String type) {
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
