/**
 * 
 */
package com.spt.tools.core.json;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;

/**
 * @author huangjian
 * 
 */
public class CustomerSerializerFactory extends BeanSerializerFactory {
	private static final long serialVersionUID = 6413443284504159263L;

	public CustomerSerializerFactory(SerializerFactoryConfig config) {
		super(config);
	}

	@Override
	protected void processViews(SerializationConfig config, BeanSerializerBuilder builder) {
		// TODO Auto-generated method stub
		List<BeanPropertyWriter> props = builder.getProperties();
		boolean includeByDefault = config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
		final int propCount = props.size();
		BeanPropertyWriter[] filtered = new BeanPropertyWriter[propCount];
		// Simple: view information is stored within individual writers, need to
		// combine:
		List<BeanPropertyWriter> properties = new ArrayList<BeanPropertyWriter>();
		for (int i = 0; i < propCount; ++i) {
			BeanPropertyWriter bpw = props.get(i);
			Class<?>[] views = bpw.getViews();
			if (views == null) { // no view info? include or exclude by default?
				if (includeByDefault) {
					CustomBeanPropertyWriter propertyWriter = new CustomBeanPropertyWriter(bpw);
					filtered[i] = propertyWriter;
					properties.add(propertyWriter);
				}
			} else {
				filtered[i] = constructFilteredBeanWriter(bpw, views);
			}
		}
		builder.setProperties(properties);
	}
}
