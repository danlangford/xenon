package com.xeomar.xenon.resource.type;

import com.xeomar.util.Product;
import com.xeomar.xenon.resource.Codec;
import com.xeomar.xenon.resource.ResourceType;

public class ProgramWelcomeType extends ResourceType {

	public static final String URI = "program:welcome";

	public ProgramWelcomeType( Product product ) {
		super( product, "welcome" );
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
