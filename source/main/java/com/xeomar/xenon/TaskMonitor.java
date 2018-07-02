package com.xeomar.xenon;

import com.xeomar.xenon.task.TaskManager;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class TaskMonitor extends Pane {

	private static final double MINIMUM_WIDTH = 100;

	private TaskManager taskManager;

	private Rectangle max;

	private Rectangle used;

	private Label label;

	private double usedPercent = 0.25;

	public TaskMonitor( TaskManager taskManager ) {
		this.taskManager = taskManager;
		getStyleClass().add( "task-monitor" );

		label = new Label();
		label.getStyleClass().add( "task-monitor-label" );

		max = new Rectangle();
		max.getStyleClass().add( "task-monitor-max" );

		used = new Rectangle();
		used.getStyleClass().add( "task-monitor-used" );

		getChildren().addAll( max, used, label );
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		double width = super.getWidth() - 1;
		double height = super.getHeight() - 1;

		used.setWidth( width * usedPercent );
		used.setHeight( height );

		max.setWidth( width );
		max.setHeight( height );
	}

	@Override
	protected double computeMinWidth( double height ) {
		return getInsets().getLeft() + MINIMUM_WIDTH + getInsets().getRight();
	}
}
