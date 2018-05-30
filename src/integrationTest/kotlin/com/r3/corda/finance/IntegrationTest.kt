package com.r3.corda.finance

import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.TestIdentity
import net.corda.testing.driver.DriverParameters
import net.corda.testing.driver.driver
import org.junit.Test
import kotlin.test.assertEquals

class DriverBasedTest {
    val issuer = TestIdentity(CordaX500Name("BankA", "", "GB"))
    val partyA = TestIdentity(CordaX500Name("BankA", "", "GB"))
    val partyB = TestIdentity(CordaX500Name("BankB", "", "US"))

    @Test
    fun `node test`() {
        driver(DriverParameters(isDebug = true, startNodesInProcess = true)) {
            // This starts two nodes simultaneously with startNode, which returns a future that completes when the node
            // has completed startup. Then these are all resolved with getOrThrow which returns the NodeHandle list.
            val (partyAHandle, partyBHandle) = listOf(
                    startNode(providedName = partyA.name),
                    startNode(providedName = partyB.name)
            ).map { it.getOrThrow() }

            // This test makes an RPC call to retrieve another node's name from the network map, to verify that the
            // nodes have started and can communicate. This is a very basic test, in practice tests would be starting
            // flows, and verifying the states in the vault and other important metrics to ensure that your CorDapp is
            // working as intended.
            assertEquals(partyAHandle.rpc.wellKnownPartyFromX500Name(partyA.name)!!.name, partyA.name)
            assertEquals(partyBHandle.rpc.wellKnownPartyFromX500Name(partyB.name)!!.name, partyB.name)
        }
    }
}