package de.tarent.crud.application.results

sealed interface MetricResult<T>

sealed interface MetricCreateResult<T> : MetricResult<T>

sealed interface MetricReadResult<T> : MetricResult<T>

sealed interface MetricDeleteResult<T> : MetricResult<T>

sealed interface MetricQueryResult<T> : MetricResult<T>
