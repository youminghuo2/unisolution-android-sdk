package com.example.module_frame.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.module_frame.entity.PermissionEntity
import com.example.module_frame.entity.PermissionListEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 常用工具列
 */
object CommonUtils {

    //保存dataStore
    suspend fun <T> editDataStore(context: Context, key: Preferences.Key<T>, data: T) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { settings ->
                settings[key] = data
            }
        }
    }

    //清除dataStore保存的数据
    suspend fun clearDataStore(context: Context) {
        context.dataStore.edit { settings ->
            settings.clear()
        }
    }

    //检查权限是否被授权
    fun checkSelfPermission(context: Context, permission: String): Boolean {
        val result = ContextCompat.checkSelfPermission(context, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }


    /**
     * 检查多个授权是否被授权
     * 返回的是未授权的entity
     * 格式严格遵守PermissionListEntity类
     */
    fun checkSelfPermissionMultiple(context: Context,permissionList: List<PermissionListEntity>):List<PermissionListEntity>{
        val entity= mutableListOf<PermissionListEntity>()

        for (permissionData in permissionList) {
            val permission = mutableListOf<String>()

            for (item in permissionData.permission) {
                val result = ContextCompat.checkSelfPermission(context, item)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permission.add(item)
                }
            }

            if (permission.isNotEmpty()) {
                val data = PermissionListEntity(permission, permissionData.dataList)
                entity.add(data)
            }
        }
       return entity
    }

    /**
     * 权限说明只要调用这一行代码哦
     * 返回了2个List
     * 分别代表：explainList= mutableListOf<PermissionEntity>()里面包含了title和 msg
     *         requireList=mutableListOf<String>申请的权限列表
     */
    fun processPermissions(multipleList: List<PermissionListEntity>): Pair<List<PermissionEntity>, List<String>> {
        val explainList=multipleList.map { PermissionEntity(it.dataList.title,it.dataList.msg) }
        val requireList=multipleList.flatMap { it.permission }

        return Pair(explainList, requireList)
    }




}