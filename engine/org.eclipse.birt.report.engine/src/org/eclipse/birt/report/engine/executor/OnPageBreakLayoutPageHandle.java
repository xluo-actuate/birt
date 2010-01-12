/*******************************************************************************
 * Copyright (c) 2004, 2009 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IPageContent;
import org.eclipse.birt.report.engine.content.IReportContent;
import org.eclipse.birt.report.engine.content.impl.PageContent;
import org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter;
import org.eclipse.birt.report.engine.emitter.IContentEmitter;
import org.eclipse.birt.report.engine.ir.MasterPageDesign;
import org.eclipse.birt.report.engine.ir.Report;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.engine.layout.ILayoutPageHandler;
import org.eclipse.birt.report.engine.nLayout.area.IArea;
import org.eclipse.birt.report.engine.nLayout.area.IAreaVisitor;
import org.eclipse.birt.report.engine.nLayout.area.IContainerArea;
import org.eclipse.birt.report.engine.nLayout.area.IImageArea;
import org.eclipse.birt.report.engine.nLayout.area.ITemplateArea;
import org.eclipse.birt.report.engine.nLayout.area.ITextArea;
import org.eclipse.birt.report.engine.nLayout.area.impl.ContainerArea;
import org.eclipse.birt.report.engine.script.internal.OnPageBreakScriptVisitor;
import org.eclipse.birt.report.model.api.ReportItemHandle;

/**
 * 
 */

public class OnPageBreakLayoutPageHandle implements ILayoutPageHandler
{

	protected ExecutionContext executionContext;
	protected IContentEmitter emitter;
	protected PageContent pageContent;
	protected boolean bufferAllContents;
	protected ArrayList<IContent> contents;

	public OnPageBreakLayoutPageHandle( ExecutionContext executionContext )
	{
		this.executionContext = executionContext;
		this.emitter = new PageContentBuilder( );
		this.contents = new ArrayList<IContent>( );
	}

	public IContentEmitter getEmitter( )
	{
		return emitter;
	}

	private void initPageBuffer( PageContent pageContent )
	{
		MasterPageDesign pageDesign = (MasterPageDesign) pageContent
				.getGenerateBy( );
		if ( pageDesign.getOnPageStart( ) != null
				|| pageDesign.getOnPageEnd( ) != null )
		{
			bufferAllContents = true;
		}
		else
		{
			bufferAllContents = false;
		}
		this.contents.clear( );
		this.pageContent = pageContent;
	}

	private ReportItemDesign getGenerateDesign( IContent content )
	{
		Object design = content.getGenerateBy( );
		if ( design instanceof ReportItemDesign )
		{
			return (ReportItemDesign) design;
		}
		if ( design instanceof ReportItemHandle )
		{
			IReportContent reportContent = content.getReportContent( );
			Report reportDesign = reportContent.getDesign( );
			return reportDesign.findDesign( (ReportItemHandle) design );
		}
		return null;
	}

	private void addContent( IContent content )
	{
		if ( !bufferAllContents )
		{
			ReportItemDesign design = getGenerateDesign( content );
			if ( design != null )
			{
				if ( design.getOnPageBreak( ) != null )
				{
					if ( !contents.contains( content ) )
					{
						contents.add( content );
					}
				}
			}
		}
		else
		{
			if ( !contents.contains( content ) )
			{
				contents.add( content );
			}
		}
	}

	private class PageBreakContentCollector implements IAreaVisitor
	{

		public void visitText( ITextArea textArea )
		{
		}

		public void visitAutoText( ITemplateArea templateArea )
		{
		}

		public void visitImage( IImageArea imageArea )
		{
		}

		public void visitContainer( IContainerArea container )
		{
			IContent content = ( (ContainerArea) container ).getContent( );
			if ( content != null )
			{
				addContent( content );
			}
			Iterator iter = container.getChildren( );
			while ( iter.hasNext( ) )
			{
				IArea child = (IArea) iter.next( );
				child.accept( this );
			}
		}
	}

	private class PageContentBuilder extends ContentEmitterAdapter
	{

		public void startPage( IPageContent pageContent ) throws BirtException
		{
			initPageBuffer( (PageContent) pageContent );
			IArea pageArea = (IArea) pageContent
					.getExtension( IContent.LAYOUT_EXTENSION );
			if ( pageArea != null )
			{
				pageArea.accept( new PageBreakContentCollector( ) );
			}
		}

		@Override
		public void startContent( IContent content ) throws BirtException
		{
			OnPageBreakLayoutPageHandle.this.addContent( content );
		}
	}

	public void onPage( long page, Object context )
	{
		// if the page content is null, it means it is the last page end event
		if ( executionContext == null || pageContent == null )
		{
			return;
		}
		// setup the page variables registered in the report design
		Report report = executionContext.getReport( );

		OnPageBreakScriptVisitor onPageBreakVisitor = new OnPageBreakScriptVisitor(
				executionContext );

		// reset the page variables
		Collection<PageVariable> pageVariables = executionContext
				.getPageVariables( );
		for ( PageVariable pageVar : pageVariables )
		{
			if ( PageVariable.SCOPE_PAGE.equals( pageVar.getScope( ) ) )
			{
				Object value = pageVar.getDefaultValue( );
				pageVar.setValue( value );
			}
		}

		onPageBreakVisitor.onPageStart( report, pageContent, contents );
		onPageBreakVisitor.onPageStart( pageContent, contents );
		for ( IContent content : contents )
		{
			onPageBreakVisitor.onPageBreak( content );
		}
		onPageBreakVisitor.onPageEnd( pageContent, contents );
		onPageBreakVisitor.onPageEnd( report, pageContent, contents );
		contents.clear( );
		pageContent = null;
	}
}
