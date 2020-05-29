package org.yeastrc.limelight.xml.msfragger.annotation;

import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.limelight.xml.msfragger.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class PSMAnnotationTypeSortOrder {

	public static List<SearchAnnotation> getPSMAnnotationTypeSortOrder() {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_FDR );
			annotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
			annotations.add( annotation );
		}

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_EVALUE );
			annotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
			annotations.add( annotation );
		}

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_HYPERSCORE );
			annotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
			annotations.add( annotation );
		}

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_DELTASCORE );
			annotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
			annotations.add( annotation );
		}

		
		return annotations;
	}
}
