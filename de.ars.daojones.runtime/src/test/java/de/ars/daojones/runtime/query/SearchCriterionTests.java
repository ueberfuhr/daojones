package de.ars.daojones.runtime.query;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class )
@SuiteClasses( { TautologyTest.class, NegationTest.class, IsEmptyTest.class, FieldComparisonTest.class,
    LogicalCombinationTest.class, SearchCriterionBuilderTest.class } )
public class SearchCriterionTests {

}
