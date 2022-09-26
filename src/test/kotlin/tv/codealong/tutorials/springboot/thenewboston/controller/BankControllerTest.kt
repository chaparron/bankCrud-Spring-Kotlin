package tv.codealong.tutorials.springboot.thenewboston.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.*
import tv.codealong.tutorials.springboot.thenewboston.model.Bank

@SpringBootTest // Inicializará toda la app para estos tests
@AutoConfigureMockMvc // para que inicie los MockMvc
internal class BankControllerTest @Autowired constructor(
    var mockMvc: MockMvc,
    var objectMapper: ObjectMapper
) {

    val baseUrl ="/api/banks"
    
    @Nested
    @DisplayName("GET /api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBanks {
        @Test
        fun `should return all banks`() {
            // When/ Then
            mockMvc.get(baseUrl)
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath( "$[0].accountNumber") {value("1234")}
                }
        }
    }

    @Nested
    @DisplayName("GET /api/bank/<accountNumber>")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class GetBank {
        @Test
        fun `should return the bank with the given account number`() {
            // Given
            val accountNumber = "1234"
            // WhenThen
            mockMvc.get("$baseUrl/$accountNumber")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.trust") {value("3.14")}
                    jsonPath("$.transactionFee") {value("17")}
                }
        }

        @Test
        fun `should return NOT FOUND if the account number does not exist`() {
            // Given
            val accountNumber = "does_not_exist"
            // WhenThen
            mockMvc.get("$baseUrl/$accountNumber")
                .andDo { print() }
                .andExpect {status { isNotFound() } }
        }
    }
    
    @Nested
    @DisplayName("POST /api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PostNewBank {
    
        @Test
        fun `should add a new bank`() {
            // Given
            val newBank = Bank("4321", 10.0, 6)
            // When
            val performPost = mockMvc.post(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(newBank)
            }
            // Then
            performPost
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.accountNumber"){value("4321")}
                    jsonPath("$.trust"){value("10.0")}
                    jsonPath("$.transactionFee"){value("6")}
                }

            mockMvc.get("$baseUrl/${newBank.accountNumber}")
                .andExpect { content { json(objectMapper.writeValueAsString(newBank)) } }
            
        }

        @Test
        fun `should return a BAD REQUEST if bank with given number already exist`() {
            // Given
            val invalidBank = Bank("1234", 9.9, 5)
            // When
            val performPost = mockMvc.post(baseUrl){
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(invalidBank)
            }
            // Then
            performPost
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    @DisplayName("PATCH /api/banks")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class PatchExistingBank {
    
        @Test
        fun `should update an existing bank`() {
            // Given
            val updatedBank = Bank("1234", 7.7, 7)
            // When
            val perfomPatchRequest = mockMvc.patch(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(updatedBank)
            }
            // Then
            perfomPatchRequest
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(objectMapper.writeValueAsString(updatedBank))
                    }
                }
            mockMvc.get("$baseUrl/${updatedBank.accountNumber}")
                .andExpect { content { json(objectMapper.writeValueAsString(updatedBank)) } }
        }

        @Test
        fun `should return a BAD REQUEST if no bank with given account number exists`() {
            // Given
            val invalidBank = Bank("not_existing_account", 0.1, 1)
            // When
            val performPatchRequest = mockMvc.patch(baseUrl) {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(invalidBank)
            }
            // Then
            performPatchRequest
                .andDo { print() }
                .andExpect { status { isNotFound() } }
        }
    }
    
    @Nested
    @DisplayName("DELETE /api/banks/<accountNumber>")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class DeleteExistingBank {

        @Test
        @DirtiesContext
        fun `should delete the bank with the given account number`() {
            // Given
            val accountNumber = "1234"
            // WhenThen
            mockMvc.delete("$baseUrl/$accountNumber")
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }
            mockMvc.get("$baseUrl/$accountNumber")
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
        }
        @Test
        fun `should return NOT FOUND if no bank with given account number doesn't exist`() {
            // Given
            val invalidAccountNumber = "does_not_exist"
            // WhenThen
            mockMvc.delete("$baseUrl/$invalidAccountNumber")
                .andDo { print() }
                .andExpect { status { isNotFound() } }
        }
    }
}