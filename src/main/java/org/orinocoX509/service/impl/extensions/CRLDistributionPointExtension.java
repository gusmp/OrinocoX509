package org.orinocoX509.service.impl.extensions;

import java.util.List;
import java.util.Vector;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.field.certificate.BaseCertificateField;
import org.orinocoX509.entity.value.certificate.CRLDistributionPointFieldValue;
import org.orinocoX509.entity.value.certificate.BaseCertificateFieldValue;

public class CRLDistributionPointExtension implements EngineExtension
{
    private BaseCertificateField certificateField;

    public CRLDistributionPointExtension(BaseCertificateField certificateField)
    {
	this.certificateField = certificateField;
    }

    public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
    {

	List<BaseCertificateFieldValue> crldpFieldValues = certificateField.getValues();
	Vector<DistributionPointName> listCRLDistributionPoints = new Vector<DistributionPointName>(crldpFieldValues.size());

	for (int i = 0; i < crldpFieldValues.size(); i++)
	{
	    CRLDistributionPointFieldValue crldpFieldValue = (CRLDistributionPointFieldValue) crldpFieldValues.get(i);

	    GeneralName gn[] = new GeneralName[crldpFieldValue.getValues().size()];
	    for (int j = 0; j < crldpFieldValue.getValues().size(); j++)
	    {
		gn[j] = new GeneralName(GeneralName.uniformResourceIdentifier, crldpFieldValue.getValues().get(j));
	    }

	    listCRLDistributionPoints.add(new DistributionPointName(new GeneralNames(gn)));
	}

	if (listCRLDistributionPoints.size() > 0)
	{
	    DistributionPoint[] dp = new DistributionPoint[listCRLDistributionPoints.size()];
	    for (int i = 0; i < listCRLDistributionPoints.size(); i++)
	    {
		dp[i] = new DistributionPoint(listCRLDistributionPoints.get(i), null, null);
	    }
	    certificateGenerator.addExtension(X509Extension.cRLDistributionPoints, false, new CRLDistPoint(dp));
	}

	return (certificateGenerator);
    }
}
