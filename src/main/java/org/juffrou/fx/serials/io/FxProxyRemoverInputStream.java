package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.juffrou.fx.serials.JFXProxy;
import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.CannotInitializeFxPropertyListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.sf.juffrou.reflect.BeanWrapperContext;
import net.sf.juffrou.reflect.BeanWrapperFactory;
import net.sf.juffrou.reflect.DefaultBeanWrapperFactory;
import net.sf.juffrou.reflect.JuffrouBeanWrapper;

/**
 * Deserializes JavaFX2 Beans into traditional Java Beans.<br>
 * The source JavaFX2 Beans must have been created by the FxInputStream class.
 * 
 * @author Carlos Martins
 */
public class FxProxyRemoverInputStream extends ObjectInputStream {
	
	private static final Logger logger = LoggerFactory.getLogger(FxProxyRemoverInputStream.class);

	// The builder who builds java fx proxys
	private final FxSerialsProxyBuilder proxyBuilder;
	
	// A Bimap with original class as key and respective proxy class as value.
	private final BiMap<Class<?>, Class<?>> proxyCache;
	
	// Factory for creating bean wrapper contexts to read the normal classes
	private final BeanWrapperFactory bwFactory;

	
	protected FxProxyRemoverInputStream() throws IOException, SecurityException {
		super();
		this.proxyBuilder = new FxSerialsProxyBuilder();
		this.proxyCache = HashBiMap.create();
		this.bwFactory = new DefaultBeanWrapperFactory();
	}

	public FxProxyRemoverInputStream(InputStream in) throws IOException {
		this(in, new FxSerialsProxyBuilder(), HashBiMap.create(), new DefaultBeanWrapperFactory());
	}
	
	public FxProxyRemoverInputStream(InputStream in, FxSerialsProxyBuilder proxyBuilder, BiMap<Class<?>, Class<?>> builderCache, BeanWrapperFactory bwFactory) throws IOException {
		super(in);
		this.proxyBuilder = proxyBuilder;
		this.proxyCache = builderCache;
		this.bwFactory = bwFactory;
		enableResolveObject(true);
	}


	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,	ClassNotFoundException {
		
		Class<?> resolveClass = super.resolveClass(desc);
		
		if(logger.isDebugEnabled())
			logger.debug("resolving " + resolveClass.getName());
		
		if( implementsFxSerialsProxy(resolveClass) ) {
				Class<?> originalClass = proxyBuilder.cleanFXSerialsProxy(resolveClass);
				
				proxyCache.put(resolveClass, originalClass);

				if(logger.isDebugEnabled())
					logger.debug("resolved with proxy " + originalClass.getName());
		}
		
		return resolveClass;
	}
	
	@Override
	protected Object resolveObject(Object obj) throws IOException {
		
		// If the object is an FxSerialsProxy instance, then initialize its properties list
		Class<?> originalClass = proxyCache.inverse().get(obj.getClass());
		if(originalClass != null) {
			try {
				
				// copy the properties from obj to proxy
				BeanWrapperContext context = bwFactory.getBeanWrapperContext(originalClass);
				JuffrouBeanWrapper srcWrapper = new JuffrouBeanWrapper(context, obj);
				Object originalObj = originalClass.newInstance();
				JuffrouBeanWrapper dstWrapper = new JuffrouBeanWrapper(context, originalObj);
				for(String propName : dstWrapper.getPropertyNames())
					dstWrapper.setValue(propName, srcWrapper.getValue(propName));
				
				return originalObj;
				
			} catch (IllegalAccessException | InstantiationException e) {
				throw new CannotInitializeFxPropertyListException("Error instatiating original class " + originalClass.getName() + ": " + e.getMessage(), e);
			}
		}
		
		return obj;
	}
	
	@Override
	public Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
		return super.resolveProxyClass(interfaces);
	}
	
	/**
	 * Test if a class declares to implement the interface FxSerials
	 * 
	 * @param clazz
	 *            class to test
	 * @return true if the class declares FxSerials implementation
	 */
	private boolean implementsFxSerialsProxy(Class<?> clazz) {
		for (Class<?> itf : clazz.getInterfaces())
			if (itf == JFXProxy.class)
				return true;
		return false;
	}
}

