package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.juffrou.fx.serials.FxSerials;
import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.FxSerialsProxyAlreadExistsException;

/**
 * Deserializes traditional Java Beans into JavaFX2 Beans.<br>
 * The source Java Beans must declare implementation of the FxSerials interface.
 * 
 * @author Carlos Martins
 */
public class FxInputStream extends ObjectInputStream {

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
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,	ClassNotFoundException {
		
		Class<?> resolveClass = super.resolveClass(desc);
		
		if( implementsFxSerials(resolveClass) )
			try {
				resolveClass = proxyBuilder.buildFXSerialsProxy(resolveClass, desc.getSerialVersionUID());
			} catch (FxSerialsProxyAlreadExistsException e) { }
		
		return resolveClass;
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

