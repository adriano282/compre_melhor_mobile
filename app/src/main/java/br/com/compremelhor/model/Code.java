package br.com.compremelhor.model;

/**
 * Created by adriano on 25/08/15.
 */
public class Code {
    String code;
    CodeType type;

    enum CodeType {
        BAR_CODE,
        QR_CODE
    }
}
