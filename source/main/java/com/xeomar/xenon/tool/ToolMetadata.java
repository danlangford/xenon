package com.xeomar.xenon.tool;

import com.xeomar.product.Product;
import com.xeomar.xenon.workarea.Workpane;
import javafx.scene.Node;

public class ToolMetadata {

	private Product product;

	private Class<? extends ProgramTool> type;

	private String name;

	private Node icon;

	private Workpane.Placement placement;

	private ToolInstanceMode instanceMode;

	public ToolMetadata( Product product, Class<? extends ProgramTool> type ) {
		this.product = product;
		this.type = type;
	}

	public Product getProduct() {
		return product;
	}

	public ToolMetadata setProduct( Product product ) {
		this.product = product;
		return this;
	}

	public Class<? extends ProgramTool> getType() {
		return type;
	}

	public ToolMetadata setType( Class<? extends ProgramTool> type ) {
		this.type = type;
		return this;
	}

	public String getName() {
		return name;
	}

	public ToolMetadata setName( String name ) {
		this.name = name;
		return this;
	}

	public Node getIcon() {
		return icon;
	}

	public ToolMetadata setIcon( Node icon ) {
		this.icon = icon;
		return this;
	}

	public Workpane.Placement getPlacement() {
		return placement;
	}

	public ToolMetadata setPlacement( Workpane.Placement placement ) {
		this.placement = placement;
		return this;
	}

	public ToolInstanceMode getInstanceMode() {
		return instanceMode;
	}

	public ToolMetadata setInstanceMode( ToolInstanceMode instanceMode ) {
		this.instanceMode = instanceMode;
		return this;
	}

	@Override
	public boolean equals( Object object ) {
		return type.equals( object );
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

}
