package org.yeastrc.limelight.xml.msfragger.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class ModParsingUtils {

	public static String getRoundedReportedPeptideString( String nakedPeptideSequence, Map<Integer, BigDecimal> modMap ) {
				
		if( modMap == null || modMap.size() < 1 )
			return nakedPeptideSequence;
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < nakedPeptideSequence.length(); i++){
		    String r = String.valueOf( nakedPeptideSequence.charAt(i) );
		    sb.append( r );
		    
		    if( modMap.containsKey( i + 1 ) ) {

		    	BigDecimal mass = modMap.get( i + 1 );
		    	
		    	sb.append( "[" );
		    	sb.append( mass.setScale( 0, RoundingMode.HALF_UP ).toString() );
		    	sb.append( "]" );
		    	
		    }
		}
				
		return sb.toString();
	}

}
