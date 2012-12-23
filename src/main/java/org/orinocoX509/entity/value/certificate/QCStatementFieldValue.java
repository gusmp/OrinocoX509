package org.orinocoX509.entity.value.certificate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import org.orinocoX509.entity.consts.DiscriminatorValues;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CER_QUALIFIED_CERTIFICATE_STATEMENT_FIELD_VALUE")
@Getter
@Setter
@DiscriminatorValue(value = DiscriminatorValues.QUALIFIED_CERTIFICATE_STATEMENT)
public class QCStatementFieldValue extends BaseCertificateFieldValue
{
    private static final long serialVersionUID = -8768575731923499769L;

    public static enum QCStatementType
    {
	ID_QCS_PKIXQCSYNTAX_V1, ID_QCS_PKIXQCSYNTAX_V2, ID_ETSI_QCS_QCCOMPILANCE, ID_ETSI_QCS_LIMITE_VALUE, ID_ETSI_QCS_RETENTION_PERIOD, ID_ETSI_QCS_QCSSCD
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "QCSTATEMENT")
    private QCStatementType QCStatement;

    @Column(name = "RETENTION_PERIOD")
    private Integer retentionPeriod;

    @Column(name = "LIMITE_VALUE")
    private Integer limiteValue;

    @Column(name = "LIMITE_EXPONENT")
    private Integer limiteExponent;

    @Column(name = "CURRENCY_CODE")
    private String currencyCode;

    public QCStatementFieldValue()
    {
    }

    public QCStatementFieldValue(QCStatementType QCStatement)
    {
	this.QCStatement = QCStatement;
    }

    public QCStatementFieldValue(QCStatementType QCStatement, Integer retentionPeriod)
    {
	this.QCStatement = QCStatement;
	this.retentionPeriod = retentionPeriod;
    }

    public QCStatementFieldValue(QCStatementType QCStatement, String currencyCode, Integer limiteValue)
    {
	this.QCStatement = QCStatement;
	this.currencyCode = currencyCode;
	this.limiteValue = limiteValue;
	this.limiteExponent = 1;
    }

}
