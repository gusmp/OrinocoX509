package org.orinocoX509.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuingDistributionPoint;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.entity.field.crl.AuthorityKeyIdentifierCRLField;
import org.orinocoX509.entity.field.crl.BaseCRLField;
import org.orinocoX509.entity.field.crl.CRLFieldType;
import org.orinocoX509.entity.field.crl.CRLNumberField;
import org.orinocoX509.entity.field.crl.IssuingDistributionPointField;
import org.orinocoX509.entity.field.crl.TimeNextUpdateField;
import org.orinocoX509.entity.value.crl.IssuingDistributionPointFieldValue;
import org.orinocoX509.entity.value.crl.BaseCRLFieldValue;
import org.orinocoX509.exception.EngineException;
import org.orinocoX509.exception.EngineException.EngineErrorCodes;
import org.orinocoX509.service.CAKeyService;
import org.orinocoX509.service.CRLProfileService;
import org.orinocoX509.service.CRLService;
import org.orinocoX509.service.CertificateFieldExtractorService;
import org.orinocoX509.service.CertificateStatusService;
import org.orinocoX509.service.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CRLServiceImpl implements CRLService
{
    @Autowired
    private CertificateStatusService certificateStatusService;

    @Autowired
    private CAKeyService CAKeyService;

    @Autowired
    private CRLProfileService crlProfileService;

    @Autowired
    private HttpService httpService;

    @Autowired
    private CertificateFieldExtractorService certificateFieldExtractorService;

    private static final Logger log = LoggerFactory.getLogger(CertificateStatusServiceImpl.class);

    @Override
    @Cacheable(value = "crlCache", key = "#crlProfile.profileDescription")
    public X509CRL generateCRL(CRLProfile crlProfile) throws EngineException
    {
	X509CRL crl = null;

	log.debug("Generating CRL...");
	crlProfileService.validateProfile(crlProfile);

	try
	{
	    X509v2CRLBuilder crlGenerator = new X509v2CRLBuilder(new X500Name(CAKeyService.getCACertificate().getSubjectDN().getName()), new Date());

	    addAuthorityKeyIdentifier(crlGenerator, crlProfile.getField(CRLFieldType.AUTHORITY_KEY_IDENTIFIER));
	    addNextUpdate(crlGenerator, crlProfile.getField(CRLFieldType.TIME_NEXT_UPDATE));
	    addCRLNumber(crlGenerator, crlProfile);
	    addCRLDistributionPoint(crlGenerator, crlProfile);

	    fillCRLEntries(crlGenerator, crlProfile);

	    ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").setProvider(CAKeyService.getProvider()).build(CAKeyService.getCAPrivateKey());
	    X509CRLHolder crlHolder = crlGenerator.build(contentSigner);

	    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
	    crl = (X509CRL) certificateFactory.generateCRL(new ByteArrayInputStream(crlHolder.getEncoded()));

	    log.debug("CRL generation successful");
	    return (crl);
	}
	catch (Exception exc)
	{
	    log.error("Error generating the CRL\nDetails:\n" + exc.toString());
	    throw new EngineException(EngineErrorCodes.GENERATION_CRL_ERROR, "Error generating the CRL\nDetails:\n" + exc.toString());
	}
    }

    private X509v2CRLBuilder fillCRLEntries(X509v2CRLBuilder crlGenerator, CRLProfile crlProfile)
    {
	List<CertificateStatus> revokedCertificates = certificateStatusService.getCertificatesStatus(CertificateStatusValues.R);
	addCRLEntries(crlGenerator, revokedCertificates, crlProfile);

	List<CertificateStatus> suspendedCertificates = certificateStatusService.getCertificatesStatus(CertificateStatusValues.S);
	addCRLEntries(crlGenerator, suspendedCertificates, crlProfile);

	return (crlGenerator);
    }

    private X509v2CRLBuilder addCRLEntries(X509v2CRLBuilder crlGenerator, List<CertificateStatus> certificateStatusList, CRLProfile crlProfile)
    {
	Date now = new Date();

	for (CertificateStatus certificateStatus : certificateStatusList)
	{
	    // skip certificates whose notAfter is after today
	    if (certificateStatus.getNotAfter().compareTo(now) < 0)
	    {
		continue;
	    }

	    crlGenerator.addCRLEntry(certificateStatus.getCertificateSerialNumber(), certificateStatus.getLastUpdate(), CRLReason.unspecified);
	}

	return (crlGenerator);
    }

    private X509v2CRLBuilder addAuthorityKeyIdentifier(X509v2CRLBuilder crlGenerator, BaseCRLField authorityKeyIdentifierField) throws IOException
    {
	if (authorityKeyIdentifierField != null)
	{
	    AuthorityKeyIdentifier authorityKeyId = null;
	    AuthorityKeyIdentifierCRLField akiField = ((AuthorityKeyIdentifierCRLField) authorityKeyIdentifierField);

	    if ((akiField.getAuthorityKeyIdentifier() == true) && (akiField.getAuthorityIssuerSerialNumberCertificate() == true))
	    {
		SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(new ByteArrayInputStream(CAKeyService.getCAPublicKey().getEncoded())).readObject());
		GeneralName genName = new GeneralName(new X500Name(CAKeyService.getCACertificate().getIssuerDN().getName()));

		authorityKeyId = new AuthorityKeyIdentifier(subjectPublicKeyInfo, new GeneralNames(genName), CAKeyService.getCACertificate().getSerialNumber());
	    }
	    else if (akiField.getAuthorityKeyIdentifier() == true)
	    {
		SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(new ByteArrayInputStream(CAKeyService.getCAPublicKey().getEncoded())).readObject());

		authorityKeyId = new AuthorityKeyIdentifier(subjectPublicKeyInfo);
	    }
	    else if (akiField.getAuthorityIssuerSerialNumberCertificate() == true)
	    {
		GeneralName genName = new GeneralName(new X500Name(CAKeyService.getCACertificate().getIssuerDN().getName()));
		authorityKeyId = new AuthorityKeyIdentifier(new GeneralNames(genName), CAKeyService.getCACertificate().getSerialNumber());
	    }

	    if (authorityKeyId != null)
	    {
		crlGenerator.addExtension(X509Extension.authorityKeyIdentifier, false, authorityKeyId);
	    }
	}
	return (crlGenerator);
    }

    private X509v2CRLBuilder addNextUpdate(X509v2CRLBuilder crlGenerator, BaseCRLField timeNextUpdateField)
    {
	if (timeNextUpdateField != null)
	{
	    Calendar calendar = GregorianCalendar.getInstance();
	    calendar.add(Calendar.DATE, ((TimeNextUpdateField) timeNextUpdateField).getDaysNextUpdate());
	    crlGenerator.setNextUpdate(calendar.getTime());
	}
	return (crlGenerator);
    }

    private X509v2CRLBuilder addCRLNumber(X509v2CRLBuilder crlGenerator, CRLProfile crlProfile) throws CertIOException
    {
	CRLNumberField crlNumber = (CRLNumberField) crlProfile.getField(CRLFieldType.CRL_NUMBER);
	if (crlNumber != null)
	{
	    crlGenerator.addExtension(X509Extension.cRLNumber, crlNumber.getCritical(), new CRLNumber(crlNumber.getCrlNumber()));
	    crlProfileService.updateCrlNumber(crlProfile);
	}
	return (crlGenerator);
    }

    private X509v2CRLBuilder addCRLDistributionPoint(X509v2CRLBuilder crlGenerator, CRLProfile crlProfile) throws CertIOException
    {
	IssuingDistributionPointField crlDistributionPoint = (IssuingDistributionPointField) crlProfile.getField(CRLFieldType.ISSUING_DISTRIBUTION_POINT);

	if (crlDistributionPoint != null)
	{
	    List<BaseCRLFieldValue> crldpFieldValues = crlDistributionPoint.getValues();

	    IssuingDistributionPointFieldValue crldpFieldValue = (IssuingDistributionPointFieldValue) crldpFieldValues.get(0);
	    GeneralName gn[] = new GeneralName[crldpFieldValue.getValues().size()];
	    for (int j = 0; j < crldpFieldValue.getValues().size(); j++)
	    {
		gn[j] = new GeneralName(GeneralName.uniformResourceIdentifier, crldpFieldValue.getValues().get(j));
	    }

	    IssuingDistributionPoint idp = new IssuingDistributionPoint(new DistributionPointName(new GeneralNames(gn)), crldpFieldValue.isOnlyContainsUserCerts(), crldpFieldValue.isOnlyContainsCACerts());

	    crlGenerator.addExtension(X509Extension.issuingDistributionPoint, crlDistributionPoint.getCritical(), idp);

	}
	return (crlGenerator);
    }

    private CertificateStatusValues getCRLStatus(BigInteger serialNumber, String crlUrl) throws EngineException
    {
	try
	{
	    byte[] crl = httpService.downloadData(crlUrl);

	    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
	    X509CRL x509CRL = (X509CRL) certificateFactory.generateCRL(new ByteArrayInputStream(crl));
	    if (x509CRL.getRevokedCertificate(serialNumber) == null)
	    {
		return (CertificateStatusValues.V);
	    }
	    else
	    {
		return (CertificateStatusValues.R);
	    }
	}
	catch (CertificateException exc)
	{
	    log.error("Error generation the CRL from the url " + crlUrl + " for serial number " + serialNumber + "\nDetails:\n" + exc.toString());
	    throw new EngineException(EngineErrorCodes.COULD_NOT_BE_CREATED_CRL_FACTORY, "Error creating the CertificateFactory (CRL) for serial number " + serialNumber + "\nDetails:\n" + exc.toString());
	}
	catch (CRLException exc)
	{
	    log.error("Error generation the CRL from the url " + crlUrl + " for serial number " + serialNumber + "\nDetails:\n" + exc.toString());
	    throw new EngineException(EngineErrorCodes.GENERATION_CRL_ERROR, "Error generation the CRL from the url " + crlUrl + " for serial number " + serialNumber + "\nDetails:\n" + exc.toString());
	}
    }

    private Boolean checkExpired(Date notAfter)
    {
	return (notAfter.before(new Date()));
    }

    @Override
    public CertificateStatusValues getStatus(BigInteger serialNumber, List<String> crlDistributionPoints) throws EngineException
    {
	if ((crlDistributionPoints == null) || (crlDistributionPoints.size() == 0))
	{
	    log.error("There is no CRL Distribution Points to donload the CRL for serial number " + serialNumber);
	    throw new EngineException(EngineErrorCodes.WRONG_URL, "There is no CRL Distribution Points to donload the CRL for serial number " + serialNumber);
	}

	for (String crlUrl : crlDistributionPoints)
	{
	    try
	    {
		return (getCRLStatus(serialNumber, crlUrl));
	    }
	    catch (EngineException exc)
	    {
	    }
	}

	return (CertificateStatusValues.U);
    }

    @Override
    public CertificateStatusValues getStatus(X509Certificate certificate)
    {
	if (checkExpired(certificate.getNotAfter()) == true)
	{
	    return (CertificateStatusValues.E);
	}

	List<String> crlDistributionPoints = certificateFieldExtractorService.getCRLDistributionsPoints(certificate);
	return (getStatus(certificate.getSerialNumber(), crlDistributionPoints));
    }

    @Override
    public CertificateStatusValues getStatus(X509Certificate certificate, List<String> crlDistributionPoints)
    {
	if (checkExpired(certificate.getNotAfter()) == true)
	{
	    return (CertificateStatusValues.E);
	}

	return (getStatus(certificate.getSerialNumber(), crlDistributionPoints));
    }

}
