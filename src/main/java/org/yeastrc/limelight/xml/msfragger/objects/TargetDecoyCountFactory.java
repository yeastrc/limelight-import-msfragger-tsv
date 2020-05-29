package org.yeastrc.limelight.xml.msfragger.objects;

public class TargetDecoyCountFactory {

    public static TargetDecoyCounts getTargetDecoyCountsByEvalue(MSFraggerResults msFraggerResults) {

        TargetDecoyCounts tdCounts = new TargetDecoyCounts();

        for (MSFraggerReportedPeptide crp : msFraggerResults.getPeptidePSMMap().keySet()) {
            for (int scanNumber : msFraggerResults.getPeptidePSMMap().get(crp).keySet()) {
                MSFraggerPSM psm = msFraggerResults.getPeptidePSMMap().get(crp).get(scanNumber);

                if (psm.isDecoy())
                    tdCounts.addDecoy(psm.geteValue());
                else
                    tdCounts.addTarget(psm.geteValue());

            }
        }

        return tdCounts;
    }
}
