package com.ppm.integration.agilesdk.connector.versionone.delta;

import java.util.ArrayList;
import java.util.List;


/**
 * This class contains the logic used by Delta to decide which sub-tasks to create under an Epic.
 * In the default implementation, the first Criteria is the Funding Type, and second Criteria is the funding Level.
 */
public class DeltaSubTaskCreationLogic {

    public static List<String> getChildrenNames(String firstCriteria, String secondCriteria) {
        String fundingType = firstCriteria == null ? "null" : firstCriteria;
        String fundingLevel = secondCriteria == null ? "null" : secondCriteria;

        List<String> childrenNames = new ArrayList<>();

        switch(fundingType) {
            case "SaaS":
                childrenNames.add("SaaS");
                childrenNames.add("OP");
                break;
            default: // CapEx or Opex or empty
                switch (fundingLevel) {
                    case "BAU (OpEx)":
                        childrenNames.add("OP"); // Opex Only
                        break;
                    default:
                        childrenNames.add("CAP");
                        childrenNames.add("OP");
                        break;
                }
                break;
        }


        return childrenNames;
    }
}
