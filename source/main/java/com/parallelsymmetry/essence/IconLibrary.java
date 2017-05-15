package com.parallelsymmetry.essence;

import com.parallelsymmetry.essence.icon.*;
import javafx.scene.image.Image;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IconLibrary {

	private static final int DEFAULT_SIZE = 16;

	private Map<String, Class<? extends ProgramIcon>> icons;

	public IconLibrary() {
		icons = new ConcurrentHashMap<>();
		register( "program", CircleSlashIcon.class );
		register( "new", DocumentIcon.class );
		register( "open", FolderIcon.class );
		register( "save", SaveIcon.class );
		register( "close", CloseIcon.class );
		register( "exit", ExitIcon.class );

		register( "undo", UndoIcon.class );
		register( "redo", RedoIcon.class );
		register( "cut", CutIcon.class );
		register( "copy", CopyIcon.class );
		register( "paste", PasteIcon.class );
		register( "delete", DeleteIcon.class );
		register( "indent", IndentIcon.class );
		register( "unindent", UnindentIcon.class );

		register( "settings", SettingsIcon.class );

		register( "welcome", WelcomeIcon.class );
		register( "help-content", QuestionIcon.class );
		register( "update", UpdateIcon.class );
		register( "about", ExclamationIcon.class );

		register( "workarea-new", WorkareaIcon.class);
		register( "workarea-rename", WorkareaRenameIcon.class);
		register( "workarea-close", WorkareaCloseIcon.class);
	}

	public ProgramIcon getIcon( String id ) {
		return getIcon( id, DEFAULT_SIZE );
	}

	public ProgramIcon getIcon( String id, double size ) {
		return getIconRenderer( id ).setSize( size );
	}

	public Image getIconImage( String id ) {
		return getIconImage( id, DEFAULT_SIZE );
	}

	public Image getIconImage( String id, int size ) {
		return getIcon( id ).setSize( size ).getImage();
	}

	public Image[] getIconImages( String id ) {
		return getIconImages( id, 16, 24, 32, 48, 64, 128, 256 );
	}

	public Image[] getIconImages( String id, int... sizes ) {
		Image[] images = new Image[ sizes.length ];
		for( int index = 0; index < sizes.length; index++ ) {
			images[ index ] = getIconImage( id, sizes[ index ] );
		}
		return images;
	}

	public void register( String id, Class<? extends ProgramIcon> icon ) {
		icons.put( id, icon );
	}

	private ProgramIcon getIconRenderer( String id ) {
		Class<? extends ProgramIcon> renderer = icons.get( id );

		ProgramIcon icon;
		try {
			icon = renderer.newInstance();
		} catch( Exception exception ) {
			icon = new BrokenIcon();
		}

		return icon.setSize( DEFAULT_SIZE );
	}

}
