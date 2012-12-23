package org.orinocoX509.entity.value.certificate;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AlternativeNameFieldValue extends BaseCertificateFieldValue
{
    private static final long serialVersionUID = 4336675896862938229L;

    public static enum AlternativeNameType
    {
	RFC822NAME, DIRECTORY_NAME;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ALTERNATIVE_NAME_TYPE")
    protected AlternativeNameType alternativeNameType;

    @Column(name = "VALUE")
    protected String value;

}
