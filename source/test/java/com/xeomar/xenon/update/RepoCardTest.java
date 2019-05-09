package com.xeomar.xenon.update;

import com.xeomar.util.TextUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RepoCardTest {

	// Test that the catalog card can be unmarshalled
	@Test
	public void testLoadCard() throws Exception {
		List<RepoCard> cards = RepoCard.forProduct();
		assertThat( cards.size(), is( 2 ) );

		assertThat( cards.get( 0 ).getName(), is( "Xeomar Official" ) );
		assertThat( cards.get( 0 ).getRepo(), is( "https://xeomar.com/download/stable" ) );
		assertThat( cards.get( 0 ).getIcon(), is( "provider" ) );
		assertThat( cards.get( 0 ).isEnabled(), is( true ) );
		assertThat( cards.get( 0 ).isRemovable(), is( false ) );

		assertThat( cards.get( 1 ).getName(), is( "Xeomar Nightly" ) );
		assertThat( cards.get( 1 ).getRepo(), is( "https://xeomar.com/download/latest" ) );
		assertThat( cards.get( 1 ).getIcon(), is( "provider" ) );
		assertThat( cards.get( 1 ).isEnabled(), is( false ) );
		assertThat( cards.get( 1 ).isRemovable(), is( true ) );

//		InputStream input = getClass().getResourceAsStream( RepoCard.CARD );
//		RepoCard card = RepoCard.loadCard( input );

//		assertThat( card.getName(), is( "Xeomar Official" ) );
//		assertThat( card.getIconUri(), is( "provider" ) );
//		assertThat( card.getCardUri(), is( "https://xeomar.com/download/stable" ) );

//		Set<String> products = new HashSet<>();
//		products.add( "https://xeomar.com/download?artifact=arrow" );
//		products.add( "https://xeomar.com/download?channel=latest&artifact=mouse&category=product&type=card" );
//		products.add( "https://xeomar.com/download?artifact=serra" );
//		products.add( "https://xeomar.com/download?artifact=marra" );
//		products.add( "https://xeomar.com/download?artifact=ocean" );
//		products.add( "https://xeomar.com/download?artifact=weave" );
//		products.add( "https://xeomar.com/download?artifact=arrow" );
//		assertThat( card.getProducts(), containsInAnyOrder( products.toArray() ) );
	}

	@Test
	public void testIgnoreMissingAndUnknownProperties() throws Exception {
		String state = "[{\"name\" : \"Xeomar\", \"extra\" : \"unknown\"}]";
		List<RepoCard> card = RepoCard.loadCards( new ByteArrayInputStream( state.getBytes( TextUtil.CHARSET) ) );
		assertThat( card.get(0).getName(), CoreMatchers.is( "Xeomar" ) );
	}

}
