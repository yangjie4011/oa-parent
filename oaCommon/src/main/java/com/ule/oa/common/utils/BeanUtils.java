package com.ule.oa.common.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import com.ule.oa.common.utils.bean.compare.Comment;
import com.ule.oa.common.utils.bean.compare.convertor.Convertor;


public class BeanUtils extends org.springframework.beans.BeanUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);
	
	public static void copyProperties(Object source, Object target) throws BeansException {
		copyProperties(source, target, null, null);
	}
	
	public static void copyProperties(Object source, Object target, Class<?> editable)
			throws BeansException {

		copyProperties(source, target, editable, null);
	}
	
	public static void copyProperties(Object source, Object target, Class<?> editable, String[] ignoreProperties)
			throws BeansException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
						"] not assignable to Editable class [" + editable.getName() + "]");
			}
			actualEditable = editable;
		}
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

		for (PropertyDescriptor targetPd : targetPds) {
			if (targetPd.getWriteMethod() != null &&
					(ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null && sourcePd.getReadMethod() != null) {
					try {
						Method readMethod = sourcePd.getReadMethod();
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if (null == value) {
							continue;
						}
						Method writeMethod = targetPd.getWriteMethod();
						if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
							writeMethod.setAccessible(true);
						}
						writeMethod.invoke(target, value);
					}
					catch (Throwable ex) {
						throw new FatalBeanException("Could not copy properties from source to target", ex);
					}
				}
			}
		}
	}
	
	public static void copyPropertiesNotEmpty(Object source, Object target)
			throws BeansException {
		copyProperties(source, target, null, null);
	}
	
	public static String compareProperties(Object source, Object target) {
	    return compareProperties(source, target, null);
	}
	
	/**
	 * 比较两个对象相同属性名的值，属性上必须有com.ule.oa.common.utils.bean.Comment注解
	 * @param source
	 * @param target
	 * @return
	 */
    public static String compareProperties(Object source, Object target, Class<?> group) {
        if (null == source || null == target) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try {
            Class<?> clazz = target.getClass();
            PropertyDescriptor[] targetPds = getPropertyDescriptors(clazz);
            for (PropertyDescriptor targetPd : targetPds) {
                Comment comment = getComment(clazz, targetPd, group);
                if (null == comment) {
                    continue;
                }
                PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                Method sourceReadMethod = sourcePd.getReadMethod();
                if (!Modifier.isPublic(sourceReadMethod.getDeclaringClass().getModifiers())) {
                    sourceReadMethod.setAccessible(true);
                }
                Object sourceValue = sourceReadMethod.invoke(source);
                Method targetReadMethod = targetPd.getReadMethod();
                if (!Modifier.isPublic(targetReadMethod.getDeclaringClass().getModifiers())) {
                    targetReadMethod.setAccessible(true);
                }
                Object targetValue = targetReadMethod.invoke(target);
                generateDiffDesc(sb, comment, sourceValue, targetValue);
            }
        } catch (Exception e) {
            logger.error("Could not compare properties from source to target : {}", e.getMessage());
        }
        return sb.toString();
    }

    private static void generateDiffDesc(StringBuilder sb, Comment comment,
            Object sourceValue, Object targetValue) {
        String before = null == sourceValue ? "" : sourceValue.toString();
        String after = null == targetValue ? "" : targetValue.toString();
        if (!before.equals(after)) {
            Convertor convertor = comment.convertor().getConvertor();
            if (null != convertor) {
                before = convertor.convert(before);
                after = convertor.convert(after);
            }
            if (!before.equals(after)) {
                sb.append(comment.value()).append(" 由 ").append(before).append(" 修改为 ")
                .append(after).append(";\r\n");
            }
        }
    }
    
	private static Comment getComment(Class<?> clazz, PropertyDescriptor pd, Class<?> group) {
	    Field field = getDeclaredField(clazz, pd.getName());
        if (null == field) {
            return null;
        }
        Comment comment = field.getAnnotation(Comment.class);
        if (null == comment) {
            return null;
        }
        if (null == group) {
            return comment;
        }
        Class<?>[] groups = comment.groups();
        if (null != groups && groups.length > 0) {
            for (Class<?> defGroup : groups) {
                if (defGroup.equals(group)) {
                    return comment;
                }
            }
        }
        return null;
	}

    private static Field getDeclaredField(Class<?> clazz, String fieldName) {
	    for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
	        try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
            }
	    }
	    return null;
	}

}
