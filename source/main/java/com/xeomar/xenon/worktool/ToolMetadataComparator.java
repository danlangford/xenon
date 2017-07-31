package com.xeomar.xenon.worktool;

import com.xeomar.xenon.ToolMetadata;

import java.util.Comparator;

public class ToolMetadataComparator implements Comparator<ToolMetadata> {

	@Override
	public int compare( ToolMetadata metadata1, ToolMetadata metadata2 ) {
		return metadata1.getName().compareToIgnoreCase( metadata2.getName() );
	}

}