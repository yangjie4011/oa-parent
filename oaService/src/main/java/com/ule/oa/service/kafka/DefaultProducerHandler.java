package com.ule.oa.service.kafka;

import java.io.Serializable;
import java.util.Random;

import kafka.utils.VerifiableProperties;

import com.ule.tools.client.kafka.producer.handler.AbstractProducerHandler;

public class DefaultProducerHandler<V extends Serializable> extends AbstractProducerHandler<V>{
	
	public DefaultProducerHandler(VerifiableProperties props) {
		super(props);
	}

	@Override
	public int partition(String key, int partitionsCount) {
		if ((key == null) || ("".equals(key.trim()))) {
			return new Random().nextInt(partitionsCount);
	    }
		int hashCode = key.hashCode();
		if(hashCode!=0) {
			 return Math.abs(hashCode) % partitionsCount;
		}
		return 0;
	}

}
