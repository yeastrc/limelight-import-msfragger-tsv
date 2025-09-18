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
import java.math.RoundingMode;
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

	private static final String[] REQUIRED_HEADERS = {
			"scannum",
			"precursor_neutral_mass",
			"retention_time",
			"charge",
			"hit_rank",
			"peptide",
			"num_matched_ions",
			"tot_num_ions",
			"massdiff",
			"modification_info",
			"hyperscore",
			"nextscore",
			"expectscore",
			"best_locs",
			"score_without_delta_mass",
			"best_score_with_delta_mass",
			"second_best_score_with_delta_mass",
			"proteins"
	};

	public static MSFraggerResults getResults(File msFraggerTSVFile, MSFraggerParameters params, boolean isOpenMod ) throws Throwable {


		MSFraggerResults results = new MSFraggerResults();
		Map<MSFraggerReportedPeptide,Map<Integer,MSFraggerPSM>> resultMap = new HashMap<>();
		results.setPeptidePSMMap(resultMap);

		try(BufferedReader br = new BufferedReader(new FileReader( msFraggerTSVFile ))) {

			String headerLine = br.readLine();
			Map<String, Integer> columnMap = getColumnMap(headerLine);
			validateRequiredHeaders(columnMap);

			for(String line = br.readLine(); line != null; line = br.readLine()) {
				MSFraggerPSM psm = getMSFraggerPSMFromLine(line, params, isOpenMod, columnMap);

				MSFraggerReportedPeptide reportedPeptide = ReportedPeptideUtils.getReportedPeptideForPSM( psm );

				if( !results.getPeptidePSMMap().containsKey( reportedPeptide ) )
					results.getPeptidePSMMap().put( reportedPeptide, new HashMap<>() );

				results.getPeptidePSMMap().get( reportedPeptide ).put( psm.getScanNumber(), psm );
			}
		}

		return results;
	}

	private static void validateRequiredHeaders(Map<String, Integer> columnMap) throws Exception {
		for (String requiredHeader : REQUIRED_HEADERS) {
			if (!columnMap.containsKey(requiredHeader)) {
				throw new Exception("Required column not found in TSV file: " + requiredHeader);
			}
		}
	}

	private static Map<String, Integer> getColumnMap(String headerLine) {
		Map<String, Integer> columnMap = new HashMap<>();
		String[] headers = headerLine.split("\\t");
		for (int i = 0; i < headers.length; i++) {
			columnMap.put(headers[i], i);
		}
		return columnMap;
	}

	/**
	 * Get a PSM object for a given line in MSFragger TSV output
	 *
	 * @param line
	 * @param params
	 * @param isOpenMod
	 * @param columnMap
	 * @return
	 * @throws Exception
	 */
	private static MSFraggerPSM getMSFraggerPSMFromLine(String line, MSFraggerParameters params, boolean isOpenMod, Map<String, Integer> columnMap) throws Exception {

		String decoyPrefix = params.getDecoyPrefix();

		String[] fields = line.split("\\t", -1);

		int scanNumber = Integer.parseInt(fields[columnMap.get("scannum")]);
		BigDecimal precursorNeutralMass = new BigDecimal(fields[columnMap.get("precursor_neutral_mass")]);
		BigDecimal retentionTime = BigDecimal.valueOf(Double.parseDouble(fields[columnMap.get("retention_time")] ) * 60);	// rt is reported as minutes, we want seconds
		int charge = Integer.parseInt(fields[columnMap.get("charge")]);
		int rank = Integer.parseInt(fields[columnMap.get("hit_rank")]);
		String sequence = fields[columnMap.get("peptide")];
		int matchedFragmentIons = Integer.parseInt(fields[columnMap.get("num_matched_ions")]);
		int totalFragmentIons = Integer.parseInt(fields[columnMap.get("tot_num_ions")]);
		BigDecimal massDiff = new BigDecimal(fields[columnMap.get("massdiff")]);
		String modString = fields[columnMap.get("modification_info")];
		BigDecimal hyperscore = new BigDecimal(fields[columnMap.get("hyperscore")]);
		BigDecimal nextHyperscore = new BigDecimal(fields[columnMap.get("nextscore")]);
		BigDecimal expectScore = new BigDecimal(fields[columnMap.get("expectscore")]);
		String localizedOpenMod = fields[columnMap.get("best_locs")];

		BigDecimal hyperscoreNoDeltaMass = null;
		BigDecimal hyperscoreWithDeltaMass = null;
		BigDecimal nextHyperscoreWithDeltaMass = null;

		if( fields[columnMap.get("score_without_delta_mass")].length() > 0 )
			hyperscoreNoDeltaMass = new BigDecimal(fields[columnMap.get("score_without_delta_mass")]);
		else
			hyperscoreNoDeltaMass = BigDecimal.ZERO;


		if( fields[columnMap.get("best_score_with_delta_mass")].length() > 0)
			hyperscoreWithDeltaMass = new BigDecimal(fields[columnMap.get("best_score_with_delta_mass")]);
		else
			hyperscoreWithDeltaMass = BigDecimal.ZERO;

		if( fields[columnMap.get("second_best_score_with_delta_mass")].length() > 0 )
			nextHyperscoreWithDeltaMass = new BigDecimal(fields[columnMap.get("second_best_score_with_delta_mass")]);
		else
			nextHyperscoreWithDeltaMass = BigDecimal.ZERO;

		String proteinMatch = fields[columnMap.get("proteins")];
		String altProteinMatches = null;
		if(columnMap.containsKey("alt_protein_matches")) {
			altProteinMatches = fields[columnMap.get("alt_protein_matches")];
		}

		MSFraggerPSM psm = new MSFraggerPSM();

		// populate the fields that require no extra processing
		psm.setScanNumber(scanNumber);
		psm.setPrecursorNeutralMass(precursorNeutralMass);
		psm.setRetentionTime(retentionTime);
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
		psm.setModifications(getDynamicModsFromString(modString, sequence, params));

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

	private static boolean isModStaticMod(String aminoAcid, BigDecimal modMass, MSFraggerParameters params ) {

		if( params.getStaticMods() == null || params.getStaticMods().size() < 1 ) {
			return false;
		}

		if( !params.getStaticMods().containsKey( aminoAcid.charAt( 0 ) ) ) {
			return false;
		}

		// round to two decimal places and compare
		BigDecimal testMass = modMass.setScale( 2, RoundingMode.HALF_UP );
		BigDecimal paramMass = BigDecimal.valueOf( params.getStaticMods().get( aminoAcid.charAt( 0 ) ) ).setScale( 2, RoundingMode.HALF_UP );

		return testMass.equals( paramMass );
	}
	/**
	 * Example of string: "10C(57.02146), 17C(57.02146), 38C(57.02146)"
	 * @param modString
	 * @return
	 */
	private static Map<Integer, BigDecimal> getDynamicModsFromString(String modString, String peptide, MSFraggerParameters params) throws Exception {

		Map<Integer, BigDecimal> modMap = new HashMap<>();

		if(modString == null || modString.length() < 1) {
			return modMap;
		}

		String[] modChunks = modString.split(", ");
		for(String modChunk : modChunks ) {
			Matcher m = modPattern.matcher(modChunk);
			if(m.matches()) {

				int position = Integer.parseInt(m.group(1));
				String aminoAcid = String.valueOf( peptide.charAt( position - 1 ) );
				BigDecimal mass = new BigDecimal(m.group(2));

				if(!isModStaticMod(aminoAcid, mass, params))
					modMap.put(position, mass);

			} else {

				m = modTermPattern.matcher(modChunk);

				if( m.matches()) {

					int position = 0;
					if(m.group(1).equals("C")) {
						position = peptide.length();
					}

					BigDecimal mass = new BigDecimal(m.group(2));

					modMap.put(position, mass);

				} else {
					throw new Exception("Did not understand reported modification: " + modChunk);
				}
			}
		}

		return modMap;
	}

	private static Pattern modPattern = Pattern.compile("^(\\d+)[A-Z]\\((.+)\\)$");
	private static Pattern modTermPattern = Pattern.compile("^([NC])\\-term\\((.+)\\)$");

}
