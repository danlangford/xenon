package com.xeomar.xenon.tool;

import com.xeomar.xenon.ProductTool;
import com.xeomar.xenon.Program;
import com.xeomar.xenon.SettingsManager;
import com.xeomar.xenon.product.Product;
import com.xeomar.xenon.resource.Resource;
import com.xeomar.xenon.resource.type.ProgramGuideType;
import com.xeomar.xenon.worktool.ToolException;

import java.util.HashSet;
import java.util.Set;

public class SettingsTool extends ProductTool {

	public SettingsTool( Product product, Resource resource ) {
		super( product, resource );
		setId( "tool-settings" );
		setTitle( product.getResourceBundle().getString( "tool", "settings-name" ) );

		// I suppose all the settings pages can be created here. The
		// pages are not part of the resource so they can be used here.
		//List<SettingsPage> pages = ((Program)getProduct()).getSettingsManager().getSettingsPages();
	}

	@Override
	protected void resourceReady() throws ToolException {
		// In theory there is now a model to use. However,
		// I haven't designed the settings model and all
		// the settings state really comes from the settings
		// manager.
		SettingsManager manager = ((Program)getProduct()).getSettingsManager();

		// The settings manager has all the settings values but
		// not the settings tool page definitions. Those could
		// technically be hard coded because they are not part
		// of the state of the resource. But I want it to be
		// reasonably easy for module developers to add settings
		// pages to the tool. But since the tool is a transient
		// object they pages may need to be registered with the
		// settings manager.
		//manager.getSettingsPages();

	}

	public Set<String> getResourceDependencies() {
		Set<String> resources = new HashSet<>();
		resources.add( ProgramGuideType.URI );
		return resources;
	}

}