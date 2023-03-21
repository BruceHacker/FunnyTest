package com.yuanm.funneytest.imagevideo

import android.opengl.GLES20
import android.util.Log

/**
 * 用来编码的GPU程序
 */
class EncodeProgram(private val width: Int, private val height: Int) {

  private val TAG = "EncodeProgram"

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


  private var program = 0

  fun build() {
    program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)
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


  // todo: 可抽象成扩展方法
  private fun checkGlError(op: String) {
    val error: Int = GLES20.glGetError()
    if (error != GLES20.GL_NO_ERROR) {
      throw RuntimeException("$op: glError $error and msg is ${Integer.toHexString(error)}")
    }
  }


}