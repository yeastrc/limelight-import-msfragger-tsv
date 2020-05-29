/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.limelight.xml.msfragger.main;

import org.yeastrc.limelight.xml.msfragger.builder.XMLBuilder;
import org.yeastrc.limelight.xml.msfragger.objects.*;
import org.yeastrc.limelight.xml.msfragger.reader.MSFraggerParamsReader;
import org.yeastrc.limelight.xml.msfragger.reader.ResultsParser;

public class ConverterRunner {

	// quickly get a new instance of this class
	public static ConverterRunner createInstance() { return new ConverterRunner(); }
	
	
	public void convertMSFraggerTPPToLimelightXML(ConversionParameters conversionParameters ) throws Throwable {
	
		System.err.print( "Reading conf file into memory..." );
		MSFraggerParameters msfraggerParams = MSFraggerParamsReader.getMSFraggerParameters( conversionParameters.getMsFraggerConfFile() );
		System.err.println( " Done." );
		
		System.err.print( "Reading MSFragger data into memory..." );
		MSFraggerResults msFraggerResults = ResultsParser.getResults( conversionParameters.getMsfraggerTSVFile(), msfraggerParams, conversionParameters.isOpenMod() );
		System.err.println( " Done." );

		System.err.print( "Performing FDR analysis of MSFragger E-values..." );
		TargetDecoyCounts tdCounts = TargetDecoyCountFactory.getTargetDecoyCountsByEvalue( msFraggerResults );
		TargetDecoyAnalysis tdAnalysis = TargetDecoyAnalysisFactory.createTargetDecoyAnalysis( tdCounts, TargetDecoyAnalysisFactory.LOWER_IS_BETTER );
		System.err.println( " Done." );

		System.err.print( "Writing out XML..." );
		(new XMLBuilder()).buildAndSaveXML( conversionParameters, msFraggerResults, msfraggerParams, tdAnalysis );
		System.err.println( " Done." );
		
	}
}
