package org.yeastrc.limelight.xml.msfragger.objects;

import java.math.BigDecimal;
import java.util.Map;

public class TargetDecoyAnalysis {

    private Map<BigDecimal, Double> scoreFDRMap;

    protected TargetDecoyAnalysis( Map<BigDecimal, Double> scoreFDRMap ) {
        this.scoreFDRMap = scoreFDRMap;
    }

    public Double getFDRForScore( BigDecimal score ) {
        return this.scoreFDRMap.get( score );
    }


}
