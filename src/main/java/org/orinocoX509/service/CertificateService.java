package org.orinocoX509.service;

import org.orinocoX509.bean.CertificateInfoBean;
import org.orinocoX509.bean.CertificateValuesBean;
import org.orinocoX509.entity.CertificateProfile;
import org.orinocoX509.entity.CertificateProfile.KeySizeValues;

public interface CertificateService
{
    public CertificateInfoBean generateCertificate(CertificateProfile certificateProfile, CertificateValuesBean certificateValues);

    public CertificateInfoBean generateCACertificate(CertificateProfile certificateProfile, CertificateValuesBean certificateValues, KeySizeValues keySize);

}
