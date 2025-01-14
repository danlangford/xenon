package com.avereon.xenon;

import com.avereon.product.ProductCard;
import com.avereon.util.LogUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;

/**
 * Mods are the way to provide functionality in the program. Otherwise the
 * program is nothing more than a framework. The Mod class provided the basic
 * interface and implementation of a Mod. Subclasses should use the register(),
 * create(), destroy() and unregister() lifecycle methods to interact with the
 * program.
 */
public abstract class Mod implements ProgramProduct, Comparable<Mod> {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private Program program;

	private ProductCard card;

	public Mod() {
		try {
			card = new ProductCard().load( this );
		} catch( IOException exception ) {
			throw new RuntimeException( "Error loading product card: " + getClass().getName() );
		}
	}

	@Override
	public Program getProgram() {
		return program;
	}

	@Override
	public ProductCard getCard() {
		return card;
	}

	/**
	 * Called by the product manager to initialize the mod. This method should not
	 * be called by other classes.
	 *
	 * @param program
	 * @param card
	 */
	public final void init( Program program, ProductCard card ) {
		this.program = program;
		this.card = card;
	}

	/**
	 * Called by the program to register a mod instance. This method is typically
	 * called before the program frame and workspaces are created and allows the
	 * mod to register icons, actions, resource types, tools, etc. This method is
	 * also called as part of the mod install process before the {@link #startup}
	 * method is called.
	 */
	public void register() {}

	/**
	 * Called by the program to startup a mod instance. This method is typically
	 * called after the program frame and workspaces are created, but not
	 * necessarily visible, and allows the mod to perform any work needed once the
	 * UI is generated. This method is also called as part of the mod install
	 * process after the {@link #register} method is called. This method is also
	 * called when a mod is enabled from the product tool.
	 */
	public void startup() {}

	/**
	 * Called by the program to shutdown a mod instance. This method is typically
	 * called before the program frame and workspaces are destroyed. This allows
	 * the mod to perform any work needed before the UI is destroyed. This method
	 * is also called as part of the mod uninstall process before the
	 * {@link #unregister} method is called. This method is also called when a mod
	 * is disabled from the product tool.
	 */
	public void shutdown() {}

	/**
	 * Called by the program to unregister a mod instance. This method is
	 * typically called after the program frame and workspaces are destroyed and
	 * allows the mod to unregister icons, actions, resource types, tools, etc.
	 * This method is also called as part of the mod uninstall process after the
	 * {@link #shutdown} method is called.
	 */
	public void unregister() {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path getDataFolder() {
		return program.getDataFolder().resolve( card.getProductKey() );
	}

	/**
	 * This implementation only compares the product card artifact values.
	 */
	@Override
	public int compareTo( Mod that ) {
		return this.card.getArtifact().compareTo( that.card.getArtifact() );
	}

	/**
	 * This implementation only returns the product card name.
	 */
	@Override
	public String toString() {
		return card.getName();
	}

}
