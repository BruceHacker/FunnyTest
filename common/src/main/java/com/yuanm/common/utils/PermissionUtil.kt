package com.yuanm.common.utils

import androidx.fragment.app.FragmentActivity
import com.yuanm.common.fragment.Permission
import com.yuanm.common.fragment.PermissionFragment

/**
 * 利用Kotlin的Flow实现一个优雅的权限申请
 *
 * 参考博文：https://blog.51cto.com/u_15501625/4969992
 */
object PermissionUtil {

  private val permissionFragment: PermissionFragment = PermissionFragment()

  /**
   * 所有权限统一返回结果
   */
  fun permissionsRequest(activity: FragmentActivity, permissions: Array<String>, accept: (allGranted: Boolean) -> Unit) {
    permissionFragment.permissions = permissions
    permissionFragment.accept = accept
    val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
    fragmentTransaction.add(permissionFragment, "permissionFragment").commit()
  }

  /**
   * 将权限申请结果逐一返回
   */
  fun permissionsRequestEach(activity: FragmentActivity, permissions: Array<String>, permissionResult: (Permission) -> Unit) {
    permissionFragment.permissions = permissions
    permissionFragment.permissionResult = permissionResult
    val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
    fragmentTransaction.add(permissionFragment, "permissionFragment").commit()
  }
}