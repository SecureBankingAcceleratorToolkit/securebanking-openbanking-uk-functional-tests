package com.forgerock.openbanking.discovery

import com.forgerock.openbanking.DOMAIN
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.isSuccessful
import com.github.kittinunf.fuel.gson.gsonDeserializer
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READPARTY
import uk.org.openbanking.datamodel.account.OBExternalPermissions1Code.READPARTYPSU

val rsDiscovery by lazy { getRsConfiguration() }

val accountAndTransaction3_1 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.filter { it.Version.equals("v3.1") }?.first()!!
}
val accountAndTransaction3_1_1 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.filter { it.Version.equals("v3.1.1") }?.first()!!
}

val accountAndTransaction3_1_3 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.filter { it.Version.equals("v3.1.3") }?.first()!!
}

val accountAndTransaction3_1_4 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.filter { it.Version.equals("v3.1.4") }?.first()!!
}

val accountAndTransaction3_1_5 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.filter { it.Version.equals("v3.1.5") }?.first()!!
}

val accountAndTransaction3_1_6 by lazy {
    rsDiscovery.Data.AccountAndTransactionAPI?.filter { it.Version.equals("v3.1.6") }?.first()!!
}

val payment3_1 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.filter { it.Version.equals("v3.1") }?.first()!!
}

val payment3_1_1 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.filter { it.Version.equals("v3.1.1") }?.first()!!
}

val payment3_1_2 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.filter { it.Version.equals("v3.1.2") }?.first()!!
}

val payment3_1_3 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.filter { it.Version.equals("v3.1.3") }?.first()!!
}

val payment3_1_4 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.filter { it.Version.equals("v3.1.4") }?.first()!!
}

val payment3_1_5 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.filter { it.Version.equals("v3.1.5") }?.first()!!
}

val payment3_1_6 by lazy {
    rsDiscovery.Data.PaymentInitiationAPI?.filter { it.Version.equals("v3.1.6") }?.first()!!
}

val accountPermissions by lazy {
    // TODO - limit permission to those enabled on cluster
    OBExternalPermissions1Code.values().asList()
}

// Events notification
val eventsNotification3_0 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.filter { it.Version.equals("v3.0") }?.first()!!
}

val eventsNotification3_1 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.filter { it.Version.equals("v3.1") }?.first()!!
}

val eventsNotification3_1_1 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.filter { it.Version.equals("v3.1.1") }?.first()!!
}

val eventsNotification3_1_2 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.filter { it.Version.equals("v3.1.2") }?.first()!!
}

val eventsNotification3_1_3 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.filter { it.Version.equals("v3.1.3") }?.first()!!
}

val eventsNotification3_1_4 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.filter { it.Version.equals("v3.1.4") }?.first()!!
}

val eventsNotification3_1_5 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.filter { it.Version.equals("v3.1.5") }?.first()!!
}

val eventsNotification3_1_6 by lazy {
    rsDiscovery.Data.EventNotificationAPI?.filter { it.Version.equals("v3.1.6") }?.first()!!
}

val rsDiscoveryMap by lazy {
    val paymentVersions = rsDiscovery.Data.PaymentInitiationAPI?.map { it.Version to it.Links.linkValues }?.toList()
    val accounts = rsDiscovery.Data.AccountAndTransactionAPI?.map { it.Version to it.Links.linkValues }?.toList()
    val funds = rsDiscovery.Data.FundsConfirmationAPI?.map { it.Version to it.Links.linkValues }?.toList()
    val events = rsDiscovery.Data.EventNotificationAPI?.map { it.Version to it.Links.linkValues }?.toList()
    return@lazy mapOf(
            "payments" to paymentVersions,
            "accounts" to accounts,
            "funds" to funds,
            "events" to events
    )
}

private fun getRsConfiguration(): RsDiscovery {
    val (_, response, result) = Fuel.get("https://rs.aspsp.$DOMAIN/open-banking/discovery").responseObject<RsDiscovery>(gsonDeserializer())
    if (!response.isSuccessful) throw AssertionError("Failed to load RS Discovery", result.component2())
    return result.get()
}

// Auto generated from JSON
data class RsDiscovery(
    val Data: RsDiscoveryData
) {
    data class RsDiscoveryData(
            val AccountAndTransactionAPI: List<RsDiscoveryAccountAndTransactionAPI>?,
            val EventNotificationAPI: List<RsDiscoveryEventNotificationAPI>?,
            val FinancialId: String,
            val FundsConfirmationAPI: List<RsDiscoveryFundsConfirmationAPI>?,
            val PaymentInitiationAPI: List<RsDiscoveryPaymentInitiationAPI>?
    ) {
        data class RsDiscoveryPaymentInitiationAPI(
                val Links: RsDiscoveryPaymentInitiationAPILinks,
                val Version: String
        ) {
            data class RsDiscoveryPaymentInitiationAPILinks(
                val `@type`: String,
                val linkValues: List<String>,
                val links: Links
            ) {
                data class Links(
                    val CreateDomesticPayment: String,
                    val CreateDomesticPaymentConsent: String,
                    val CreateDomesticScheduledPayment: String,
                    val CreateDomesticScheduledPaymentConsent: String,
                    val CreateDomesticStandingOrder: String,
                    val CreateDomesticStandingOrderConsent: String,
                    val CreateFilePayment: String,
                    val CreateFilePaymentConsent: String,
                    val CreateFilePaymentFile: String,
                    val CreateInternationalPayment: String,
                    val CreateInternationalPaymentConsent: String,
                    val CreateInternationalScheduledPayment: String,
                    val CreateInternationalScheduledPaymentConsent: String,
                    val CreateInternationalStandingOrder: String,
                    val CreateInternationalStandingOrderConsent: String,
                    val GetDomesticPayment: String,
                    val GetDomesticPaymentConsent: String,
                    val GetDomesticPaymentConsentsConsentIdFundsConfirmation: String,
                    val GetDomesticScheduledPayment: String,
                    val GetDomesticScheduledPaymentConsent: String,
                    val GetDomesticStandingOrder: String,
                    val GetDomesticStandingOrderConsent: String,
                    val GetFilePayment: String,
                    val GetFilePaymentConsent: String,
                    val GetFilePaymentFile: String,
                    val GetFilePaymentReport: String,
                    val GetInternationalPayment: String,
                    val GetInternationalPaymentConsent: String,
                    val GetInternationalPaymentConsentsConsentIdFundsConfirmation: String,
                    val GetInternationalScheduledPayment: String,
                    val GetInternationalScheduledPaymentConsent: String,
                    val GetInternationalScheduledPaymentConsentsConsentIdFundsConfirmation: String,
                    val GetInternationalStandingOrder: String,
                    val GetInternationalStandingOrderConsent: String
                )
            }
        }

        data class RsDiscoveryFundsConfirmationAPI(
                val Links: RsDiscoveryFundsConfirmationAPILinks,
                val Version: String
        ) {
            data class RsDiscoveryFundsConfirmationAPILinks(
                val `@type`: String,
                val linkValues: List<String>,
                val links: Links
            ) {
                data class Links(
                    val CreateFundsConfirmation: String,
                    val GetFundsConfirmation: String,
                    val GetFundsConfirmationConsent: String
                )
            }
        }

        data class RsDiscoveryAccountAndTransactionAPI(
                val Links: RsDiscoveryAccountAndTransactionAPILinks,
                val Version: String
        ) {
            data class RsDiscoveryAccountAndTransactionAPILinks(
                val `@type`: String,
                val linkValues: List<String>,
                val links: Links
            ) {
                data class Links(
                    val CreateAccountAccessConsent: String,
                    val DeleteAccountAccessConsent: String,
                    val GetAccount: String,
                    val GetAccountAccessConsent: String,
                    val GetAccountBalances: String,
                    val GetAccountBeneficiaries: String,
                    val GetAccountDirectDebits: String,
                    val GetAccountOffers: String,
                    val GetAccountParties: String,
                    val GetAccountParty: String,
                    val GetAccountProduct: String,
                    val GetAccountScheduledPayments: String,
                    val GetAccountStandingOrders: String,
                    val GetAccountStatement: String,
                    val GetAccountStatementFile: String,
                    val GetAccountStatementTransactions: String,
                    val GetAccountStatements: String,
                    val GetAccountTransactions: String,
                    val GetAccounts: String,
                    val GetBalances: String,
                    val GetBeneficiaries: String,
                    val GetDirectDebits: String,
                    val GetOffers: String,
                    val GetParty: String,
                    val GetProducts: String,
                    val GetScheduledPayments: String,
                    val GetStandingOrders: String,
                    val GetStatements: String,
                    val GetTransactions: String
                )
            }
        }

        data class RsDiscoveryEventNotificationAPI(
                val Links: RsDiscoveryEventNotificationAPILinks,
                val Version: String
        ) {
            data class RsDiscoveryEventNotificationAPILinks(
                val `@type`: String,
                val linkValues: List<String>,
                val links: Links
            ) {
                data class Links(
                    val AmendCallbackUrl: String,
                    val AmendEventSubscription: String,
                    val CreateCallbackUrl: String,
                    val CreateEventSubscription: String,
                    val DeleteCallbackUrl: String,
                    val DeleteEventSubscription: String,
                    val EventAggregatedPolling: String,
                    val GetCallbackUrls: String,
                    val GetEventSubscription: String
                )
            }
        }
    }
}