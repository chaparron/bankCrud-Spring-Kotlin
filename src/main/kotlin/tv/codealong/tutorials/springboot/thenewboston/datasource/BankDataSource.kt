package tv.codealong.tutorials.springboot.thenewboston.datasource

import org.springframework.stereotype.Repository
import tv.codealong.tutorials.springboot.thenewboston.model.Bank

@Repository
interface BankDataSource {
    fun retrieveBanks(): Collection<Bank>
    fun retrieveBank(accountNumber: String): Bank
    fun createBank(bank: Bank): Bank
    fun updateBank(bank: Bank): Bank
    fun deleteBank(accountNumber: String)
}