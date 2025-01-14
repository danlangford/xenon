package com.avereon.xenon.event;

public class SettingsSavedEvent extends ProgramSettingsEvent {

	private String root;

	private String scope;

	public SettingsSavedEvent( Object source, String root ) {
		this( source, root, null );
	}

	public SettingsSavedEvent( Object source, String root, String scope ) {
		super( source );
		this.root = root;
		this.scope = scope;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString());
		if( scope != null ) {
			builder.append( ":" );
			builder.append( scope );
		}
		if( root != null ) {
			builder.append( ":" );
			builder.append( root );
		}
		return builder.toString();
	}

}
