package com.yuanm.common.utils

import android.os.Process
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * 参考博文：https://zhuanlan.zhihu.com/p/34405230
 */
object ThreadPoolUtil {

  private val mService by lazy {
    // 使用Executors的各类方法所创建的线程池会产生无界队列（任务的排队队列）
    Executors.newFixedThreadPool(3) {
      val thread = Thread(it, "ThreadPoolUtil")
      Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND) // 设置线程优先级
      return@newFixedThreadPool thread
    }
  }

  // 获取全局线程池
  fun getService(): ExecutorService {
    return mService
  }


  /**
   * 使用ThreadPoolExecutor方法创建 有界队列 线程池，并明确拒绝任务时的行为
   */
  private val workQueue = LinkedBlockingQueue<Runnable>(10)

  private val mService2 by lazy {
    // keepAliveTime的单位毫秒
    val executor = ThreadPoolExecutor(2, 4, 60L, TimeUnit.SECONDS, workQueue)
    executor.rejectedExecutionHandler = ThreadPoolExecutor.DiscardPolicy()
    executor
  }

}