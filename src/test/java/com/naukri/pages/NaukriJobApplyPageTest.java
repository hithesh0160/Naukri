package com.naukri.pages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class NaukriJobApplyPageTest {

    private NaukriJobApplyPage jobApplyPage;
    private Set<String> excludedCompanies;

    @BeforeEach
    public void setUp() throws Exception {
        // Create a mock instance with null driver (we only test the exclusion logic)
        jobApplyPage = new NaukriJobApplyPage(null);
        
        // Access the private excludedCompanies field using reflection
        Field excludedCompaniesField = NaukriJobApplyPage.class.getDeclaredField("excludedCompanies");
        excludedCompaniesField.setAccessible(true);
        excludedCompanies = (Set<String>) excludedCompaniesField.get(jobApplyPage);
    }

    @Test
    public void testIsExcludedCompany_ExactMatch() throws Exception {
        // Setup
        excludedCompanies.clear();
        excludedCompanies.add("wipro");
        
        // Test
        Method isExcludedCompany = NaukriJobApplyPage.class.getDeclaredMethod("isExcludedCompany", String.class);
        isExcludedCompany.setAccessible(true);
        
        boolean result = (boolean) isExcludedCompany.invoke(jobApplyPage, "Wipro");
        assertTrue(result, "Should match exact company name");
    }

    @Test
    public void testIsExcludedCompany_CaseInsensitive() throws Exception {
        // Setup
        excludedCompanies.clear();
        excludedCompanies.add("wipro");
        
        // Test
        Method isExcludedCompany = NaukriJobApplyPage.class.getDeclaredMethod("isExcludedCompany", String.class);
        isExcludedCompany.setAccessible(true);
        
        assertTrue((boolean) isExcludedCompany.invoke(jobApplyPage, "WIPRO"), "Should match uppercase");
        assertTrue((boolean) isExcludedCompany.invoke(jobApplyPage, "wipro"), "Should match lowercase");
        assertTrue((boolean) isExcludedCompany.invoke(jobApplyPage, "Wipro"), "Should match mixed case");
    }

    @Test
    public void testIsExcludedCompany_PartialMatch() throws Exception {
        // Setup
        excludedCompanies.clear();
        excludedCompanies.add("wipro");
        
        // Test
        Method isExcludedCompany = NaukriJobApplyPage.class.getDeclaredMethod("isExcludedCompany", String.class);
        isExcludedCompany.setAccessible(true);
        
        assertTrue((boolean) isExcludedCompany.invoke(jobApplyPage, "Wipro Limited"), "Should match partial company name");
        assertTrue((boolean) isExcludedCompany.invoke(jobApplyPage, "Wipro Technologies"), "Should match partial company name");
    }

    @Test
    public void testIsExcludedCompany_NoMatch() throws Exception {
        // Setup
        excludedCompanies.clear();
        excludedCompanies.add("wipro");
        
        // Test
        Method isExcludedCompany = NaukriJobApplyPage.class.getDeclaredMethod("isExcludedCompany", String.class);
        isExcludedCompany.setAccessible(true);
        
        assertFalse((boolean) isExcludedCompany.invoke(jobApplyPage, "Infosys"), "Should not match different company");
        assertFalse((boolean) isExcludedCompany.invoke(jobApplyPage, "TCS"), "Should not match different company");
    }

    @Test
    public void testIsExcludedCompany_NullCompany() throws Exception {
        // Setup
        excludedCompanies.clear();
        excludedCompanies.add("wipro");
        
        // Test
        Method isExcludedCompany = NaukriJobApplyPage.class.getDeclaredMethod("isExcludedCompany", String.class);
        isExcludedCompany.setAccessible(true);
        
        assertFalse((boolean) isExcludedCompany.invoke(jobApplyPage, (String) null), "Should not match null company");
    }

    @Test
    public void testIsExcludedCompany_EmptyCompany() throws Exception {
        // Setup
        excludedCompanies.clear();
        excludedCompanies.add("wipro");
        
        // Test
        Method isExcludedCompany = NaukriJobApplyPage.class.getDeclaredMethod("isExcludedCompany", String.class);
        isExcludedCompany.setAccessible(true);
        
        assertFalse((boolean) isExcludedCompany.invoke(jobApplyPage, ""), "Should not match empty company");
        assertFalse((boolean) isExcludedCompany.invoke(jobApplyPage, "   "), "Should not match whitespace company");
    }

    @Test
    public void testIsExcludedCompany_EmptyExcludedList() throws Exception {
        // Setup
        excludedCompanies.clear();
        
        // Test
        Method isExcludedCompany = NaukriJobApplyPage.class.getDeclaredMethod("isExcludedCompany", String.class);
        isExcludedCompany.setAccessible(true);
        
        assertFalse((boolean) isExcludedCompany.invoke(jobApplyPage, "Wipro"), "Should not match when excluded list is empty");
    }

    @Test
    public void testIsExcludedCompany_MultipleExcludedCompanies() throws Exception {
        // Setup
        excludedCompanies.clear();
        excludedCompanies.add("wipro");
        excludedCompanies.add("infosys");
        excludedCompanies.add("tcs");
        
        // Test
        Method isExcludedCompany = NaukriJobApplyPage.class.getDeclaredMethod("isExcludedCompany", String.class);
        isExcludedCompany.setAccessible(true);
        
        assertTrue((boolean) isExcludedCompany.invoke(jobApplyPage, "Wipro"), "Should match first company");
        assertTrue((boolean) isExcludedCompany.invoke(jobApplyPage, "Infosys Limited"), "Should match second company");
        assertTrue((boolean) isExcludedCompany.invoke(jobApplyPage, "TCS Technologies"), "Should match third company");
        assertFalse((boolean) isExcludedCompany.invoke(jobApplyPage, "Amazon"), "Should not match non-excluded company");
    }
}
