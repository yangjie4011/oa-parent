package com.ule.oa.common.persistence.page;

import java.lang.reflect.Field;
import java.util.Map;

import com.ule.oa.common.utils.PageModel;

public class PageHelper {

	public static PageModel<?> hasPage(Object obj) {
		if (null == obj) {
			return null;
		}
		if (obj instanceof PageModel) {
			return (PageModel<?>) obj;
		}
		if (obj instanceof Map) {
			Map<?,?> map = (Map<?,?>) obj;
			for (Map.Entry<?,?> entry : map.entrySet()) {
				Object value = entry.getValue();
				if (null == value) {
					continue;
				}
				if (value instanceof PageModel) {
					return (PageModel<?>) value;
				}
				PageModel<?> pm = getFromAnnotation(value);
				if (null != pm) {
					return pm;
				}
			}
		} else {
			PageModel<?> pm1 = getFromAnnotation(obj);
			if (null != pm1) {
				return pm1;
			}
		}
		return null;
	}
	
	private static PageModel<?> getFromAnnotation(Object obj) {
		Class<?> clazz = obj.getClass();
		PageContainer pc = (PageContainer) clazz.getAnnotation(PageContainer.class);
		if (null != pc) {
			try {
				Field field = clazz.getDeclaredField(pc.fieldName());
				field.setAccessible(true);
				PageModel<?> pm = (PageModel<?>) field.get(obj);
				if (null != pm) {
					return pm;
				}
			} catch (Exception e){
			}
		}
		return null;
	}
}
