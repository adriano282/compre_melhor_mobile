package br.com.compremelhor.model;

/**
 * Created by adriano on 25/08/15.
 */
public class Code extends DomainEntity {
    private String code;
    private String type;

    public Code() {}

    public Code(String code, String type) {
        this.code = code;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setType(String type) {
        this.type = type;
    }

    public enum CodeType {
        BAR_CODE,
        QR_CODE
    }
}
