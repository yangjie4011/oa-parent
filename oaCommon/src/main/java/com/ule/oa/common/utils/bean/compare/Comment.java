package com.ule.oa.common.utils.bean.compare;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ule.oa.common.utils.bean.compare.convertor.Convertor.Convertors;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {

    String value();
    
    Class<?>[] groups() default {};
    
    Convertors convertor() default Convertors.NULL;
    
}
