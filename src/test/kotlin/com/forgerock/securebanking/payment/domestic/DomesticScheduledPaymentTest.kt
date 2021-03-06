package com.forgerock.securebanking.payment.domestic

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_4
import com.forgerock.openbanking.common.model.version.OBVersion.v3_1_6
import com.forgerock.securebanking.Status.consentCondition
import com.forgerock.securebanking.Status.paymentCondition
import com.forgerock.securebanking.discovery.payment3_1_2
import com.forgerock.securebanking.discovery.payment3_1_4
import com.forgerock.securebanking.discovery.payment3_1_6
import com.forgerock.securebanking.junit.CreateTppCallback
import com.forgerock.securebanking.junit.EnabledIfVersion
import com.forgerock.securebanking.payment.PaymentAS
import com.forgerock.securebanking.payment.PaymentRS
import com.forgerock.securebanking.psu
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import uk.org.openbanking.datamodel.payment.*
import uk.org.openbanking.datamodel.service.converter.payment.OBDomesticScheduledConverter.toOBWriteDomesticScheduled2DataInitiation
import uk.org.openbanking.testsupport.payment.OBWriteDomesticScheduledConsentTestDataFactory.*

class DomesticScheduledPaymentTest(val tppResource: CreateTppCallback.TppResource) {

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentConsent_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()

        // When
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse2>(
            payment3_1_2.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp
        )
        val getConsentResult = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse2>(
            payment3_1_2.Links.links.CreateDomesticScheduledPaymentConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateDomesticScheduledPaymentConsent + "/" + consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateDomesticScheduledPaymentConsent"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentConsent_optionalFields_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()
        consentRequest.data.initiation.debtorAccount(null)
        consentRequest.data.initiation.creditorPostalAddress(null)
        consentRequest.data.initiation.remittanceInformation(null)
        consentRequest.data.initiation.supplementaryData(null)
        consentRequest.data.authorisation(null)
        consentRequest.risk.deliveryAddress(null)

        // When
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse2>(
            payment3_1_2.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp
        )
        val getConsentResult = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse2>(
            payment3_1_2.Links.links.CreateDomesticScheduledPaymentConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateDomesticScheduledPaymentConsent + "/" + consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse2>(
            payment3_1_2.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp
        )

        // accessToken to submit payment use the grant type Authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteDomesticScheduled2DataInitiation(consent.data.initiation))
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
            payment3_1_2.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse2>(
            payment3_1_2.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accesstokenClientCredentials
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.2",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_optionalFields_v3_1_2() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent2()
        consentRequest.data.initiation.debtorAccount(null)
        consentRequest.data.initiation.creditorPostalAddress(null)
        consentRequest.data.initiation.remittanceInformation(null)
        consentRequest.data.initiation.supplementaryData(null)
        consentRequest.data.authorisation(null)
        consentRequest.risk.deliveryAddress(null)

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse2>(
            payment3_1_2.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(toOBWriteDomesticScheduled2DataInitiation(consent.data.initiation))
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse2>(
            payment3_1_2.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse2>(
            payment3_1_2.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accessTokenClientCredentials
        )

        // Then
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_2.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payment-consents"]
    )
    @Test
    fun shouldCreateDomesticScheduledPaymentConsent_optionalFields_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent3()
        consentRequest.data.initiation.debtorAccount(null)
        consentRequest.data.initiation.creditorPostalAddress(null)
        consentRequest.data.initiation.remittanceInformation(null)
        consentRequest.data.initiation.supplementaryData(null)
        consentRequest.data.authorisation(null)
        consentRequest.risk.deliveryAddress(null)

        // When
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse3>(
            payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )
        val getConsentResult = PaymentRS().getConsent<OBWriteDomesticScheduledConsentResponse3>(
            payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent + "/" + consent.data.consentId,
            tppResource.tpp
        )

        // Then
        assertThat(getConsentResult).isNotNull()
        assertThat(getConsentResult.data.consentId).isNotEmpty()
        Assertions.assertThat(getConsentResult.data.status.toString()).`is`(consentCondition)
        assertThat(getConsentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent + "/" + consent.data.consentId)
        assertThat(getConsentResult.meta).isNotNull()

    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )

        // accessToken to submit payment use the grant type Authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accesstokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_v3_1_4_readRefund() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )

        // accessToken to submit payment use the grant type Authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accesstokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_v3_1_4_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )

        // accessToken to submit payment use the grant type Authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accesstokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_v3_1_4_readRefund_Null() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        consentRequest.data.readRefundAccount(null)
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )

        // accessToken to submit payment use the grant type Authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accesstokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.4",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_optionalFields_v3_1_4() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        consentRequest.data.initiation.debtorAccount(null)
        consentRequest.data.initiation.creditorPostalAddress(null)
        consentRequest.data.initiation.remittanceInformation(null)
        consentRequest.data.initiation.supplementaryData(null)
        consentRequest.data.authorisation(null)
        consentRequest.risk.deliveryAddress(null)

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_4
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_4
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse4>(
            payment3_1_4.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accessTokenClientCredentials,
            v3_1_4
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_4.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_v3_1_6() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )

        // accessToken to submit payment use the grant type Authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accesstokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.debtor.name).isEqualTo(consentRequest.data.initiation.debtorAccount.name)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_v3_1_6_readRefund() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )

        // accessToken to submit payment use the grant type Authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accesstokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund.account.identification).isEqualTo(consent.data.initiation.debtorAccount.identification)
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        assertThat(paymentResult.data.debtor.name).isEqualTo(consentRequest.data.initiation.debtorAccount.name)
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_v3_1_6_readRefund_debtorAccountNotPresent() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        consentRequest.data.readRefundAccount(OBReadRefundAccountEnum.YES)
        consentRequest.data.initiation.debtorAccount(null)
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )

        // accessToken to submit payment use the grant type Authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accesstokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.YES)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_v3_1_6_readRefund_null() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        consentRequest.data.readRefundAccount(null)
        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )

        // accessToken to submit payment use the grant type Authorization_code
        val accesstokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accesstokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accesstokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accesstokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isNull()
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }

    @EnabledIfVersion(
        type = "payments",
        apiVersion = "v3.1.6",
        operations = ["CreateDomesticScheduledPaymentConsent", "CreateDomesticScheduledPayment"],
        apis = ["domestic-scheduled-payments"]
    )
    @Test
    fun shouldCreateDomesticScheduledPayment_optionalFields_v3_1_6() {
        // Given
        val consentRequest = aValidOBWriteDomesticScheduledConsent4()
        consentRequest.data.initiation.debtorAccount(null)
        consentRequest.data.initiation.creditorPostalAddress(null)
        consentRequest.data.initiation.remittanceInformation(null)
        consentRequest.data.initiation.supplementaryData(null)
        consentRequest.data.authorisation(null)
        consentRequest.risk.deliveryAddress(null)

        val consent = PaymentRS().consent<OBWriteDomesticScheduledConsentResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPaymentConsent,
            consentRequest,
            tppResource.tpp,
            v3_1_6
        )
        // accessToken to submit payment use the grant type authorization_code
        val accessTokenAuthorizationCode = PaymentAS().headlessAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            psu,
            tppResource.tpp
        )
        // accessToken to get the payment use the grant type client_credentials
        val accessTokenClientCredentials = PaymentAS().clientCredentialsAuthentication(
            consent.data.consentId,
            tppResource.tpp.registrationResponse,
            tppResource.tpp
        )
        val paymentSubmissionRequest = OBWriteDomesticScheduled2().data(
            OBWriteDomesticScheduled2Data()
                .consentId(consent.data.consentId)
                .initiation(consent.data.initiation)
        ).risk(consent.risk)
        val submissionResp = PaymentRS().submitPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPayment,
            paymentSubmissionRequest,
            accessTokenAuthorizationCode,
            tppResource.tpp,
            v3_1_6
        )

        // When
        val paymentResult = PaymentRS().getPayment<OBWriteDomesticScheduledResponse5>(
            payment3_1_6.Links.links.CreateDomesticScheduledPayment,
            submissionResp.data.domesticScheduledPaymentId,
            accessTokenClientCredentials,
            v3_1_6
        )

        // Then
        assertThat(consent.data.readRefundAccount).isEqualTo(OBReadRefundAccountEnum.NO)
        assertThat(paymentResult).isNotNull()
        assertThat(paymentResult.data.refund).isNull()
        assertThat(paymentResult.data.domesticScheduledPaymentId).isNotEmpty()
        assertThat(paymentResult.data.creationDateTime).isNotNull()
        Assertions.assertThat(paymentResult.data.status.toString()).`is`(paymentCondition)
        assertThat(paymentResult.links.self).isEqualTo(payment3_1_6.Links.links.CreateDomesticScheduledPayment + "/" + submissionResp.data.domesticScheduledPaymentId)
        assertThat(paymentResult.meta).isNotNull()
    }
}
