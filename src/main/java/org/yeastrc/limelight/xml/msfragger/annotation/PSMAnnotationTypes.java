package org.yeastrc.limelight.xml.msfragger.annotation;

import org.yeastrc.limelight.limelight_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.limelight.limelight_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.limelight.xml.msfragger.constants.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class PSMAnnotationTypes {

	public static final String MSFRAGGER_ANNOTATION_TYPE_FDR = "FDR";
	public static final String MSFRAGGER_ANNOTATION_TYPE_EVALUE = "E-Value";
	public static final String MSFRAGGER_ANNOTATION_TYPE_HYPERSCORE = "hyperscore";
	public static final String MSFRAGGER_ANNOTATION_TYPE_DELTASCORE = "deltascore";
	public static final String MSFRAGGER_ANNOTATION_TYPE_MASSDIFF = "mass diff";
	public static final String MSFRAGGER_ANNOTATION_TYPE_RANK = "hit rank";
	public static final String MSFRAGGER_ANNOTATION_TYPE_MATCHED_IONS = "matched frag. ions";
	public static final String MSFRAGGER_ANNOTATION_TYPE_MATCHED_RATIO = "matched ions ratio";
	public static final String MSFRAGGER_ANNOTATION_TYPE_HYPERSCORE_NO_DELTA = "hyperscore (no mass diff)";
	public static final String MSFRAGGER_ANNOTATION_TYPE_HYPERSCORE_WITH_DELTA = "hyperscore (with mass diff)";
	public static final String MSFRAGGER_ANNOTATION_TYPE_DELTASCORE_WITH_DELTA = "deltascore (with mass diff)";



	public static List<FilterablePsmAnnotationType> getFilterablePsmAnnotationTypes( String programName ) {
		List<FilterablePsmAnnotationType> types = new ArrayList<FilterablePsmAnnotationType>();

		if( programName.equals( Constants.PROGRAM_NAME_MSFRAGGER ) ) {

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_FDR );
				type.setDescription( "FDR associated with e-value, calculated by limelight XML converter using target/decoy counts" );
				type.setFilterDirection( FilterDirectionType.BELOW );
				type.setDefaultFilterValue(new BigDecimal("0.01"));

				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_EVALUE );
				type.setDescription( "Expect value" );
				type.setFilterDirection( FilterDirectionType.BELOW );

				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_HYPERSCORE );
				type.setDescription( "Similar to scoring done in X! Tandem. See: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5409104/" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_DELTASCORE );
				type.setDescription( "Difference in hyperscore between top and next hit." );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_MASSDIFF );
				type.setDescription( "Mass diff, as calculated by " + Constants.PROGRAM_NAME_MSFRAGGER );
				type.setFilterDirection( FilterDirectionType.BELOW );

				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_DELTASCORE_WITH_DELTA );
				type.setDescription( "The difference between the best and the second best hyperscore from the ion matching with delta mass" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_HYPERSCORE_WITH_DELTA );
				type.setDescription( "The best hyperscore from the ion matching with delta mass" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_HYPERSCORE_NO_DELTA );
				type.setDescription( "Hyperscore from the ion matching without delta mass" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_MATCHED_RATIO );
				type.setDescription( "Ratio of matched fragment ions to total # of possible fragment ions" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_MATCHED_IONS );
				type.setDescription( "Matched fragment ions" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( MSFRAGGER_ANNOTATION_TYPE_RANK );
				type.setDescription( "Hit rank" );
				type.setFilterDirection( FilterDirectionType.BELOW );

				types.add( type );
			}

		}

		
		return types;
	}
	
	
}
