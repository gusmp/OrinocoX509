package org.orinocoX509.service.impl.extensions;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.qualified.Iso4217CurrencyCode;
import org.bouncycastle.asn1.x509.qualified.MonetaryValue;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.orinocoX509.entity.value.certificate.CertificateFieldValue;
import org.orinocoX509.entity.value.certificate.QCStatementFieldValue;
import org.orinocoX509.entity.value.certificate.QCStatementFieldValue.QCStatementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QualifiedCertificateStatementsExtension implements EngineExtension
{
	private List<CertificateFieldValue> values;
	private Boolean critical;
	private static final Logger log = LoggerFactory.getLogger(QualifiedCertificateStatementsExtension.class);
	
	public QualifiedCertificateStatementsExtension(List<CertificateFieldValue> values, Boolean critical)
	{
		this.values = values;
		this.critical = critical;
	}
	
	public X509v3CertificateBuilder applyExtension(X509v3CertificateBuilder certificateGenerator) throws CertIOException
	{
		
		ArrayList<ASN1Encodable> qCStatementArray = new ArrayList<ASN1Encodable>(this.values.size());
		
		for(int i=0; i < this.values.size(); i++)
		{
			QCStatementFieldValue qCStatementFieldValue = (QCStatementFieldValue) this.values.get(i);
			if (qCStatementFieldValue.getQCStatement() == QCStatementType.ID_QCS_PKIXQCSYNTAX_V1)
			{
				qCStatementArray.add(new QCStatement(QCStatement.id_qcs_pkixQCSyntax_v1));
			}
			else if (qCStatementFieldValue.getQCStatement() == QCStatementType.ID_QCS_PKIXQCSYNTAX_V2)
			{
				qCStatementArray.add(new QCStatement(QCStatement.id_qcs_pkixQCSyntax_v2));
			}
			else if (qCStatementFieldValue.getQCStatement() == QCStatementType.ID_ETSI_QCS_QCCOMPILANCE)
			{
				qCStatementArray.add(new QCStatement(QCStatement.id_etsi_qcs_QcCompliance));
			}
			else if (qCStatementFieldValue.getQCStatement() == QCStatementType.ID_ETSI_QCS_LIMITE_VALUE)
			{
				Iso4217CurrencyCode currencyCode = new Iso4217CurrencyCode(qCStatementFieldValue.getCurrencyCode());
				MonetaryValue value = new MonetaryValue(currencyCode, qCStatementFieldValue.getLimiteValue() , qCStatementFieldValue.getLimiteExponent());
				qCStatementArray.add(new QCStatement(QCStatement.id_etsi_qcs_LimiteValue, value));
			}
			else if (qCStatementFieldValue.getQCStatement() == QCStatementType.ID_ETSI_QCS_RETENTION_PERIOD)
			{
				qCStatementArray.add(new QCStatement(QCStatement.id_etsi_qcs_RetentionPeriod, new ASN1Integer(qCStatementFieldValue.getRetentionPeriod())));
			}
			else if (qCStatementFieldValue.getQCStatement() == QCStatementType.ID_ETSI_QCS_QCSSCD)
			{
				qCStatementArray.add(new QCStatement(QCStatement.id_etsi_qcs_QcSSCD));
			}
		}
		
		if (qCStatementArray.size() > 0)
		{
			log.debug("Number of QCStatement to apply " + qCStatementArray.size() + " Critical: " + critical.booleanValue());
			ASN1Encodable qCStatementEncodable[] = qCStatementArray.toArray(new ASN1Encodable[qCStatementArray.size()]);
			DERSequence derSequence = new DERSequence(qCStatementEncodable);
			certificateGenerator.addExtension(X509Extension.qCStatements, critical.booleanValue(), derSequence);
		}
		
		return(certificateGenerator);
	}
}
