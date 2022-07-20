import com.registration.data.api.ApiHelper


class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun getUsers() = apiHelper.getUsers()
    suspend fun addUser(accessToken:String,
                        name:String,email:String,gender:String,status:String) =
        apiHelper.addUser(accessToken, name,email,gender,status)
    suspend fun deleteUser(accessToken:String,
                        id:Int) =
        apiHelper.deleteUser(accessToken, id)
}