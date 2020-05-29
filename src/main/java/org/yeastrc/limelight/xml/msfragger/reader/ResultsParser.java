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

package org.yeastrc.limelight.xml.msfragger.reader;

import org.yeastrc.limelight.xml.msfragger.objects.*;
import org.yeastrc.limelight.xml.msfragger.utils.ReportedPeptideUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Riffle
 *
 */
public class ResultsParser {

	public static MSFraggerResults getResults(File msFraggerTSVFile, MSFraggerParameters params, boolean isOpenMod ) throws Throwable {


		MSFraggerResults results = new MSFraggerResults();
		Map<MSFraggerReportedPeptide,Map<Integer,MSFraggerPSM>> resultMap = new HashMap<>();
		results.setPeptidePSMMap(resultMap);

		try(BufferedReader br = new BufferedReader(new FileReader( msFraggerTSVFile ))) {

			br.readLine();	// skip header

			for(String line = br.readLine(); line != null; line = br.readLine()) {
				MSFraggerPSM psm = getMSFraggerPSMFromLine(line, params, params.getDecoyPrefix(), isOpenMod);

				MSFraggerReportedPeptide reportedPeptide = ReportedPeptideUtils.getReportedPeptideForPSM( psm );

				if( !results.getPeptidePSMMap().containsKey( reportedPeptide ) )
					results.getPeptidePSMMap().put( reportedPeptide, new HashMap<>() );

				results.getPeptidePSMMap().get( reportedPeptide ).put( psm.getScanNumber(), psm );
			}
		}

		return results;
	}

	/**
	 * Get a PSM object for a given line in MSFragger TSV output
	 *
	 * @param line
	 * @param params
	 * @param decoyPrefix
	 * @param isOpenMod
	 * @return
	 * @throws Exception
	 */
	private static MSFraggerPSM getMSFraggerPSMFromLine(String line, MSFraggerParameters params, String decoyPrefix, boolean isOpenMod) throws Exception {

		String[] fields = line.split("\\t");

		int scanNumber = Integer.parseInt(fields[0]);
		int charge = Integer.parseInt(fields[3]);
		int rank = Integer.parseInt(fields[4]);
		String sequence = fields[5];
		int matchedFragmentIons = Integer.parseInt(fields[9]);
		int totalFragmentIons = Integer.parseInt(fields[10]);
		BigDecimal massDiff = new BigDecimal(fields[12]);
		String modString = fields[15];
		BigDecimal hyperscore = new BigDecimal(fields[16]);
		BigDecimal nextHyperscore = new BigDecimal(fields[17]);
		BigDecimal expectScore = new BigDecimal(fields[18]);
		String localizedOpenMod = fields[19];
		BigDecimal hyperscoreNoDeltaMass = new BigDecimal(fields[20]);
		BigDecimal hyperscoreWithDeltaMass = new BigDecimal(fields[21]);
		BigDecimal nextHyperscoreWithDeltaMass = new BigDecimal(fields[22]);

		String proteinMatch = fields[8];
		String altProteinMatches = null;
		if(fields.length == 25) {
			altProteinMatches = fields[24];
		}

		MSFraggerPSM psm = new MSFraggerPSM();

		// populate the fields that require no extra processing
		psm.setScanNumber(scanNumber);
		psm.setCharge(charge);
		psm.setHitRank(rank);
		psm.setPeptideSequence(sequence);
		psm.setMatchedFragmentIons(matchedFragmentIons);
		psm.setTotalFragmentIons(totalFragmentIons);
		psm.setMassDiff(massDiff);
		psm.setHyperScore(hyperscore);
		psm.setNextScore(nextHyperscore);
		psm.seteValue(expectScore);
		psm.setHyperScoreNoDeltaMass(hyperscoreNoDeltaMass);
		psm.setHyperScoreWithDeltaMass(hyperscoreWithDeltaMass);
		psm.setNextHyperScoreWithDeltaMass(nextHyperscoreWithDeltaMass);

		// add in dynamic mods
		psm.setModifications(getDynamicModsFromString(modString));

		// add in open mod and localizations
		if(isOpenMod) {
			psm.setOpenModification(getOpenModificationForPsm(massDiff, localizedOpenMod));
		}

		// set whether or not this is a decoy hit
		psm.setDecoy(isDecoyHit(decoyPrefix, proteinMatch, altProteinMatches));

		return psm;
	}

	/**
	 * Returns true if the PSM only matches decoy proteins, false otherwise.
	 *
	 * @param decoyPrefix
	 * @param proteinName
	 * @param altProteinNames
	 * @return
	 */
	private static boolean isDecoyHit(String decoyPrefix, String proteinName, String altProteinNames) {

		if(decoyPrefix == null || decoyPrefix.length() < 1) { return false; }

		if(!proteinName.startsWith(decoyPrefix)) { return false; }

		if(altProteinNames != null && altProteinNames.length() > 0) {
			String[] altProteins = altProteinNames.split("@@");	// MSFragger separates alt protein entries with "@@"
			for (String altProtein : altProteins) {
				if (!altProtein.startsWith(decoyPrefix)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Get an OpenModification and its localizations. Localization is based on the localized peptide
	 * sequence string generated by MSFragger, where any lowercase residues are predicted localizations.
	 * First residue is position 1.
	 *
	 * @param massdiff Mass diff of PSM
	 * @param localizedOpenMod Localization peptide sequence in the form of peptIDE
	 * @return
	 */
	private static OpenModification getOpenModificationForPsm(BigDecimal massdiff, String localizedOpenMod) {

		Collection<Integer> positions = new HashSet<>();

		for (int i = 0; i < localizedOpenMod.length(); i++) {
			char c = localizedOpenMod.charAt(i);
			if(Character.isLowerCase(c)){
				positions.add(i + 1);
			}
		}

		return new OpenModification(massdiff, positions);
	}

	/**
	 * Example of string: "10C(57.02146), 17C(57.02146), 38C(57.02146)"
	 * @param modString
	 * @return
	 */
	private static Map<Integer, BigDecimal> getDynamicModsFromString(String modString) throws Exception {

		Map<Integer, BigDecimal> modMap = new HashMap<>();

		if(modString == null || modString.length() < 1) {
			return modMap;
		}

		String[] modChunks = modString.split(", ");
		for(String modChunk : modChunks ) {
			Matcher m = modPattern.matcher(modChunk);
			if(m.matches()) {

				int position = Integer.parseInt(m.group(1));
				BigDecimal mass = new BigDecimal(m.group(2));

				modMap.put(position, mass);

			} else {
				throw new Exception("Did not understand reported modification: " + modChunk);
			}
		}

		return modMap;
	}

	private static Pattern modPattern = Pattern.compile("^(\\d+)[A-Z]\\((.+)\\)$");

}
