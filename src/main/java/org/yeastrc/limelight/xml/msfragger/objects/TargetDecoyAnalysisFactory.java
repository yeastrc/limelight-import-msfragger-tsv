package org.yeastrc.limelight.xml.msfragger.objects;

import java.math.BigDecimal;
import java.util.*;

public class TargetDecoyAnalysisFactory {

    public static final int HIGHER_IS_BETTER = 0;
    public static final int LOWER_IS_BETTER = 1;


    /**
     * Create a target decoy analysis given the supplied target decoy counts (essentially a map of
     * the numbers of targets and decoys have each score).
     *
     * @param counts The target decoy counts indexed by score
     * @param direction The direction in which better scores can be found (higher or lower), see TargetDecoyAnalysis.HIGHER_IS_BETTER and TargetDecoyAnalysis.LOWER_IS_BETTER
     * @return
     */
    public static TargetDecoyAnalysis createTargetDecoyAnalysis(TargetDecoyCounts counts, int direction ) {

        List<BigDecimal> allScoresList = getSortedScoresList( counts, direction );

        int totalTargets = 0;
        int totalDecoys = 0;

        Map<BigDecimal, Double> scoreFDRMap = new HashMap<>();

        for( BigDecimal score : allScoresList ) {

            if( counts.getTargetCounts().containsKey( score ) ) {
                totalTargets += counts.getTargetCounts().get( score );
            }

            if( counts.getDecoyCounts().containsKey( score ) ) {
                totalDecoys += counts.getDecoyCounts().get( score );
            }

            double fdr = (double)totalDecoys / ( (double)totalDecoys + (double)totalTargets );

            // no existing fdr in the scoreFDRMap should be greater than this FDR
            setMaximumFDRInMap( scoreFDRMap, fdr );

            scoreFDRMap.put( score, fdr );

        }

        return new TargetDecoyAnalysis( scoreFDRMap );
    }


    private static void setMaximumFDRInMap( Map<BigDecimal, Double> scoreFDRMap, double maximumScore ) {

        for( BigDecimal score : scoreFDRMap.keySet() ) {
            if( scoreFDRMap.get( score ).compareTo( maximumScore ) > 0 ) {
                scoreFDRMap.put( score, maximumScore );
            }
        }

    }

    private static List<BigDecimal> getSortedScoresList(TargetDecoyCounts counts, int direction ) {

        Collection<BigDecimal> allScoresSet = new HashSet<>();
        allScoresSet.addAll( counts.getTargetCounts().keySet() );
        allScoresSet.addAll( counts.getDecoyCounts().keySet() );

        List<BigDecimal> allScoresList = new ArrayList<>( allScoresSet );

        if( direction == LOWER_IS_BETTER ) {
            Collections.sort( allScoresList );
        } else if( direction == HIGHER_IS_BETTER ) {
            Collections.sort( allScoresList, Collections.reverseOrder() );
        }

        return allScoresList;
    }


}
