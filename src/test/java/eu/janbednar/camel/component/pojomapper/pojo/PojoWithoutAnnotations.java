package eu.janbednar.camel.component.pojomapper.pojo;

import java.util.Date;
import java.util.List;

public class PojoWithoutAnnotations {
    private int privateInt;
    private boolean privateBoolean;
    private byte privateByte;
    private char privateChar;
    private double privateDouble;
    private float privateFloat;
    private long privateLong;
    private short privateShort;

    private Date privateDate;
    private Object privateObject;
    private byte[] privateByteArray;
    private String[] privateStringArray;
    private List<String> privateListString;

    public int getPrivateInt() {
        return privateInt;
    }

    public boolean isPrivateBoolean() {
        return privateBoolean;
    }

    public byte getPrivateByte() {
        return privateByte;
    }

    public char getPrivateChar() {
        return privateChar;
    }

    public double getPrivateDouble() {
        return privateDouble;
    }

    public float getPrivateFloat() {
        return privateFloat;
    }

    public long getPrivateLong() {
        return privateLong;
    }

    public short getPrivateShort() {
        return privateShort;
    }

    public Date getPrivateDate() {
        return privateDate;
    }

    public Object getPrivateObject() {
        return privateObject;
    }

    public byte[] getPrivateByteArray() {
        return privateByteArray;
    }

    public String[] getPrivateStringArray() {
        return privateStringArray;
    }

    public List<String> getPrivateListString() {
        return privateListString;
    }
}
