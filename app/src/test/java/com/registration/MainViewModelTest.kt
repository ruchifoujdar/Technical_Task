package com.registration

import MainRepository

import com.registration.data.api.ApiHelper
import com.registration.data.api.ApiHelperImpl
import com.registration.data.api.ApiHelperImpl.Companion.ACCESS_TOKEN
import com.registration.data.api.RetrofitBuilder.apiService
import com.registration.ui.viewstate.MainState
import junit.framework.Assert.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Before
import org.mockito.MockitoAnnotations
import java.util.*
/**
 * Unit tests for the MainViewModelTest
 */
class MainViewModelTest {

    private lateinit var repository: MainRepository
    private lateinit var apiHelper: ApiHelper
    private var _state = MutableStateFlow<MainState>(MainState.Idle)

    @Before
     fun setUp() {
        MockitoAnnotations.initMocks(this)
        runBlocking {
            apiHelper = ApiHelperImpl(apiService)
            repository = MainRepository(apiHelper)

        }
     }

    @Test
    fun `add user if all data is correct`() {
        runBlocking {
            val expected = true
            var result = false
            val randomStr = generateString()

            _state.value = MainState.Loading
            _state.value = try {
                MainState.AddUser(repository.addUser(ACCESS_TOKEN,
                    randomStr, "$randomStr@gmail.com","female","active"))
                result = true
                MainState.Success("User data added")
            } catch (e: Exception) {
                MainState.Error(e.localizedMessage)
            }
            assertEquals(expected, result)
        }
    }

    @Test
    fun `add user fail when access-token is incorrect`() {
        runBlocking {
            var result = false
            val randomStr = generateString()
            _state.value = MainState.Loading
            _state.value = try {
                MainState.AddUser(repository.addUser(ACCESS_TOKEN+"ASHG",
                    randomStr, "${randomStr}@gmail.com","female","active"))
                result = true
                MainState.Success("User data added")
            } catch (e: Exception) {
                MainState.Error(e.localizedMessage)
            }
            assertFalse(result)
        }
    }

    @Test
    fun `add user fail when email is already taken`() {
        runBlocking {
            var result = false
            val randomStr = generateString()
            _state.value = MainState.Loading
            _state.value = try {
                MainState.AddUser(repository.addUser(ACCESS_TOKEN,
                    randomStr, "ruch@gmail.com","female","active"))
                result = true
                MainState.Success("User data added")
            } catch (e: Exception) {
                MainState.Error(e.localizedMessage)
            }
            assertFalse(result)
        }
    }

    @Test
    fun `fetch user all`() {
        runBlocking {
            var result = false
            _state.value = MainState.Loading
            _state.value = try {
                result = true
                MainState.Users(repository.getUsers())
            } catch (e: Exception) {
                MainState.Error(e.localizedMessage)
            }
            assertTrue(result)
        }
    }

   /* @Test
    fun `delete user successfully`() {
        runBlocking {
            var result = false
            val r = Random()
            val numberRandom = r.nextInt(45 - 28) + 28

            _state.value = MainState.Loading
            _state.value = try {
                MainState.DeleteUser(repository.deleteUser(
                    "fdb446c71eeaf3c11723c5edfa5956971b88581754f1485fa577fbc9fb4e246e",
                    numberRandom))
                result = true
                MainState.Success("User deleted")
            } catch (e: Exception) {
                MainState.Error(e.localizedMessage)
            }
            assertTrue(result)
        }
    }*/

    @Test
    fun `delete user fail if id does not exits`() {
        runBlocking {
            var result = false
            val r = Random()
            val numberRandom = r.nextInt(45 - 28) + 28

            _state.value = MainState.Loading
            _state.value = try {
                MainState.DeleteUser(repository.deleteUser(
                    ACCESS_TOKEN,
                    numberRandom))
                result = true
                MainState.Success("User deleted")
            } catch (e: Exception) {
                MainState.Error(e.localizedMessage)
            }
            assertFalse(result)
        }
    }

    private fun generateString(): String {
        val randomStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        val str = StringBuilder()
        val rnd = Random()
        while (str.length < 10) { // length of the random string.
            str.append(randomStr[((rnd.nextFloat() * randomStr.length).toInt())])
        }
        return str.toString()
    }
}