package org.yeastrc.limelight.xml.msfragger.utils;

import org.yeastrc.limelight.xml.msfragger.objects.MSFraggerPSM;
import org.yeastrc.limelight.xml.msfragger.objects.MSFraggerReportedPeptide;
import org.yeastrc.limelight.xml.msfragger.objects.MSFraggerResults;

public class ReportedPeptideUtils {

	public static MSFraggerReportedPeptide getReportedPeptideForPSM(MSFraggerPSM psm ) throws Exception {
		
		MSFraggerReportedPeptide rp = new MSFraggerReportedPeptide();
		
		rp.setNakedPeptide( psm.getPeptideSequence() );
		rp.setMods( psm.getModifications() );
		rp.setReportedPeptideString( ModParsingUtils.getRoundedReportedPeptideString( psm.getPeptideSequence(), psm.getModifications() ));

		return rp;
	}

	public static boolean reportedPeptideOnlyContainsDecoys(MSFraggerResults results, MSFraggerReportedPeptide reportedPeptide ) {

		for( int scanNumber : results.getPeptidePSMMap().get( reportedPeptide ).keySet() ) {

			MSFraggerPSM psm = results.getPeptidePSMMap().get( reportedPeptide ).get( scanNumber );
			if( !psm.isDecoy() ) {
				return false;
			}
		}

		return true;
	}

}
