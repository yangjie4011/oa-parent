package com.ule.oa.common.utils.bean.compare.convertor;

/**
 * 属性值转换器
 * @author zhangwei002
 *
 */
public interface Convertor {

    String convert(Object o);
    
    public enum Convertors{
        TrueFalse(new TrueFalseConvertor()),
        
        NULL();
        
        private Convertor convertor;
        
        Convertors() {}
        
        Convertors(Convertor convertor) {
            this.convertor = convertor;
        }

        public Convertor getConvertor() {
            return convertor;
        }

//        public void setConvertor(Convertor convertor) {
//            this.convertor = convertor;
//        }
        
    }
    
}
