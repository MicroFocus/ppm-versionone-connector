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
                break;
            case "Exploration":
                childrenNames.add("OP");
                break;
            default: // CapEx or Opex or empty
                switch (fundingLevel) {
                    case "Pillars / Enablers":
                    case "Mandates":
                    case "CBF - Critical Business Foundations":
                        childrenNames.add("CAP");
                        break;
                    case "BAU (OpEx)":
                    case "CBF - Fix Broken Experiences":
                        childrenNames.add("OP");
                        break;
                    default:
                        childrenNames.add("OP");
                        break;
                }
                break;
        }


        return childrenNames;
    }
}
