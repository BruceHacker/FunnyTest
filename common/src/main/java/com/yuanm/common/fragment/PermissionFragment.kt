package com.yuanm.common.fragment

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch

class PermissionFragment : Fragment() {

  private lateinit var requestMutiplePermissionslauncher: ActivityResultLauncher<Array<String>>
  var permissions: Array<String>? = null
  var accept: ((Boolean) -> Unit)? = null
  var permissionResult: ((Permission) -> Unit)? = null
  var denied: ((String) -> Unit)? = null
  var alwaysDenied: ((String) -> Unit)? = null

  fun removeFragment() {
    val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
    fragmentTransaction.remove(this).commit()
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    requestMutiplePermissionslauncher =
      registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { it ->
        lifecycleScope.launch {
          // 是否所有权限都通过
          val allGranted = it.iterator()
            .asFlow()
            .flowOn(Dispatchers.Main)
            .map { it.value }
            .reduce { accumulator, value ->
              accumulator && value
            }
          accept?.invoke(allGranted)
          it.iterator()
            .asFlow()
            .flowOn(Dispatchers.Main)
            .onEach { entry ->
              Log.d("yuanm", "所有权限：" + entry.key)
              permissionResult?.invoke(
                Permission(
                  entry.key, entry.value,
                  ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), entry.key)
                )
              )
            }
            .filter { !it.value }
            .onEach { entry ->
              Log.d("yuanm", "拒绝的权限：" + entry.key)
              denied?.invoke(entry.key)
            }
            .filter {
              !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it.key)
            }
            .onEach { entry ->
              Log.d("yuanm", "拒绝并且点了'不再询问'的权限：" + entry.key)
              alwaysDenied?.invoke(entry.key)
            }
            .collect()
          if (isAdded) {
            removeFragment()
          }
        }
      }

    if (permissions?.isNotEmpty() == true)
      requestMutiplePermissionslauncher.launch(permissions)

  }
}

data class Permission(
  var name: String?,
  var granted: Boolean,
  var shouldShowRequestPermissionRationale: Boolean
)