package org.yeastrc.limelight.xml.msfragger.reader;

import org.yeastrc.limelight.xml.msfragger.objects.MSFraggerParameters;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MSFraggerParamsReader {

	/**
	 * Get the relevant parameters from the msfragger params file
	 *
	 * @param paramsFile
	 * @return
	 * @throws Exception
	 */
	public static MSFraggerParameters getMSFraggerParameters(File paramsFile ) throws Exception {

		MSFraggerParameters msFraggerParams = new MSFraggerParameters();

		try ( InputStream is = new FileInputStream( paramsFile ) ) {
			msFraggerParams.setStaticMods( getStaticModsFromParamsFile( is ) );
		}

		try ( InputStream is = new FileInputStream( paramsFile ) ) {
			msFraggerParams.setDecoyPrefix( getDecoyPrefixFromParamsFile( is ) );
		}


		return msFraggerParams;
	}

	/**
	 * Get the decoy string defined in the params file
	 *
	 * From params file:
	 * decoy_filter = random     #identifier for all decoys in the database.
	 *
	 * @param paramsInputStream
	 * @return
	 * @throws Exception
	 */
	public static String getDecoyPrefixFromParamsFile( InputStream paramsInputStream ) throws Exception {

		try (BufferedReader br = new BufferedReader( new InputStreamReader( paramsInputStream ) ) ) {

			for ( String line = br.readLine(); line != null; line = br.readLine() ) {

				// skip immediately if it's not a line we want
				if( !line.startsWith( "decoy_prefix" ) )
					continue;

				String[] fields = line.split( "\\s+" );

				if( !fields[ 0 ].equals( "decoy_prefix" ) || !fields[ 1 ].equals( "=" ) ) {
					throw new Exception( "Error processing decoy_filter from params file. Got this line: " + line );
				}

				return fields[ 2 ];

			}

		}

		return null;	// could not find a decoy_filter--just assume we don't have one instead of throwing an exception

	}


	/**
	 *
	 * Example line: add_C_cysteine = 57.02146 #foo comments
	 *
	 * @param paramsInputStream
	 * @return
	 * @throws IOException
	 */
	public static Map<Character, Double> getStaticModsFromParamsFile( InputStream paramsInputStream ) throws IOException {

		Map<Character, Double> staticMods = new HashMap<>();

		Pattern p = Pattern.compile( "^add_(\\w)_\\w+\\s+=\\s+([0-9]+(\\.[0-9]+)?).*$" );

		try (BufferedReader br = new BufferedReader( new InputStreamReader( paramsInputStream ) ) ) {

			for ( String line = br.readLine(); line != null; line = br.readLine() ) {

				Matcher m = p.matcher( line );
				if( m.matches() ) {
					char residue = m.group( 1 ).charAt( 0 );
					double d = Double.valueOf( m.group( 2 ) );

					if( d >= 0.000001 )
						staticMods.put( residue, d );
				}
			}

		}

		return staticMods;
	}

}