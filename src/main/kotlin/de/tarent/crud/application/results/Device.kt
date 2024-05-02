package de.tarent.crud.application.results

sealed interface DeviceResult<T>

sealed interface DeviceReadResult<T> : DeviceResult<T>

sealed interface DeviceCreateResult<T> : DeviceResult<T>

sealed interface DeviceUpdateResult<T> : DeviceResult<T>

sealed interface DeviceDeleteResult<T> : DeviceResult<T>

sealed interface DeviceListResult<T> : DeviceResult<T>
