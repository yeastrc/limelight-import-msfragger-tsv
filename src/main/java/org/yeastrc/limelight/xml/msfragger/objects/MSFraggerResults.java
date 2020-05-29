package org.yeastrc.limelight.xml.msfragger.objects;

import java.util.Map;

public class MSFraggerResults {

	private Map<MSFraggerReportedPeptide, Map<Integer, MSFraggerPSM>> peptidePSMMap;

	/**
	 * @return the peptidePSMMap
	 */
	public Map<MSFraggerReportedPeptide, Map<Integer, MSFraggerPSM>> getPeptidePSMMap() {
		return peptidePSMMap;
	}
	/**
	 * @param peptidePSMMap the peptidePSMMap to set
	 */
	public void setPeptidePSMMap(Map<MSFraggerReportedPeptide, Map<Integer, MSFraggerPSM>> peptidePSMMap) {
		this.peptidePSMMap = peptidePSMMap;
	}

}
