package de.tarent.crud.service.results

sealed interface MetricResult<T>
sealed interface MetricCreateResult<T> : MetricResult<T>
sealed interface MetricReadResult<T> : MetricResult<T>
sealed interface MetricDeleteResult<T> : MetricResult<T>
sealed interface MetricQueryResult<T> : MetricResult<T>