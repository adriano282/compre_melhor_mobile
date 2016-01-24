package br.com.compremelhor;

import com.stripe.model.Token;

public interface TokenList {
    void addToList(Token token);
}
