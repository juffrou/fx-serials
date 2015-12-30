package org.juffrou.fx.serials;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.beans.property.ReadOnlyProperty;
import net.sf.juffrou.reflect.BeanWrapperFactory;
import net.sf.juffrou.reflect.DefaultBeanWrapperFactory;

import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.FxProxyInstantiationException;
import org.juffrou.fx.serials.error.FxTransformerException;
import org.juffrou.fx.serials.error.ObjectIsNotFxProxyException;
import org.juffrou.fx.serials.io.FxProxyRemoverInputStream;
import org.juffrou.fx.serials.io.FxProxyCreatorInputStream;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Transforms a traditional Java Bean into a JavaFX2 Bean.
 * 
 * @author Carlos Martins
 */
public class FxSerialsContext {
	
	private final FxSerialsProxyBuilder proxyBuilder = new FxSerialsProxyBuilder();
	private final BiMap<Class<?>, Class<?>> builderCache = HashBiMap.create();
	private final BeanWrapperFactory bwFactory = new DefaultBeanWrapperFactory();

	/**
	 * Transforms a traditional Java Bean into a JavaFX2 Bean.
	 * @param bean a traditional java bean implementing the FXSerials interface.
	 * @return a JavaFX2 Bean
	 */
	public <T> T getProxy(T bean) {
		Class<? extends Object> beanClass = bean.getClass();
		if(Collection.class.isAssignableFrom(beanClass)) {
			Object element = ((Collection<?>)bean).iterator().next();
			if(element == null)
				return bean;
			beanClass = element.getClass();
		}
		else if(Map.class.isAssignableFrom(beanClass)) {
			Set<?> entrySet = ((Map<?,?>)bean).entrySet();
			if(entrySet.isEmpty())
				return bean;
			Entry<?,?> entry = (Entry<?, ?>) entrySet.iterator().next();
			Object element = entry.getValue();
			beanClass = element.getClass();
		}
		if( ! JFXSerializable.class.isAssignableFrom(beanClass))
			throw new IllegalArgumentException("bean must implement FxSerials");
		if( JFXProxy.class.isAssignableFrom(beanClass))
			return bean;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(bean);
			out.flush();
			out.close();
			
			FxProxyCreatorInputStream fxInputStream = new FxProxyCreatorInputStream(new ByteArrayInputStream(bos.toByteArray()), proxyBuilder, builderCache, bwFactory);
			return (T) fxInputStream.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			throw new FxTransformerException("Error deserializing bean", e);
		}
	}
	
	/**
	 * Transforms a JavaFX2 bean created using {@link #getProxy(Class)} into its original bean
	 * @param proxy a JavaFX2 proxy bean created with {@link #getProxy(Class)}
	 * @return the original bean with updated values.
	 */
	public Object getOriginalBean(Object proxy) {
		Class<? extends Object> proxyClass = proxy.getClass();
		if(Collection.class.isAssignableFrom(proxyClass)) {
			Object element = ((Collection<?>)proxy).iterator().next();
			if(element == null)
				return proxy;
			proxyClass = element.getClass();
		}
		else if(Map.class.isAssignableFrom(proxyClass)) {
			Set<?> entrySet = ((Map<?,?>)proxy).entrySet();
			if(entrySet.isEmpty())
				return proxy;
			Entry<?,?> entry = (Entry<?, ?>) entrySet.iterator().next();
			Object element = entry.getValue();
			proxyClass = element.getClass();
		}
		if( ! JFXProxy.class.isAssignableFrom(proxyClass))
			throw new IllegalArgumentException("bean must implement FxSerialsProxy");
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(proxy);
			out.flush();
			out.close();
			
			FxProxyRemoverInputStream fxInputStream = new FxProxyRemoverInputStream(new ByteArrayInputStream(bos.toByteArray()), proxyBuilder, builderCache, bwFactory);
			return fxInputStream.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			throw new FxTransformerException("Error deserializing original bean", e);
		}
		
	}
	
	/**
	 * Creates a proxy of beanClass and instantiates it.
	 * @param beanClass the bean class to proxy
	 * @return new instance of the bean class proxy
	 */
	public <T> T getProxy(Class<T> beanClass) {
		
		ObjectStreamClass lookup = ObjectStreamClass.lookup(beanClass);
		long serialVersionUID = lookup == null ? 0L : lookup.getSerialVersionUID();
		Class<? extends T> serialsProxyClass = proxyBuilder.buildFXSerialsProxy(beanClass, serialVersionUID);
		try {
			T serialsProxy = serialsProxyClass.newInstance();
			return serialsProxy;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new FxProxyInstantiationException(e.getMessage(), e);
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
		if( ! JFXProxy.class.isAssignableFrom(proxy.getClass()))
			throw new ObjectIsNotFxProxyException(); // @throws ObjectIsNotFxProxyException if the object passed does not implement the FxSerialsProxy interface
		JFXProxy fxProxy = (JFXProxy) proxy;
		return fxProxy.getProperty(fieldName);
	}
}
