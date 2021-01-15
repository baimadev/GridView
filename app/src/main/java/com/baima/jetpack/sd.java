package com.baima.jetpack;

class sd {

    public boolean condition1;
    public boolean condition2;
    public boolean condition3;
    public boolean condition4;
    public boolean condition5;
    public boolean condition6;
    public boolean condition7;
    public boolean condition8;
    public boolean condition9;
    public boolean condition10;

    public void setModeA() {
        condition1 = true;
        condition2 = true;
        condition3 = true;
        condition4 = false;
        condition5 = false;
        condition6 = false;
        condition7 = false;
        condition8 = false;
        condition9 = false;
        condition10 = false;
    }

    public void setModeB() {
        condition1 = true;
        condition2 = false;
        condition3 = true;
        condition4 = false;
        condition5 = true;
        condition6 = false;
        condition7 = true;
        condition8 = false;
        condition9 = true;
        condition10 = false;
    }

    public void setModeC() {

    }

}

class ssd {
    static final int condition1 = 1 << 0;
    static final int condition2 = 1 << 1;
    static final int condition3 = 1 << 2;
    static final int condition4 = 1 << 3;
    static final int condition5 = 1 << 4;
    static final int condition6 = 1 << 5;
    static final int condition7 = 1 << 6;
    static final int condition8 = 1 << 7;
    static final int condition9 = 1 << 8;
    static final int condition10 = 1 << 9;
    static final int modeA = condition1 | condition2 | condition3 ;
    static final int modeB = condition1 | condition3 | condition5 | condition7 | condition9;
    static final int modeC = condition2 | condition6 | condition8 | condition10| condition4;

    int mFlags;

    public void setModeA() {
        mFlags = modeA;
    }

    public void setModeB() {
        mFlags = modeB;
    }

    public void setModeC() {
        mFlags = modeC;
    }

    public void removeCondition(int condition){
        mFlags = mFlags & ~condition;
    }
    public boolean isContainCondition(int condition){
        return  (mFlags & condition) != 0;
    }
    public void addCondition(int condition){
        mFlags |= condition;
    }
}

