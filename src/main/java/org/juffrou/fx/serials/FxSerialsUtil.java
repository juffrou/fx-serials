package org.juffrou.fx.serials;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javafx.beans.property.ReadOnlyProperty;

import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.FxTransformerException;
import org.juffrou.fx.serials.error.ObjectIsNotFxProxyException;
import org.juffrou.fx.serials.io.FxInputStream;

/**
 * Transforms a traditional Java Bean into a JavaFX2 Bean.
 * 
 * @author Carlos Martins
 */
public class FxSerialsUtil {
	
	private final FxSerialsProxyBuilder proxyBuilder = new FxSerialsProxyBuilder();


	/**
	 * Transforms a traditional Java Bean into a JavaFX2 Bean.
	 * @param bean a traditional java bean implementing the FXSerials interface.
	 * @return a JavaFX2 Bean
	 */
	public <T> T getProxy(T bean) {
		if( ! FxSerials.class.isAssignableFrom(bean.getClass()))
			throw new IllegalArgumentException("bean must implement FxSerials");
		if( FxSerialsProxy.class.isAssignableFrom(bean.getClass()))
			return bean;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(bean);
			out.flush();
			out.close();
			
			FxInputStream fxInputStream = new FxInputStream(new ByteArrayInputStream(bos.toByteArray()), proxyBuilder);
			return (T) fxInputStream.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			throw new FxTransformerException("Error deserializing bean", e);
		}
	}
	
	/**
	 * Gets a JavaFX property from a FxSerialsProxy instance.
	 * @param proxy proxy instance
	 * @param fieldName name of field to get the corresponding JavaFX property
	 * @return a read only or a read/write JavaFX property depending on whether the field is read only.
	 * 
	 */
	public static ReadOnlyProperty<?> getProperty(Object proxy, String fieldName) {
		if( ! FxSerialsProxy.class.isAssignableFrom(proxy.getClass()))
			throw new ObjectIsNotFxProxyException(); // @throws ObjectIsNotFxProxyException if the object passed does not implement the FxSerialsProxy interface
		FxSerialsProxy fxProxy = (FxSerialsProxy) proxy;
		return fxProxy.getProperty(fieldName);
	}
}
