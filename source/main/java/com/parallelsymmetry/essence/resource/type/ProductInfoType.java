package com.parallelsymmetry.essence.resource.type;

import com.parallelsymmetry.essence.product.Product;
import com.parallelsymmetry.essence.resource.Codec;
import com.parallelsymmetry.essence.resource.ResourceType;

public class ProductInfoType extends ResourceType {

	public ProductInfoType( Product product ) {
		super( product, "product-info" );
	}

	@Override
	public Codec getDefaultCodec() {
		return null;
	}

}
