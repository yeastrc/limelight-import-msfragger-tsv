package org.yeastrc.limelight.xml.msfragger.utils;

public class ProteinInferenceUtils {

	/**
	 * Returns true if the protein sequence contains the peptide sequence. No check is made
	 * for tryptic ends. I/L are treated as the same residue for matching.
	 * 
	 * @param proteinSequence
	 * @param reportedPeptide
	 * @return
	 */
	public static boolean proteinContainsReportedPeptide( String proteinSequence, String reportedPeptide ) {
		
		if( reportedPeptide.equals( "" ) )
			throw new IllegalArgumentException( "Peptide sequence cannot be empty." );
		
		// handle leucine, iso-leucine
		reportedPeptide = reportedPeptide.replaceAll( "L", "I" );
		proteinSequence = proteinSequence.replaceAll( "L",  "I" );
		
		if( proteinSequence.contains( reportedPeptide ) )
			return true;
		
		return false;
	}
	
}
