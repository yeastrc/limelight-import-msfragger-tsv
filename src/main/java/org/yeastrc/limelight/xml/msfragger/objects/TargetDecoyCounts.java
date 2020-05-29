package org.yeastrc.limelight.xml.msfragger.objects;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TargetDecoyCounts {

    public void addTarget( BigDecimal score ) {

        if( this.targetCounts == null )
            this.targetCounts = new HashMap<>();

        if( !this.targetCounts.containsKey( score ) )
            this.targetCounts.put( score, 0 );

        this.targetCounts.put( score, this.targetCounts.get( score ) + 1 );
    }

    public void addDecoy( BigDecimal score ) {
        if( this.decoyCounts == null )
            this.decoyCounts = new HashMap<>();

        if( !this.decoyCounts.containsKey( score ) )
            this.decoyCounts.put( score, 0 );

        this.decoyCounts.put( score, this.decoyCounts.get( score ) + 1 );
    }

    public Map<BigDecimal, Integer> getTargetCounts() {
        return targetCounts;
    }

    public Map<BigDecimal, Integer> getDecoyCounts() {
        return decoyCounts;
    }

    private Map<BigDecimal, Integer> targetCounts;
    private Map<BigDecimal, Integer> decoyCounts;
}
