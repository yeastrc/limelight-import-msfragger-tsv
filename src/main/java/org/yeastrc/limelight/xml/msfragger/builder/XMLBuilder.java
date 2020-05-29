package org.yeastrc.limelight.xml.msfragger.builder;

import org.yeastrc.limelight.limelight_import.api.xml_dto.*;
import org.yeastrc.limelight.limelight_import.api.xml_dto.ReportedPeptide.ReportedPeptideAnnotations;
import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.limelight.limelight_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;
import org.yeastrc.limelight.xml.msfragger.annotation.PSMAnnotationTypeSortOrder;
import org.yeastrc.limelight.xml.msfragger.annotation.PSMAnnotationTypes;
import org.yeastrc.limelight.xml.msfragger.annotation.PSMDefaultVisibleAnnotationTypes;
import org.yeastrc.limelight.xml.msfragger.constants.Constants;
import org.yeastrc.limelight.xml.msfragger.objects.*;
import org.yeastrc.limelight.xml.msfragger.utils.ReportedPeptideUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DecimalFormat;

public class XMLBuilder {

	public void buildAndSaveXML( ConversionParameters conversionParameters,
			                     MSFraggerResults msFraggerResults,
			                     MSFraggerParameters magnumParameters,
								 TargetDecoyAnalysis tdAnalysis )
    throws Exception {

		LimelightInput limelightInputRoot = new LimelightInput();

		limelightInputRoot.setFastaFilename( conversionParameters.getFastaFile().getName() );
		
		// add in the conversion program (this program) information
		ConversionProgramBuilder.createInstance().buildConversionProgramSection( limelightInputRoot, conversionParameters);
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		limelightInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );

		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
				
			searchProgram.setName( Constants.PROGRAM_NAME_MSFRAGGER );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_MSFRAGGER );
			searchProgram.setVersion( "Unknown" );
			
			
			//
			// Define the annotation types present in magnum data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
			
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
			
			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes( Constants.PROGRAM_NAME_MSFRAGGER ) ) {
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
			
		}
		
		
		//
		// Define which annotation types are visible by default
		//
		DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( xmlDefaultVisibleAnnotations );
		
		VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
		xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations( xmlVisiblePsmAnnotations );

		for( SearchAnnotation sa : PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes() ) {
			xmlVisiblePsmAnnotations.getSearchAnnotation().add( sa );
		}
		
		//
		// Define the default display order in limelight
		//
		AnnotationSortOrder xmlAnnotationSortOrder = new AnnotationSortOrder();
		searchProgramInfo.setAnnotationSortOrder( xmlAnnotationSortOrder );
		
		PsmAnnotationSortOrder xmlPsmAnnotationSortOrder = new PsmAnnotationSortOrder();
		xmlAnnotationSortOrder.setPsmAnnotationSortOrder( xmlPsmAnnotationSortOrder );
		
		for( SearchAnnotation xmlSearchAnnotation : PSMAnnotationTypeSortOrder.getPSMAnnotationTypeSortOrder() ) {
			xmlPsmAnnotationSortOrder.getSearchAnnotation().add( xmlSearchAnnotation );
		}
		
		//
		// Define the static mods
		//
		if( magnumParameters.getStaticMods() != null && magnumParameters.getStaticMods().keySet().size() > 0 ) {
			StaticModifications smods = new StaticModifications();
			limelightInputRoot.setStaticModifications( smods );
			
			
			for( char residue : magnumParameters.getStaticMods().keySet() ) {
				
				StaticModification xmlSmod = new StaticModification();
				xmlSmod.setAminoAcid( String.valueOf( residue ) );
				xmlSmod.setMassChange( BigDecimal.valueOf( magnumParameters.getStaticMods().get( residue ) ) );
				
				smods.getStaticModification().add( xmlSmod );
			}
		}

		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		limelightInputRoot.setReportedPeptides( reportedPeptides );
		
		// iterate over each distinct reported peptide
		for( MSFraggerReportedPeptide msFraggerReportedPeptide : msFraggerResults.getPeptidePSMMap().keySet() ) {

			// skip this reported peptide if it only contains decoys
			if(ReportedPeptideUtils.reportedPeptideOnlyContainsDecoys( msFraggerResults, msFraggerReportedPeptide ) ) {
				continue;
			}

			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add( xmlReportedPeptide );
			
			xmlReportedPeptide.setReportedPeptideString( msFraggerReportedPeptide.getReportedPeptideString() );
			xmlReportedPeptide.setSequence( msFraggerReportedPeptide.getNakedPeptide() );
			
			// add in the filterable peptide annotations (e.g., q-value)
			ReportedPeptideAnnotations xmlReportedPeptideAnnotations = new ReportedPeptideAnnotations();
			xmlReportedPeptide.setReportedPeptideAnnotations( xmlReportedPeptideAnnotations );

			// add in the mods for this peptide
			if( msFraggerReportedPeptide.getMods() != null && msFraggerReportedPeptide.getMods().keySet().size() > 0 ) {

				PeptideModifications xmlModifications = new PeptideModifications();
				xmlReportedPeptide.setPeptideModifications( xmlModifications );

				for( int position : msFraggerReportedPeptide.getMods().keySet() ) {
					PeptideModification xmlModification = new PeptideModification();
					xmlModifications.getPeptideModification().add( xmlModification );

					xmlModification.setMass( msFraggerReportedPeptide.getMods().get( position ).stripTrailingZeros().setScale( 0, RoundingMode.HALF_UP ) );

					if(position == 0)
						xmlModification.setIsNTerminal(true);

					else if(position == msFraggerReportedPeptide.getNakedPeptide().length())
						xmlModification.setIsCTerminal(true);

					else
						xmlModification.setPosition( new BigInteger( String.valueOf( position ) ) );

				}
			}

			
			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms( xmlPsms );

			// iterate over all PSMs for this reported peptide

			for( int scanNumber : msFraggerResults.getPeptidePSMMap().get(msFraggerReportedPeptide).keySet() ) {

				MSFraggerPSM psm = msFraggerResults.getPeptidePSMMap().get(msFraggerReportedPeptide).get( scanNumber );

				if(psm.isDecoy()) {
					continue;
				}

				Psm xmlPsm = new Psm();
				xmlPsms.getPsm().add( xmlPsm );

				xmlPsm.setScanNumber( new BigInteger( String.valueOf( scanNumber ) ) );
				xmlPsm.setPrecursorCharge( new BigInteger( String.valueOf( psm.getCharge() ) ) );

				// add in the filterable PSM annotations (e.g., score)
				FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
				xmlPsm.setFilterablePsmAnnotations( xmlFilterablePsmAnnotations );

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_FDR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );


					DecimalFormat formatter = new DecimalFormat("0.###E0");

					double fdr = tdAnalysis.getFDRForScore( psm.geteValue() );

					BigDecimal bd = BigDecimal.valueOf( fdr );
					bd = bd.round( new MathContext( 3 ) );

					xmlFilterablePsmAnnotation.setValue( bd );
				}

				// handle msfragger scores
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_EVALUE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
					xmlFilterablePsmAnnotation.setValue( psm.geteValue() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_DELTASCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
					xmlFilterablePsmAnnotation.setValue( psm.getHyperScore().subtract( psm.getNextScore() ) );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_HYPERSCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
					xmlFilterablePsmAnnotation.setValue( psm.getHyperScore() );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_MASSDIFF );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
					xmlFilterablePsmAnnotation.setValue( psm.getMassDiff() );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_RANK );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
					xmlFilterablePsmAnnotation.setValue( BigDecimal.valueOf(psm.getHitRank()).setScale(0, RoundingMode.HALF_UP) );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_MATCHED_IONS );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
					xmlFilterablePsmAnnotation.setValue( BigDecimal.valueOf(psm.getMatchedFragmentIons()).setScale(0, RoundingMode.HALF_UP) );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_MATCHED_RATIO );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
					xmlFilterablePsmAnnotation.setValue( BigDecimal.valueOf( (double)psm.getMatchedFragmentIons() / (double)psm.getTotalFragmentIons() ).setScale(4, RoundingMode.HALF_UP) );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_HYPERSCORE_NO_DELTA );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
					xmlFilterablePsmAnnotation.setValue( psm.getHyperScoreNoDeltaMass() );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_HYPERSCORE_WITH_DELTA );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
					xmlFilterablePsmAnnotation.setValue( psm.getHyperScoreWithDeltaMass() );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MSFRAGGER_ANNOTATION_TYPE_DELTASCORE_WITH_DELTA );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
					xmlFilterablePsmAnnotation.setValue( psm.getHyperScoreWithDeltaMass().subtract(psm.getNextHyperScoreWithDeltaMass()) );
				}

				// add in the open mod mass if this is an open mod search
				if(conversionParameters.isOpenMod()) {
					PsmOpenModification xmlPsmOpenMod = new PsmOpenModification();
					xmlPsmOpenMod.setMass(psm.getOpenModification().getMass());
					xmlPsm.setPsmOpenModification(xmlPsmOpenMod);

					if(psm.getOpenModification().getPositions() != null && psm.getOpenModification().getPositions().size() > 0) {
						for(int position : psm.getOpenModification().getPositions()) {
							PsmOpenModificationPosition xmlPsmOpenModPosition = new PsmOpenModificationPosition();
							xmlPsmOpenModPosition.setPosition(BigInteger.valueOf(position));
							xmlPsmOpenMod.getPsmOpenModificationPosition().add(xmlPsmOpenModPosition);
						}
					}
				}
				
				
			}// end iterating over psms for a reported peptide
		
		}//end iterating over reported peptides


		
		
		// add in the matched proteins section
		MatchedProteinsBuilder.getInstance().buildMatchedProteins(
				                                                   limelightInputRoot,
				                                                   conversionParameters.getFastaFile(),
				                                                   msFraggerResults.getPeptidePSMMap().keySet()
				                                                  );
		
		
		// add in the config file(s)
		ConfigurationFiles xmlConfigurationFiles = new ConfigurationFiles();
		limelightInputRoot.setConfigurationFiles( xmlConfigurationFiles );
		
		ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
		xmlConfigurationFiles.getConfigurationFile().add( xmlConfigurationFile );
		
		xmlConfigurationFile.setSearchProgram( Constants.PROGRAM_NAME_MSFRAGGER );
		xmlConfigurationFile.setFileName( conversionParameters.getMsFraggerConfFile().getName() );
		xmlConfigurationFile.setFileContent( Files.readAllBytes( FileSystems.getDefault().getPath( conversionParameters.getMsFraggerConfFile().getAbsolutePath() ) ) );
		
		
		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain( conversionParameters.getLimelightXMLOutputFile(), limelightInputRoot);
		
	}
	
	
}
