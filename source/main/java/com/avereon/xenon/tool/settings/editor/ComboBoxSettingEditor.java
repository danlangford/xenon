package com.avereon.xenon.tool.settings.editor;

import com.avereon.settings.SettingsEvent;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.tool.settings.Setting;
import com.avereon.xenon.tool.settings.SettingEditor;
import com.avereon.xenon.tool.settings.SettingOption;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.List;

public class ComboBoxSettingEditor extends SettingEditor implements ChangeListener<SettingOption> {

	private Label label;

	private ComboBox<SettingOption> combobox;

	public ComboBoxSettingEditor( ProgramProduct product, Setting setting ) {
		super( product, setting );
	}

	@Override
	public void addComponents( GridPane pane, int row ) {
		String rbKey = setting.getBundleKey();
		String value = setting.getSettings().get( key );

		label = new Label( product.getResourceBundle().getString( "settings", rbKey ) );

		List<SettingOption> options = setting.getOptions();
		combobox = new ComboBox<>();
		combobox.getItems().addAll( options );
		combobox.setMaxWidth( Double.MAX_VALUE );

		SettingOption selected = setting.getOption( value );
		if( selected == null ) {
			combobox.getSelectionModel().clearSelection();
		} else {
			combobox.getSelectionModel().select( selected );
		}

		// Add the change handlers
		combobox.valueProperty().addListener( this );

		// Set component state
		setDisable( setting.isDisable() );
		setVisible( setting.isVisible() );

		// Add the components
		GridPane.setHgrow( combobox, Priority.ALWAYS );
		pane.addRow( row, label, combobox );
	}

	@Override
	public void setDisable( boolean disable ) {
		label.setDisable( disable );
		combobox.setDisable( disable );
	}

	@Override
	public void setVisible( boolean visible ) {
		label.setVisible( visible );
		combobox.setVisible( visible );
	}

	// Selection change listener
	@Override
	public void changed( ObservableValue<? extends SettingOption> observable, SettingOption oldValue, SettingOption newValue ) {
		setting.getSettings().set( setting.getKey(), newValue.getOptionValue() );
	}

	// Setting listener
	@Override
	public void handleEvent( SettingsEvent event ) {
		Object newValue = event.getNewValue();
		SettingOption option = setting.getOption( newValue == null ? null : newValue.toString() );
		if( event.getType() == SettingsEvent.Type.CHANGED && key.equals( event.getKey() ) ) combobox.getSelectionModel().select( option );
	}

}
