package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

import org.juffrou.fx.serials.JFXSerializable;
import org.juffrou.fx.serials.core.FXProxyCache;
import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.CannotInitializeFxPropertyListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.juffrou.reflect.BeanWrapperContext;
import net.sf.juffrou.reflect.BeanWrapperFactory;
import net.sf.juffrou.reflect.DefaultBeanWrapperFactory;
import net.sf.juffrou.reflect.JuffrouBeanWrapper;

public class FxProxyCreatorOutputStream extends ObjectOutputStream {

	private static final Logger logger = LoggerFactory.getLogger(FxProxyCreatorOutputStream.class);

	// The builder who builds java fx proxys
	private final FxSerialsProxyBuilder proxyBuilder;

	// A Bimap with Class as key and Proxy Class as Value
	private final FXProxyCache proxyCache;

	// Factory for creating bean wrapper contexts to read the normal classes
	private final BeanWrapperFactory bwFactory;

	public FxProxyCreatorOutputStream(OutputStream out) throws IOException {
		this(out, new FxSerialsProxyBuilder(), new FXProxyCache(), new DefaultBeanWrapperFactory());
	}
	
	public FxProxyCreatorOutputStream(OutputStream out, FxSerialsProxyBuilder proxyBuilder,
			FXProxyCache builderCache, BeanWrapperFactory bwFactory) throws IOException {
		super(out);
		this.proxyBuilder = proxyBuilder;
		this.proxyCache = builderCache;
		this.bwFactory = bwFactory;
		enableReplaceObject(true);
	}

	@Override
	protected Object replaceObject(Object obj) throws IOException {

		Class<? extends Object> resolveClass = obj.getClass();
		if (implementsFxSerials(resolveClass)) {
			
			Class<?> proxyClass = proxyCache.getProxyFromOriginalClass(resolveClass);
			if (proxyClass == null) {
				
				if (logger.isDebugEnabled())
					logger.debug("resolving proxy of " + resolveClass.getName());

				ObjectStreamClass desc = ObjectStreamClass.lookup(resolveClass);
				proxyClass = proxyBuilder.buildFXSerialsProxy(resolveClass, desc.getSerialVersionUID());
				
				proxyCache.put(resolveClass, proxyClass);
				
				if (logger.isDebugEnabled())
					logger.debug("resolved: " + proxyClass.getName());
			}
			
			try {
				
				// copy the properties from obj to proxy
				BeanWrapperContext context = bwFactory.getBeanWrapperContext(resolveClass);
				JuffrouBeanWrapper srcWrapper = new JuffrouBeanWrapper(context, obj);
				Object proxyObj = proxyClass.newInstance();
				JuffrouBeanWrapper dstWrapper = new JuffrouBeanWrapper(context, proxyObj);
				for (String propName : srcWrapper.getPropertyNames())
					dstWrapper.setValue(propName, srcWrapper.getValue(propName));
				
				obj = proxyObj;
			}
			catch (SecurityException | IllegalAccessException | IllegalArgumentException e) {
				throw new CannotInitializeFxPropertyListException("Error calling initPropertiesList() on "
						+ obj.getClass().getSimpleName() + ": " + e.getMessage(), e);
			} catch (InstantiationException e) {
				throw new CannotInitializeFxPropertyListException(
						"Error instatiating proxy class " + proxyClass.getName() + ": " + e.getMessage(), e);
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
	private boolean implementsFxSerials(Class<?> clazz) {
		for (Class<?> itf : clazz.getInterfaces())
			if (itf == JFXSerializable.class)
				return true;
		return false;
	}

}
