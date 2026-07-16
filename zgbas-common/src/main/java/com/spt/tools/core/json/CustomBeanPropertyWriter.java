/**
 * 
 */
package com.spt.tools.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;

/**
 * @author huangjian
 * 
 */
public class CustomBeanPropertyWriter extends BeanPropertyWriter {

	private static final long serialVersionUID = 3384990883269415368L;

	protected CustomBeanPropertyWriter(BeanPropertyWriter base) {
		super(base);
	}

	private JsonCode2Name getCode2Name() {
		if (_accessorMethod != null) {
			return _accessorMethod.getAnnotation(JsonCode2Name.class);
		}
		return _field.getAnnotation(JsonCode2Name.class);
	}

	public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider prov) throws Exception {
		Object value = get(bean);
		JsonCode2Name code2Name = getCode2Name();
		if (code2Name != null) {
//			value = DictUtil.getDictNameByCd(code2Name.dictType(), (String) value);
		}
		// Null handling is bit different, check that first
		if (value == null) {
			if (_nullSerializer != null) {
				jgen.writeFieldName(_name);
				_nullSerializer.serialize(null, jgen, prov);
			}
			return;
		}
		// then find serializer to use
		JsonSerializer<Object> ser = _serializer;
		if (ser == null) {
			Class<?> cls = value.getClass();
			PropertySerializerMap map = _dynamicSerializers;
			ser = map.serializerFor(cls);
			if (ser == null) {
				ser = _findAndAddDynamic(map, cls, prov);
			}
		}
		// and then see if we must suppress certain values (default, empty)
		if (_suppressableValue != null) {
			if (MARKER_FOR_EMPTY == _suppressableValue) {
				if (ser.isEmpty(prov,value)) {
					return;
				}
			} else if (_suppressableValue.equals(value)) {
				return;
			}
		}
		// For non-nulls: simple check for direct cycles
		if (value == bean) {
			_handleSelfReference(bean, jgen, prov, ser);
		}
		jgen.writeFieldName(_name);
		if (_typeSerializer == null) {
			ser.serialize(value, jgen, prov);
		} else {
			ser.serializeWithType(value, jgen, prov, _typeSerializer);
		}
	}

}
