package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.juffrou.fx.serials.JFXProxy;
import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.CannotInitializeFxPropertyListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;

import net.sf.juffrou.reflect.BeanWrapperContext;
import net.sf.juffrou.reflect.BeanWrapperFactory;
import net.sf.juffrou.reflect.JuffrouBeanWrapper;

public class FxProxyRemoverOutputStream extends ObjectOutputStream {

	private static final Logger logger = LoggerFactory.getLogger(FxProxyRemoverOutputStream.class);

	// The builder who builds java fx proxys
	private final FxSerialsProxyBuilder proxyBuilder;

	// A Bimap with Class as key and Proxy Class as Value
	private final BiMap<Class<?>, Class<?>> proxyCache;

	// Factory for creating bean wrapper contexts to read the normal classes
	private final BeanWrapperFactory bwFactory;

	public FxProxyRemoverOutputStream(OutputStream out, FxSerialsProxyBuilder proxyBuilder,
			BiMap<Class<?>, Class<?>> builderCache, BeanWrapperFactory bwFactory) throws IOException {
		super(out);
		this.proxyBuilder = proxyBuilder;
		this.proxyCache = builderCache;
		this.bwFactory = bwFactory;
		enableReplaceObject(true);
	}

	@Override
	protected Object replaceObject(Object obj) throws IOException {

		Class<? extends Object> proxyClass = obj.getClass();
		if (implementsFxProxy(proxyClass)) {
			
			Class<?> originalClass = proxyCache.inverse().get(proxyClass);
			if (originalClass == null) {
				
				if (logger.isDebugEnabled())
					logger.debug("resolving proxy " + proxyClass.getName());

				originalClass = proxyBuilder.cleanFXSerialsProxy(proxyClass);

				proxyCache.put(originalClass, proxyClass);

				if (logger.isDebugEnabled())
					logger.debug("resolved original " + originalClass.getName());
			}
			
			try {
				
				// copy the properties from obj to proxy
				BeanWrapperContext context = bwFactory.getBeanWrapperContext(originalClass);
				JuffrouBeanWrapper srcWrapper = new JuffrouBeanWrapper(context, obj);
				Object originalObj = originalClass.newInstance();
				JuffrouBeanWrapper dstWrapper = new JuffrouBeanWrapper(context, originalObj);
				for (String propName : srcWrapper.getPropertyNames())
					dstWrapper.setValue(propName, srcWrapper.getValue(propName));
				
				obj = originalObj;
			}
			catch (SecurityException | IllegalAccessException | IllegalArgumentException e) {
				throw new CannotInitializeFxPropertyListException("Error calling initPropertiesList() on "
						+ obj.getClass().getSimpleName() + ": " + e.getMessage(), e);
			} catch (InstantiationException e) {
				throw new CannotInitializeFxPropertyListException(
						"Error instatiating proxy class " + originalClass.getName() + ": " + e.getMessage(), e);
			}
			
			
		}

		return obj;
	}
	
	/**
	 * Test if a class declares to implement the interface FxSerials
	 * 
	 * @param clazz
	 *            class to test
	 * @return true if the class declares FxSerials implementation
	 */
	private boolean implementsFxProxy(Class<?> clazz) {
		for (Class<?> itf : clazz.getInterfaces())
			if (itf == JFXProxy.class)
				return true;
		return false;
	}

}
