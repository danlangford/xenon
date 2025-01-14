import com.avereon.xenon.Mod;

module com.avereon.xenon {
	requires java.logging;
	requires java.management;
	requires java.sql;
	requires javafx.controls;
	requires jdk.crypto.ec;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.annotation;
	requires com.avereon.zenna;
	requires com.avereon.zevra;
	requires org.slf4j;
	requires org.slf4j.jul;
	requires miglayout.javafx;

	exports com.avereon.xenon;
	exports com.avereon.xenon.demo;
	exports com.avereon.xenon.notice;
	exports com.avereon.xenon.resource;
	exports com.avereon.xenon.task;
	exports com.avereon.xenon.task.chain;
	exports com.avereon.xenon.tool;
	exports com.avereon.xenon.update;
	exports com.avereon.xenon.util;
	exports com.avereon.xenon.workarea;
	exports com.avereon.xenon.workspace;

	opens com.avereon.xenon.bundles;
	opens settings;

	// WORKAROUND Dev time problem
	opens com.avereon.xenon.update to com.fasterxml.jackson.databind;

	uses Mod;
}
