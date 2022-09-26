package tv.codealong.tutorials.springboot.thenewboston.datasource.mock

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MockBankDataSourceTest {


    private val mockDataSource = MockBankDataSource()

    @Test
    fun `should provide a collection of banks`() {
        // When
        val banks = mockDataSource.retrieveBanks()

        // Then
        assertThat(banks.size).isGreaterThanOrEqualTo(3)
    }

    @Test
    fun `should have all different account numbers`() {

        // When
        val banks = mockDataSource.retrieveBanks()
        val listOfAccountNumbers = mutableListOf<String>()
        for (bank in banks) {
            listOfAccountNumbers.add(bank.accountNumber)
        }
        val setOfAccountNumbers = listOfAccountNumbers.toSet()

        // Then
        assertThat(banks.size).isEqualTo(setOfAccountNumbers.size)
    }
    @Test
    fun `should provide some mock data`() {

        // When
        val banks = mockDataSource.retrieveBanks()

        // Then
        assertThat(banks).allMatch { it.accountNumber.isNotBlank() }
        assertThat(banks).anyMatch { it.trust != 0.0 }
        assertThat(banks).anyMatch { it.transactionFee != 0 }
    }
}