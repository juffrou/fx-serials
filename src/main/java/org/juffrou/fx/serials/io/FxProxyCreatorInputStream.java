package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.juffrou.fx.serials.JFXProxy;
import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.CannotInitializeFxPropertyListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.sf.juffrou.reflect.BeanWrapperFactory;
import net.sf.juffrou.reflect.DefaultBeanWrapperFactory;

/**
 * Deserializes traditional Java Beans into JavaFX2 Beans.<br>
 * The source Java Beans must declare implementation of the FxSerials interface.
 * 
 * @author Carlos Martins
 */
public class FxProxyCreatorInputStream extends ObjectInputStream {

	private static final Logger logger = LoggerFactory.getLogger(FxProxyCreatorInputStream.class);

	// The builder who builds java fx proxys
	private final FxSerialsProxyBuilder proxyBuilder;

	// A Bimap with Class as key and Proxy Class as Value
	private final BiMap<Class<?>, Class<?>> proxyCache;

	// Factory for creating bean wrapper contexts to read the normal classes
	private final BeanWrapperFactory bwFactory;

	protected FxProxyCreatorInputStream() throws IOException, SecurityException {
		super();
		this.proxyBuilder = new FxSerialsProxyBuilder();
		this.proxyCache = HashBiMap.create();
		this.bwFactory = new DefaultBeanWrapperFactory();

	}

	public FxProxyCreatorInputStream(InputStream in) throws IOException {
		this(in, new FxSerialsProxyBuilder(), HashBiMap.create(), new DefaultBeanWrapperFactory());
	}

	public FxProxyCreatorInputStream(InputStream in, FxSerialsProxyBuilder proxyBuilder,
			BiMap<Class<?>, Class<?>> builderCache, BeanWrapperFactory bwFactory) throws IOException {
		super(in);
		this.proxyBuilder = proxyBuilder;
		this.proxyCache = builderCache;
		this.bwFactory = bwFactory;
		enableResolveObject(true);
	}

	/*
	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {

		Class<?> resolveClass = super.resolveClass(desc);

		if (implementsFxSerials(resolveClass)) {
			if (logger.isDebugEnabled())
				logger.debug("resolving " + resolveClass.getName());

			Class<?> proxyClass = proxyBuilder.buildFXSerialsProxy(resolveClass, desc.getSerialVersionUID());

			proxyCache.put(resolveClass, proxyClass);

			if (logger.isDebugEnabled())
				logger.debug("resolved with proxy " + proxyClass.getName());
		}

		return resolveClass;
	}
	*/

	@Override
	protected Object resolveObject(Object obj) throws IOException {

		// If the object is an FxSerialsProxy instance, then initialize its
		// properties list
		if(JFXProxy.class.isAssignableFrom(obj.getClass())) {
			try {

				Class<?> proxyClass = obj.getClass();
				
				// initialize the properties map
				Method method = proxyClass.getMethod("initPropertiesList", null);
				method.invoke(obj, null);

			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new CannotInitializeFxPropertyListException("Error calling initPropertiesList() on "
						+ obj.getClass().getSimpleName() + ": " + e.getMessage(), e);
			}
		}

		return obj;
	}

}
