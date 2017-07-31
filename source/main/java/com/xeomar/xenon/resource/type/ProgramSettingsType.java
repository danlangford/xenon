package com.xeomar.xenon.resource.type;

import com.xeomar.xenon.product.Product;
import com.xeomar.xenon.resource.Codec;
import com.xeomar.xenon.resource.ResourceType;

public class ProgramSettingsType extends ResourceType {

	public static final String URI = "program:settings";

	public ProgramSettingsType( Product product ) {
		super( product, "settings" );
	}

	@Override
	public boolean isUserType() {
		return false;
	}

	@Override
	public Codec getDefaultCodec() {
		return null;
	}

}