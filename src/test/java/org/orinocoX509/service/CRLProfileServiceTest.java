package org.orinocoX509.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orinocoX509.TestConst;
import org.orinocoX509.TestSupport;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.field.crl.AuthorityKeyIdentifierCRLField;
import org.orinocoX509.entity.field.crl.IssuingDistributionPointField;
import org.orinocoX509.entity.field.crl.CRLFieldType;
import org.orinocoX509.entity.field.crl.CRLNumberField;
import org.orinocoX509.entity.field.crl.TimeNextUpdateField;
import org.orinocoX509.entity.value.crl.IssuingDistributionPointFieldValue;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../applicationContext-Test.xml")
public class CRLProfileServiceTest
{
    @Autowired
    CRLProfileService crlProfileService;

    @Autowired
    TestSupport testSupport;

    private CRLProfile crlProfile = null;

    @Before
    public void init()
    {
    }

    @Test
    public void addCRLProfileTest()
    {
	crlProfile = testSupport.createCRLProfile("addCRLProfileTest");
	crlProfileService.saveProfile(crlProfile);
	assertNotNull(crlProfileService.getProfile(crlProfile));
    }

    @Test
    public void deleteProfileTest()
    {
	crlProfile = testSupport.createCRLProfile("deleteProfileTest");
	crlProfileService.saveProfile(crlProfile);
	crlProfileService.deleteProfile(crlProfile);
	crlProfile = crlProfileService.getProfile(crlProfile);
	assertNull(crlProfile);
    }

    @Test
    public void getProfileTest()
    {
	String PROFILE_NAME = "getProfileTest";

	crlProfile = testSupport.createCRLProfile(PROFILE_NAME);
	crlProfileService.saveProfile(crlProfile);
	CRLProfile crlProfile2 = crlProfileService.getProfile(crlProfile);

	assertNotNull(crlProfile2);
	assertEquals(PROFILE_NAME, crlProfile2.getProfileName());
	assertEquals(TestConst.PROFILE_DESCRIPTION, crlProfile2.getProfileDescription());
    }

    @Test
    public void addDoubleFieldTest()
    {
	try
	{
	    crlProfile = testSupport.createCRLProfile("addDoubleFieldTest");

	    crlProfile.addField(new CRLNumberField(crlProfile, false));
	    crlProfile.addField(new CRLNumberField(crlProfile, false));
	    fail();
	}
	catch (EngineException exc)
	{
	    assertEquals(EngineErrorCodes.DUPLICATE_FIELD_IN_PROFILE, exc.getErrorCode());
	}
    }

    @Test
    public void addDoubleProfileTest()
    {
	String PROFILE_NAME = "addDoubleCrlProfileTest";
	try
	{
	    crlProfile = testSupport.createCRLProfile(PROFILE_NAME);
	    crlProfileService.saveProfile(crlProfile);
	    CRLProfile crlProfile2 = testSupport.createCRLProfile(PROFILE_NAME);
	    crlProfileService.saveProfile(crlProfile2);
	    fail();
	}
	catch (EngineException exc)
	{
	    assertEquals(EngineErrorCodes.DUPLICATE_PROFILE, exc.getErrorCode());
	}
    }

    @Test
    public void updateProfileTest()
    {
	Boolean CRITICAL = false;
	Boolean KEY_IDENTIFIER = false;
	Boolean ISSUER_SERIAL_NUMBER = false;

	crlProfile = testSupport.createCRLProfile("updateProfileTest");
	crlProfile.addField(new AuthorityKeyIdentifierCRLField(crlProfile, CRITICAL, KEY_IDENTIFIER, ISSUER_SERIAL_NUMBER));
	crlProfileService.saveProfile(crlProfile);

	CRLProfile crlProfile2 = crlProfileService.getProfile(crlProfile);
	crlProfile2.setProfileDescription(TestConst.PROFILE_DESCRIPTION + "2");
	AuthorityKeyIdentifierCRLField akiCRLField = (AuthorityKeyIdentifierCRLField) crlProfile2.getField(CRLFieldType.AUTHORITY_KEY_IDENTIFIER);
	akiCRLField.setCritical(!CRITICAL);
	akiCRLField.setAuthorityKeyIdentifier(!KEY_IDENTIFIER);
	akiCRLField.setAuthorityIssuerSerialNumberCertificate(!ISSUER_SERIAL_NUMBER);
	crlProfileService.saveProfile(crlProfile2);

	crlProfile = crlProfileService.getProfile(crlProfile2);
	assertEquals(TestConst.PROFILE_DESCRIPTION + "2", crlProfile.getProfileDescription());
	akiCRLField = (AuthorityKeyIdentifierCRLField) crlProfile.getField(CRLFieldType.AUTHORITY_KEY_IDENTIFIER);
	assertEquals(!CRITICAL, akiCRLField.getCritical());
	assertEquals(!KEY_IDENTIFIER, akiCRLField.getAuthorityKeyIdentifier());
	assertEquals(!ISSUER_SERIAL_NUMBER, akiCRLField.getAuthorityIssuerSerialNumberCertificate());
    }

    // Authority Key Identifier

    @Test
    public void addAuthorityKeyIdentifierFieldTest()
    {
	Boolean CRITICAL = false;
	Boolean KEY_IDENTIFIER = true;
	Boolean ISSUER_SERIAL_NUMBER = false;

	crlProfile = testSupport.createCRLProfile("addAuthorityKeyIdentifierFieldTest");
	crlProfile.addField(new AuthorityKeyIdentifierCRLField(crlProfile, CRITICAL, KEY_IDENTIFIER, ISSUER_SERIAL_NUMBER));
	crlProfileService.saveProfile(crlProfile);

	CRLProfile crlProfile2 = crlProfileService.getProfile(crlProfile);

	AuthorityKeyIdentifierCRLField akiField = (AuthorityKeyIdentifierCRLField) crlProfile2.getField(CRLFieldType.AUTHORITY_KEY_IDENTIFIER);
	assertNotNull(crlProfile2);
	assertEquals(CRITICAL, akiField.getCritical());
	assertEquals(KEY_IDENTIFIER, akiField.getAuthorityKeyIdentifier());
	assertEquals(ISSUER_SERIAL_NUMBER, akiField.getAuthorityIssuerSerialNumberCertificate());
    }

    // Issuing Distribution Point
    @Test
    public void addIssuingDistributionPointFieldTest()
    {
	Boolean CRITICAL = false;

	crlProfile = testSupport.createCRLProfile("addIssuingDistributionPointFieldTest");
	IssuingDistributionPointField issuingDistributionPoint = new IssuingDistributionPointField(crlProfile, CRITICAL);
	issuingDistributionPoint.addValue(new IssuingDistributionPointFieldValue(Arrays.asList(TestConst.CRLDP1_URL1, TestConst.CRLDP1_URL2), false, false));
	crlProfile.addField(issuingDistributionPoint);

	crlProfileService.saveProfile(crlProfile);

	CRLProfile crlProfile2 = crlProfileService.getProfile(crlProfile);
	issuingDistributionPoint = (IssuingDistributionPointField) crlProfile2.getField(CRLFieldType.ISSUING_DISTRIBUTION_POINT);
	assertEquals(1, issuingDistributionPoint.getValues().size());
	assertEquals(CRITICAL, issuingDistributionPoint.getCritical());

	for (int i = 0; i < issuingDistributionPoint.getValues().size(); i++)
	{
	    IssuingDistributionPointFieldValue issuingDpFieldValue = (IssuingDistributionPointFieldValue) issuingDistributionPoint.getValues().get(i);

	    if ((issuingDpFieldValue.getValues().get(0).equalsIgnoreCase(TestConst.CRLDP1_URL1) == true) && (issuingDpFieldValue.getValues().get(1).equalsIgnoreCase(TestConst.CRLDP1_URL2) == true))
	    {
		continue;
	    }
	    /*
	     * else if
	     * ((issuingDpFieldValue.getValues().get(0).equalsIgnoreCase(
	     * TestConst.CRLDP2_URL1) == true) &&
	     * (issuingDpFieldValue.getValues().get(1).equalsIgnoreCase(TestConst
	     * .CRLDP2_URL2) == true))
	     * {
	     * continue;
	     * }
	     */

	    fail();
	}
    }

    // CRL Number

    @Test
    public void addCRLNumberFieldTest()
    {
	Boolean CRITICAL = false;

	crlProfile = testSupport.createCRLProfile("addCRLNumberFieldTest");
	crlProfile.addField(new CRLNumberField(crlProfile, CRITICAL));
	crlProfileService.saveProfile(crlProfile);

	CRLProfile crlProfile2 = crlProfileService.getProfile(crlProfile);
	CRLNumberField crlNumber = (CRLNumberField) crlProfile2.getField(CRLFieldType.CRL_NUMBER);
	assertNotNull(crlNumber);
	assertEquals(CRITICAL, crlNumber.getCritical());
    }

    // Time Next Update
    @Test
    public void addTimeNextUpdateFieldTest()
    {
	crlProfile = testSupport.createCRLProfile("addTimeNextUpdateFieldTest");
	crlProfile.addField(new TimeNextUpdateField(crlProfile, TestConst.DAYS_NEXT_UPDATE));
	crlProfileService.saveProfile(crlProfile);

	CRLProfile crlProfile2 = crlProfileService.getProfile(crlProfile);
	TimeNextUpdateField timeNextUpdateField = (TimeNextUpdateField) crlProfile2.getField(CRLFieldType.TIME_NEXT_UPDATE);
	assertNotNull(timeNextUpdateField);
	assertEquals(TestConst.DAYS_NEXT_UPDATE, timeNextUpdateField.getDaysNextUpdate());
    }

    // List profiles
    @Test
    public void getAllCRLprofiles()
    {
	crlProfile = testSupport.createCRLProfile("getAllCRLprofiles1");
	crlProfileService.saveProfile(crlProfile);
	CRLProfile crlProfile2 = testSupport.createCRLProfile("getAllCRLprofiles2");
	crlProfileService.saveProfile(crlProfile2);

	List<CRLProfile> profileList = crlProfileService.getProfiles();
	assertNotNull(profileList);
	assertEquals(2, profileList.size());
	crlProfileService.deleteProfile(crlProfile2);
    }

    @After
    public void tearDown()
    {
	if (crlProfile != null)
	{
	    crlProfileService.deleteProfile(crlProfile);
	}
    }

}
