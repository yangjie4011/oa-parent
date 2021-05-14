package com.ule.oa.common.utils.bean.compare.convertor;

public class TrueFalseConvertor implements Convertor {

    private String t = "0";
    
    private String tStr = "是";
    
    private String f = "1";
    
    private String fStr = "否";
    
    @Override
    public String convert(Object o) {
        if (null != o) {
            if (t.equals(o.toString())) {
                return tStr;
            }
            if (f.equals(o.toString())) {
                return fStr;
            }
        }
        return null;
    }

}
