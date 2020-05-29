package org.yeastrc.limelight.xml.msfragger.objects;

import java.util.Map;

public class MSFraggerParameters {


	@Override
	public String toString() {

		String str = "MSFraggerParameters: ";

		if( this.staticMods != null ) {
			str += " Static mods: [";
			for( char r : this.staticMods.keySet() ) {
				str += "(";
				str += r + "," + this.staticMods.get( r );
				str += ")";
			}
			str += "]";
		}

		if( this.decoyPrefix != null ) {
			str += " decoyPrefix:";
			str += this.getDecoyPrefix();
		} else {
			str += " decoyPrefix:null";
		}

		return str;

	}

	/**
	 * @return the staticMods
	 */
	public Map<Character, Double> getStaticMods() {
		return staticMods;
	}
	/**
	 * @param staticMods the staticMods to set
	 */
	public void setStaticMods(Map<Character, Double> staticMods) {
		this.staticMods = staticMods;
	}


	public String getDecoyPrefix() {
		return decoyPrefix;
	}

	public void setDecoyPrefix(String decoyPrefix) {
		this.decoyPrefix = decoyPrefix;
	}


	private Map<Character, Double> staticMods;
	private String decoyPrefix;
}
