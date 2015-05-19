/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.api.account.impl.shared.dto;

import org.eclipse.che.api.core.rest.shared.dto.Hyperlinks;
import org.eclipse.che.api.core.rest.shared.dto.Link;
import org.eclipse.che.dto.shared.DTO;

import java.util.List;

/**
 * @author Max Shaposhnik
 */
@DTO
public interface InvoiceDescriptor extends Hyperlinks {
    String getAccountId();

    void setAccountId(String accountId);

    InvoiceDescriptor withAccountId(String accountId);


    Long getId();

    void setId(Long id);

    InvoiceDescriptor withId(Long id);


    Double getTotal();

    void setTotal(Double total);

    InvoiceDescriptor withTotal(Double total);


    Long getCreationDate();

    void setCreationDate(Long creationDate);

    InvoiceDescriptor withCreationDate(Long creationDate);


    Long getFromDate();

    void setFromDate(Long fromDate);

    InvoiceDescriptor withFromDate(Long fromDate);


    Long getTillDate();

    void setTillDate(Long tillDate);

    InvoiceDescriptor withTillDate(Long tillDate);


    String getCreditCardId();

    void setCreditCardId(String creditCardId);

    InvoiceDescriptor withCreditCardId(String creditCardId);


    String getPaymentState();

    void setPaymentState(String paymentState);

    InvoiceDescriptor withPaymentState(String paymentState);


    Long getPaymentDate();

    void setPaymentDate(Long paymentDate);

    InvoiceDescriptor withPaymentDate(Long paymentDate);


    Long getMailingDate();

    void setMailingDate(Long mailingDate);

    InvoiceDescriptor withMailingDate(Long mailingDate);


    List<Charge> getCharges();

    void setCharges(List<Charge> charges);

    InvoiceDescriptor withCharges(List<Charge> charges);

    InvoiceDescriptor withLinks(List<Link> links);
}