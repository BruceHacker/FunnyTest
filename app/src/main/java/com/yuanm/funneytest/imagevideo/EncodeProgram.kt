package com.yuanm.funneytest.imagevideo

import android.graphics.Bitmap
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10

/**
 * 用来编码的GPU程序
 */
class EncodeProgram(private val width: Int, private val height: Int) {

  private val TAG = "Video_EncodeProgram"

  /**
   * 逐行解释
   *
   * 1.4*4的矩阵
   * 2.4维向量
   * 3.2维向量
   * 4.varying 修饰从定点着色器传递到片元着色器过来的数据
   */
  private val VERTEX_SHADER = """
                attribute vec4 position;
                attribute vec2 aTexCoord;
                varying vec2 vTexCoord;
                void main() {
                    vTexCoord = aTexCoord;
                    gl_Position = position;
                }
        """

  /**
   * 逐行解释
   *
   * 1、float 精度修饰， medium 16bit，用于纹理坐标
   * 2、varying 修饰从顶点着色器传递到片元着色器过来的数据
   * 3、二维纹理声明
   *
   * 4、使用texture2D取出纹理坐标点上的纹理像素值
   * */
  private val FRAGMENT_SHADER = """
                precision mediump float;
                varying vec2 vTexCoord;
                uniform sampler2D texture;
                void main() {
                    gl_FragColor = texture2D(texture, vTexCoord);
                }
                """

  private val vertexBuffer by lazy {
    val data = floatArrayOf(-1f, 1f, 0f, -1f, -1f, 0f, 1f, -1f, 0f, 1f, 1f, 0f)
    val buffer = createFloatBuffer(data)
    buffer.position(0)
    buffer
  }

  private val texBuffer by lazy {
    val buffer = createFloatBuffer(floatArrayOf(0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f))
    buffer.position(0)
    buffer
  }

  private val indexBuffer by lazy {
    val data = intArrayOf(0, 1, 2, 0, 3, 2)
    val allocate = IntBuffer.allocate(data.size).put(data)
    allocate.position(0)
    allocate
  }


  private var program = 0
  private var posHandle: Int = -1
  private var texHandle: Int = -1
  private var textureHandle: Int = -1
  private var textureID = 0

  fun build() {
    program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
    initLocation()
  }

  fun renderBitmap(bitmap: Bitmap) {
    GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT)

    // 设置当前活动的纹理单元为纹理单元0
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
    // 将纹理ID绑定到当前活动的纹理单元上
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID)
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
    bitmap.recycle()

    // 顶点坐标
    GLES20.glEnableVertexAttribArray(posHandle)
    GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)

    // 纹理坐标
    GLES20.glEnableVertexAttribArray(texHandle)
    GLES20.glVertexAttribPointer(texHandle, 2, GLES20.GL_FLOAT, false, 0, texBuffer)

    GLES20.glUniform1i(texHandle, 0)
    GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_INT, indexBuffer)

    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE)
  }

  // 释放纹理
  fun release() {
    val id = intArrayOf(textureID)
    GLES20.glDeleteTextures(id.size, id, 0)
  }


  /**
   * todo: 可抽象成扩展方法
   *
   * 创建一个显卡可执行程序，运行在GPU
   */
  private fun createProgram(vertexSource: String, fragmentSource: String): Int {
    val ints = IntArray(1)
    GLES20.glGetIntegerv(GLES20.GL_MAX_VERTEX_ATTRIBS, ints, 0)
    Log.d(TAG, "create program max vertex attribs : ${ints[0]}")

    val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
    Log.d(TAG, "createProgram vertexShader: $vertexShader")
    if (vertexShader == 0) {
      return 0
    }
    val pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
    Log.d(TAG, "createProgram vertexShader: $pixelShader")
    if (pixelShader == 0) {
      return 0
    }

    // 创建一个显卡可执行程序
    var program = GLES20.glCreateProgram()
    if (program == 0) {
      Log.e(TAG, "Could not create program")
    }
    // 将编译好的shader着色器加载到这个可执行程序上
    GLES20.glAttachShader(program, vertexShader)
    checkGlError("glAttachShader")
    GLES20.glAttachShader(program, pixelShader)
    checkGlError("glAttachShader")
    // 链接程序
    GLES20.glLinkProgram(program)
    // 检查程序状态，第三个参数是返回值，返回1就是成功，返回0就是失败
    val linkStatus = IntArray(1)
    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
    if (linkStatus[0] != GLES20.GL_TRUE) {
      Log.e(TAG, "Could not link program")
      Log.e(TAG, GLES20.glGetProgramInfoLog(program))
      GLES20.glDeleteProgram(program)
      program = 0
    }
    if (program == 0) {
      throw RuntimeException("create GPU program failed")
    }
    GLES20.glUseProgram(program)
    return program
  }

  // todo: 可抽象成扩展方法
  private fun loadShader(shaderType: Int, source: String): Int {
    // 创建一个对象，作为shader容器，此函数返回容器对象地址
    var shader = GLES20.glCreateShader(shaderType)
    checkGlError("glCreateShader type=${shaderType}")
    // 为shader添加源代码，shader content(着色器程序，根据GLSL语法和内嵌函数编写)
    // 将开发者编写的着色器程序加载到着色器对象的内存中
    GLES20.glShaderSource(shader, source)
    // 编译这个着色器
    GLES20.glCompileShader(shader)
    val compiled = IntArray(1)
    // 验证是否编译成功，第二个参数是需要验证shader的状态值。第三个参数是返回值，返回1说明成功，返回0则失败
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
    if (compiled[0] == 0) {
      Log.d(TAG, "Could not compile shader $shaderType")
      Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader))
      GLES20.glDeleteShader(shader)
      shader = 0
    }
    return shader
  }

  private fun initLocation() {
    posHandle = getAttribLocation(program, "position")
    texHandle = getAttribLocation(program, "aTexCoord")

    textureHandle = getUniformLocation(program, "texture")
    textureID = buildTextureId(GLES20.GL_TEXTURE_2D)

    GLES20.glClearColor(0f, 0f, 0f, 0f)
    GLES20.glViewport(0, 0, width, height)
  }

  private fun getAttribLocation(program: Int, name: String): Int {
    val location = GLES20.glGetAttribLocation(program, name)
    checkLocation(location, name)
    return location
  }

  private fun getUniformLocation(program: Int, name: String): Int {
    val uniform = GLES20.glGetUniformLocation(program, name)
    checkLocation(uniform, name)
    return uniform
  }

  /**
   * 创建一个纹理对象，并且和ES绑定
   *
   * 生成Camera特殊的Texture
   * 在Android中Camera产生的preview texture是一种特殊的格式传送的，因此shader里的纹理类型不是普通的sampler2D，而是samplerExternalOES，在shader
   * 的头部也必须声明OES的扩展。
   * 除此之外，external OES的纹理和Sampler2D在使用时没有区别
   */
  private fun buildTextureId(target: Int = GLES11Ext.GL_TEXTURE_EXTERNAL_OES): Int {
    val ids = IntArray(1)
    GLES20.glGenTextures(1, ids, 0)
    checkGlError("create texture check")
    val id = ids[0]
    bindAndSetTexture(target, id)
    return id
  }

  private fun bindAndSetTexture(target: Int, id: Int) {
    // 这里的绑定纹理是将GPU的纹理数据和ID对应起来，载入纹理到此ID处
    // 渲染时绑定纹理，是绑定纹理ID到激活的纹理单元
    GLES20.glBindTexture(target, id)
    checkGlError("bind texture : $id check")

    // 设置纹理参数、过滤器放大缩小
    GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
    GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)

    GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
    GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
  }


  private fun checkLocation(location: Int, label: String) {
    if (location < 0) {
      throw RuntimeException("Unable to locate '$label' in program")
    }
  }

  // todo: 可抽象成扩展方法
  private fun checkGlError(op: String) {
    val error: Int = GLES20.glGetError()
    if (error != GLES20.GL_NO_ERROR) {
      throw RuntimeException("$op: glError $error and msg is ${Integer.toHexString(error)}")
    }
  }

  // todo: 需要抽象到工具类中
  private fun createFloatBuffer(array: FloatArray): FloatBuffer {
    val buffer = ByteBuffer
      // 分配顶点坐标分量分数 * Float占的Byte位数
      .allocateDirect(array.size * 4)
      // 按照本地字节序排序
      .order(ByteOrder.nativeOrder())
      // Byte类型转Float类型
      .asFloatBuffer()
    // 将Dalvik的内存数据复制到Native内存中
    buffer.put(array).position(0)
    return buffer
  }


}