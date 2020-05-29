package org.yeastrc.limelight.xml.msfragger.objects;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class MSFraggerPSM {

	private BigDecimal hyperScore;
	private BigDecimal nextScore;
	private BigDecimal eValue;
	private BigDecimal massDiff;
	private OpenModification openModification;
	private BigDecimal fdr;
	private int matchedFragmentIons;
	private int totalFragmentIons;
	private BigDecimal hyperScoreNoDeltaMass;
	private BigDecimal hyperScoreWithDeltaMass;
	private BigDecimal nextHyperScoreWithDeltaMass;

	private boolean isDecoy;

	public boolean isDecoy() {
		return isDecoy;
	}

	public void setDecoy(boolean decoy) {
		isDecoy = decoy;
	}

	public OpenModification getOpenModification() {
		return openModification;
	}

	public void setOpenModification(OpenModification openModification) {
		this.openModification = openModification;
	}

	private int hitRank;
	
	private int scanNumber;
	private BigDecimal precursorNeutralMass;
	private int charge;
	private BigDecimal retentionTime;
	
	private String peptideSequence;

	public BigDecimal getFdr() {
		return fdr;
	}

	public void setFdr(BigDecimal fdr) {
		this.fdr = fdr;
	}

	public int getMatchedFragmentIons() {
		return matchedFragmentIons;
	}

	public void setMatchedFragmentIons(int matchedFragmentIons) {
		this.matchedFragmentIons = matchedFragmentIons;
	}

	public int getTotalFragmentIons() {
		return totalFragmentIons;
	}

	public void setTotalFragmentIons(int totalFragmentIons) {
		this.totalFragmentIons = totalFragmentIons;
	}

	public BigDecimal getHyperScoreNoDeltaMass() {
		return hyperScoreNoDeltaMass;
	}

	public void setHyperScoreNoDeltaMass(BigDecimal hyperScoreNoDeltaMass) {
		this.hyperScoreNoDeltaMass = hyperScoreNoDeltaMass;
	}

	public BigDecimal getHyperScoreWithDeltaMass() {
		return hyperScoreWithDeltaMass;
	}

	public void setHyperScoreWithDeltaMass(BigDecimal hyperScoreWithDeltaMass) {
		this.hyperScoreWithDeltaMass = hyperScoreWithDeltaMass;
	}

	public BigDecimal getNextHyperScoreWithDeltaMass() {
		return nextHyperScoreWithDeltaMass;
	}

	public void setNextHyperScoreWithDeltaMass(BigDecimal nextHyperScoreWithDeltaMass) {
		this.nextHyperScoreWithDeltaMass = nextHyperScoreWithDeltaMass;
	}

	private Map<Integer,BigDecimal> modifications;

	public BigDecimal getHyperScore() {
		return hyperScore;
	}

	public void setHyperScore(BigDecimal hyperScore) {
		this.hyperScore = hyperScore;
	}

	public BigDecimal getNextScore() {
		return nextScore;
	}

	public void setNextScore(BigDecimal nextScore) {
		this.nextScore = nextScore;
	}

	public BigDecimal getMassDiff() {
		return massDiff;
	}

	public void setMassDiff(BigDecimal massDiff) {
		this.massDiff = massDiff;
	}


	public BigDecimal geteValue() {
		return eValue;
	}

	public void seteValue(BigDecimal eValue) {
		this.eValue = eValue;
	}

	public int getHitRank() {
		return hitRank;
	}

	public void setHitRank(int hitRank) {
		this.hitRank = hitRank;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public BigDecimal getPrecursorNeutralMass() {
		return precursorNeutralMass;
	}

	public void setPrecursorNeutralMass(BigDecimal precursorNeutralMass) {
		this.precursorNeutralMass = precursorNeutralMass;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public BigDecimal getRetentionTime() {
		return retentionTime;
	}

	public void setRetentionTime(BigDecimal retentionTime) {
		this.retentionTime = retentionTime;
	}

	public String getPeptideSequence() {
		return peptideSequence;
	}

	public void setPeptideSequence(String peptideSequence) {
		this.peptideSequence = peptideSequence;
	}

	public Map<Integer, BigDecimal> getModifications() {
		return modifications;
	}

	public void setModifications(Map<Integer, BigDecimal> modifications) {
		this.modifications = modifications;
	}

}
