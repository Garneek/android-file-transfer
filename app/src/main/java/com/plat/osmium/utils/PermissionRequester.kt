package com.plat.osmium.utils

import android.content.pm.PackageManager

class PermissionRequester(val requestValues: RequestedValues) {
  enum class RequestedValues {
    Any,
    All
  }

  val permissions: ArrayList<String> = arrayListOf()

  fun add(permission: String) = permissions.add(permission)
  fun add(pms: Collection<String>) = permissions.addAll(pms)

  fun request(): Boolean {
    val granted: List<Boolean> = permissions.map { p ->
      Common.appContext.checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED
    }

    val satisfied = granted.fold(false) { ret, v ->
      when (requestValues) {
        RequestedValues.Any -> ret && v
        RequestedValues.All -> ret || v
      }
    }

    val filteredPermissions =
      permissions.filterIndexed { index, _ -> !granted[index] }.toTypedArray()

    if (filteredPermissions.isNotEmpty())
      Common.activity.requestPermissions(
        filteredPermissions, 1234
      )

    return true
  }
}
