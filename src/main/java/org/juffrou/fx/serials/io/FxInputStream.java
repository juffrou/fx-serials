package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.juffrou.reflect.BeanWrapperContext;
import net.sf.juffrou.reflect.JuffrouBeanWrapper;

import org.juffrou.fx.serials.FxSerials;
import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.CannotInitializeFxPropertyListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deserializes traditional Java Beans into JavaFX2 Beans.<br>
 * The source Java Beans must declare implementation of the FxSerials interface.
 * 
 * @author Carlos Martins
 */
public class FxInputStream extends ObjectInputStream {
	
	private static final Logger logger = LoggerFactory.getLogger(FxInputStream.class);

	private final FxSerialsProxyBuilder proxyBuilder;
	
	private final Map<Class<?>, Class<?>> proxyCache = new HashMap<>();

	protected FxInputStream() throws IOException, SecurityException {
		super();
		this.proxyBuilder = new FxSerialsProxyBuilder();
	}

	public FxInputStream(InputStream in) throws IOException {
		this(in, new FxSerialsProxyBuilder());
	}

	public FxInputStream(InputStream in, FxSerialsProxyBuilder proxyBuilder) throws IOException {
		super(in);
		this.proxyBuilder = proxyBuilder;
		enableResolveObject(true);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,	ClassNotFoundException {
		
		Class<?> resolveClass = super.resolveClass(desc);
		
		if(logger.isDebugEnabled())
			logger.debug("resolving " + resolveClass.getName());
		
		if( implementsFxSerials(resolveClass) ) {
				Class<?> proxyClass = proxyBuilder.buildFXSerialsProxy(resolveClass, desc.getSerialVersionUID());
				
				proxyCache.put(resolveClass, proxyClass);

				if(logger.isDebugEnabled())
					logger.debug("resolved with proxy " + proxyClass.getName());
		}
		
		return resolveClass;
	}
	
	@Override
	protected Object resolveObject(Object obj) throws IOException {
		
		// If the object is an FxSerialsProxy instance, then initialize its properties list
		Class<?> proxyClass = proxyCache.get(obj.getClass());
		if(proxyClass != null) {
			try {
				BeanWrapperContext context = BeanWrapperContext.create(obj.getClass());
				JuffrouBeanWrapper srcWrapper = new JuffrouBeanWrapper(context, obj);
				Object proxyObj = proxyClass.newInstance();
				JuffrouBeanWrapper dstWrapper = new JuffrouBeanWrapper(context, proxyObj);
				for(String propName : srcWrapper.getPropertyNames())
					dstWrapper.setValue(propName, srcWrapper.getValue(propName));
				
				// initialize the properties map
				Method method = proxyClass.getMethod("initPropertiesList", null);
				method.invoke(proxyObj, null);
				
				return proxyObj;
				
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new CannotInitializeFxPropertyListException("Error calling initPropertiesList() on " + obj.getClass().getSimpleName() + ": " + e.getMessage(), e);
			} catch (InstantiationException e) {
				throw new CannotInitializeFxPropertyListException("Error instatiating proxy bean" + proxyClass.getName() + ": " + e.getMessage(), e);
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
	private boolean implementsFxSerials(Class<?> clazz) {
		for (Class<?> itf : clazz.getInterfaces())
			if (itf == FxSerials.class)
				return true;
		return false;
	}
}

