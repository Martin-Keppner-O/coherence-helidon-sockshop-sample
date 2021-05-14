/*
 * Copyright (c) 2020,2021 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package io.helidon.examples.sockshop.payment;

import com.oracle.coherence.cdi.Name;
import com.tangosol.net.NamedMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import javax.inject.Inject;

import org.eclipse.microprofile.opentracing.Traced;

import java.util.Collection;

import static com.tangosol.util.Filters.equal;

/**
 * An implementation of {@link io.helidon.examples.sockshop.payment.PaymentRepository}
 * that that uses Coherence as a backend data store.
 */
@ApplicationScoped
@Traced
public class CoherencePaymentRepository implements PaymentRepository {
    protected final NamedMap<AuthorizationId, Authorization> payments;

    @Inject
    CoherencePaymentRepository(@Name("payments") NamedMap<AuthorizationId, Authorization> payments) {
        this.payments = payments;
    }

    @PostConstruct
    void createIndexes() {
        payments.addIndex(Authorization::getOrderId, false, null);
    }

    @Override
    public void saveAuthorization(Authorization auth) {
        payments.put(auth.getId(), auth);
    }

    @Override
    public Collection<? extends Authorization> findAuthorizationsByOrder(String orderId) {
        return payments.values(equal(Authorization::getOrderId, orderId));
    }
}
