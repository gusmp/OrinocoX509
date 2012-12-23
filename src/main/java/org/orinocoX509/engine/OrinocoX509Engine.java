package org.orinocoX509.engine;

import java.math.BigInteger;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.List;

import org.orinocoX509.bean.CertificateInfoBean;
import org.orinocoX509.bean.CertificateValuesBean;
import org.orinocoX509.entity.CRLProfile;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.CertificateStatus;
import org.orinocoX509.entity.CertificateProfile.KeySizeValues;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;
import org.orinocoX509.service.CRLProfileService;
import org.orinocoX509.service.CRLService;
import org.orinocoX509.service.CertificateProfileService;
import org.orinocoX509.service.CertificateService;
import org.orinocoX509.service.CertificateStatusService;
import org.orinocoX509.service.OCSPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrinocoX509Engine
{
    @Autowired
    private CertificateProfileService certificateProfileService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateStatusService certificateStatusService;

    @Autowired
    private CRLProfileService crlProfileService;

    @Autowired
    private CRLService crlService;

    @Autowired
    private OCSPService ocspService;

    // ************************
    // Certificate profiles
    // ************************

    public CertificateProfile saveCertificateProfile(CertificateProfile certificateProfile)
    {
	return (certificateProfileService.saveProfile(certificateProfile));
    }

    public void deleteCertificateProfile(CertificateProfile certificateProfile)
    {
	certificateProfileService.deleteProfile(certificateProfile);
    }

    public CertificateProfile getCertificateProfile(CertificateProfile certificateProfile)
    {
	return (certificateProfileService.getProfile(certificateProfile));
    }

    public CertificateProfile getCertificateProfile(Integer certificateProfileId)
    {
	CertificateProfile certificateProfile = new CertificateProfile();
	certificateProfile.setProfileId(certificateProfileId);
	return (certificateProfileService.getProfile(certificateProfile));
    }

    public List<CertificateProfile> getCertificateProfiles()
    {
	return (certificateProfileService.getProfiles());
    }

    // ************************
    // Certificate operations
    // ************************

    public CertificateInfoBean generateCertificate(CertificateProfile certificateProfile, CertificateValuesBean certificateValues)
    {
	CertificateInfoBean certificateInfo = certificateService.generateCertificate(certificateProfile, certificateValues);
	saveStatus(certificateInfo);
	return (certificateInfo);
    }

    public CertificateInfoBean generateCACertificate(CertificateProfile certificateProfile, CertificateValuesBean certificateValues, KeySizeValues keySize)
    {
	CertificateInfoBean certificateInfo = certificateService.generateCACertificate(certificateProfile, certificateValues, keySize);
	saveStatus(certificateInfo);
	return (certificateInfo);
    }

    private void saveStatus(CertificateInfoBean certificateInfo)
    {
	certificateStatusService.saveStatus(new CertificateStatus(certificateInfo.getSerialNumber(), CertificateStatusValues.V, certificateInfo.getNotAfter(), certificateInfo.getNotBefore()));
    }

    public CertificateStatus getCertificateStatus(BigInteger serialNumber)
    {
	return (certificateStatusService.getStatus(new CertificateStatus(serialNumber)));
    }

    // ************************
    // CRL profiles
    // ************************

    public CRLProfile saveCRLProfile(CRLProfile crlProfile)
    {
	return (crlProfileService.saveProfile(crlProfile));
    }

    public void deleteCRLProfile(CRLProfile crlProfile)
    {
	crlProfileService.deleteProfile(crlProfile);
    }

    public CRLProfile getCRLProfile(CRLProfile crlProfile)
    {
	return (crlProfileService.getProfile(crlProfile));
    }

    public CRLProfile getCRLProfile(Integer crlProfileId)
    {
	CRLProfile crlProfile = new CRLProfile();
	crlProfile.setProfileId(crlProfileId);
	return (crlProfileService.getProfile(crlProfile));
    }

    public List<CRLProfile> getCRLProfiles()
    {
	return (crlProfileService.getProfiles());
    }

    // ************************
    // CRL operations
    // ************************

    public X509CRL generateCRL(CRLProfile crlProfile)
    {
	return (crlService.generateCRL(crlProfile));
    }

    public void suspendCertificate(BigInteger serialNumber)
    {
	CertificateStatus certificateStatus = certificateStatusService.getStatus(new CertificateStatus(serialNumber));
	if (certificateStatus.getCertificateStatus() == CertificateStatusValues.V)
	{
	    certificateStatus.setCertificateStatus(CertificateStatusValues.S);
	    certificateStatusService.saveStatus(certificateStatus);
	}
    }

    public void restoreCertificate(BigInteger serialNumber)
    {
	CertificateStatus certificateStatus = certificateStatusService.getStatus(new CertificateStatus(serialNumber));
	if (certificateStatus.getCertificateStatus() == CertificateStatusValues.S)
	{
	    certificateStatus.setCertificateStatus(CertificateStatusValues.V);
	    certificateStatusService.saveStatus(certificateStatus);
	}
    }

    public void revokeCertificate(BigInteger serialNumber)
    {
	CertificateStatus certificateStatus = certificateStatusService.getStatus(new CertificateStatus(serialNumber));
	if ((certificateStatus.getCertificateStatus() == CertificateStatusValues.V) || (certificateStatus.getCertificateStatus() == CertificateStatusValues.S))
	{
	    certificateStatus.setCertificateStatus(CertificateStatusValues.R);
	    certificateStatusService.saveStatus(certificateStatus);
	}
    }

    public CertificateStatus getCertificateStatusByCRL(X509Certificate certificate)
    {
	CertificateStatus certificateStatus = new CertificateStatus(certificate.getSerialNumber());
	certificateStatus.setCertificateStatus(crlService.getStatus(certificate));
	return (certificateStatus);
    }

    public CertificateStatus getCertificateStatusByCRL(BigInteger serialNumber, List<String> crlDistributionPointList)
    {
	CertificateStatus certificateStatus = new CertificateStatus(serialNumber);
	certificateStatus.setCertificateStatus(crlService.getStatus(serialNumber, crlDistributionPointList));
	return (certificateStatus);
    }

    public CertificateStatus getCertificateStatusByCRL(X509Certificate certificate, List<String> crlDistributionPointList)
    {
	CertificateStatus certificateStatus = new CertificateStatus(certificate.getSerialNumber());
	certificateStatus.setCertificateStatus(crlService.getStatus(certificate, crlDistributionPointList));
	return (certificateStatus);
    }

    // ************************
    // OCSP
    // ************************

    public CertificateStatus getCertificateStatusByOCSP(X509Certificate issuer, BigInteger serialNumber)
    {
	CertificateStatus certificateStatus = new CertificateStatus(serialNumber);
	certificateStatus.setCertificateStatus(ocspService.getStatus(issuer, serialNumber));
	return (certificateStatus);
    }

    public CertificateStatus getCertificateStatusByOCSP(X509Certificate issuer, BigInteger serialNumber, String urlOcsp)
    {
	CertificateStatus certificateStatus = new CertificateStatus(serialNumber);
	certificateStatus.setCertificateStatus(ocspService.getStatus(issuer, serialNumber, urlOcsp));
	return (certificateStatus);
    }

}
