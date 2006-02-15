/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Actuate Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.birt.chart.ui.swt.type;

import java.util.Collection;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Angle3D;
import org.eclipse.birt.chart.model.attribute.AxisType;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.IntersectionType;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.Orientation;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.RiserType;
import org.eclipse.birt.chart.model.attribute.impl.Angle3DImpl;
import org.eclipse.birt.chart.model.attribute.impl.Rotation3DImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.ComponentPackage;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.AxisImpl;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.BaseSampleData;
import org.eclipse.birt.chart.model.data.DataFactory;
import org.eclipse.birt.chart.model.data.OrthogonalSampleData;
import org.eclipse.birt.chart.model.data.SampleData;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.impl.NumberDataElementImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.LineSeries;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.StockSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.birt.chart.ui.extension.i18n.Messages;
import org.eclipse.birt.chart.ui.swt.DefaultChartSubTypeImpl;
import org.eclipse.birt.chart.ui.swt.DefaultChartTypeImpl;
import org.eclipse.birt.chart.ui.swt.HelpContentImpl;
import org.eclipse.birt.chart.ui.swt.interfaces.IHelpContent;
import org.eclipse.birt.chart.ui.swt.interfaces.ISelectDataComponent;
import org.eclipse.birt.chart.ui.swt.interfaces.ISelectDataCustomizeUI;
import org.eclipse.birt.chart.ui.swt.interfaces.IUIServiceProvider;
import org.eclipse.birt.chart.ui.swt.wizard.data.DefaultBaseSeriesComponent;
import org.eclipse.birt.chart.ui.util.ChartUIUtil;
import org.eclipse.birt.chart.ui.util.UIHelper;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.graphics.Image;

/**
 * BarChart
 */
public class BarChart extends DefaultChartTypeImpl
{

	/**
	 * Comment for <code>TYPE_LITERAL</code>
	 */
	public static final String TYPE_LITERAL = "Bar Chart"; //$NON-NLS-1$

	private static final String STACKED_SUBTYPE_LITERAL = "Stacked"; //$NON-NLS-1$

	private static final String PERCENTSTACKED_SUBTYPE_LITERAL = "Percent Stacked"; //$NON-NLS-1$

	private static final String SIDE_SUBTYPE_LITERAL = "Side-by-side"; //$NON-NLS-1$

	public static final String CHART_TITLE = Messages.getString( "BarChart.Txt.DefaultBarChartTitle" ); //$NON-NLS-1$

	private static final String sStackedDescription = Messages.getString( "BarChart.Txt.StackedDescription" ); //$NON-NLS-1$

	private static final String sPercentStackedDescription = Messages.getString( "BarChart.Txt.PercentStackedDescription" ); //$NON-NLS-1$

	private static final String sSideBySideDescription = Messages.getString( "BarChart.Txt.SideBySideDescription" ); //$NON-NLS-1$

	private transient Image imgIcon = null;

	private transient Image imgStacked = null;

	private transient Image imgStackedWithDepth = null;

	private transient Image imgPercentStacked = null;

	private transient Image imgPercentStackedWithDepth = null;

	private transient Image imgSideBySide = null;

	private transient Image imgSideBySideWithDepth = null;

	private transient Image imgSideBySide3D = null;

	private static final String[] saDimensions = new String[]{
			TWO_DIMENSION_TYPE,
			TWO_DIMENSION_WITH_DEPTH_TYPE,
			THREE_DIMENSION_TYPE
	};

	public BarChart( )
	{
		imgIcon = UIHelper.getImage( "icons/obj16/barcharticon.gif" ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.IChartType#getTypeName()
	 */
	public String getName( )
	{
		return TYPE_LITERAL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.IChartType#getTypeName()
	 */
	public Image getImage( )
	{
		return imgIcon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.IChartType#getHelp()
	 */
	public IHelpContent getHelp( )
	{
		return new HelpContentImpl( TYPE_LITERAL,
				Messages.getString( "BarChart.Txt.HelpText" ) ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#getChartSubtypes(java.lang.String)
	 */
	public Collection getChartSubtypes( String sDimension,
			Orientation orientation )
	{
		Vector vSubTypes = new Vector( );
		if ( sDimension.equals( TWO_DIMENSION_TYPE )
				|| sDimension.equals( ChartDimension.TWO_DIMENSIONAL_LITERAL.getName( ) ) )
		{
			if ( orientation.equals( Orientation.VERTICAL_LITERAL ) )
			{
				imgStacked = UIHelper.getImage( "icons/wizban/stackedbarchartimage.gif" ); //$NON-NLS-1$
				imgPercentStacked = UIHelper.getImage( "icons/wizban/percentstackedbarchartimage.gif" ); //$NON-NLS-1$
				imgSideBySide = UIHelper.getImage( "icons/wizban/sidebysidebarchartimage.gif" ); //$NON-NLS-1$
			}
			else
			{
				imgStacked = UIHelper.getImage( "icons/wizban/horizontalstackedbarchartimage.gif" ); //$NON-NLS-1$
				imgPercentStacked = UIHelper.getImage( "icons/wizban/horizontalpercentstackedbarchartimage.gif" ); //$NON-NLS-1$
				imgSideBySide = UIHelper.getImage( "icons/wizban/horizontalsidebysidebarchartimage.gif" ); //$NON-NLS-1$
			}

			vSubTypes.add( new DefaultChartSubTypeImpl( SIDE_SUBTYPE_LITERAL,
					imgSideBySide,
					sSideBySideDescription,
					Messages.getString( "BarChart.SubType.Side" ) ) );
			vSubTypes.add( new DefaultChartSubTypeImpl( STACKED_SUBTYPE_LITERAL,
					imgStacked,
					sStackedDescription,
					Messages.getString( "BarChart.SubType.Stacked" ) ) );
			vSubTypes.add( new DefaultChartSubTypeImpl( PERCENTSTACKED_SUBTYPE_LITERAL,
					imgPercentStacked,
					sPercentStackedDescription,
					Messages.getString( "BarChart.SubType.PercentStacked" ) ) );
		}
		else if ( sDimension.equals( TWO_DIMENSION_WITH_DEPTH_TYPE )
				|| sDimension.equals( ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL.getName( ) ) )
		{
			if ( orientation.equals( Orientation.VERTICAL_LITERAL ) )
			{
				imgStackedWithDepth = UIHelper.getImage( "icons/wizban/stackedbarchartwithdepthimage.gif" ); //$NON-NLS-1$
				imgPercentStackedWithDepth = UIHelper.getImage( "icons/wizban/percentstackedbarchartwithdepthimage.gif" ); //$NON-NLS-1$
				imgSideBySideWithDepth = UIHelper.getImage( "icons/wizban/sidebysidebarchartwithdepthimage.gif" ); //$NON-NLS-1$
			}
			else
			{
				imgStackedWithDepth = UIHelper.getImage( "icons/wizban/horizontalstackedbarchartwithdepthimage.gif" ); //$NON-NLS-1$
				imgPercentStackedWithDepth = UIHelper.getImage( "icons/wizban/horizontalpercentstackedbarchartwithdepthimage.gif" ); //$NON-NLS-1$
				imgSideBySideWithDepth = UIHelper.getImage( "icons/wizban/horizontalsidebysidebarchartwithdepthimage.gif" ); //$NON-NLS-1$
			}
			vSubTypes.add( new DefaultChartSubTypeImpl( SIDE_SUBTYPE_LITERAL,
					imgSideBySideWithDepth,
					sSideBySideDescription,
					Messages.getString( "BarChart.SubType.Side" ) ) );
			vSubTypes.add( new DefaultChartSubTypeImpl( STACKED_SUBTYPE_LITERAL,
					imgStackedWithDepth,
					sStackedDescription,
					Messages.getString( "BarChart.SubType.Stacked" ) ) );
			vSubTypes.add( new DefaultChartSubTypeImpl( PERCENTSTACKED_SUBTYPE_LITERAL,
					imgPercentStackedWithDepth,
					sPercentStackedDescription,
					Messages.getString( "BarChart.SubType.PercentStacked" ) ) );
		}
		else if ( sDimension.equals( THREE_DIMENSION_TYPE )
				|| sDimension.equals( ChartDimension.THREE_DIMENSIONAL_LITERAL.getName( ) ) )
		{
			if ( orientation.equals( Orientation.VERTICAL_LITERAL ) )
			{
				imgSideBySide3D = UIHelper.getImage( "icons/wizban/sidebysidebarchart3dimage.gif" ); //$NON-NLS-1$
			}
			else
			{
				imgSideBySide3D = UIHelper.getImage( "icons/wizban/horizontalsidebysidebarchart3dimage.gif" ); //$NON-NLS-1$
			}
			vSubTypes.add( new DefaultChartSubTypeImpl( SIDE_SUBTYPE_LITERAL,
					imgSideBySide3D,
					sSideBySideDescription,
					Messages.getString( "BarChart.SubType.Side" ) ) );
		}
		return vSubTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#getModel(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public Chart getModel( String sSubType, Orientation orientation,
			String sDimension, Chart currentChart )
	{
		ChartWithAxes newChart = null;
		if ( currentChart != null )
		{
			newChart = (ChartWithAxes) getConvertedChart( currentChart,
					sSubType,
					orientation,
					sDimension );
			if ( newChart != null )
			{
				return newChart;
			}
		}
		newChart = ChartWithAxesImpl.create( );
		newChart.setType( TYPE_LITERAL );
		newChart.setSubType( sSubType );
		newChart.setOrientation( orientation );
		newChart.setDimension( getDimensionFor( sDimension ) );
		newChart.setUnits( "Points" ); //$NON-NLS-1$

		( (Axis) newChart.getAxes( ).get( 0 ) ).setOrientation( Orientation.HORIZONTAL_LITERAL );
		( (Axis) newChart.getAxes( ).get( 0 ) ).setType( AxisType.TEXT_LITERAL );
		( (Axis) newChart.getAxes( ).get( 0 ) ).setCategoryAxis( true );

		SeriesDefinition sdX = SeriesDefinitionImpl.create( );
		Series categorySeries = SeriesImpl.create( );
		sdX.getSeries( ).add( categorySeries );
		sdX.getSeriesPalette( ).update( 0 );
		( (Axis) newChart.getAxes( ).get( 0 ) ).getSeriesDefinitions( )
				.add( sdX );

		newChart.getTitle( ).getLabel( ).getCaption( ).setValue( CHART_TITLE );

		if ( sSubType.equalsIgnoreCase( STACKED_SUBTYPE_LITERAL ) )
		{
			( (Axis) ( (Axis) newChart.getAxes( ).get( 0 ) ).getAssociatedAxes( )
					.get( 0 ) ).setOrientation( Orientation.VERTICAL_LITERAL );
			( (Axis) ( (Axis) newChart.getAxes( ).get( 0 ) ).getAssociatedAxes( )
					.get( 0 ) ).setType( AxisType.LINEAR_LITERAL );

			SeriesDefinition sdY = SeriesDefinitionImpl.create( );
			sdY.getSeriesPalette( ).update( 0 );
			Series valueSeries = BarSeriesImpl.create( );
			valueSeries.getLabel( ).setVisible( true );
			valueSeries.setStacked( true );
			sdY.getSeries( ).add( valueSeries );
			( (Axis) ( (Axis) newChart.getAxes( ).get( 0 ) ).getAssociatedAxes( )
					.get( 0 ) ).getSeriesDefinitions( ).add( sdY );
		}
		else if ( sSubType.equalsIgnoreCase( PERCENTSTACKED_SUBTYPE_LITERAL ) )
		{
			( (Axis) ( (Axis) newChart.getAxes( ).get( 0 ) ).getAssociatedAxes( )
					.get( 0 ) ).setOrientation( Orientation.VERTICAL_LITERAL );
			( (Axis) ( (Axis) newChart.getAxes( ).get( 0 ) ).getAssociatedAxes( )
					.get( 0 ) ).setType( AxisType.LINEAR_LITERAL );
			( (Axis) ( (Axis) newChart.getAxes( ).get( 0 ) ).getAssociatedAxes( )
					.get( 0 ) ).setPercent( true );

			SeriesDefinition sdY = SeriesDefinitionImpl.create( );
			sdY.getSeriesPalette( ).update( 0 );
			Series valueSeries = BarSeriesImpl.create( );
			valueSeries.getLabel( ).setVisible( true );
			valueSeries.setStacked( true );
			( (BarSeries) valueSeries ).setStacked( true );
			sdY.getSeries( ).add( valueSeries );
			( (Axis) ( (Axis) newChart.getAxes( ).get( 0 ) ).getAssociatedAxes( )
					.get( 0 ) ).getSeriesDefinitions( ).add( sdY );
		}
		else if ( sSubType.equalsIgnoreCase( SIDE_SUBTYPE_LITERAL ) )
		{
			( (Axis) ( (Axis) newChart.getAxes( ).get( 0 ) ).getAssociatedAxes( )
					.get( 0 ) ).setOrientation( Orientation.VERTICAL_LITERAL );
			( (Axis) ( (Axis) newChart.getAxes( ).get( 0 ) ).getAssociatedAxes( )
					.get( 0 ) ).setType( AxisType.LINEAR_LITERAL );

			SeriesDefinition sdY = SeriesDefinitionImpl.create( );
			sdY.getSeriesPalette( ).update( 0 );
			Series valueSeries = BarSeriesImpl.create( );
			valueSeries.getLabel( ).setVisible( true );
			( (BarSeries) valueSeries ).setStacked( false );
			sdY.getSeries( ).add( valueSeries );
			( (Axis) ( (Axis) newChart.getAxes( ).get( 0 ) ).getAssociatedAxes( )
					.get( 0 ) ).getSeriesDefinitions( ).add( sdY );
		}

		if ( sDimension.equals( THREE_DIMENSION_TYPE ) )
		{
			newChart.setRotation( Rotation3DImpl.create( new Angle3D[]{
				Angle3DImpl.create( -20, 45, 0 )
			} ) );

			newChart.setUnitSpacing( 50 );

			newChart.getPrimaryBaseAxes( )[0].getAncillaryAxes( ).clear( );

			Axis zAxisAncillary = AxisImpl.create( Axis.ANCILLARY_BASE );
			zAxisAncillary.setTitlePosition( Position.BELOW_LITERAL );
			zAxisAncillary.getTitle( )
					.getCaption( )
					.setValue( Messages.getString( "ChartWithAxesImpl.Z_Axis.title" ) ); //$NON-NLS-1$
			zAxisAncillary.getTitle( ).setVisible( true );
			zAxisAncillary.setPrimaryAxis( true );
			zAxisAncillary.setLabelPosition( Position.BELOW_LITERAL );
			zAxisAncillary.setOrientation( Orientation.HORIZONTAL_LITERAL );
			zAxisAncillary.getOrigin( ).setType( IntersectionType.MIN_LITERAL );
			zAxisAncillary.getOrigin( )
					.setValue( NumberDataElementImpl.create( 0 ) );
			zAxisAncillary.getTitle( ).setVisible( false );
			zAxisAncillary.setType( AxisType.TEXT_LITERAL );
			newChart.getPrimaryBaseAxes( )[0].getAncillaryAxes( )
					.add( zAxisAncillary );

			newChart.getPrimaryOrthogonalAxis( newChart.getPrimaryBaseAxes( )[0] )
					.getTitle( )
					.getCaption( )
					.getFont( )
					.setRotation( 0 );

			SeriesDefinition sdZ = SeriesDefinitionImpl.create( );
			sdZ.getSeriesPalette( ).update( 0 );
			sdZ.getSeries( ).add( SeriesImpl.create( ) );
			zAxisAncillary.getSeriesDefinitions( ).add( sdZ );
		}

		addSampleData( newChart );
		return newChart;
	}

	private void addSampleData( Chart newChart )
	{
		SampleData sd = DataFactory.eINSTANCE.createSampleData( );
		sd.getBaseSampleData( ).clear( );
		sd.getOrthogonalSampleData( ).clear( );

		// Create Base Sample Data
		BaseSampleData sdBase = DataFactory.eINSTANCE.createBaseSampleData( );
		sdBase.setDataSetRepresentation( "A, B, C" ); //$NON-NLS-1$
		sd.getBaseSampleData( ).add( sdBase );

		// Create Orthogonal Sample Data (with simulation count of 2)
		OrthogonalSampleData oSample = DataFactory.eINSTANCE.createOrthogonalSampleData( );
		oSample.setDataSetRepresentation( "5,4,12" ); //$NON-NLS-1$
		oSample.setSeriesDefinitionIndex( 0 );
		sd.getOrthogonalSampleData( ).add( oSample );

		if ( newChart.getDimension( ) == ChartDimension.THREE_DIMENSIONAL_LITERAL )
		{
			BaseSampleData sdAncillary = DataFactory.eINSTANCE.createBaseSampleData( );
			sdAncillary.setDataSetRepresentation( "Series 1" ); //$NON-NLS-1$
			sd.getAncillarySampleData( ).add( sdAncillary );
		}

		newChart.setSampleData( sd );
	}

	private Chart getConvertedChart( Chart currentChart, String sNewSubType,
			Orientation newOrientation, String sNewDimension )
	{
		Chart helperModel = (Chart) EcoreUtil.copy( currentChart );
		ChartDimension oldDimension = currentChart.getDimension( );
		if ( ( currentChart instanceof ChartWithAxes ) )
		{
			if ( currentChart.getType( ).equals( TYPE_LITERAL ) ) // Original
			// chart is
			// of this type
			// (BarChart)
			{
				if ( !currentChart.getSubType( ).equals( sNewSubType ) ) // Original
				// chart
				// is
				// of
				// the
				// required
				// subtype
				{
					currentChart.setSubType( sNewSubType );
					EList axes = ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
							.get( 0 ) ).getAssociatedAxes( );
					for ( int i = 0; i < axes.size( ); i++ )
					{
						if ( sNewSubType.equalsIgnoreCase( PERCENTSTACKED_SUBTYPE_LITERAL ) )
						{
							( (Axis) axes.get( i ) ).setPercent( true );
						}
						else
						{
							( (Axis) axes.get( i ) ).setPercent( false );
						}
						EList seriesdefinitions = ( (Axis) axes.get( i ) ).getSeriesDefinitions( );
						for ( int j = 0; j < seriesdefinitions.size( ); j++ )
						{
							Series series = ( (SeriesDefinition) seriesdefinitions.get( j ) ).getDesignTimeSeries( );
							if ( ( sNewSubType.equalsIgnoreCase( STACKED_SUBTYPE_LITERAL ) || sNewSubType.equalsIgnoreCase( PERCENTSTACKED_SUBTYPE_LITERAL ) ) )
							{
								series.setStacked( true );
							}
							else
							{
								series.setStacked( false );
							}
						}
					}
				}
			}
			else if ( currentChart.getType( ).equals( LineChart.TYPE_LITERAL )
					|| currentChart.getType( ).equals( AreaChart.TYPE_LITERAL )
					|| currentChart.getType( ).equals( StockChart.TYPE_LITERAL )
					|| currentChart.getType( )
							.equals( ScatterChart.TYPE_LITERAL ) )
			{
				if ( !currentChart.getType( ).equals( LineChart.TYPE_LITERAL ) )
				{
					currentChart.setSampleData( getConvertedSampleData( currentChart.getSampleData( ) ) );
					( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
							.get( 0 ) ).setType( AxisType.TEXT_LITERAL );
				}
				currentChart.setType( TYPE_LITERAL );
				currentChart.setSubType( sNewSubType );
				currentChart.getTitle( )
						.getLabel( )
						.getCaption( )
						.setValue( CHART_TITLE );
				EList axes = ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
						.get( 0 ) ).getAssociatedAxes( );
				for ( int i = 0; i < axes.size( ); i++ )
				{
					if ( sNewSubType.equalsIgnoreCase( PERCENTSTACKED_SUBTYPE_LITERAL ) )
					{
						( (Axis) axes.get( i ) ).setPercent( true );
					}
					else
					{
						( (Axis) axes.get( i ) ).setPercent( false );
					}
					EList seriesdefinitions = ( (Axis) axes.get( i ) ).getSeriesDefinitions( );
					for ( int j = 0; j < seriesdefinitions.size( ); j++ )
					{
						Series series = ( (SeriesDefinition) seriesdefinitions.get( j ) ).getDesignTimeSeries( );
						series = getConvertedSeries( series );
						if ( ( sNewSubType.equalsIgnoreCase( STACKED_SUBTYPE_LITERAL ) || sNewSubType.equalsIgnoreCase( PERCENTSTACKED_SUBTYPE_LITERAL ) ) )
						{
							series.setStacked( true );
						}
						else
						{
							series.setStacked( false );
						}
						( (SeriesDefinition) seriesdefinitions.get( j ) ).getSeries( )
								.clear( );
						( (SeriesDefinition) seriesdefinitions.get( j ) ).getSeries( )
								.add( series );
					}
				}
			}
			else
			{
				return null;
			}
			( (Axis) ( (ChartWithAxes) currentChart ).getAxes( ).get( 0 ) ).setCategoryAxis( true );
		}
		else
		{
			// Create a new instance of the correct type and set initial
			// properties
			currentChart = ChartWithAxesImpl.create( );
			currentChart.setType( TYPE_LITERAL );
			currentChart.setSubType( sNewSubType );
			( (ChartWithAxes) currentChart ).setOrientation( newOrientation );
			currentChart.setDimension( getDimensionFor( sNewDimension ) );

			( (Axis) ( (ChartWithAxes) currentChart ).getAxes( ).get( 0 ) ).setOrientation( Orientation.HORIZONTAL_LITERAL );
			( (Axis) ( (ChartWithAxes) currentChart ).getAxes( ).get( 0 ) ).setType( AxisType.TEXT_LITERAL );
			( (Axis) ( (ChartWithAxes) currentChart ).getAxes( ).get( 0 ) ).setCategoryAxis( true );

			( (Axis) ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
					.get( 0 ) ).getAssociatedAxes( ).get( 0 ) ).setOrientation( Orientation.VERTICAL_LITERAL );
			( (Axis) ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
					.get( 0 ) ).getAssociatedAxes( ).get( 0 ) ).setType( AxisType.LINEAR_LITERAL );

			// Copy generic chart properties from the old chart
			currentChart.setBlock( helperModel.getBlock( ) );
			currentChart.setDescription( helperModel.getDescription( ) );
			currentChart.setGridColumnCount( helperModel.getGridColumnCount( ) );
			currentChart.setSampleData( helperModel.getSampleData( ) );
			currentChart.setScript( helperModel.getScript( ) );
			currentChart.setSeriesThickness( helperModel.getSeriesThickness( ) );
			currentChart.setUnits( helperModel.getUnits( ) );

			if ( helperModel.getInteractivity( ) != null )
			{
				currentChart.getInteractivity( )
						.setEnable( helperModel.getInteractivity( ).isEnable( ) );
				currentChart.getInteractivity( )
						.setLegendBehavior( helperModel.getInteractivity( )
								.getLegendBehavior( ) );
			}

			if ( helperModel.getType( ).equals( PieChart.TYPE_LITERAL )
					|| helperModel.getType( ).equals( MeterChart.TYPE_LITERAL ) )
			{
				// Clear existing series definitions
				( (Axis) ( (ChartWithAxes) currentChart ).getAxes( ).get( 0 ) ).getSeriesDefinitions( )
						.clear( );

				// Copy base series definitions
				( (Axis) ( (ChartWithAxes) currentChart ).getAxes( ).get( 0 ) ).getSeriesDefinitions( )
						.add( ( (ChartWithoutAxes) helperModel ).getSeriesDefinitions( )
								.get( 0 ) );

				// Clear existing series definitions
				( (Axis) ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
						.get( 0 ) ).getAssociatedAxes( ).get( 0 ) ).getSeriesDefinitions( )
						.clear( );

				// Copy orthogonal series definitions
				( (Axis) ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
						.get( 0 ) ).getAssociatedAxes( ).get( 0 ) ).getSeriesDefinitions( )
						.addAll( ( (SeriesDefinition) ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
								.get( 0 ) ).getSeriesDefinitions( ).get( 0 ) ).getSeriesDefinitions( ) );

				// Update the base series
				Series series = ( (SeriesDefinition) ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
						.get( 0 ) ).getSeriesDefinitions( ).get( 0 ) ).getDesignTimeSeries( );
				series = getConvertedSeries( series );

				// Clear existing series
				( (SeriesDefinition) ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
						.get( 0 ) ).getSeriesDefinitions( ).get( 0 ) ).getSeries( )
						.clear( );

				// Add converted series
				( (SeriesDefinition) ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
						.get( 0 ) ).getSeriesDefinitions( ).get( 0 ) ).getSeries( )
						.add( series );

				// Update the orthogonal series
				EList seriesdefinitions = ( (Axis) ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
						.get( 0 ) ).getAssociatedAxes( ).get( 0 ) ).getSeriesDefinitions( );
				for ( int j = 0; j < seriesdefinitions.size( ); j++ )
				{
					series = ( (SeriesDefinition) seriesdefinitions.get( j ) ).getDesignTimeSeries( );
					series = getConvertedSeries( series );
					if ( ( sNewSubType.equalsIgnoreCase( STACKED_SUBTYPE_LITERAL ) || sNewSubType.equalsIgnoreCase( PERCENTSTACKED_SUBTYPE_LITERAL ) ) )
					{
						series.setStacked( true );
					}
					else
					{
						series.setStacked( false );
					}
					// Clear any existing series
					( (SeriesDefinition) seriesdefinitions.get( j ) ).getSeries( )
							.clear( );
					// Add the new series
					( (SeriesDefinition) seriesdefinitions.get( j ) ).getSeries( )
							.add( series );
				}
			}
			else
			{
				return null;
			}
			currentChart.getLegend( )
					.setItemType( LegendItemType.SERIES_LITERAL );
			currentChart.getTitle( )
					.getLabel( )
					.getCaption( )
					.setValue( CHART_TITLE );
		}
		if ( currentChart instanceof ChartWithAxes
				&& !( (ChartWithAxes) currentChart ).getOrientation( )
						.equals( newOrientation ) )
		{
			( (ChartWithAxes) currentChart ).setOrientation( newOrientation );
		}
		if ( !currentChart.getDimension( )
				.equals( getDimensionFor( sNewDimension ) ) )
		{
			currentChart.setDimension( getDimensionFor( sNewDimension ) );
		}

		if ( sNewDimension.equals( THREE_DIMENSION_TYPE )
				&& getDimensionFor( sNewDimension ) != oldDimension )
		{
			( (ChartWithAxes) currentChart ).setRotation( Rotation3DImpl.create( new Angle3D[]{
				Angle3DImpl.create( -20, 45, 0 )
			} ) );

			( (ChartWithAxes) currentChart ).setUnitSpacing( 50 );

			( (ChartWithAxes) currentChart ).getPrimaryBaseAxes( )[0].getAncillaryAxes( )
					.clear( );

			Axis zAxisAncillary = AxisImpl.create( Axis.ANCILLARY_BASE );
			zAxisAncillary.setTitlePosition( Position.BELOW_LITERAL );
			zAxisAncillary.getTitle( )
					.getCaption( )
					.setValue( Messages.getString( "ChartWithAxesImpl.Z_Axis.title" ) ); //$NON-NLS-1$
			zAxisAncillary.getTitle( ).setVisible( true );
			zAxisAncillary.setPrimaryAxis( true );
			zAxisAncillary.setLabelPosition( Position.BELOW_LITERAL );
			zAxisAncillary.setOrientation( Orientation.HORIZONTAL_LITERAL );
			zAxisAncillary.getOrigin( ).setType( IntersectionType.MIN_LITERAL );
			zAxisAncillary.getOrigin( )
					.setValue( NumberDataElementImpl.create( 0 ) );
			zAxisAncillary.getTitle( ).setVisible( false );
			zAxisAncillary.setType( AxisType.TEXT_LITERAL );
			( (ChartWithAxes) currentChart ).getPrimaryBaseAxes( )[0].getAncillaryAxes( )
					.add( zAxisAncillary );

			SeriesDefinition sdZ = SeriesDefinitionImpl.create( );
			sdZ.getSeriesPalette( ).update( 0 );
			sdZ.getSeries( ).add( SeriesImpl.create( ) );
			zAxisAncillary.getSeriesDefinitions( ).add( sdZ );
			
			if ( currentChart.getSampleData( )
					.getAncillarySampleData( )
					.isEmpty( ) )
			{
				BaseSampleData sdAncillary = DataFactory.eINSTANCE.createBaseSampleData( );
				sdAncillary.setDataSetRepresentation( "Series 1" ); //$NON-NLS-1$
				currentChart.getSampleData( )
						.getAncillarySampleData( )
						.add( sdAncillary );
			}		

			Series series = null;
			EList seriesdefinitions = ( (Axis) ( (Axis) ( (ChartWithAxes) currentChart ).getAxes( )
					.get( 0 ) ).getAssociatedAxes( ).get( 0 ) ).getSeriesDefinitions( );
			for ( int j = 0; j < seriesdefinitions.size( ); j++ )
			{
				series = ( (SeriesDefinition) seriesdefinitions.get( j ) ).getDesignTimeSeries( );
				if ( ( series instanceof BarSeries) && 
						( series.getLabelPosition( ) != Position.OUTSIDE_LITERAL ) )
				{
					series.setLabelPosition( Position.OUTSIDE_LITERAL );
				}
			}		
		}

		return currentChart;
	}

	private Series getConvertedSeries( Series series )
	{
		// Do not convert base series
		if ( series.getClass( )
				.getName( )
				.equals( "org.eclipse.birt.chart.model.component.impl.SeriesImpl" ) ) //$NON-NLS-1$
		{
			return series;
		}
		BarSeries barseries = (BarSeries) BarSeriesImpl.create( );
		barseries.setRiser( RiserType.RECTANGLE_LITERAL );

		// Copy generic series properties
		barseries.setLabel( series.getLabel( ) );
		if ( series.getLabelPosition( ).equals( Position.INSIDE_LITERAL )
				|| series.getLabelPosition( ).equals( Position.OUTSIDE_LITERAL ) )
		{
			barseries.setLabelPosition( series.getLabelPosition( ) );
		}
		else
		{
			barseries.setLabelPosition( Position.OUTSIDE_LITERAL );
		}
		barseries.setVisible( series.isVisible( ) );
		if ( series.eIsSet( ComponentPackage.eINSTANCE.getSeries_Triggers( ) ) )
		{
			barseries.getTriggers( ).addAll( series.getTriggers( ) );
		}
		if ( series.eIsSet( ComponentPackage.eINSTANCE.getSeries_DataPoint( ) ) )
		{
			barseries.setDataPoint( series.getDataPoint( ) );
		}
		if ( series.eIsSet( ComponentPackage.eINSTANCE.getSeries_DataDefinition( ) ) )
		{
			barseries.getDataDefinition( ).add( series.getDataDefinition( )
					.get( 0 ) );
		}

		// Copy series specific properties
		if ( series instanceof LineSeries )
		{
			barseries.setRiserOutline( ( (LineSeries) series ).getLineAttributes( )
					.getColor( ) );
		}
		else if ( series instanceof PieSeries )
		{
			barseries.setRiserOutline( ( (PieSeries) series ).getSliceOutline( ) );
		}
		else if ( series instanceof StockSeries )
		{
			barseries.setRiserOutline( ( (StockSeries) series ).getLineAttributes( )
					.getColor( ) );
		}
		return barseries;
	}

	private SampleData getConvertedSampleData( SampleData currentSampleData )
	{
		// Convert base sample data
		EList bsdList = currentSampleData.getBaseSampleData( );
		Vector vNewBaseSampleData = new Vector( );
		for ( int i = 0; i < bsdList.size( ); i++ )
		{
			BaseSampleData bsd = (BaseSampleData) bsdList.get( i );
			bsd.setDataSetRepresentation( getConvertedBaseSampleDataRepresentation( bsd.getDataSetRepresentation( ) ) );
			vNewBaseSampleData.add( bsd );
		}
		currentSampleData.getBaseSampleData( ).clear( );
		currentSampleData.getBaseSampleData( ).addAll( vNewBaseSampleData );

		// Convert orthogonal sample data
		EList osdList = currentSampleData.getOrthogonalSampleData( );
		Vector vNewOrthogonalSampleData = new Vector( );
		for ( int i = 0; i < osdList.size( ); i++ )
		{
			OrthogonalSampleData osd = (OrthogonalSampleData) osdList.get( i );
			osd.setDataSetRepresentation( getConvertedOrthogonalSampleDataRepresentation( osd.getDataSetRepresentation( ) ) );
			vNewOrthogonalSampleData.add( osd );
		}
		currentSampleData.getOrthogonalSampleData( ).clear( );
		currentSampleData.getOrthogonalSampleData( )
				.addAll( vNewOrthogonalSampleData );
		return currentSampleData;
	}

	private String getConvertedBaseSampleDataRepresentation(
			String sOldRepresentation )
	{
		StringTokenizer strtok = new StringTokenizer( sOldRepresentation, "," ); //$NON-NLS-1$
		StringBuffer sbNewRepresentation = new StringBuffer( "" ); //$NON-NLS-1$
		while ( strtok.hasMoreTokens( ) )
		{
			String sElement = strtok.nextToken( ).trim( );
			if ( !sElement.startsWith( "'" ) ) //$NON-NLS-1$
			{
				sbNewRepresentation.append( "'" ); //$NON-NLS-1$
				sbNewRepresentation.append( sElement );
				sbNewRepresentation.append( "'" ); //$NON-NLS-1$
			}
			else
			{
				sbNewRepresentation.append( sElement );
			}
			sbNewRepresentation.append( "," ); //$NON-NLS-1$
		}
		return sbNewRepresentation.toString( ).substring( 0,
				sbNewRepresentation.length( ) - 1 );
	}

	private String getConvertedOrthogonalSampleDataRepresentation(
			String sOldRepresentation )
	{
		StringTokenizer strtok = new StringTokenizer( sOldRepresentation, "," ); //$NON-NLS-1$
		StringBuffer sbNewRepresentation = new StringBuffer( "" ); //$NON-NLS-1$
		while ( strtok.hasMoreTokens( ) )
		{
			String sElement = strtok.nextToken( ).trim( );
			if ( sElement.startsWith( "H" ) ) //$NON-NLS-1$ 
			// Orthogonal sample data is for
			// a stock chart (Orthogonal
			// sample data CANNOT
			// be text
			{
				StringTokenizer strStockTokenizer = new StringTokenizer( sElement );
				sbNewRepresentation.append( strStockTokenizer.nextToken( )
						.trim( )
						.substring( 1 ) );
			}
			else
			{
				sbNewRepresentation.append( sElement );
			}
			sbNewRepresentation.append( "," ); //$NON-NLS-1$
		}
		return sbNewRepresentation.toString( ).substring( 0,
				sbNewRepresentation.length( ) - 1 );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#getSupportedDimensions()
	 */
	public String[] getSupportedDimensions( )
	{
		return saDimensions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#getDefaultDimension()
	 */
	public String getDefaultDimension( )
	{
		return saDimensions[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#supportsTransposition()
	 */
	public boolean supportsTransposition( )
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#supportsTransposition(java.lang.String)
	 */
	public boolean supportsTransposition( String dimension )
	{
		if ( getDimensionFor( dimension ) == ChartDimension.THREE_DIMENSIONAL_LITERAL )
		{
			return false;
		}

		return supportsTransposition( );
	}

	private ChartDimension getDimensionFor( String sDimension )
	{
		if ( sDimension == null || sDimension.equals( TWO_DIMENSION_TYPE ) )
		{
			return ChartDimension.TWO_DIMENSIONAL_LITERAL;
		}
		if ( sDimension.equals( THREE_DIMENSION_TYPE ) )
		{
			return ChartDimension.THREE_DIMENSIONAL_LITERAL;
		}
		return ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.ui.swt.interfaces.IChartType#canAdapt(org.eclipse.birt.chart.model.Chart,
	 *      java.util.Hashtable)
	 */
	public boolean canAdapt( Chart cModel, Hashtable htModelHints )
	{
		return false;
	}

	public ISelectDataComponent getBaseUI( Chart chart,
			ISelectDataCustomizeUI selectDataUI, IUIServiceProvider builder,
			Object oContext, String sTitle )
	{
		return new DefaultBaseSeriesComponent( (SeriesDefinition) ChartUIUtil.getBaseSeriesDefinitions( chart )
				.get( 0 ),
				builder,
				oContext,
				sTitle );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.chart.ui.swt.DefaultChartTypeImpl#getDisplayName()
	 */
	public String getDisplayName( )
	{
		return Messages.getString( "BarChart.Txt.DisplayName" );
	}
}