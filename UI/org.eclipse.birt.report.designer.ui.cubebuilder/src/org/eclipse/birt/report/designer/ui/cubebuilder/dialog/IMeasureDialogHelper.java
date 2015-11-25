/*******************************************************************************
 * Copyright (c) 2013 Actuate Corporation.
 * All rights reserved.
 *******************************************************************************/
package org.eclipse.birt.report.designer.ui.cubebuilder.dialog;

import org.eclipse.birt.report.designer.ui.cubebuilder.provider.CubeMeasureExpressionProvider;
import org.eclipse.birt.report.model.api.olap.TabularMeasureHandle;

/**
 * 
 */

public interface IMeasureDialogHelper
{

	CubeMeasureExpressionProvider newProvider( TabularMeasureHandle handle );

	boolean hideSecurityPart( );

	boolean hideHyperLinkPart( );

	boolean hideFormatPart( );

	boolean hideAlignmentPart( );

}
