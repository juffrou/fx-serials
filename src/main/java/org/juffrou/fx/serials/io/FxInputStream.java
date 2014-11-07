package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.juffrou.fx.serials.FxSerials;
import org.juffrou.fx.serials.FxSerialsProxy;
import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.CannotInitializeFxPropertyListException;
import org.juffrou.fx.serials.error.FxSerialsProxyAlreadExistsException;
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
		
		if( implementsFxSerials(resolveClass) )
			try {
				resolveClass = proxyBuilder.buildFXSerialsProxy(resolveClass, desc.getSerialVersionUID());

				if(logger.isDebugEnabled())
					logger.debug("created proxy " + resolveClass.getName());
				
			} catch (FxSerialsProxyAlreadExistsException e) { 
				if(logger.isDebugEnabled())
					logger.debug("did not create proxy (already exists): " + resolveClass.getName() + " - " + e.getMessage(), e);
			}
		
		return resolveClass;
	}
	
	@Override
	protected Object resolveObject(Object obj) throws IOException {
		
		// If the object is an FxSerialsProxy instance, then initialize its properties list
		if(FxSerialsProxy.class.isAssignableFrom(obj.getClass())) {
			try {
				Method method = obj.getClass().getMethod("initPropertiesList", null);
				method.invoke(obj, null);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new CannotInitializeFxPropertyListException("Error calling initPropertiesList() on " + obj.getClass().getSimpleName() + ": " + e.getMessage(), e);
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
			if (itf == FxSerials.class)
				return true;
		return false;
	}
}

