package com.gpw.radar.domain.generator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

public class StringIdGenerator implements IdentifierGenerator {

    private static SecureRandom random = new SecureRandom();

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {
        return new BigInteger(250, random).toString(32);
    }
}
