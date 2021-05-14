package com.ule.oa.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloneObject {
	private static Logger log = LoggerFactory.getLogger(CloneObject.class);

	public static Object clone(Object object) {
		Object o = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(object);

			ByteArrayInputStream byteIn = new ByteArrayInputStream(
					byteOut.toByteArray());
			ObjectInputStream in = new ObjectInputStream(byteIn);

			o = in.readObject();
		} catch (IOException e) {
			log.error("clone IO", e);
		} catch (ClassNotFoundException e) {
			log.error("clone ClassNotFound", e);
		}
		return o;
	}
}
