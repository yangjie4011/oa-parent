package com.ule.oa.common.utils.xml;

import java.util.Collection;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XmlUtils {
	
	public static Object fromXML(String xml,Collection<Class<?>> classList){
		XStream xstream = new XStream();
		if (null != classList) {
			xstream.processAnnotations(classList.toArray(new Class[classList.size()]));
			xstream.autodetectAnnotations(true);
		}
		return xstream.fromXML(xml);
	}
	
	public static Object fromXML(String xml, Class<?>[] clazzArray){
		XStream xstream = new XStream();
		if (null != clazzArray) {
			xstream.processAnnotations(clazzArray);
			xstream.autodetectAnnotations(true);
		}
		return xstream.fromXML(xml);
	}
	
	public static Object fromXML(String xml,Class<?> clazz){
		XStream xstream = new XStream();
		xstream.processAnnotations(clazz);
		xstream.autodetectAnnotations(true);
		return xstream.fromXML(xml);
	}
	
	public static String toXML(Object obj,Class<?> clazz){
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(clazz);
		xstream.autodetectAnnotations(true);
		return  xstream.toXML(obj);
	}
}
