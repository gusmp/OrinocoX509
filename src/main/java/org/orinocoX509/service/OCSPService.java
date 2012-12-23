package org.orinocoX509.service;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import org.orinocoX509.entity.CertificateStatus.CertificateStatusValues;

public interface OCSPService
{
    public CertificateStatusValues getStatus(X509Certificate issuer, BigInteger serialNumber);

    public CertificateStatusValues getStatus(X509Certificate issuer, BigInteger serialNumber, String ocspUrl);

}
