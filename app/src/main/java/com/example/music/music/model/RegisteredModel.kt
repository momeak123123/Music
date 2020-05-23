package  com.example.music.music.model

import com.example.music.music.contract.RegisteredContract
import mvp.ljb.kt.model.BaseModel

/**
 * @Author Kotlin MVP Plugin
 * @Date 2020/05/20
 * @Description input description
 **/
class RegisteredModel : BaseModel(), RegisteredContract.IModel {
    override fun registerdata(): Boolean {
        return true
    }
}